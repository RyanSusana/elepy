package com.elepy.plugins.gallery;


import com.elepy.Elepy;
import com.elepy.admin.concepts.ElepyAdminPanelPlugin;
import com.elepy.dao.QuerySetup;
import com.elepy.dao.SortOption;
import com.google.common.io.ByteStreams;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFSDBFile;
import spark.Service;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class ElepyGallery extends ElepyAdminPanelPlugin {
    public ElepyGallery() {
        super("Gallery", "/images");
    }

    @Override
    public void setup(Service http, Elepy elepy) {
        ImageDao imageDao = new ImageDao(elepy.getSingleton(DB.class));

        http.post(getAdminPanel().elepy().getBaseSlug() + "/images/upload", (request, response) -> {
            try {
                request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

                final Part part = request.raw().getPart("image");
                imageDao.upload(part);
                return "You have succesfully uploaded an image";
            } catch (Exception e) {
                response.status(401);
                return e.getMessage();
            }

        });

        http.get(getAdminPanel().elepy().getBaseSlug() + "/images/gallery", (request, response) -> {
            List<Image> images = new ArrayList<>();
            images.addAll(imageDao.search(new QuerySetup("", "", SortOption.ASCENDING, 1L, Integer.MAX_VALUE)).getValues());
            return elepy.getObjectMapper().writeValueAsString(images);
        });
        http.get(getAdminPanel().elepy().getBaseSlug() + "/images/:id", (request, response) -> {
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
        http.delete(getAdminPanel().elepy().getBaseSlug() + "/images/:id", (request, response) -> {
            final Optional<Image> image = imageDao.getById(request.params("id"));

            if (image.isPresent()) {

                imageDao.delete(request.params("id"));

                return "Successfully deleted image!";

            }
            response.status(404);
            return "Image not found";
        });
    }

    @Override
    public String renderContent(Map<String, Object> map) {
        PebbleEngine engine = new PebbleEngine.Builder().build();

        try {
            final PebbleTemplate images = engine.getTemplate("images.peb");
            Writer writer = new StringWriter();

            Map<String, Object> context = new HashMap<>();
            context.put("name", "Mitchell");

            images.evaluate(writer, context);

            return writer.toString();
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return "<h1 class = \"uk-text-center uk-margin-top\" >Hey, I support plugins now!!!!!!!!!!!!!</h1>";
    }
}
