package edu.tuberlin.senser.images.flink.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BufferedImageKryoSerializer extends Serializer<BufferedImage> {

    @Override
    public void write(Kryo kryo, Output output, BufferedImage object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(object, "png", byteArrayOutputStream);
            output.writeInt(byteArrayOutputStream.size());
            output.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BufferedImage read(Kryo kryo, Input input, Class<BufferedImage> type) {
        try {
            int length = input.readInt();
            byte[] inputData = new byte[length];
            input.read(inputData);
            return ImageIO.read(new ByteArrayInputStream(inputData));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}