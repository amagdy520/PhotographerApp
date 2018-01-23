package com.stylist.stylist;

/**
 * Created by Ahmed Magdy on 8/28/2017.
 */

public class offer {
    private String name;
    private String details;
    private String price;
    private String note;
    private String date;
    private String image;

    public offer() {
    }

    public offer(String name, String details, String price, String note, String image , String date) {
        this.name = name;
        this.details = details;
        this.price = price;
        this.note = note;
        this.image = image;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
