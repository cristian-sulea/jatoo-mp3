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

import java.io.IOException;
import java.io.InputStream;

/**
 * The input for a {@link MP3} instance.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 1.0, December 3, 2013
 */
public interface MP3Input {

  /**
   * Creates a new {@link InputStream} to the sound (mp3) source.
   * 
   * @throws IOException
   */
  InputStream createStream() throws IOException;

}
