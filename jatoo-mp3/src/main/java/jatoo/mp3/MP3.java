/*
 * Copyright (C) 2014 Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.mp3;

import javax.sound.sampled.SourceDataLine;

/**
 * Mp3 Interface
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 1.1, May 19, 2014
 */
public interface MP3 {

  /**
   * Starts to play the MP3 file (or resume if is paused).
   */
  void play();

  /**
   * Waits for the player thread to finish playing.
   * 
   * @see Thread#join()
   */
  void join();

  /**
   * Tests if this mp3 is playing.
   */
  boolean isPlaying();

  /**
   * Pauses the play.
   */
  void pause();

  /**
   * Tests if this mp3 is paused.
   */
  boolean isPaused();

  /**
   * Stops the play.
   */
  void stop();

  /**
   * Tests if this mp3 is stopped.
   */
  boolean isStopped();

  /**
   * Sets a new volume for this player. The value is actually the percent value,
   * so the value must be in interval [0,200].
   * 
   * @param volume
   *          the new volume
   * 
   * @throws IllegalArgumentException
   *           if the volume is not in interval [0,200]
   */
  void setVolume(int volume);

  /**
   * Returns the actual volume.
   */
  int getVolume();

  /**
   * Retrieves the position in milliseconds of the current audio sample being
   * played. This method delegates to the {@link SourceDataLine} that is used by
   * this player to sound the decoded audio samples.
   */
  long getPosition();

}