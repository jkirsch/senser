package edu.tuberlin.senser.images.facedetection;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.image.BufferedImage;

/**
 */
public class OpenCvUtils {

    private static final Java2DFrameConverter java2DConverter = new Java2DFrameConverter();

    public static BufferedImage toBufferedImage(IplImage ipl) {
        OpenCVFrameConverter.ToIplImage openCVConverter = new OpenCVFrameConverter.ToIplImage();
        return java2DConverter.convert(openCVConverter.convert(ipl));
    }

    public static BufferedImage toBufferedImage(Mat mat) {
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        return java2DConverter.convert(openCVConverter.convert(mat));
    }

    public static BufferedImage toBufferedImage(Frame frame) {
        return java2DConverter.convert(frame);
    }

}

