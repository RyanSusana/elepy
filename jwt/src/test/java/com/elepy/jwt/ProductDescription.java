package com.elepy.jwt;

import com.elepy.annotations.PrettyName;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductDescription {

    @NotNull
    @Size(min = 5)
    @javax.validation.constraints.Email
    @PrettyName("The damn text")
    private String text;

    @Min(5)
    private int number;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
