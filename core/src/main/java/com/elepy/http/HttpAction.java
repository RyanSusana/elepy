package com.elepy.http;

import com.elepy.schemas.InputModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpAction {
    private String name;
    private String path;
    private String[] requiredPermissions;
    private HttpMethod method;

    private boolean singleRecord;
    private boolean multipleRecords;

    private String description;
    private String warning;
    private InputModel inputModel;


    @JsonCreator
    public HttpAction(@JsonProperty("name") String name,
                      @JsonProperty("path ") String path,
                      @JsonProperty("requiredPermissions") String[] requiredPermissions,
                      @JsonProperty("method") HttpMethod method,
                      @JsonProperty("singleRecord") boolean singleRecord,
                      @JsonProperty("multipleRecords") boolean multipleRecords,
                      @JsonProperty("description") String description,
                      @JsonProperty("warning") String warning,
                      @JsonProperty("inputModel") InputModel input) {

        this.name = name;
        this.path = path;
        this.requiredPermissions = requiredPermissions;
        this.method = method;
        this.singleRecord = singleRecord;
        this.multipleRecords = multipleRecords;
        this.description = description;
        this.warning = warning;
        this.inputModel = input;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRequiredPermissions(String[] requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setSingleRecord(boolean singleRecord) {
        this.singleRecord = singleRecord;
    }

    public void setMultipleRecords(boolean multipleRecords) {
        this.multipleRecords = multipleRecords;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public void setInputModel(InputModel inputModel) {
        this.inputModel = inputModel;
    }

    public String getPath() {
        return path;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    public HttpMethod getMethod() {
        return method;
    }


    public InputModel getInputModel() {
        return inputModel;
    }

    public String getName() {
        return name;
    }

    public boolean isSingleRecord() {
        return singleRecord;
    }

    public boolean isMultipleRecords() {
        return multipleRecords;
    }

    public String getDescription() {
        return description;
    }

    public String getWarning() {
        return warning;
    }
}
