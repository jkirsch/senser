package edu.tuberlin.senser.images.web.domain;

import com.google.common.io.BaseEncoding;

import javax.persistence.*;

/**
 * A face.
 */
@Entity
public class FaceImage {

    @Id
    @GeneratedValue
    private Long id;

    // We mark up the byte array with a long object datatype, setting the fetch type to lazy.
    /**
     * This is the JPEG serialized byte array.
     */
    @Lob
    @Basic(fetch= FetchType.EAGER) // this gets ignored anyway, but it is recommended for blobs
    protected  byte[]  imageFile;

    public FaceImage() {
    }

    public FaceImage(byte[] imageFile) {
        this.imageFile = imageFile;
    }

    public byte[] getImageFile() {
        return imageFile;
    }

    public void setImageFile(byte[] imageFile) {
        this.imageFile = imageFile;
    }

    public String getAsBase64() {
        return BaseEncoding.base64().encode(imageFile);
    }
}
