package com.elepy.tests;


import com.elepy.annotations.Model;
import com.elepy.annotations.View;

@Model(name = "Settings", path = "/settings")
@View(View.Defaults.SINGLE)
public class Settings {
    private String id;

    private String siteTitle;

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
