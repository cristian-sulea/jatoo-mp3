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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation for {@link MP3}.This provides a convenient base class
 * from which other applications can be easily derived.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 1.0, December 3, 2013
 */
public abstract class AbstractMP3 implements MP3 {

	protected final Log logger = LogFactory.getLog(getClass());

	protected final Object mutex = new Object();

	protected enum STATUS {
		PLAYING,
		PAUSED,
		STOPPED
	}

	protected volatile STATUS status = STATUS.STOPPED;
	protected volatile int volume = 100;

	@Override
	public final void play() {
		synchronized (mutex) {
			playImpl();
		}
	}

	protected abstract void playImpl();

	@Override
	public final boolean isPlaying() {
		synchronized (mutex) {
			return status == STATUS.PLAYING;
		}
	}

	@Override
	public final void pause() {

		logger.info("pause");

		synchronized (mutex) {
			status = STATUS.PAUSED;
		}
	}

	@Override
	public final boolean isPaused() {
		synchronized (mutex) {
			return status == STATUS.PAUSED;
		}
	}

	@Override
	public final void stop() {

		logger.info("stop");

		synchronized (mutex) {
			status = STATUS.STOPPED;
		}
	}

	@Override
	public final boolean isStopped() {
		synchronized (mutex) {
			return status == STATUS.STOPPED;
		}
	}

	@Override
	public void setVolume(int volume) {

		logger.info("volume: " + volume);

		if (volume < 0 || volume > 200) {
			throw new IllegalArgumentException("Wrong value for volume (" + volume + "), must be in interval [0..200].");
		}

		this.volume = volume;
	}

	@Override
	public int getVolume() {
		return volume;
	}

}
