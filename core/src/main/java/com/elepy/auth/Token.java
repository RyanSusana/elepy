package com.elepy.auth;


import java.util.Objects;

public class Token implements Comparable<Token> {
    private String id;

    private User user;

    private long creationTime;

    private long duration;


    public String getId() {
        return id;
    }

    public Token setId(String id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Token setUser(User user) {
        this.user = user;
        return this;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public Token setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public Token setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String) {
            return o.equals(this.id);
        }
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(id, token.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public int compareTo(Token o) {
        return Long.compare(this.creationTime, o.creationTime);
    }
}
