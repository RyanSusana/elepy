package com.elepy.di;

import com.elepy.exceptions.ElepyConfigException;

public class ElepyDependencyInjectionException extends ElepyConfigException {
    private final int amount;

    public ElepyDependencyInjectionException(String message, int amount) {
        super(message);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
