package com.hotel.services.booktel.owner;

public class PicInfo {
     String image,thumb_image;
    public PicInfo() {
    }

    public PicInfo(String image, String thumb_image) {
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getthumb_image() {
        return thumb_image;
    }

    public void setthumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
