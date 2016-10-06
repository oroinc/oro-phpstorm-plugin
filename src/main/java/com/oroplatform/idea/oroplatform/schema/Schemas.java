package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;
import java.util.LinkedList;

public class Schemas {
    public static final Collection<Schema> ALL;

    static {
        ALL = new LinkedList<Schema>();
        ALL.addAll(SchemasV1.ALL);
        ALL.addAll(SchemasV2.ALL);
    }
}
