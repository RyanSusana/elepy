package com.elepy.describers;

import com.elepy.describers.props.PropertyConfig;
import com.elepy.models.FieldType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Property implements Comparable<Property> {

    private String name;
    private String prettyName;
    private boolean editable;
    private boolean required;
    private boolean unique;
    private boolean generated;
    private boolean hiddenFromCMS;
    private boolean searchable;
    private int importance;
    private FieldType type;
    private Map<String, Object> extraProperties = new HashMap<>();


    public boolean isHiddenFromCMS() {
        return hiddenFromCMS;
    }

    public void setHiddenFromCMS(boolean hiddenFromCMS) {
        this.hiddenFromCMS = hiddenFromCMS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    @JsonProperty
    public boolean isPrimitive() {
        return type != null && type.isPrimitive();
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public void config(PropertyConfig propertyConfig) {
        propertyConfig.config(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }


    public void setExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties = extraProperties;
    }

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        extraProperties.put(key, value);
    }

    public <T> T getExtra(String key) {
        return (T) extraProperties.get(key);
    }

    @Override
    public int compareTo(Property o) {
        return Integer.compare(o.importance, this.importance);
    }
}
