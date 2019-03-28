package com.elepy.plugins.gallery;


import com.elepy.dao.Page;
import com.elepy.dao.SearchQuery;
import com.elepy.dao.jongo.DefaultMongoDao;
import com.elepy.utils.StringUtils;
import com.github.slugify.Slugify;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public class ImageDao extends DefaultMongoDao<Image> {

    private final DB db;


    ImageDao(DB db) {
        super(db, "images", Image.class);
        this.db = db;
    }

    void upload(Part part) throws IOException {


        String newFileName = String.format("%s-%s", StringUtils.getRandomHexString(5), new Slugify().slugify(part.getSubmittedFileName().split("\\.")[0]));
        GridFSInputFile originalGfs = new GridFS(db, "images").createFile(part.getInputStream());
        originalGfs.setFilename(newFileName + "-original");
        originalGfs.setContentType(part.getContentType());
        originalGfs.save();

        final BufferedImage original = ImageIO.read(part.getInputStream());
        final Dimension scale = scaleDown(original, new Dimension(250, 250));
        final BufferedImage caption = Scalr.resize(original, scale.width, scale.height);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(caption, "png", os);
        GridFSInputFile captionGfs = new GridFS(db, "images").createFile(new ByteArrayInputStream(os.toByteArray()));
        captionGfs.setContentType("image/png");
        captionGfs.setFilename(newFileName + "-caption");
        captionGfs.save();
        final Image image = new Image(newFileName);
        create(image);
    }

    private Dimension scaleDown(BufferedImage imgSize, Dimension boundary) {

        int originalWidth = imgSize.getWidth();
        int originalHeight = imgSize.getHeight();
        int boundWidth = boundary.width;
        int boundHeight = boundary.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > boundWidth) {
            newWidth = boundWidth;
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        if (newHeight > boundHeight) {
            newHeight = boundHeight;
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return new Dimension(newWidth, newHeight);
    }

    @Override
    public void delete(Object id) {
        super.delete(id);
        final GridFS images = new GridFS(db, "images");
        images.remove(id + "-original");
        images.remove(id + "-caption");
    }

    @Override
    public Page<Image> search(SearchQuery searchQuery) {

        return new Page<>(1, 1, Lists.newArrayList(collection().find().as(modelClassType()).iterator()));
    }

    @Override
    public void create(Image item) {
        collection().insert(item);
    }

    Optional<GridFSDBFile> getGridFile(String id) {
        return Optional.ofNullable(new GridFS(db, "images").findOne(id));
    }

}