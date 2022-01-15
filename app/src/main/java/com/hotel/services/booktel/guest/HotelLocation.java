package com.hotel.services.booktel.guest;

public class HotelLocation {
    String htllat,htllng,ownerid;

    public HotelLocation() {

    }

    public HotelLocation(String htllat, String htllng, String ownerid) {
        this.htllat = htllat;
        this.htllng = htllng;
        this.ownerid = ownerid;
    }

    public String getHtllat() {
        return htllat;
    }

    public void setHtllat(String htllat) {
        this.htllat = htllat;
    }

    public String getHtllng() {
        return htllng;
    }

    public void setHtllng(String htllng) {
        this.htllng = htllng;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }
}
