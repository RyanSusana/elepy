package com.elepy.admin.models;

import com.elepy.annotations.PrettyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum UserType {

    @PrettyName("Super Admin") SUPER_ADMIN(100),
    @PrettyName("Administrator") ADMIN(95),
    @PrettyName("Moderator") MODERATOR(90),
    @PrettyName("Regular User") USER(0);

    private final int level;

    UserType(int level) {
        this.level = level;
    }

    public static Optional<UserType> getRank(String s) {
        try {
            return Optional.of(UserType.valueOf(s.trim().replace(" ", "_").toUpperCase()));
        } catch (Exception e) {
            if (!s.toUpperCase().contains("ISA")) {
                return getRank("ISA_" + s);
            } else {
                return Optional.empty();
            }
        }
    }

    public List<UserType> getRanksAbove() {
        List<UserType> list = new ArrayList<>();
        for (UserType userType : UserType.values()) {
            if (userType.level >= level) {
                list.add(userType);
            }
        }
        return list;
    }

    public List<UserType> getRanksBelow() {
        List<UserType> list = new ArrayList<>();
        for (UserType userType : UserType.values()) {
            if (userType.level < level) {
                list.add(userType);
            }
        }
        return list;
    }

    public boolean hasEqualRightsTo(UserType other) {
        return this.level >= other.level;
    }

    public boolean hasMoreRightsThan(UserType other) {
        return this.level > other.level;
    }

    public int getLevel() {
        return level;
    }

}
