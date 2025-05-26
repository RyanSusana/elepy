package com.elepy.uploads;

import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.crud.Crud;
import com.elepy.query.Filters;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ApplicationScoped
public class FileUploadExtension implements ElepyExtension {

    @Inject
    private FileService fileService;

    @Inject
    private Crud<FileReference> fileCrud;

    private final ImageProcessor imageProcessor = new ImageProcessor();

    @Override
    public void setup(HttpService httpService, ElepyPostConfiguration elepy) {
        httpService.addRoute(RouteBuilder.anElepyRoute()
                        .method(HttpMethod.POST)
                        .path("/elepy/uploads")
                        .permissions("files.upload")
                        .route(this::handleUpload)
                .build());
        httpService.addRoute(RouteBuilder.anElepyRoute()
                .method(HttpMethod.DELETE)
                .path("/elepy/uploads/:fileName")
                .permissions("files.delete")
                .route(this::handleFileDelete)
                .build());
        httpService.get("/elepy/uploads/:fileName", this::handleFileGet);
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
        final RawFile file = fileService.readFile(request.params("fileName")).orElseThrow(() -> ElepyException.notFound("File"));

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

        final List<RawFile> files = request.uploadedFiles("files");
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
