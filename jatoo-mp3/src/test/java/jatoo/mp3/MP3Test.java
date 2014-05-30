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
