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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link File} {@link MP3Input}
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.1, June 12, 2014
 */
public class MP3InputFromFile implements MP3Input {

  private File file;

  public MP3InputFromFile(File file) {
    this.file = file;
  }

  public MP3InputFromFile(String file) {
    this.file = new File(file);
  }

  @Override
  public InputStream createStream() throws IOException {
    return new FileInputStream(file);
  }

  @Override
  public String toString() {
    return file.getAbsolutePath();
  }

}
