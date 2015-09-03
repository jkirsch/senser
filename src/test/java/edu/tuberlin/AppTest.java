package edu.tuberlin;


import com.google.common.io.Resources;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;


/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

    @Test
    public void testReadVideo() throws Exception {

        File file = new File(Resources.getResource("samples/SampleVideo_720x480_1mb.mp4").getFile());
        Assert.assertTrue("File exists: " + file, file.exists());

        String pathToVideo = file.getAbsolutePath();
        LOG.info("reading {}", pathToVideo);

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(pathToVideo);
        grabber.start();

        int frames = 0;

        Frame frame;
        do {
            frame = grabber.grabFrame();
            frames++;
        } while (frame != null);


        LOG.info("read {} frames", frames);
        Assert.assertThat(frames, is(greaterThan(10)));

        grabber.stop();

    }

}
