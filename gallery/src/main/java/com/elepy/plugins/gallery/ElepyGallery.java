package com.elepy.plugins.gallery;


import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.concepts.ElepyAdminPanelPlugin;
import com.elepy.dao.QuerySetup;
import com.elepy.dao.SortOption;
import com.elepy.http.HttpService;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class ElepyGallery extends ElepyAdminPanelPlugin {
    private static final Logger logger = LoggerFactory.getLogger(ElepyGallery.class);

    public ElepyGallery() {
        super("Gallery", "/images");
    }

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        ImageDao imageDao = new ImageDao(elepy.getDependency(DB.class));

        http.post(elepy.getBaseSlug() + "/images/upload", (request, response) -> {
            try {
                request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

                final Part part = request.raw().getPart("image");
                imageDao.upload(part);
                response.result("You have succesfully uploaded an image");
            } catch (Exception e) {
                response.status(401);
                response.result(e.getMessage());
            }
        });

        http.get(elepy.getBaseSlug() + "/images/gallery", (request, response) -> {
            List<Image> images = new ArrayList<>(imageDao.search(new QuerySetup("", "", SortOption.ASCENDING, 1L, Integer.MAX_VALUE)).getValues());
            response.result(elepy.getObjectMapper().writeValueAsString(images));
        });
        http.get(elepy.getBaseSlug() + "/images/:id", (request, response) -> {
            final Optional<GridFSDBFile> image = imageDao.getGridFile(request.params("id"));
            if (image.isPresent()) {
                response.type(image.get().getContentType());
                HttpServletResponse raw = response.raw();


                raw.getOutputStream().write(IOUtils.toByteArray(image.get().getInputStream()));
                raw.getOutputStream().flush();
                raw.getOutputStream().close();
            }
            response.status(404);
            response.result("");
        });
        http.delete(elepy.getBaseSlug() + "/images/:id", (request, response) -> {
            final Optional<Image> image = imageDao.getById(request.params("id"));

            if (image.isPresent()) {
                imageDao.delete(request.params("id"));

                response.result("Successfully deleted image!");

            }
            response.status(404);
            response.result("Image not found");
        });
    }

    @Override
    public String renderContent(Map<String, Object> map) {
        PebbleEngine engine = new PebbleEngine.Builder().build();

        try {
            final PebbleTemplate images = engine.getTemplate("images.peb");
            Writer writer = new StringWriter();

            Map<String, Object> context = new HashMap<>();

            images.evaluate(writer, context);

            return writer.toString();
        } catch (PebbleException | IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "<h1 class = \"uk-text-center uk-margin-top\" >Hey, I support plugins now!!!!!!!!!!!!!</h1>";
    }
}
