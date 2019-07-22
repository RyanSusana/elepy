package com.elepy.describers;

import com.elepy.annotations.RestModel;
import com.elepy.models.FieldType;
import com.elepy.models.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;

@RestModel(name = "Resources", slug = "/resources")
public class ResourceWithArray {

    private int id;

    private List<String> arrayString;
    private Set<Integer> arrayNumber;
    private List<Date> arrayDate;
    private List<Resource> arrayObject;
    private List<FieldType> arrayEnum;
    private List<Boolean> arrayBoolean;


    public Set<Integer> getArrayNumber() {
        return arrayNumber;
    }

    public void setArrayNumber(Set<Integer> arrayNumber) {
        this.arrayNumber = arrayNumber;
    }

    public List<Resource> getArrayObject() {
        return arrayObject;
    }

    public void setArrayObject(List<Resource> arrayObject) {
        this.arrayObject = arrayObject;
    }

    public List<FieldType> getArrayEnum() {
        return arrayEnum;
    }

    public void setArrayEnum(List<FieldType> arrayEnum) {
        this.arrayEnum = arrayEnum;
    }

    public List<Boolean> getArrayBoolean() {
        return arrayBoolean;
    }

    public void setArrayBoolean(List<Boolean> arrayBoolean) {
        this.arrayBoolean = arrayBoolean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getArrayString() {
        return arrayString;
    }

    public void setArrayString(List<String> arrayString) {
        this.arrayString = arrayString;
    }


    public List<Date> getArrayDate() {
        return arrayDate;
    }

    public void setArrayDate(List<Date> arrayDate) {
        this.arrayDate = arrayDate;
    }
}
