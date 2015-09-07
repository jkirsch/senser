package edu.tuberlin.senser.images.facedetection.video;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.destroyAllWindows;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

/**
 * This is an example how to detect face in a video file with javacv
 *
 * @author Vincent He (chinadragon0515@gmail.com)
 */
@ConditionalOnProperty(havingValue = "false", prefix = "twitter", name = "enabled")
@Component
public class FaceRecognizerInVideo implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FaceRecognizerInVideo.class);


    @Autowired
    JmsTemplate jmsTemplate;

    //  Stream size
    //String videoFileName = "http://zdf_hds_de-f.akamaihd.net/i/de14_v1@147090/index_1456_av-b.m3u8?sd=10&rebase=on";
    // 480x272
    String videoFileName = "http://zdf_hds_de-f.akamaihd.net/i/de14_v1@147090/index_436_av-p.m3u8?sd=10&rebase=on";
    private OpenCVFrameConverter.ToMat converterToMat;
    private CascadeClassifier face_cascade;

    private transient volatile boolean running = true;


    public FaceRecognizerInVideo() throws IOException {

        init();
    }

    private void init() throws IOException {
        File file = Loader.extractResource(Resources.getResource("haarcascades/haarcascade_frontalface_alt.xml"), null, "classifier", ".xml");
        file.deleteOnExit();
        String classifierName = file.getAbsolutePath();

        face_cascade = new CascadeClassifier(classifierName);

        converterToMat = new OpenCVFrameConverter.ToMat();

        //FaceRecognizer lbphFaceRecognizer = createLBPHFaceRecognizer();
        //lbphFaceRecognizer.load(trainedResult);
    }

    @PostConstruct
    private void startSpring() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            LOG.info("Stating the Face recognizer ...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start();
    }

    private void start() {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFileName);

        try {
            grabber.start();
            CanvasFrame framer = new CanvasFrame("Face Time", CanvasFrame.getDefaultGamma() / grabber.getGamma());

            // process frames

            Frame videoFrame = null;
            Mat videoMat = new Mat();

            while (running) {
                videoFrame = grabber.grab();
                if (videoFrame.image == null) continue;
                videoMat = converterToMat.convert(videoFrame);
                Mat videoMatGray = new Mat();
                // Convert the current frame to grayscale:
                cvtColor(videoMat, videoMatGray, COLOR_BGRA2GRAY);
                equalizeHist(videoMatGray, videoMatGray);

                Point p = new Point();
                Rect faces = new Rect();
                // Find the faces in the frame:
                face_cascade.detectMultiScale(videoMatGray, faces);

                // At this point you have the position of the faces in
                // faces. Now we'll get the faces, make a prediction and
                // annotate it in the video. Cool or what?

                //System.out.println(faces.limit());

                for (int i = 0; i < faces.limit(); i++) {
                    Rect face_i = faces.position(i);

                    Mat face = new Mat(videoMatGray, faces.position(i));
                    // If fisher face recognizer is used, the face need to be
                    // resized.
                    // resize(face, face_resized, new Size(im_width, im_height),
                    // 1.0, 1.0, INTER_CUBIC);

                    // Now perform the prediction, see how easy that is:
                    //int prediction = lbphFaceRecognizer.predict(face);

                    int prediction = 1;

                    // And finally write all we've found out to the original image!
                    // First of all draw a green rectangle around the detected face:

                    rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

                    // Create the text we will annotate the box with:
                    String box_text = "Someone";
                    // Calculate the position for annotated text (make sure we don't
                    // put illegal values in there):
                    int pos_x = Math.max(face_i.tl().x() - 10, 0);
                    int pos_y = Math.max(face_i.tl().y() - 10, 0);
                    // And now put it into the image:
                    putText(videoMat, box_text, new Point(pos_x, pos_y),
                            FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 0, 0, 2.0));
                }
                // Show the result:
                framer.showImage(videoFrame);
                // Show the result:
                //imshow("face_recognizer", videoMat);
                //videoMat.release();

                // Publish the face
                if(jmsTemplate != null && faces.limit() > 0) {

                    // Create a simple string .. repeat the word face n times

                    String face = Joiner.on(" ").join(Iterables.limit(Iterables.cycle("face"), faces.limit()));
                    //System.out.println(face);
                    jmsTemplate.convertAndSend("input", face);
                }

            }
            LOG.error("Done processing");
            grabber.stop();

            destroyAllWindows();

        } catch (Exception e) {
            LOG.error("Error processing video", e);
        }


    }

    @PreDestroy
    public void stop() throws java.lang.Exception {
        LOG.info("Stopping image processor");
        running = false;

    }

    public static void main(String[] args) throws java.lang.Exception {


        FaceRecognizerInVideo faceRecognizerInVideo = new FaceRecognizerInVideo();

        new Thread(faceRecognizerInVideo).start();

        FaceRecognizerInVideo.waitForEnter();
        faceRecognizerInVideo.stop();

    }




    public static void waitForEnter() {

        System.out.println("\n\nPress ENTER to continue..");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

    }

}