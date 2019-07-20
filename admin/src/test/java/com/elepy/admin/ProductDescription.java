package com.elepy.admin;

import com.elepy.annotations.PrettyName;
import com.elepy.annotations.Text;
import com.elepy.models.TextType;

import java.util.UUID;

public class ProductDescription {


    @PrettyName("Product Name")
    private String name;

    @Text(TextType.TEXTAREA)
    @PrettyName("Short Description")
    private String shortDescription;

    @Text(TextType.MARKDOWN)
    @PrettyName("Markdown Description")
    private String longDescription;

    public ProductDescription() {
        name = UUID.randomUUID().toString();

        shortDescription = UUID.randomUUID().toString();

        longDescription = "<p>" + UUID.randomUUID().toString() + "</p>";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
