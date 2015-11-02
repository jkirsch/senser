package edu.tuberlin.senser.images.facedetection.video;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.junit.Test;

import java.io.File;

import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 */
public class FaceRecognizerInVideoTest {

    int height = 100;
    int width = 75;

    @Test
    public void testCreateTraining() throws Exception {

        String fileName = FaceRecognizerInVideo.storageFile;

        new File(fileName).delete();

        opencv_face.LBPHFaceRecognizer lbphFaceRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, FaceRecognizerInVideo.lbphThreshold);
        //createEigenFaceRecognizer(10, lbphThreshold);


        //createLBPHFaceRecognizer(1, 8, 8, 8, lbphThreshold);

        opencv_core.MatVector images = new opencv_core.MatVector(11);



        opencv_core.Mat labels = new opencv_core.Mat(0,0,0,0,0,0,0,0,0,0,0);

        //CanvasFrame canvasFrame = new CanvasFrame("dn", 1);

        for (int i = 1; i <= 11; i++) {

            String file = "src/test/resources/samples/faces/josh" + i + ".png";

            // load file
            opencv_core.Mat image = imread(file);

            //canvasFrame.showImage(OpenCvUtils.toBufferedImage(image));

            opencv_core.Mat face_resized = new opencv_core.Mat();
            resize(image, face_resized, new opencv_core.Size(width, height),
            1.0, 1.0, INTER_CUBIC);

            // Convert the current frame to grayscale:
            cvtColor(face_resized, face_resized, COLOR_BGR2GRAY);
            equalizeHist(face_resized, face_resized);

            images.put(i-1, face_resized);

        }

        lbphFaceRecognizer.train(images, labels);

        lbphFaceRecognizer.save(fileName);

    }
}