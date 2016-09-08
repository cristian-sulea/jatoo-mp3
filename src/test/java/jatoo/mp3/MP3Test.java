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

import org.junit.Test;

public class MP3Test {

  @Test
  public void test1() throws Exception {

    MP3 mp3 = MP3Factory.createMP3(MP3Test.class.getResource("file1.mp3"));

    mp3.play();
    Thread.sleep(2000);
    mp3.stop();
    Thread.sleep(2000);
    mp3.play();
    Thread.sleep(2000);
    mp3.play();
    Thread.sleep(2000);
    mp3.pause();
    Thread.sleep(2000);
    mp3.play();
    Thread.sleep(2000);
  }

  @Test
  public void test2() throws Exception {

    MP3 mp3 = MP3Factory.createMP3(MP3Test.class.getResource("file2.mp3"));

    mp3.play();
    mp3.join();
  }

}
