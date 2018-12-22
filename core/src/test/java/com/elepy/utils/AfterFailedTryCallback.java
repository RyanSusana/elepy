package com.elepy.utils;

public interface AfterFailedTryCallback {

    void afterTry(int amountOfTriesLeft, Exception thrownException);


}
