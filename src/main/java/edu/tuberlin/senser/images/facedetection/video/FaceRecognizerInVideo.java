package edu.tuberlin.senser.images.facedetection.video;

import com.google.common.io.Resources;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

/**
 * This is an example how to detect face in a video file with javacv
 * @author Vincent He (chinadragon0515@gmail.com)
 *
 */
public class FaceRecognizerInVideo {

    public static void main(String[] args) throws Exception, IOException {

        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

        String videoFileName = "http://zdf_hds_de-f.akamaihd.net/i/de14_v1@147090/index_1456_av-b.m3u8?sd=10&rebase=on";

        File file = Loader.extractResource(Resources.getResource("haarcascades/haarcascade_frontalface_alt.xml"), null, "classifier", ".xml");
        file.deleteOnExit();
        String classifierName = file.getAbsolutePath();

        CascadeClassifier face_cascade = new CascadeClassifier(classifierName);

        //FaceRecognizer lbphFaceRecognizer = createLBPHFaceRecognizer();
        //lbphFaceRecognizer.load(trainedResult);

        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(videoFileName);

            grabber.start();
            //grabber = OpenCVFrameGrabber.createDefault(f);
        } catch (Exception e) {
            System.err.println("Failed start the grabber.");
        }

        Frame videoFrame = null;
        Mat videoMat = new Mat();
        while (true) {
            videoFrame = grabber.grab();
            if(videoFrame.image == null) continue;
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

            System.out.println(faces.limit());

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
                String box_text = "Prediction = " + prediction;
                // Calculate the position for annotated text (make sure we don't
                // put illegal values in there):
                int pos_x = Math.max(face_i.tl().x() - 10, 0);
                int pos_y = Math.max(face_i.tl().y() - 10, 0);
                // And now put it into the image:
                putText(videoMat, box_text, new Point(pos_x, pos_y),
                        FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
            }
            // Show the result:
            imshow("face_recognizer", videoMat);

            char key = (char) waitKey(20);
            // Exit this loop on escape:
            if (key == 27) {
                destroyAllWindows();
                break;
            }
        }
    }

}