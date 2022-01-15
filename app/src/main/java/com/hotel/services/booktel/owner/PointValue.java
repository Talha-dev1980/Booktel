package com.hotel.services.booktel.owner;

class PointValue {
    int xValue, yValue;
    String dateRecieved;

    public PointValue() {
    }

    public PointValue(int xValue, int yValue, String dateRecieved) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.dateRecieved = dateRecieved;
    }

    public int getxValue() {
        return xValue;
    }

    public void setxValue(int xValue) {
        this.xValue = xValue;
    }

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }

    public String getDateRecieved() {
        return dateRecieved;
    }
    public String getrec(int value) {
        return dateRecieved;
    }

    public void setDateRecieved(String dateRecieved) {
        this.dateRecieved = dateRecieved;
    }
}