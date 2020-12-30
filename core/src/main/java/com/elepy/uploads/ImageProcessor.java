package com.elepy.uploads;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ImageProcessor {

    private Cache<ImageKey, BufferedImage> cachedImages = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();


    public byte[] processImage(Request request, FileUpload file) throws ExecutionException {

        final var size = intParam("size", request);
        final var width = intParam("width", request);
        final var height = intParam("height", request);
        final var scale = Optional.ofNullable(request.queryParams("scale")).map(Double::parseDouble);

        final var imageKey = new ImageKey(file.getName(),
                size.orElse(null),
                width.orElse(null),
                height.orElse(null),
                scale.orElse(null)
        );

        final var scaledImage = cachedImages.get(imageKey, () -> {
            final var original = ImageIO.read(file.getContent());
            final var thumbnailBuilder = Thumbnails.of(original);

            size.ifPresent(s -> thumbnailBuilder.size(s, s));

            width.ifPresent(thumbnailBuilder::width);

            height.ifPresent(thumbnailBuilder::height);

            scale.ifPresent(thumbnailBuilder::scale);

            return thumbnailBuilder.asBufferedImage();
        });

        return toBytes(scaledImage, file.getContentType());
    }

    private Optional<Integer> intParam(String param, Request request) {
        return Optional.ofNullable(request.queryParams(param)).map(Integer::parseInt);
    }

    private byte[] toBytes(BufferedImage originalImage, String contentType) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ImageIO.write(originalImage, contentType.split("/")[1], outputStream);
            outputStream.flush();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw ElepyException.internalServerError(e);
        }
    }

} 
