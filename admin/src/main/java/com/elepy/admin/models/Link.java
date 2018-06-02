package com.elepy.admin.models;

public class Link {

    private String href;
    private String text;

    private String fontAwesomeClass;

    public Link(String href, String text) {
        this(href, text, "fas fa-link");
    }

    public Link(String href, String text, String fontAwesomeClass) {
        this.href = href;
        this.text = text;
        this.fontAwesomeClass = fontAwesomeClass;
    }


    public String getHref() {
        return href;
    }

    public String getText() {
        return text;
    }

    public String getFontAwesomeClass() {
        return fontAwesomeClass;
    }
}
