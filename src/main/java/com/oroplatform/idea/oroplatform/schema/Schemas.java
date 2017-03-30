package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;
import java.util.LinkedList;

public class Schemas {
    public static final Collection<Schema> ALL;

    public static class FilePathMatchers {
        public final static FileMatcher ORO_CONFIG_FILES = new FilePathMatcher("Resources/config/oro/**.yml");
    }

    static {
        ALL = new LinkedList<>();
        ALL.addAll(SchemasV1.ALL);
        ALL.addAll(SchemasV2.ALL);
    }
}
