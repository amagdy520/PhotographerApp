package com.stylist.stylist;

/**
 * Created by Ahmed Magdy on 8/28/2017.
 */

public class prof {
    private String title;
    private String description;
    private String image;
    public prof(){

    }
    public prof(String title,String description,String image){
        this.title = title;
        this.description = description;
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
