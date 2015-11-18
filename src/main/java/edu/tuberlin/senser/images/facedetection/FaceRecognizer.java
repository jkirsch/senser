package edu.tuberlin.senser.images.facedetection;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 */
public class FaceRecognizer {

    private final CvMemStorage storage;
    private final OpenCVFrameConverter.ToIplImage converter;
    private final opencv_objdetect.CvHaarClassifierCascade classifier;
    private final opencv_face.LBPHFaceRecognizer lbphFaceRecognizer;
    //private final LBPHFaceRecognizer lbphFaceRecognizer;

    public FaceRecognizer() throws IOException {

        // Load the classifier file from Java resources.

        URL url = new URL("https://raw.githubusercontent.com/Itseez/opencv/2.4.10/data/haarcascades/haarcascade_frontalface_alt.xml");

        File file = Loader.extractResource(url, null, "classifier", ".xml");
        file.deleteOnExit();
        String classifierName = file.getAbsolutePath();

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        classifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        storage = CvMemStorage.create();

        converter = new OpenCVFrameConverter.ToIplImage();

        lbphFaceRecognizer = createLBPHFaceRecognizer();

    }

    OpenCVFrameConverter.ToIplImage openCVConverter = new OpenCVFrameConverter.ToIplImage();

    public Frame recognize(Frame frame) {

        opencv_core.IplImage grabbedImage = converter.convert(frame);

        CvPoint hatPoints = new CvPoint(3);


        IplImage grayImage = IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);

        // Let's try to detect some faces! but we need a grayscale image...
        cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
        opencv_core.CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
                1.1, 3, 0);

        int total = faces.total();

        for (int i = 0; i < total; i++) {
            BytePointer p = cvGetSeqElem(faces, i);
            CvRect r = new CvRect(p);


            int predicted = 0;
            double confidence = 0;
            //int predict = lbphFaceRecognizer.predict(r, predicted, confidence);

            int x = r.x(), y = r.y(), w = r.width(), h = r.height();
            cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x + w, y + h), CvScalar.RED, 1, CV_AA, 0);

            // To access or pass as argument the elements of a native array, call position() before.
            hatPoints.position(0).x(x - w / 10).y(y - h / 10);
            hatPoints.position(1).x(x + w * 11 / 10).y(y - h / 10);
            hatPoints.position(2).x(x + w / 2).y(y - h / 2);
            cvFillConvexPoly(grabbedImage, hatPoints.position(0), 3, CvScalar.GREEN, CV_AA, 0);


        }


        cvClearMemStorage(storage);

        return openCVConverter.convert(grabbedImage);
    }


}
