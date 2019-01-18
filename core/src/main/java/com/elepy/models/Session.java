package com.elepy.models;

import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * Wrapper around the Spark Session object.
 *
 * @see <a href="http://javadoc.io/doc/com.sparkjava/spark-core/2.8.0">Spark Documentation</a>
 */
public class Session {
    private final spark.Session sparkSession;

    public Session(spark.Session sparkSession) {
        this.sparkSession = sparkSession;
    }

    public HttpSession raw() {
        return sparkSession.raw();
    }

    public <T> T attribute(String name) {
        return sparkSession.attribute(name);
    }

    public void attribute(String name, Object value) {
        sparkSession.attribute(name, value);
    }

    public Set<String> attributes() {
        return sparkSession.attributes();
    }

    public long creationTime() {
        return sparkSession.creationTime();
    }

    public String id() {
        return sparkSession.id();
    }

    public long lastAccessedTime() {
        return sparkSession.lastAccessedTime();
    }

    public int maxInactiveInterval() {
        return sparkSession.maxInactiveInterval();
    }

    public void maxInactiveInterval(int interval) {
        sparkSession.maxInactiveInterval(interval);
    }

    public void invalidate() {
        sparkSession.invalidate();
    }

    public boolean isNew() {
        return sparkSession.isNew();
    }

    public void removeAttribute(String name) {
        sparkSession.removeAttribute(name);
    }
}
