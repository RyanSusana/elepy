package com.elepy.auth.authorization;

public class DefaultPermissions {
    public static final String SCHEMA_READER = "schema.read";
    private static final String QUALIFIER = "resources.";
    public static String[] UPDATE = new String[]{QUALIFIER + "update"};
    public static String[] DELETE = new String[]{QUALIFIER + "delete"};
    public static String[] CREATE = new String[]{QUALIFIER + "create"};
    public static String[] READ = new String[]{QUALIFIER + "read"};

}
