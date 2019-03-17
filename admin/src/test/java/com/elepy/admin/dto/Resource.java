package com.elepy.admin.dto;

import com.elepy.annotations.DateTime;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Text;
import com.elepy.models.TextType;

import java.util.Date;

@RestModel(slug = "/resources", name = "Resources")
public class Resource {
    private String id;
    private int someInt;

    @DateTime(includeTime = true)
    private Date date;

    @Text(TextType.TEXTFIELD)
    private String textField;

    @Text(TextType.TEXTAREA)
    private String textArea;

    @Text(TextType.MARKDOWN)
    private String markdown;


    public Resource() {
    }

    public int getSomeInt() {
        return someInt;
    }

    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getTextArea() {
        return textArea;
    }

    public void setTextArea(String textArea) {
        this.textArea = textArea;
    }


}
