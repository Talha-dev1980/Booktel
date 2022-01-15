package com.hotel.services.booktel;

public class hotel {

    String ownerid, htlpic, htllat, htllng, htlName, htlContact, tRooms, tEarning;

    public hotel() {
    }

    public hotel(String ownerid, String htlpic, String htllat, String htllng, String htlName, String htlContact, String tRooms, String tEarning) {
        this.ownerid = ownerid;
        this.htlpic = htlpic;
        this.htllat = htllat;
        this.htllng = htllng;
        this.htlName = htlName;
        this.htlContact = htlContact;
        this.tRooms = tRooms;
        this.tEarning = tEarning;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getHtlpic() {
        return htlpic;
    }

    public void setHtlpic(String htlpic) {
        this.htlpic = htlpic;
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

    public String getHtlName() {
        return htlName;
    }

    public void setHtlName(String htlName) {
        this.htlName = htlName;
    }

    public String getHtlContact() {
        return htlContact;
    }

    public void setHtlContact(String htlContact) {
        this.htlContact = htlContact;
    }

    public String gettRooms() {
        return tRooms;
    }

    public void settRooms(String tRooms) {
        this.tRooms = tRooms;
    }

    public String gettEarning() {
        return tEarning;
    }

    public void settEarning(String tEarning) {
        this.tEarning = tEarning;
    }
}