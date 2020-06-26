package com.elepy;

import com.elepy.annotations.Number;
import com.elepy.annotations.*;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Model(name = "Test Resource", path = "/resources")
@Delete(requiredPermissions = {})
@Create(requiredPermissions = {})
@Update(requiredPermissions = {})
@Find(requiredPermissions = {})
public class Resource {
    @Identifier
    private int id;

    @Featured
    private String featuredProperty;

    @DateTime(minimumDate = "0", maximumDate = "2019-22-12")
    private Date date;

    @Input
    private String textField;

    @TextArea
    private String textArea;

    @Markdown
    private String MARKDOWN;

    private TextType textType;

    @Unique
    @JsonProperty("unique")
    private String unique;

    @Required
    private String required;

    @Size(min = 20)
    private String minLen20;

    @Size(max = 40)
    private String maxLen40;

    @Size(min = 10, max = 50)
    @TextArea
    private String minLen10MaxLen50;

    @Number(minimum = 20)
    private BigDecimal numberMin20;

    @Number(maximum = 40)
    private BigDecimal numberMax40;

    @Number(minimum = 10, maximum = 50)
    private BigDecimal numberMin10Max50;

    @Searchable
    private String searchableField;

    private ResourceCustomObject resourceCustomObject;

    @FileReference(allowedMimeType = "image/png")
    private String fileReference;

    @Uneditable
    private String nonEditable;

    @Hidden
    private String hidden;


    @CustomAnno
    private String customAnnotation;

    public Resource() {
    }

    public String getFeaturedProperty() {
        return featuredProperty;
    }

    public void setFeaturedProperty(String featuredProperty) {
        this.featuredProperty = featuredProperty;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TextType getTextType() {
        return textType;
    }

    public void setTextType(TextType textType) {
        this.textType = textType;
    }

    public ResourceCustomObject getResourceCustomObject() {
        return resourceCustomObject;
    }

    public void setResourceCustomObject(ResourceCustomObject resourceCustomObject) {
        this.resourceCustomObject = resourceCustomObject;
    }

    public String getNonEditable() {
        return nonEditable;
    }

    public void setNonEditable(String nonEditable) {
        this.nonEditable = nonEditable;
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


    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("generated")
    @JsonIgnore
    @Generated
    public String generatedField() {
        return "I am generated";
    }
}
