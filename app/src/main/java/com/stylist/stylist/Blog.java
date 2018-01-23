package com.stylist.stylist;

/**
 * Created by Ahmed Magdy on 8/27/2017.
 */

public class Blog {
    private String title;
    private String description;
    private String image;
    private String post_time;
    public Blog(){

    }
    public Blog(String title,String description,String image,String post_time){
        this.title = title;
        this.description = description;
        this.image = image;
        this.post_time = post_time;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
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
