package com.elepy.http;

import com.elepy.models.InputModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpAction {
    private final String name;
    private final String path;
    private final String[] requiredPermissions;
    private final HttpMethod method;

    private final boolean singleRecord;
    private final boolean multipleRecords;

    private final String description;
    private final String warning;
    private final InputModel inputModel;

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

    public static HttpAction of(String name,
                                String path,
                                String[] requiredPermissions,
                                HttpMethod method,
                                boolean singleRecord,
                                boolean multipleRecords,
                                String description,
                                String warning) {
        return of(name, path, requiredPermissions, method, singleRecord, multipleRecords, description, warning, null);
    }

    public static HttpAction of(String name,
                                String path,
                                String[] requiredPermissions,
                                HttpMethod method,
                                boolean singleRecord,
                                boolean multipleRecords,
                                String description,
                                String warning,
                                InputModel inputModel) {

        return new HttpAction(name, path, requiredPermissions, method, singleRecord, multipleRecords, description, warning, inputModel);
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
