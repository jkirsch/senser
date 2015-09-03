package edu.tuberlin;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.bytedeco.javacpp.opencv_core.cvLoad;

/**
 */
public class FaceRecognizerTest {

    @Test
    public void testLoad() throws Exception {

        //FaceRecognizer faceRecognizer = new FaceRecognizer();

        //URL url = Resources.getResource("haarcascades/haarcascade_frontalface_default.xml");

        URL url = new URL("https://raw.githubusercontent.com/Itseez/opencv/2.4.10/data/haarcascades/haarcascade_frontalface_alt.xml");

        File file = Loader.extractResource(url, null, "classifier", ".xml");
        file.deleteOnExit();
        String classifierName = file.getAbsolutePath();

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        cvLoad(classifierName);


    }
}