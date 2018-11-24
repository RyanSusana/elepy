package com.elepy.concepts;

import com.elepy.annotations.*;
import com.elepy.annotations.Number;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.math.BigDecimal;

@RestModel(name = "Test Resource", slug = "/resources")
public class Resource {
    @Identifier
    private String id;

    @Text(TextType.TEXTFIELD)
    private String textField;

    @Text(TextType.TEXTAREA)
    private String textArea;

    @Text(TextType.MARKDOWN)
    private String MARKDOWN;

    @Unique
    @JsonProperty("unique")
    private String unique;

    @Required
    private String required;

    @Text(minimumLength = 20)
    private String minLen20;

    @Text(maximumLength = 40)
    private String maxLen40;

    @Text(minimumLength = 10, maximumLength = 50)
    private String minLen10MaxLen50;

    @Number(minimum = 20)
    private BigDecimal numberMin20;

    @Number(maximum = 40)
    private BigDecimal numberMax40;

    @Number(minimum = 10, maximum = 50)
    private BigDecimal numberMin10Max50;

    @Searchable
    private String searchableField;

    private Object innerObject;

    @Uneditable
    private String nonEditable;

    public Object getInnerObject() {
        return innerObject;
    }

    public void setInnerObject(Object innerObject) {
        this.innerObject = innerObject;
    }

    public String getNonEditable() {
        return nonEditable;
    }

    public void setNonEditable(String nonEditable) {
        this.nonEditable = nonEditable;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextField() {
        return this.textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getTextArea() {
        return this.textArea;
    }

    public void setTextArea(String textArea) {
        this.textArea = textArea;
    }

    public String getMARKDOWN() {
        return this.MARKDOWN;
    }

    public void setMARKDOWN(String MARKDOWN) {
        this.MARKDOWN = MARKDOWN;
    }

    public String getUnique() {
        return this.unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getRequired() {
        return this.required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getMinLen20() {
        return this.minLen20;
    }

    public void setMinLen20(String minLen20) {
        this.minLen20 = minLen20;
    }

    public String getMaxLen40() {
        return this.maxLen40;
    }

    public void setMaxLen40(String maxLen40) {
        this.maxLen40 = maxLen40;
    }

    public String getMinLen10MaxLen50() {
        return this.minLen10MaxLen50;
    }

    public void setMinLen10MaxLen50(String minLen10MaxLen50) {
        this.minLen10MaxLen50 = minLen10MaxLen50;
    }

    public BigDecimal getNumberMin20() {
        return this.numberMin20;
    }

    public void setNumberMin20(BigDecimal numberMin20) {
        this.numberMin20 = numberMin20;
    }

    public BigDecimal getNumberMax40() {
        return this.numberMax40;
    }

    public void setNumberMax40(BigDecimal numberMax40) {
        this.numberMax40 = numberMax40;
    }

    public BigDecimal getNumberMin10Max50() {
        return this.numberMin10Max50;
    }

    public void setNumberMin10Max50(BigDecimal numberMin10Max50) {
        this.numberMin10Max50 = numberMin10Max50;
    }

    public String getSearchableField() {
        return searchableField;
    }

    public void setSearchableField(String searchableField) {
        this.searchableField = searchableField;
    }

    @JsonProperty("generated")
    @JsonIgnore
    @Generated
    public String generatedField() {
        return "I am generated";
    }
}
