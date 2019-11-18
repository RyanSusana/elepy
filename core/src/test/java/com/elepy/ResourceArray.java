package com.elepy;

import com.elepy.annotations.Number;
import com.elepy.annotations.*;
import com.elepy.models.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Set;

@RestModel(name = "Resources", slug = "/resources")
public class ResourceArray {

    private int id;

    private List<String> arrayString;

    private List<@FileReference String> arrayFileReference;
    private Set<Integer> arrayNumber;
    private List<Date> arrayDate;
    private List<Resource> arrayObject;
    private List<FieldType> arrayEnum;
    private List<Boolean> arrayBoolean;
    private List<Resource> arrayObjects;


    @Array(maximumArrayLength = 2, minimumArrayLength = 1)
    private List<@Text(minimumLength = 10) String> arrayStringMax2Min1TextWithMinimumLengthOf10;

    private List<@Number(minimum = 10) Integer> arrayNumberMax2Min1NumberWithMinimumOf10;


    public List<String> getArrayFileReference() {
        return arrayFileReference;
    }

    public void setArrayFileReference(List<String> arrayFileReference) {
        this.arrayFileReference = arrayFileReference;
    }

    public List<Resource> getArrayObjects() {
        return arrayObjects;
    }

    public void setArrayObjects(List<Resource> arrayObjects) {
        this.arrayObjects = arrayObjects;
    }

    public List<Integer> getArrayNumberMax2Min1NumberWithMinimumOf10() {
        return arrayNumberMax2Min1NumberWithMinimumOf10;
    }

    public void setArrayNumberMax2Min1NumberWithMinimumOf10(List<Integer> arrayNumberMax2Min1NumberWithMinimumOf10) {
        this.arrayNumberMax2Min1NumberWithMinimumOf10 = arrayNumberMax2Min1NumberWithMinimumOf10;
    }

    public List<String> getArrayStringMax2Min1TextWithMinimumLengthOf10() {
        return arrayStringMax2Min1TextWithMinimumLengthOf10;
    }

    public void setArrayStringMax2Min1TextWithMinimumLengthOf10(List<String> arrayStringMax2Min1TextWithMinimumLengthOf10) {
        this.arrayStringMax2Min1TextWithMinimumLengthOf10 = arrayStringMax2Min1TextWithMinimumLengthOf10;
    }

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
