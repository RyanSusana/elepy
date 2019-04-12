package com.elepy.init;

import com.elepy.http.HttpService;
import com.elepy.http.UploadedFile;
import com.elepy.uploads.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.utils.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UploadIgniter {
    private final HttpService httpService;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    public UploadIgniter(HttpService httpService, ObjectMapper objectMapper, FileService fileService) {
        this.httpService = httpService;
        this.objectMapper = objectMapper;
        this.fileService = fileService;
    }

    public void ignite() {
        httpService.post("/uploads", (request, response) -> {
            final List<UploadedFile> files = request.uploadedFiles("files");
            files.forEach(fileService::uploadFile);


            final List<String> fileNames = files.stream().map(UploadedFile::getName).collect(Collectors.toList());


            Map<String, Object> map = new HashMap<>();

            map.put("files", fileNames);
            map.put("status", 200);
            map.put("message", "Uploaded files");
            response.status(200);
            response.result(objectMapper.writeValueAsString(map));
        });

        httpService.get("/uploads/:fileName", (request, response) -> {

            final UploadedFile file = fileService.readFile(request.params("fileName"));


            response.type(file.getContentType());
            HttpServletResponse raw = response.servletResponse();


            raw.getOutputStream().write(IOUtils.toByteArray(file.getContent()));
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
        });
    }
}
