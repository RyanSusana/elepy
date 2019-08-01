package com.elepy;

import com.elepy.annotations.Array;
import com.elepy.annotations.Number;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Text;
import com.elepy.models.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Set;

@RestModel(name = "Resources", slug = "/resources")
public class ResourceArray {

    private int id;

    private List<String> arrayString;
    private Set<Integer> arrayNumber;
    private List<Date> arrayDate;
    private List<Resource> arrayObject;
    private List<FieldType> arrayEnum;
    private List<Boolean> arrayBoolean;


    @Array(maximumArrayLength = 2, minimumArrayLength = 1)
    @Text(minimumLength = 10)
    private List<String> arrayStringMax2Min1TextWithMinimumLengthOf10;

    @Number(minimum = 10)
    private List<Integer> arrayNumberMax2Min1NumberWithMinimumOf10;


    private List<Resource> arrayObjects;

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
