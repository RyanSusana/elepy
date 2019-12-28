package com.elepy.auth;


import com.elepy.annotations.Hidden;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Service;
import com.elepy.handlers.DisabledHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "elepy_token")
@Table(name = "elepy_tokens")
@RestModel(name = "Tokens", path = "/tokens")
@Hidden
@Service(DisabledHandler.class)
public class Token implements Comparable<Token> {
    @Id
    private String id;

    @Column
    private String userId;

    @Column
    private long maxDate;

    public long getMaxDate() {
        return maxDate;
    }

    public Token setMaxDate(long maxDate) {
        this.maxDate = maxDate;
        return this;
    }

    public String getId() {
        return id;
    }

    public Token setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Token setUserId(String userId) {
        this.userId = userId;
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
        return Long.compare(this.maxDate, o.maxDate);
    }
}
