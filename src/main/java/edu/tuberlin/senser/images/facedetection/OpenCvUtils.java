package edu.tuberlin.senser.images.facedetection;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 */
public class OpenCvUtils {

    private static final Java2DFrameConverter java2DConverter = new Java2DFrameConverter();
    private static final OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();

    public static BufferedImage toBufferedImage(IplImage ipl) {
        OpenCVFrameConverter.ToIplImage openCVConverter = new OpenCVFrameConverter.ToIplImage();
        return java2DConverter.convert(openCVConverter.convert(ipl));
    }

    public static Mat toMat(BufferedImage bufferedImage) {
        return openCVConverter.convert(java2DConverter.convert(bufferedImage));
    }

    public static BufferedImage toFrame(Mat mat) {

        return java2DConverter.convert(openCVConverter.convert(mat));
    }

    public static BufferedImage toFrame(Frame frame) {
        return java2DConverter.convert(frame);
    }

    public static Frame copyFrame(Frame frame) {
        // Frame that will hold the copy
        Frame cFrame = new Frame(frame.imageWidth, frame.imageHeight, frame.imageDepth, frame.imageChannels);

        // Copy the byte buffer from frame
        ByteBuffer originalByteBuffer = (ByteBuffer) frame.image[0];

        // Create the clone buffer with same capacity as the original
        ByteBuffer cloneBuffer = ByteBuffer.allocate(originalByteBuffer.capacity());
        //ByteBuffer cloneBuffer = deepCopy(originalByteBuffer);

        // Save parameters from the original byte buffer
        int position = originalByteBuffer.position();
        int limit = originalByteBuffer.limit();


        // Set range to the entire buffer
        originalByteBuffer.position(0).limit(originalByteBuffer.capacity());

        // Read from original and put into clone
        cloneBuffer.put(originalByteBuffer);

        // Set the order same as original
        cloneBuffer.order(originalByteBuffer.order());

        // Set clone position to 0 and set the range as the original
        cloneBuffer.position(0);
        cloneBuffer.position(position).limit(limit);

        // Save the clone
        cFrame.image[0] = cloneBuffer;

        return cFrame;
    }
}

