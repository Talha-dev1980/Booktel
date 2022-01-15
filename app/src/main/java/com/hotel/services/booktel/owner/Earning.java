package com.hotel.services.booktel.owner;

class Earning {
    String customer, earning, roomNo;

    public Earning() {
    }

    public Earning(String customer, String earning, String roomNo) {
        this.customer = customer;
        this.earning = earning;
        this.roomNo = roomNo;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getEarning() {
        return earning;
    }

    public void setEarning(String earning) {
        this.earning = earning;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }
}