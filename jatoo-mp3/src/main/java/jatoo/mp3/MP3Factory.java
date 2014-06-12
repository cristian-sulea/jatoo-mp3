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

import java.io.File;
import java.net.URL;

/**
 * Factory class for creating {@link MP3} objects from various inputs.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.1, June 12, 2014
 */
public final class MP3Factory {

  /**
   * Private constructor (this is a factory class).
   */
  private MP3Factory() {}

  public static MP3 createMP3(final MP3Input input) {
    return new MP3Impl(input);
  }

  public static MP3 createMP3(final File file) {
    return createMP3(new MP3InputFromFile(file));
  }

  public static MP3 createMP3(final String file) {
    return createMP3(new MP3InputFromFile(file));
  }

  public static MP3 createMP3(final URL url) {
    return createMP3(new MP3InputFromURL(url));
  }

}
