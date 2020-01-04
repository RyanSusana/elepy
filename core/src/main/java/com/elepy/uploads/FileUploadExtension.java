package com.elepy.uploads;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.annotations.Inject;
import com.elepy.auth.Permissions;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.elepy.http.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
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
        httpService.delete("/uploads/:fileName", this::handleFileDelete);
    }

    private void handleFileDelete(Request request, Response response) {

        fileCrud.delete(request.params("fileName"));

        response.result(Message.of("File removed", 200));
    }

    private void handleFileGet(Request request, Response response) throws IOException {
        final FileUpload file = fileService.readFile(request.params("fileName")).orElseThrow(() -> new ElepyException("File not found", 404));

        response.type(file.getContentType());

        if (file.getContentType().startsWith("image") && shouldScale(request)) {

            handleFileGetImage(request, response, file);

        } else {
            response.result(IOUtils.toByteArray(file.getContent()));
        }

    }

    private void handleFileGetImage(Request request, Response response, FileUpload file) throws IOException {
        final var original = ImageIO.read(file.getContent());


        final var targetSize = dimensionFromRequest(request, original.getHeight(), original.getWidth());

        final Scalr.Mode mode = modeFromRequest(request);
        final var resized = Scalr.resize(original, Scalr.Method.AUTOMATIC, mode, targetSize.width, targetSize.height);

        response.result(toImage(resized, file.getContentType()));
    }

    private static byte[] toImage(BufferedImage originalImage, String contentType) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ImageIO.write(originalImage, contentType.split("/")[1], outputStream);
            outputStream.flush();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new ElepyException("Failed to resize image", 500, e);
        }
    }

    private static Scalr.Mode modeFromRequest(Request request) {
        final var fit = request.queryParamOrDefault("fit", "auto").toLowerCase();

        if (fit.equals("height")) {
            return Scalr.Mode.FIT_TO_HEIGHT;
        } else if (fit.equals("width")) {
            return Scalr.Mode.FIT_TO_WIDTH;
        } else {
            return Scalr.Mode.AUTOMATIC;
        }
    }

    private static boolean shouldScale(Request request) {
        return request.queryParams().stream()
                .anyMatch(queryParam -> Set.of("width", "height", "size").contains(queryParam.toLowerCase()));
    }

    private static Dimension dimensionFromRequest(Request request, int currentHeight, int currentWidth) {
        final var targetSize = request.queryParams("size");

        final var targetHeight = request.queryParamOrDefault("height", "" + currentHeight);

        final var targetWidth = request.queryParamOrDefault("width", "" + currentWidth);

        if (targetSize == null) {
            return new Dimension(Integer.parseInt(targetWidth), Integer.parseInt(targetHeight));
        } else {
            final var size = Integer.parseInt(targetSize);
            return new Dimension(size, size);
        }
    }


    private void handleUpload(Request request, Response response) {
        request.requirePermissions(Permissions.CAN_ADMINISTRATE_FILES);

        final List<FileUpload> files = request.uploadedFiles("files");
        final List<FileReference> references = files.stream().map(uploadedFile -> {

            final String originalName = uploadedFile.getName();


            final var reference = FileUploadEvaluator.fromRequest(request).evaluate(uploadedFile);
            uploadedFile.setName(generateUniqueFileName(originalName));
            reference.setUploadName(uploadedFile.getName());


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
