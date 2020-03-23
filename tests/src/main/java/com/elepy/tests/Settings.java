package com.elepy.tests;

import com.elepy.annotations.Model;
import com.elepy.annotations.TextArea;
import com.elepy.annotations.View;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Model(name = "Settings", path = "/settings")
@Entity(name = "settings")
@Table(name = "settings")
@View(View.Defaults.SINGLE)
public class Settings {
    @Id
    private String id;
    private String title;

    @TextArea
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
