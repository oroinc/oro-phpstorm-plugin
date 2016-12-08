package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;

import static java.util.Arrays.asList;

public class SchemasV2 {

    public static class FilePathPatterns {
        public final static String WORKFLOW = "Resources/config/oro/workflows.yml";
        public final static String DATAGRID = "Resources/config/oro/datagrids.yml";
        public final static String ACL = "Resources/config/oro/acls.yml";
        public final static String API = SchemasV1.FilePathPatterns.API;
        public final static String SEARCH = "Resources/config/oro/search.yml";
        public final static String SYSTEM_CONFIGURATION = "Resources/config/oro/system_configuration.yml";
        public final static String DASHBOARD = "Resources/config/oro/dashboards.yml";
        public final static String NAVIGATION = "Resources/config/oro/navigation.yml";
        public final static String ACTIONS = SchemasV1.FilePathPatterns.ACTIONS;
    }

    static final Collection<Schema> ALL = asList(workflow(), datagrid(), acl(), api(), search(), systemConfiguration(), dashboard(), navigation());

    private static Schema workflow() {
        return new Schema(new WorkflowMatcher("workflows.yml"), SchemasV1.workflowElement());
    }

    private static Schema datagrid() {
        return new Schema(new FilePathMatcher(FilePathPatterns.DATAGRID), Container.with(
            Property.named("datagrids", SchemasV1.datagridElementProperties())
        ));
    }

    private static Schema acl() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ACL), Container.with(
            Property.named("acls", SchemasV1.aclElementProperties())
        ));
    }

    private static Schema api() {
        return new Schema(new FilePathMatcher(FilePathPatterns.API), SchemasV1.apiElement("api"));
    }

    private static Schema search() {
        return new Schema(new FilePathMatcher(FilePathPatterns.SEARCH), Container.with(
            Property.named("search", SchemasV1.searchElementProperties(new PropertyPath("search")))
        ));
    }

    private static Schema systemConfiguration() {
        return new Schema(new FilePathMatcher(FilePathPatterns.SYSTEM_CONFIGURATION), Container.with(
            Property.named("system_configuration", SchemasV1.systemConfigurationElementProperties("system_configuration"))
        ));
    }

    private static Schema dashboard() {
        return new Schema(new FilePathMatcher(FilePathPatterns.DASHBOARD), SchemasV1.dashboardElement("dashboards"));
    }

    private static Schema navigation() {
        return new Schema(new FilePathMatcher(FilePathPatterns.NAVIGATION), Container.with(
            Property.named("navigation", SchemasV1.navigationElementProperties(""))
        ));
    }
}
