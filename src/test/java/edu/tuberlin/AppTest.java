package edu.tuberlin;


import com.google.common.io.Resources;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_highgui.VideoCapture;
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

        String pathToVideo = new File(Resources.getResource("samples/SampleVideo_720x480_1mb.mp4").getFile()).getAbsolutePath();

        LOG.info("reading {}", pathToVideo);

        VideoCapture videoCapture = new VideoCapture(pathToVideo);

        //Assert.assertTrue("Cannot access video source: " + pathToVideo, videoCapture.isOpened());

        Mat image = new Mat();

        int frames = 0;

        while (videoCapture.read(image)) {
            frames++;
        }

        Assert.assertThat(frames, is(greaterThan(10)));

        videoCapture.release();

    }

}
