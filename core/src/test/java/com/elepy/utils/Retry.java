package com.elepy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Retry {


    private static final Logger logger = LoggerFactory.getLogger(Retry.class);

    public static void retry(RetryInterface retryInterface) {
        retry(retryInterface, 3, (amountOfTriesLeft, thrownException) -> {
            logger.warn(String
                    .format("Test failed with %d retries left. Exception message: %s", amountOfTriesLeft,
                            thrownException.getMessage()), thrownException);
        });
    }

    public static void retry(RetryInterface retryInterface, int amountOfTries,
                             AfterFailedTryCallback afterFailedTryCallback) {

        try {
            retryInterface.retry();
        } catch (Exception e) {
            if (amountOfTries > 1) {
                afterFailedTryCallback.afterTry(amountOfTries - 1, e);
                retry(retryInterface, amountOfTries - 1, afterFailedTryCallback);
            } else {
                logger.error("Test failed", e);
                throw new RetryException("Test failed.", e);
            }
        }

    }
}
