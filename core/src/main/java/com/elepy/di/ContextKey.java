package com.elepy.di;

import java.util.Objects;

public class ContextKey<T> {
    private final Class<T> classType;
    private final String tag;

    public ContextKey(Class<T> classType, String tag) {
        this.classType = classType;
        this.tag = tag;
    }

    public Class<T> getClassType() {
        return classType;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextKey<?> that = (ContextKey<?>) o;
        return Objects.equals(classType, that.classType) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classType, tag);
    }
}
