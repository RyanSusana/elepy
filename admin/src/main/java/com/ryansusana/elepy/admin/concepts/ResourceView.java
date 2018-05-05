package com.ryansusana.elepy.admin.concepts;

import java.util.Map;

public interface ResourceView {
    String renderView(Map<String, Object> descriptor);
}
