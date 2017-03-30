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
        return new Schema(new ImportedFileMatcher("workflows.yml", SchemasV2.FilePathPatterns.WORKFLOW), SchemasV1.workflowElement());
    }

    private static Schema datagrid() {
        return new Schema(new ImportedFileMatcher("datagrids.yml", FilePathPatterns.DATAGRID), Container.with(
            Property.named("imports", Sequence.of(Container.with(
                Property.named("resource", Scalars.filePath)
            ))),
            Property.named("datagrids", SchemasV1.datagridElementProperties())
        ));
    }

    private static Schema acl() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ACL), Container.with(
            Property.named("acls", SchemasV1.aclElementProperties())
        ));
    }

    private static Schema api() {
        final Property dependOnProperty = Property.named("depends_on", Sequence.of(Scalars.field(new PropertyPath("api", "entities", "$this"))));
        final Container fieldsExtension = Container.with(
            Container.with(
                dependOnProperty,
                Property.named("fields", SchemasV1.apiFields(new PropertyPath("api", "entities", "$this"))),
                Property.named("fields", Container.with(
                    Container.with(
                        dependOnProperty
                    ).allowExtraProperties()
                ))
            ).allowExtraProperties()
        );

        return new Schema(new FilePathMatcher(FilePathPatterns.API), SchemasV1.apiElement("api", new Container(
            Property.named("entity_aliases", Container.with(
                Property.any(Container.with(
                    Property.named("alias", Scalars.regexp("^[a-z][a-z0-9_]*$")),
                    Property.named("plural_alias", Scalars.regexp("^[a-z][a-z0-9_]*$"))
                )).withKeyElement(Scalars.fullEntity)
            )),
            Property.named("entities", Container.with(
                Container.with(
                    Property.named("documentation_resource", Scalars.resource("**.md")),
                    Property.named("fields", fieldsExtension),
                    Property.named("disable_meta_properties", Scalars.bool)
                ).allowExtraProperties()
            ))
        ), new Container(
            Property.named("disable_meta_properties", Scalars.bool)
        )));
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
        final Container attributes = Container.with(
            Property.named("class", Scalars.any),
            Property.named("id", Scalars.any)
        );

        return new Schema(new FilePathMatcher(FilePathPatterns.NAVIGATION), Container.with(
            Property.named("navigation", Container.with(
                Property.named("menu_config", Container.with(
                    Property.named("templates", Container.with(
                        Container.with(
                            Property.named("template", Scalars.twig),
                            Property.named("clear_matcher", Scalars.bool),
                            Property.named("depth", Scalars.integer),
                            Property.named("current_as_link", Scalars.bool),
                            Property.named("current_class", Scalars.any),
                            Property.named("ancestor_class", Scalars.any),
                            Property.named("first_class", Scalars.any),
                            Property.named("last_class", Scalars.any),
                            Property.named("compressed", Scalars.bool),
                            Property.named("block", Scalars.any),
                            Property.named("root_class", Scalars.any),
                            Property.named("is_dropdown", Scalars.bool),
                            Property.named("allow_safe_labels", Scalars.bool)
                        )
                    )),
                    Property.named("items", Container.with(
                        Container.with(
                            Property.named("acl_resource_id", Scalars.acl),
                            Property.named("translate_domain", Scalars.transDomain),
                            Property.named("translate_parameters", Container.any),
                            Property.named("translate_disabled", Scalars.bool),
                            Property.named("label", Scalars.trans),
                            Property.named("name", Scalars.any),
                            Property.named("uri", Scalars.any),
                            Property.named("route", Scalars.route),
                            Property.named("route_parameters", Container.any),
                            Property.named("attributes", attributes),
                            Property.named("linkAttributes", attributes.andWith(
                                Property.named("class", Scalars.any),
                                Property.named("id", Scalars.any),
                                Property.named("target", Scalars.any),
                                Property.named("title", Scalars.any),
                                Property.named("rel", Scalars.any),
                                Property.named("type", Scalars.any),
                                Property.named("name", Scalars.any)
                            )),
                            Property.named("label_attributes", attributes),
                            Property.named("children_attributes", attributes),
                            Property.named("show_non_authorized", Scalars.bool),
                            Property.named("display", Scalars.bool),
                            Property.named("display_children", Scalars.bool),
                            Property.named("extras", Container.any)
                        )
                    )),
                    Property.named("tree", Container.with(
                        Property.any(Container.with(
                            Property.named("type", Scalars.any),
                            Property.named("scope_type", Scalars.any),
                            Property.named("read_only", Scalars.bool),
                            Property.named("max_nesting_level", Scalars.integer),
                            Property.named("merge_strategy", Scalars.strictChoices("append", "replace", "move")),
                            Property.named("extras", Container.with(
                                Property.named("brand", Scalars.any),
                                Property.named("brandLink", Scalars.any)
                            )),
                            Property.named("children", navigationTree(10))
                        ))
                    ))
                )),
                Property.named("titles", Container.with(
                    Property.any(Scalars.any).withKeyElement(Scalars.route)
                )),
                Property.named("navigation_elements", Container.with(
                    Container.with(
                        Property.named("routes", Container.with(
                            Property.any(Scalars.bool).withKeyElement(Scalars.route)
                        )),
                        Property.named("default", Scalars.bool)
                    )
                ))
            ))
        ));
    }

    private static Element navigationTree(int deep) {
        if(deep == 0) return Scalars.any;

        return Container.with(
            Property.any(navigationTree(deep - 1))
                .withKeyElement(Scalars.propertiesFromPath(new PropertyPath("navigation", "menu_config", "items").pointsToValue())),
            Property.named("position", Scalars.integer)
        );
    }
}
