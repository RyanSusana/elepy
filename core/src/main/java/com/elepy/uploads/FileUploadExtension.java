package com.elepy.uploads;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.dao.Filters;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.elepy.http.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FileUploadExtension implements ElepyExtension {

    @Inject
    private FileService fileService;

    @Inject
    private Crud<FileReference> fileCrud;

    private ImageProcessor imageProcessor = new ImageProcessor();

    @Override
    public void setup(HttpService httpService, ElepyPostConfiguration elepy) {
        httpService.post("/elepy/uploads", this::handleUpload);
        httpService.get("/elepy/uploads/:fileName", this::handleFileGet);
        httpService.delete("/elepy/uploads/:fileName", this::handleFileDelete);


        // backwards compatibility
        httpService.get("/uploads/:fileName", this::handleFileGet);
    }

    private void handleFileDelete(HttpContext httpContext) {
        Request request = httpContext.request();
        Response response = httpContext.response();
        fileCrud.delete(request.params("fileName"));

        response.result(Message.of("File removed", 200));
    }

    private void handleFileGet(HttpContext httpContext) throws IOException, ExecutionException {
        Request request = httpContext.request();
        Response response = httpContext.response();
        final FileUpload file = fileService.readFile(request.params("fileName")).orElseThrow(() -> new ElepyException("File not found", 404));

        response.type(file.getContentType());

        if (file.getContentType().startsWith("image") && shouldScale(request)) {
            response.result(imageProcessor.processImage(request, file));
        } else {
            response.result(IOUtils.toByteArray(file.getContent()));
        }

    }

    private static boolean shouldScale(Request request) {
        return request.queryParams().stream()
                .anyMatch(queryParam -> Set.of("width", "height", "size", "scale").contains(queryParam.toLowerCase()));
    }

    private void handleUpload(HttpContext httpContext) {
        Request request = httpContext.request();
        Response response = httpContext.response();
        request.requirePermissions("files.upload");

        final List<FileUpload> files = request.uploadedFiles("files");
        final List<FileReference> references = files.stream().map(uploadedFile -> {

            final String originalName = uploadedFile.getName();


            final var reference = FileUploadEvaluator.fromRequest(request).evaluate(uploadedFile);
            uploadedFile.setName(generateUniqueFileName(originalName));
            reference.setUploadName(uploadedFile.getName());

            reference.setFullPath(request.uri() + "/" + reference.getUploadName());

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

        if (!fileCrud.findLimited(1, Filters.eq("uploadName", generatedName)).isEmpty()) {
            return generateUniqueFileName(originalName);
        }
        return generatedName;
    }
}
