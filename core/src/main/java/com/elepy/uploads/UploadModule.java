package com.elepy.uploads;

import com.elepy.ElepyModule;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.elepy.http.Response;
import spark.utils.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UploadModule implements ElepyModule {
    @Inject
    private FileService fileService;

    @Inject
    private Crud<UploadedFile> fileCrud;


    @Override
    public void beforeElepyConstruction(HttpService httpService, ElepyPreConfiguration elepy) {
        elepy.addModel(UploadedFile.class);
    }

    @Override
    public void afterElepyConstruction(HttpService httpService, ElepyPostConfiguration elepy) {
        httpService.post("/uploads", this::handleUpload);
        httpService.get("/uploads/:fileName", this::handleFileGet);
    }

    private void handleFileGet(Request request, Response response) throws IOException {
        final UploadedFile file = fileService.readFile(request.params("fileName")).orElseThrow(() -> new ElepyException("File not found", 404));

        response.type(file.getContentType());
        HttpServletResponse raw = response.servletResponse();

        raw.getOutputStream().write(IOUtils.toByteArray(file.getContent()));
        raw.getOutputStream().flush();
        raw.getOutputStream().close();
    }

    private void handleUpload(Request request, Response response) {
        final List<UploadedFile> files = request.uploadedFiles("files");
        files.forEach(uploadedFile -> {
            new FileIdentityProvider().provideId(uploadedFile, fileCrud);
            fileCrud.create(uploadedFile);
            fileService.uploadFile(uploadedFile);
        });

        final List<String> fileNames = files.stream().map(f -> "/uploads/" + f.getName()).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();

        map.put("files", fileNames);
        map.put("status", 200);
        map.put("message", "Uploaded files");
        response.status(200);
        response.json(map);
    }
}
