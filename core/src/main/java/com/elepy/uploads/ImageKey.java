package com.elepy.uploads;

import java.util.Objects;

public class ImageKey {
    private final String name;

    private final Integer size;
    private final Integer width;
    private final Integer height;

    private final Double scale;

    public ImageKey(String name, Integer size, Integer width, Integer height, Double scale) {
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageKey imageKey = (ImageKey) o;
        return name.equals(imageKey.name) &&
                Objects.equals(size, imageKey.size) &&
                Objects.equals(width, imageKey.width) &&
                Objects.equals(height, imageKey.height) &&
                Objects.equals(scale, imageKey.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, width, height, scale);
    }
}
