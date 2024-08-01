package com.elepy.query;

import java.util.Set;

public record FilterTypeDescription(FilterType filterType, String prettyName, String name, Set<String> synonyms) {

}
