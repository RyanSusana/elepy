package com.elepy;

import com.elepy.annotations.Model;
import com.elepy.annotations.Reference;

import java.util.List;

@Model(name = "Products", path = "/products")
public class Product {
    private String id, title;

    @Reference(to = Category.class)
    private String categoryId;

    private List<@Reference(to = Category.class) String> categories;


    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
