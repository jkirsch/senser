package edu.tuberlin.senser.images.facedetection.video;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import edu.tuberlin.senser.images.web.service.PersonService;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_highgui.destroyAllWindows;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

/**
 * This is an example how to detect face in a video file with javacv
 *
 * @author Vincent He (chinadragon0515@gmail.com)
 */
@Component
public class FaceRecognizerInVideo implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FaceRecognizerInVideo.class);

    int height = 100;
    int width = 75;

    int counter = 1;

    static String storageFile = "data/faces.db";

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    PersonService personService;

    //  Stream size
    @Value("${senser.videosource}")
    String videoFileName;
    // 480x272
    //String videoFileName = "http://zdf_hds_de-f.akamaihd.net/i/de14_v1@147090/index_436_av-p.m3u8?sd=10&rebase=on";
    private OpenCVFrameConverter.ToMat converterToMat;
    private CascadeClassifier face_cascade;

    private transient volatile boolean running = true;
    private opencv_face.LBPHFaceRecognizer lbphFaceRecognizer;

    public static final double lbphThreshold = 80;

    public FaceRecognizerInVideo() throws IOException {

        init();
    }

    private void init() throws IOException {
        File file = Loader.extractResource(Resources.getResource("haarcascades/haarcascade_frontalface_alt.xml"), null, "classifier", ".xml");
        file.deleteOnExit();
        String classifierName = file.getAbsolutePath();

        face_cascade = new CascadeClassifier(classifierName);

        converterToMat = new OpenCVFrameConverter.ToMat();


        //lbphFaceRecognizer = createEigenFaceRecognizer(4, lbphThreshold);
        //lbphFaceRecognizer = createFisherFaceRecognizer();
        lbphFaceRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, lbphThreshold);
        lbphFaceRecognizer.load(storageFile);
    }

    @PostConstruct
    private void startSpring() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            LOG.info("Starting the Face recognizer ...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start();
    }

    private void start() {

        LOG.info("Reading from {}", videoFileName);

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFileName);

        try {
            grabber.start();
            CanvasFrame framer = new CanvasFrame("Face Time", CanvasFrame.getDefaultGamma() / grabber.getGamma());

            // process frames

            Mat videoMat = new Mat();

            while (running) {
                Frame videoFrame = grabber.grab();
                if (videoFrame == null || videoFrame.image == null) continue;
                videoMat = converterToMat.convert(videoFrame);
                Mat videoMatGray = new Mat();
                // Convert the current frame to grayscale:
                cvtColor(videoMat, videoMatGray, COLOR_BGR2GRAY);
                equalizeHist(videoMatGray, videoMatGray);

                RectVector faces = new RectVector();

                // Find the faces in the frame:
                face_cascade.detectMultiScale(videoMatGray, faces);

                // At this point you have the position of the faces in
                // faces. Now we'll get the faces, make a prediction and
                // annotate it in the video. Cool or what?

                //System.out.println(faces.limit());

                for (int i = 0; i < faces.size(); i++) {
                    Rect face_i = faces.get(i);

                    Mat face = new Mat(videoMatGray, face_i);
                    // If fisher face recognizer is used, the face need to be
                    // resized.
                    Mat face_resized = new Mat();
                    resize(face, face_resized, new Size(width, height),
                            1.0, 1.0, INTER_CUBIC);

                    // Now perform the prediction, see how easy that is:

                    int[] plabel = {-1};
                    double[] pconfidence = new double[1];

                    lbphFaceRecognizer.predict(face_resized.getUMat(ACCESS_READ), plabel, pconfidence);
                    LOG.info("Prediction confidence {}", pconfidence[0]);
                    // And finally write all we've found out to the original image!
                    // First of all draw a green rectangle around the detected face:

                    rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

                    String box_text;
                    Mat label;

                    int personID = plabel[0];
                    double confidence = pconfidence[0];

                    if (personID == 0) {
                        label = new Mat(new int[]{++counter});
                        LOG.info("New face ... stat: {}", counter);
                    } else {
                        label = new Mat(new int[]{personID});
                    }


                    box_text = personService.registerImage(personID, face_resized, counter, confidence);

                    MatVector images = new MatVector(new Mat[]{face_resized});
                    lbphFaceRecognizer.update(images, label);


                    // Calculate the position for annotated text (make sure we don't
                    // put illegal values in there):
                    int pos_x = Math.max(face_i.tl().x() - 10, 0);
                    int pos_y = Math.max(face_i.tl().y() - 10, 0);
                    // And now put it into the image:
                    putText(videoMat, box_text, new Point(pos_x, pos_y),
                            FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));

                }
                // Show the result:
                framer.showImage(videoFrame);
                // Show the result:
                //imshow("face_recognizer", videoMat);
                //videoMat.release();

                // Publish the face
                if (jmsTemplate != null && faces.limit() > 0) {

                    // Create a simple string .. repeat the word face n times

                    String face = Joiner.on(" ").join(Iterables.limit(Iterables.cycle("face"), (int) faces.limit()));
                    //System.out.println(face);
                    jmsTemplate.convertAndSend("input", face);
                }

            }
            LOG.error("Done processing");
            grabber.stop();

            destroyAllWindows();

        } catch (Exception e) {
            running = false;
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

        faceRecognizerInVideo.personService = new PersonService() {
            @Override
            public String registerImage(int personID, Mat face_resized, int counter, double confidence) {
                return "Unknown";
            }
        };

        FaceRecognizerInVideo.waitForEnter();
        faceRecognizerInVideo.stop();

    }


    public static void waitForEnter() {

        System.out.println("\n\nPress ENTER to continue..");

        Scanner scanner = new Scanner(System.in, Charsets.UTF_8.name());
        scanner.nextLine();

    }

}