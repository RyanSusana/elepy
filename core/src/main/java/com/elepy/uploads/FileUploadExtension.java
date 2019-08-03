package com.elepy.uploads;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.annotations.Inject;
import com.elepy.auth.Permissions;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.elepy.http.Response;
import org.apache.commons.lang3.RandomStringUtils;
import spark.utils.IOUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUploadExtension implements ElepyExtension {

    @Inject
    private FileService fileService;

    @Inject
    private Crud<FileReference> fileCrud;

    @Override
    public void setup(HttpService httpService, ElepyPostConfiguration elepy) {
        httpService.post("/uploads", this::handleUpload);
        httpService.get("/uploads/:fileName", this::handleFileGet);
    }

    private void handleFileGet(Request request, Response response) throws IOException {
        final FileUpload file = fileService.readFile(request.params("fileName")).orElseThrow(() -> new ElepyException("File not found", 404));

        response.type(file.getContentType());
        response.result(IOUtils.toByteArray(file.getContent()));
    }

    private void handleUpload(Request request, Response response) {
        request.requirePermissions(Permissions.AUTHENTICATED);

        final List<FileUpload> files = request.uploadedFiles("files");
        final List<FileReference> references = files.stream().map(uploadedFile -> {

            final String originalName = uploadedFile.getName();


            final var reference = FileUploadEvaluator.fromRequest(request).evaluate(uploadedFile);
            uploadedFile.setName(generateUniqueFileName(originalName));
            reference.setUploadName(uploadedFile.getName());

            this.fileCrud.searchInField("name", reference.getName()).stream().findFirst().ifPresent(file -> {
                throw new ElepyException("There is already a file called: " + file.getName(), 409);
            });

            fileService.uploadFile(uploadedFile);
            fileCrud.create(reference);

            return reference;
        }).collect(Collectors.toList());


        Map<String, Object> map = new HashMap<>();

        map.put("files", references);
        map.put("status", 200);
        map.put("message", "Uploaded files");
        response.status(200);
        response.json(map);
    }

    private String generateUniqueFileName(String originalName) {
        var random = RandomStringUtils.randomAlphanumeric(7);
        var datePrefix = new SimpleDateFormat("yyyy_MM").format(Calendar.getInstance().getTime());

        final String generatedName = String.format("%s_%s_%s", datePrefix, random, originalName);
        if (!fileCrud.searchInField("uploadName", generatedName).isEmpty()) {
            return generateUniqueFileName(originalName);
        }
        return generatedName;
    }
}
