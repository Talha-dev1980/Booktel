package com.hotel.services.booktel.owner;

public class singleRoom {
    String roomNo;
    String beds;
    String net;
    String hotel;
    String hotelID;
    String rent;
    String ownerID;
    String rating;
    String customer;
    String checkedIn;
    String earning;

    public singleRoom() {
    }

    public singleRoom(String roomNo, String beds, String net, String hotel, String hotelID, String rent, String ownerID, String rating, String customer, String checkedIn) {
        this.roomNo = roomNo;
        this.beds = beds;
        this.net = net;
        this.hotel = hotel;
        this.hotelID = hotelID;
        this.rent = rent;
        this.ownerID = ownerID;
        this.rating = rating;
        this.customer = customer;
        this.checkedIn = checkedIn;
    }

    public String getEarning() {
        return earning;
    }

    public void setEarning(String earning) {
        this.earning = earning;
    }

    public String getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(String checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getHotelID() {
        return hotelID;
    }

    public void setHotelID(String hotelID) {
        this.hotelID = hotelID;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
