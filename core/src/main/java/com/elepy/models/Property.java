package com.elepy.models;

import com.elepy.annotations.Localized;
import com.elepy.dao.FilterTypeDescription;
import com.elepy.models.options.Options;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Set;


public class Property implements Comparable<Property> {

    private String name;

    @Localized
    private String description;
    private String javaName;

    @Localized
    private String label;
    private String showIf = "true";
    private boolean editable;
    private boolean required;
    private boolean unique;
    private boolean generated;
    private boolean hiddenFromCMS;
    private boolean searchable;
    private int importance;

    @JsonUnwrapped
    private Options options;


    private Set<FilterTypeDescription> availableFilters;
    private FieldType type;

    public String getShowIf() {
        return showIf;
    }

    public void setShowIf(String showIf) {
        this.showIf = showIf;
    }

    public Set<FilterTypeDescription> getAvailableFilters() {
        return availableFilters;
    }

    public void setAvailableFilters(Set<FilterTypeDescription> availableFilters) {
        this.availableFilters = availableFilters;
    }

    @SuppressWarnings("unchecked")
    public <T extends Options> T getOptions() {
        return (T) options;
    }


    public void setOptions(Options options) {
        this.options = options;
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        return type != null && getType().isPrimitive();
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }


    @Override
    public int compareTo(Property o) {
        return Integer.compare(o.importance, this.importance);
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
