package com.ryansusana.elepy.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum UserType {
    SUPER_ADMIN(100), ADMIN(95), MODERATOR(90), USER(0), DISABLED(-1);


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
