package com.elepy.oauth;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface EmailExtractor {
    String getEmail(ObjectNode accessToken) throws InterruptedException, ExecutionException, IOException;
}
