package com.ryansusana.elepy.modules;

import com.google.common.io.ByteStreams;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.ryansusana.elepy.Elepy;
import com.ryansusana.elepy.ElepyModule;
import com.ryansusana.elepy.dao.ImageDao;
import com.ryansusana.elepy.models.Image;
import spark.Service;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImagePi extends ElepyModule {
    public ImagePi(Elepy inst, Service http) {
        super(inst, http);
    }

    @Override
    public void setup() {

    }

    @Override
    public void routes() {

        ImageDao imageDao = new ImageDao(elepy().getDb(), elepy().getMapper());
        http().post("/images/upload", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            final Part part = request.raw().getPart("image");


            final GridFSInputFile upload = imageDao.upload(part);
            return upload.getFilename();

        });

        http().get("/images/gallery", (request, response) -> {
            List<Image> images = new ArrayList<>();
            for (Image image : imageDao.getAll()) {
                if (image.isExternal()) {
                    images.add(image);
                } else {
                    images.add(image.withHost(request.scheme() + "://" + request.host()));
                }
            }
            return elepy().getObjectMapper().writeValueAsString(images);
        });
        http().get("/images/:id", (request, response) -> {
            final Optional<GridFSDBFile> image = imageDao.getGridFile(request.params("id"));
            if (image.isPresent()) {
                response.type(image.get().getContentType());
                HttpServletResponse raw = response.raw();

                raw.getOutputStream().write(ByteStreams.toByteArray(image.get().getInputStream()));
                raw.getOutputStream().flush();
                raw.getOutputStream().close();

                response.raw().getOutputStream();
                return response.raw();

            }
            response.status(404);
            return "";
        });


    }
}
