package com.elepy.utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static com.elepy.utils.Retry.retry;


public class TryAgain implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) {

        if (!(throwable instanceof RetryException)) {
            retry(() -> extensionContext.getRequiredTestMethod()
                    .invoke(extensionContext.getRequiredTestInstance()));
        }
    }
}
