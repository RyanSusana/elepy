package com.elepy.test;

import com.elepy.annotations.Model;

@Model(name = "Products", path = "/products")
public class Product {
    private String id;

    private String image;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
