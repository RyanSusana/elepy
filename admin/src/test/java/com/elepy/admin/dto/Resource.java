package com.elepy.admin.dto;

import com.elepy.annotations.DateTime;
import com.elepy.annotations.RestModel;

import java.util.Date;

@RestModel(slug = "/resources", name = "Res")
public class Resource {
    private String id;
    private int someInt;

    @DateTime(includeTime = true)
    private Date date;

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
}
