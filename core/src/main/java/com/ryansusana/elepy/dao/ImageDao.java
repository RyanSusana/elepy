package com.ryansusana.elepy.dao;

import com.github.slugify.Slugify;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.ryansusana.elepy.models.Image;
import org.jongo.Mapper;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

public class ImageDao extends MongoDao<Image> {

    private final DB db;


    public ImageDao(DB db, Mapper objectMapper) {
        super(db, "images", objectMapper, Image.class);
        this.db = db;
    }

    public GridFSInputFile upload(Part part) throws IOException {

        String newFileName = String.format("%s-%s", getRandomHexString(5), new Slugify().slugify(part.getSubmittedFileName().split("\\.")[0]));
        GridFSInputFile gfsFile = new GridFS(db, "images").createFile(part.getInputStream());
        gfsFile.setFilename(newFileName);
        gfsFile.setContentType(part.getContentType());
        gfsFile.save();

        create(new Image(newFileName,Calendar.getInstance().getTime()));
        return gfsFile;
    }


    public Optional<GridFSDBFile> getGridFile(String id) {
        return Optional.ofNullable(new GridFS(db, "images").findOne(id));
    }

    private String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

}
