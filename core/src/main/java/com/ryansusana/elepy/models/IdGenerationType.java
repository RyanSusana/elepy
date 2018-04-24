package com.ryansusana.elepy.models;

import com.ryansusana.elepy.concepts.IdProvider;

public enum IdGenerationType {
    NONE(0), HEX_8(8), HEX_10(10), HEX_20(20), UUID(0);


    private final int hexLength;

    IdGenerationType(int hexLength) {
        this.hexLength = hexLength;
    }

    public int getHexLength() {
        return hexLength;
    }

    public String generateId() {
        if (this.equals(NONE)) {
            throw new IllegalStateException("You must generate your own id.");
        }
        if (this.equals(UUID)) {
            return IdProvider.getRandomUUID();
        }
        return IdProvider.getRandomHexString(hexLength);
    }
}
