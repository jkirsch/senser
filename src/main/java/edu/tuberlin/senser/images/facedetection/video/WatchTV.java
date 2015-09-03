package edu.tuberlin.senser.images.facedetection.video;

//import org.bytedeco.javacpp.opencv_videoio;

import edu.tuberlin.senser.images.facedetection.FaceRecognizer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * Check
 * <p>
 * https://crankylinuxuser.wordpress.com/2015/03/01/uwho-face-recognition-and-tracking-program/
 */
public class WatchTV extends JFrame implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(WatchTV.class);


    /**
     * Component for displaying the image
     */
    private final JLabel imageView = new JLabel();

    public WatchTV() throws HeadlessException {
        super("Watch TV");

        // Image display in the center
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
        imageScrollPane.setPreferredSize(new Dimension(640, 480));
        add(imageScrollPane, BorderLayout.CENTER);

        new Thread(this).start();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final WatchTV frame = new WatchTV();
                frame.pack();
                // Mark for display in the center of the screen
                frame.setLocationRelativeTo(null);
                // Exit application when frame is closed.
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(false);
            }
        });
    }


    @Override
    public void run() {
        // SEE
        String pathToVideo = "http://zdf_hds_de-f.akamaihd.net/i/de14_v1@147090/index_1456_av-b.m3u8?sd=10&rebase=on";

        LOG.info("reading {}", pathToVideo);

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(pathToVideo);

        CanvasFrame framer = new CanvasFrame("Face Time", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        FaceRecognizer faceRecognizer;
        try {
            faceRecognizer = new FaceRecognizer();
        } catch (IOException e) {
            LOG.error("Error loading Face recognizer", e);
            return;
        }

        //grabber.setFormat("mp4");
        try {
            grabber.start();

            org.bytedeco.javacv.Frame frame = grabber.grab();

            while (frame != null) {

                if (frame.image != null) {
                    Frame recognize = faceRecognizer.recognize(frame);

                    framer.showImage(recognize);
                }
                frame = grabber.grab();

            }

            grabber.release();

        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }


    }

    static final Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

    public String convertTime(long time) {
        Date date = new Date(time);
        return format.format(date);
    }
}
