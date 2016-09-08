/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.mp3;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Default implementation (using javazoom) for {@link MP3}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.2, June 12, 2014
 */
class MP3Impl extends AbstractMP3 implements MP3 {

  private volatile MP3Input input;
  private volatile Thread thread;
  private volatile SourceDataLine source;

  private int sourceVolume = DEFAULT_VOLUME;
  private float[] sourceVolumes = new float[2 * DEFAULT_VOLUME + 1];

  MP3Impl(MP3Input input) {
    this.input = input;
  }

  @Override
  public void join() {
    try {
      thread.join();
    } catch (InterruptedException e) {
      logger.error("failed to #join()", e);
    }
  }

  @Override
  protected void playImpl() {

    //
    // if paused, play will unpause

    if (status == STATUS.PAUSED) {

      logger.info("unpause");

      //
      // change the status to playing

      status = STATUS.PLAYING;

      //
      // wake up the playing thread

      mutex.notifyAll();

      //
      // and that's all

      return;
    }

    //
    // if playing, play will first stop then play

    if (status == STATUS.PLAYING) {

      logger.info("stop (forced stop before new play)");

      //
      // change the status to stopped

      status = STATUS.STOPPED;

      //
      // do this on a new thread, or we will lock everything

      new Thread() {
        public void run() {

          //
          // wait for the playing thread to finish

          try {
            thread.join();
          } catch (NullPointerException | InterruptedException e) {
            logger.error("Hmm... The method Thread#join() failed?", e);
          }

          //
          // launch a new play command

          play();
        }
      }.start();

      //
      // and that's all

      return;
    }

    //
    // it's a normal play command

    logger.info("play");

    //
    // if playing thread is not null, then the mp3 is already playing

    if (thread == null) {

      //
      // change the status to playing

      status = STATUS.PLAYING;

      //
      // create and launch the playing thread

      thread = new Thread() {
        public void run() {

          InputStream inputStream = null;
          Bitstream soundStream = null;

          try {

            inputStream = input.createStream();
            soundStream = new Bitstream(inputStream);

            Decoder decoder = new Decoder();

            //
            // start the cycle
            // will end on stop command or on end of stream

            while (true) {

              synchronized (mutex) {

                //
                // if is a stop command
                // break the play

                if (status == STATUS.STOPPED) {
                  break;
                }

                //
                // if is a pause command

                if (status == STATUS.PAUSED) {

                  //
                  // flush the audio data

                  if (source != null) {
                    source.flush();
                  }

                  //
                  // and put the playing thread in wait for unpause

                  // TODO: daca se da stop dupa pauza, nu ramen in wait forever?

                  while (status == STATUS.PAUSED) {
                    try {
                      mutex.wait();
                    } catch (InterruptedException e) {
                      logger.error("The thread WAIT method failed, this means PAUSE will not work.", e);
                    }
                  }
                }
              }

              //
              // play

              try {

                //
                // read the stream frame by frame

                Header frame = soundStream.readFrame();

                //
                // if frame is null, the end of the stream has been reached

                if (frame == null) {
                  break;
                }

                else {

                  //
                  // first time / first frame
                  // create the playing source if is needed

                  if (source == null) {

                    int frequency = frame.frequency();
                    int channels = (frame.mode() == Header.SINGLE_CHANNEL) ? 1 : 2;

                    AudioFormat format = new AudioFormat(frequency, 16, channels, true, false);
                    Line line = AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));

                    source = (SourceDataLine) line;
                    source.open(format);
                    source.start();

                    //
                    // compute the source volumes

                    try {

                      FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);

                      float stepMinimum = Math.abs(gainControl.getMinimum() / DEFAULT_VOLUME);
                      float stepMaximum = gainControl.getMaximum() / DEFAULT_VOLUME;

                      sourceVolumes[0] = gainControl.getMinimum();
                      sourceVolumes[DEFAULT_VOLUME] = 0;
                      sourceVolumes[2 * DEFAULT_VOLUME] = gainControl.getMaximum();

                      for (int i = 1, n = DEFAULT_VOLUME - 1; i <= n; i++) {
                        sourceVolumes[i] = sourceVolumes[i - 1] + stepMinimum;
                      }

                      for (int i = DEFAULT_VOLUME + 1, n = 2 * DEFAULT_VOLUME; i <= n; i++) {
                        sourceVolumes[i] = sourceVolumes[i - 1] + stepMaximum;
                      }
                    }

                    catch (Throwable t) {

                      logger.error("Unexpected error calculating the source volumes.", t);

                      //
                      // in case of any error, set the source volume to 0
                      // meaning the signal's loudness is unaffected

                      for (int i = 0, n = 2 * DEFAULT_VOLUME; i <= n; i++) {
                        sourceVolumes[i] = 0;
                      }
                    }
                  }

                  //
                  // update the volume on the source

                  if (sourceVolume != getVolume()) {

                    sourceVolume = getVolume();

                    FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                    BooleanControl muteControl = (BooleanControl) source.getControl(BooleanControl.Type.MUTE);

                    if (sourceVolume == 0) {
                      muteControl.setValue(true);
                    } else {
                      muteControl.setValue(false);
                      gainControl.setValue(sourceVolumes[sourceVolume]);
                    }
                  }

                  //
                  // play the frame

                  SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, soundStream);

                  short[] buffer = output.getBuffer();
                  int offs = 0;
                  int len = output.getBufferLength();

                  source.write(toByteArray(buffer, offs, len), 0, len * 2);

                  //
                  // don't forget to close the frame

                  soundStream.closeFrame();
                }
              }

              catch (Exception e) {
                logger.error("Unexpected problems while playing.", e);
                break;
              }
            }
          }

          catch (IOException e) {
            logger.error("Unable to get the stream from input: " + input.toString(), e);
          }

          finally {

            if (source != null) {
              try {
                source.flush();
              } catch (Exception e) {
                logger.error("Error flushing the playing source.", e);
              }
              try {
                source.stop();
              } catch (Exception e) {
                logger.error("Error stopping the playing source.", e);
              }
              try {
                source.close();
              } catch (Exception e) {
                logger.error("Error closing the playing source.", e);
              }
            }

            if (soundStream != null) {
              try {
                soundStream.close();
              } catch (Exception e) {
                logger.error("Error closing the sound stream.", e);
              }
            }

            if (inputStream != null) {
              try {
                inputStream.close();
              } catch (Exception e) {
                logger.error("Error closing the input stream.", e);
              }
            }
          }

          //
          // end of run() method, so thread will die after this
          // don't forget to update status
          // and to reset thread and source

          status = STATUS.STOPPED;

          thread = null;
          source = null;
        }
      };

      thread.start();
    }
  }

  @Override
  public long getPosition() {
    if (source != null) {
      return source.getMicrosecondPosition() / 1000L;
    } else {
      return 0L;
    }
  }

  private byte[] toByteArray(short[] ss, int offs, int len) {

    byte[] bb = new byte[len * 2];

    int idx = 0;
    short s;

    while (len-- > 0) {

      s = ss[offs++];

      bb[idx++] = (byte) s;
      bb[idx++] = (byte) (s >>> 8);
    }

    return bb;
  }

}
