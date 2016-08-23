package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;

import static java.util.Arrays.asList;

public class Schemas {

    public static class FilePathPatterns {
        public final static String ACL = "Resources/config/acl.yml";
        public final static String ENTITY = "Resources/config/oro/entity.yml";
        public final static String DATAGRID = "Resources/config/datagrid.yml";
        public final static String WORKFLOW = "Resources/config/workflow.yml";
        public final static String SYSTEM_CONFIGURATION = "Resources/config/system_configuration.yml";
        public final static String API = "Resources/config/oro/api.yml";
        public final static String ACTIONS = "Resources/config/oro/actions.yml";
    }

    public static final Collection<Schema> ALL = asList(acl(), entity(), datagrid(), workflow(), systemConfiguration(), api(), actions());

    private static Schema acl() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ACL), Container.with(
            OneOf.from(
                Container.with(
                    Property.named("type", Scalars.strictChoices("entity")),
                    Property.named("bindings", Sequence.of(Container.with(
                        Property.named("class", Scalars.controller).required(),
                        Property.named("method", Scalars.phpMethod("*Action")).required()
                    ))),
                    Property.named("class", Scalars.entity).required(),
                    Property.named("permission", Scalars.strictChoices("VIEW", "EDIT", "CREATE", "DELETE", "ASSIGN", "SHARE")).required(),
                    Property.named("group_name", Scalars.any),
                    Property.named("description", Scalars.any)
                ),
                Container.with(
                    Property.named("type", Scalars.strictChoices("action")),
                    Property.named("label", Scalars.any).required(),
                    Property.named("bindings", Sequence.of(Container.with(
                        //define as regular scalars in order to avoid double suggestions
                        Property.named("class", Scalars.any).required(),
                        Property.named("method", Scalars.any).required()
                    ))),
                    Property.named("group_name", Scalars.any),
                    Property.named("description", Scalars.any),
                    Property.named("category", Scalars.any)
                )
            )
        ));
    }

    private static Schema entity() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ENTITY), Container.with(
            Property.named("oro_entity",
                Container.with(
                    Property.named("exclusions", Sequence.of(
                        Container.with(
                            Property.named("entity", Scalars.fullEntity).required(),
                            Property.named("field", Scalars.field(new PropertyPath("oro_entity", "$this", "exclusions", "$this", "entity").pointsToValue()))
                        )
                    )),
                    Property.named("entity_alias_exclusions", Sequence.of(Scalars.fullEntity)),
                    Property.named("entity_aliases", Container.with(
                        Property.any(Container.with(
                            Property.named("alias", Scalars.regexp("^[a-z][a-z0-9_]*$")),
                            Property.named("plural_alias", Scalars.regexp("^[a-z][a-z0-9_]*$"))
                        )).withKeyElement(Scalars.fullEntity)
                    ))
                ).allowExtraProperties()
            ).required()
        ));
    }

    private static Schema datagrid() {
        final Element queryJoin = Container.with(
            Property.named("join", Scalars.any).required(),
            Property.named("alias", Scalars.any).required(),
            Property.named("conditionType", Scalars.any),
            Property.named("condition", Scalars.strictChoices("ON", "WITH"))
        );

        return new Schema(new FilePathMatcher(FilePathPatterns.DATAGRID), Container.with(
            Property.named("datagrid",
                Container.with(
                    Container.with(
                        Property.named("extended_entity_name", OneOf.from(Scalars.entity, Scalars.any)),
                        Property.named("acl_resource", Scalars.any),
                        Property.named("mixins", Sequence.of(Scalars.datagrid)),
                        Property.named("source", Container.with(
                            Property.named("type", Scalars.choices("orm", "search")),
                            Property.named("acl_resource", Scalars.any),
                            Property.named("query", Container.with(
                                Property.named("select", Sequence.of(Scalars.any)),
                                Property.named("from", Sequence.of(Container.with(
                                    Property.named("table", Scalars.entity),
                                    Property.named("alias", Scalars.any)
                                ))),
                                Property.named("join", Container.with(
                                    Property.named("left", Sequence.of(queryJoin)),
                                    Property.named("inner", Sequence.of(queryJoin))
                                )),
                                Property.named("where", Container.with(
                                    Property.named("and", Sequence.of(Scalars.any)),
                                    Property.named("or", Sequence.of(Scalars.any))
                                )),
                                Property.named("having", Scalars.any),
                                Property.named("orderBy", Container.with(
                                    Property.named("column", Scalars.any),
                                    Property.named("dir", Scalars.strictChoices("ASC", "DESC"))
                                )),
                                Property.named("groupBy", Scalars.any),
                                Property.named("distinct", Scalars.bool)
                            ))
                        ).allowExtraProperties()),
                        Property.named("columns", Container.with(
                            Container.with(
                                Property.named("label", Scalars.any),
                                Property.named("translatable", Scalars.bool),
                                Property.named("data_name", Scalars.any),
                                Property.named("frontend_type", Scalars.choices("string", "html", "date", "time", "datetime", "integer", "number", "decimal", "percent", "currency", "boolean", "array", "simple_array", "row_array", "select", "multi-select", "phone", "relation")),
                                Property.named("choices", Scalars.any),
                                Property.named("template", Scalars.any),
                                Property.named("type", Scalars.choices("field", "url", "link", "twig", "translatable", "callback", "localized_number")),
                                Property.named("renderable", Scalars.bool),
                                Property.named("editable", Scalars.bool),
                                Property.named("order", Scalars.integer),
                                Property.named("required", Scalars.bool),
                                Property.named("manageable", Scalars.bool),
                                Property.named("context", Scalars.any),
                                Property.named("inline_editing", Container.with(
                                    Property.named("enable", Scalars.bool),
                                    Property.named("editor", Container.with(
                                        Property.named("component", Scalars.any),
                                        Property.named("component_options", Container.any),
                                        Property.named("view", Scalars.any),
                                        Property.named("view_options", Container.any)
                                    )),
                                    Property.named("save_api_accessor", Container.any),
                                    Property.named("autocomplete_api_accessor", Container.any),
                                    Property.named("validation_rules", Container.any)
                                ))
                            ).allowExtraProperties()
                        )),
                        Property.named("sorters", Container.with(
                            Property.named("columns", Container.with(
                                Container.with(
                                    Property.named("data_name", Scalars.any),
                                    Property.named("disabled", Scalars.bool),
                                    Property.named("type", Scalars.any),
                                    Property.named("apply_callback", Scalars.any)
                                )
                            )),
                            Property.named("default", Container.any),
                            Property.named("multiple_sorting", Scalars.bool),
                            Property.named("toolbar_sorting", Scalars.bool)
                        )),
                        Property.named("filters", Container.with(
                            Property.named("columns", Container.with(
                                Container.with(
                                    Property.named("type", Scalars.choices(
                                        "string", "selectrow", "number", "number-range", "percent", "currency", "choice",
                                        "single_choice", "entity", "boolean", "date", "datetime", "many-to-many", "choice-tree",
                                        "dictionary", "enum", "multi_enum"
                                    )),
                                    Property.named("data_name", Scalars.any),
                                    Property.named("filter_condition", Scalars.strictChoices("AND", "OR")),
                                    Property.named("filter_by_having", Scalars.bool),
                                    Property.named("enabled", Scalars.bool),
                                    Property.named("translatable", Scalars.bool),
                                    Property.named("options", Container.any)
                                ).allowExtraProperties()
                            )),
                            Property.named("default", Container.any)
                        )),
                        Property.named("properties", Container.with(
                            Container.with(
                                Property.named("type", Scalars.choices("url", "callback")),
                                Property.named("route", Scalars.any),
                                Property.named("params", Sequence.of(Scalars.any))
                            ).allowExtraProperties()
                        )),
                        Property.named("actions", Container.with(
                            Container.with(
                                Property.named("label", Scalars.any),
                                Property.named("type", Scalars.choices("navigate", "ajax", "delete", "ajaxdelete", "frontend")),
                                Property.named("acl_resource", Scalars.any),
                                Property.named("icon", Scalars.any),
                                Property.named("link", Scalars.any),
                                Property.named("rowAction", Scalars.bool),
                                Property.named("selector", Scalars.any)
                            ).allowExtraProperties()
                        )),
                        Property.named("mass_action", Container.with(
                            Container.with(
                                Property.named("label", Scalars.any),
                                Property.named("type", Scalars.choices("frontend", "merge")),
                                Property.named("data_identifier", Scalars.any),
                                Property.named("icon", Scalars.any),
                                Property.named("selector", Scalars.any)
                            )
                        )),
                        Property.named("totals", Container.with(
                            Container.with(
                                Property.named("per_page", Scalars.bool),
                                Property.named("hide_if_one_page", Scalars.bool),
                                Property.named("extends", Scalars.any),
                                Property.named("columns", Container.with(
                                    Container.with(
                                        Property.named("label", Scalars.any),
                                        Property.named("expr", Scalars.any),
                                        Property.named("formatter", Scalars.strictChoices(
                                            "date", "datetime", "time", "decimal", "integer", "percent", "currency"
                                        ))
                                    )
                                ))
                            ).allowExtraProperties()
                        )),
                        Property.named("inline_editing", Container.with(
                            Property.named("enable", Scalars.bool),
                            Property.named("entity_name", Scalars.fullEntity),
                            Property.named("behaviour", Scalars.strictChoices("enable_all", "enable_selected")),
                            Property.named("plugin", Scalars.any),
                            Property.named("default_editors", Scalars.any),
                            Property.named("cell_editor", Container.with(
                                Property.named("component", Scalars.any),
                                Property.named("component_options", Container.any)
                            )),
                            Property.named("save_api_accessor", Container.with(
                                Property.named("route", Scalars.any),
                                Property.named("http_method", Scalars.any),
                                Property.named("headers", Scalars.any),
                                Property.named("default_route_parameters", Container.any),
                                Property.named("query_parameter_names", Sequence.of(Scalars.any))
                            ))
                        )),
                        Property.named("action_configuration", Scalars.any),
                        Property.named("options", Container.with(
                            Property.named("entityHint", Scalars.any),
                            Property.named("entity_pagination", Scalars.bool),
                            Property.named("toolbarOptions", Container.with(
                                Property.named("hide", Scalars.bool),
                                Property.named("addResetAction", Scalars.bool),
                                Property.named("addRefreshAction", Scalars.bool),
                                Property.named("addColumnManager", Scalars.bool),
                                Property.named("turnOffToolbarRecordsNumber", Scalars.integer),
                                Property.named("pageSize", Container.with(
                                    Property.named("hide", Scalars.bool),
                                    Property.named("default_per_page", Scalars.integer),
                                    Property.named("items", Sequence.of(Scalars.integer))
                                )),
                                Property.named("pagination", Container.with(
                                    Property.named("hide", Scalars.bool),
                                    Property.named("onePage", Scalars.bool)
                                )),
                                Property.named("placement", Container.with(
                                    Property.named("top", Scalars.bool),
                                    Property.named("bottom", Scalars.bool)
                                )),
                                Property.named("columnManager", Container.with(
                                    Property.named("minVisibleColumnsQuantity", Scalars.integer)
                                ))
                            )),
                            Property.named("export", OneOf.from(
                                Scalars.bool,
                                Container.with(
                                    Container.with(Property.named("label", Scalars.any))
                                )
                            )),
                            Property.named("rowSelection", Container.with(
                                Property.named("dataField", Scalars.any),
                                Property.named("columnName", Scalars.any),
                                Property.named("selectors", Scalars.any)
                            )),
                            Property.named("skip_count_walker", Scalars.bool),
                            Property.named("requireJSModules", Sequence.of(Scalars.any)),
                            Property.named("routerEnabled", Scalars.bool)
                        ).allowExtraProperties())
                    ).allowExtraProperties())
                )
            )
        );
    }

    private static Schema workflow() {
        final Container acl = Container.with(
            Property.named("update", Scalars.bool),
            Property.named("delete", Scalars.bool)
        );
        final Element entityAcl = OneOf.from(acl, Container.with(acl));

        final Element attributesElement = Scalars.propertiesFromPath(new PropertyPath("workflows", "$this", "attributes"), "$");
        final Element workflowAttributes = OneOf.from(attributesElement, Sequence.of(attributesElement));
        final Element conditions = Repeated.atAnyLevel(Container.with(
            Property.any(workflowAttributes).withKeyElement(Scalars.condition)
        ));

        final Sequence actions = Sequence.of(
            Container.with(
                Property.any(OneOf.from(workflowAttributes, Container.with(workflowAttributes))).withKeyElement(Scalars.action)
            )
        );

        final Container attribute = Container.with(
            Property.named("type", Scalars.strictChoices("boolean", "bool", "integer", "int", "float", "string", "array", "object", "entity")),
            Property.named("label", Scalars.any),
            Property.named("entity_acl", entityAcl),
            Property.named("property_path", Scalars.any),
            Property.named("options", Container.with(
                Property.named("class", Scalars.phpClass),
                Property.named("multiple", Scalars.bool)
            ))
        );

        final Container step = Container.with(
            Property.named("label", Scalars.any),
            Property.named("order", Scalars.integer),
            Property.named("is_final", Scalars.bool),
            Property.named("entity_acl", entityAcl),
            Property.named("allowed_transitions", Sequence.of(Scalars.any))
        );

        final Container transition = Container.with(
            Property.named("step_to", Scalars.any),
            Property.named("transition_definition", Scalars.propertiesFromPath(new PropertyPath("workflows", "$this", "transition_definitions"))),
            Property.named("is_start", Scalars.bool),
            Property.named("is_hidden", Scalars.bool),
            Property.named("is_unavailable_hidden", Scalars.bool),
            Property.named("acl_resource", Scalars.any),
            Property.named("acl_message", Scalars.any),
            Property.named("message", Scalars.any),
            Property.named("display_type", Scalars.any),
            Property.named("page_template", Scalars.any),
            Property.named("dialog_template", Scalars.any),
            Property.named("frontend_options", Container.with(
                Property.named("class", Scalars.any),
                Property.named("icon", Scalars.any)
            )),
            Property.named("form_options", Container.any),
            Property.named("label", Scalars.any)
        );


        final Container transitionDefinition = Container.with(
            Property.named("pre_conditions", conditions),
            Property.named("conditions", conditions),
            Property.named("post_actions", actions),
            Property.named("init_actions", actions)
        );

        return new Schema(new WorkflowMatcher(), Container.with(
            Property.named("imports", Sequence.of(Container.with(
                Property.named("resource", Scalars.file)
            ))),
            Property.named("workflows", Container.with(
                Container.with(
                    Property.named("label", Scalars.any),
                    Property.named("entity", Scalars.fullEntity),
                    Property.named("entity_attribute", Scalars.field(new PropertyPath("workflows", "$this", "entity").pointsToValue())),
                    Property.named("is_system", Scalars.bool),
                    Property.named("start_step", Scalars.any),
                    Property.named("steps_display_ordered", Scalars.bool),
                    Property.named("attributes", OneOf.from(
                        Container.with(attribute),
                        Sequence.of(attribute.andWith(Property.named("name", Scalars.any)))
                    )),
                    Property.named("steps", OneOf.from(
                        Container.with(step),
                        Sequence.of(step.andWith(Property.named("name", Scalars.any)))
                    )),
                    Property.named("transitions", OneOf.from(
                        Container.with(transition),
                        Sequence.of(transition.andWith(Property.named("name", Scalars.any)))
                    )),
                    Property.named("transition_definitions", OneOf.from(
                        Container.with(transitionDefinition),
                        Sequence.of(transitionDefinition.andWith(Property.named("name", Scalars.any)))
                    ))
                )
            ))
        ));
    }

    private static Schema systemConfiguration() {
        return new Schema(new FilePathMatcher(FilePathPatterns.SYSTEM_CONFIGURATION), Container.with(
            Property.named("oro_system_configuration", Container.with(
                Property.named("groups", Container.with(
                    Container.with(
                        Property.named("icon", Scalars.any),
                        Property.named("title", Scalars.any),
                        Property.named("page_reload", Scalars.bool),
                        Property.named("priority", Scalars.integer),
                        Property.named("description", Scalars.any),
                        Property.named("tooltip", Scalars.any),
                        Property.named("configurator", Scalars.phpCallback)
                    )
                )),
                Property.named("fields", Container.with(
                    Container.with(
                        Property.named("type", Scalars.formType),
                        Property.named("options", Scalars.any),
                        Property.named("acl_resource", Scalars.any),
                        Property.named("priority", Scalars.integer),
                        Property.named("ui_only", Scalars.bool),
                        Property.named("data_type", dataType()),
                        Property.named("tooltip", Scalars.any)
                    )
                )),
                Property.named("tree", Container.with(
                    systemConfigurationTree(10)
                )),
                Property.named("api_tree", Container.with(
                    Repeated.atAnyLevel(
                        Container.with(
                            Property.any(Container.any).withKeyElement(Scalars.propertiesFromPath(new PropertyPath("oro_system_configuration", "fields")))
                        )
                    )
                ))
            ))
        ));
    }

    private static Scalar dataType() {
        return Scalars.choices("boolean", "integer", "float", "double", "string", "array");
    }

    private static Element systemConfigurationTree(int deep) {
        if(deep == 0) return Scalars.any;

        return Container.with(
            Property.any(Container.with(
                Property.named("priority", Scalars.integer),
                Property.named("children", OneOf.from(
                    systemConfigurationTree(deep - 1),
                    Sequence.of(Scalars.propertiesFromPath(new PropertyPath("oro_system_configuration", "fields")))
                ))
            )).withKeyElement(Scalars.propertiesFromPath(new PropertyPath("oro_system_configuration", "groups")))
        );
    }

    private static Container apiFilters(PropertyPath entityPropertyPath) {
        return Container.with(
            Property.named("exclusion_policy", Scalars.choices("all", "none")),
            Property.named("fields", Container.with(
                Property.any(Container.with(
                    Property.named("exclude", Scalars.bool),
                    Property.named("description", Scalars.any),
                    Property.named("property_path", Scalars.any),
                    Property.named("data_type", Scalars.choices("boolean", "bool", "integer", "int", "float", "string")),
                    Property.named("allow_array", Scalars.bool)
                )).withKeyElement(Scalars.field(entityPropertyPath))
            ))
        );
    }

    private static Container apiFields(PropertyPath entityPropertyPath) {
        return Container.with(
            Property.any(Container.with(
                Property.named("exclude", Scalars.bool),
                Property.named("description", Scalars.any),
                Property.named("property_path", Scalars.any),
                Property.named("data_transformer", OneOf.from(Scalars.service, Scalars.phpClass)),
                Property.named("collapse", Scalars.bool),
                Property.named("form_type", Scalars.formType),
                Property.named("form_options", Scalars.any),
                Property.named("data_type", dataType()),
                Property.named("meta_property", Scalars.bool),
                Property.named("target_class", Scalars.fullEntity),
                Property.named("target_type", Scalars.choices("to-one", "to-many", "collection"))
            )).withKeyElement(Scalars.field(entityPropertyPath))
        );
    }
    private static Container apiSorters(PropertyPath entityPropertyPath) {
        return Container.with(
            Property.named("exclusion_policy", Scalars.choices("all", "none")),
            Property.named("fields", Container.with(
                Property.any(Container.with(
                    Property.named("exclude", Scalars.bool),
                    Property.named("property_path", Scalars.any)
                )).withKeyElement(Scalars.field(entityPropertyPath))
            ))
        );
    }

    private static Schema api() {
        final Container formOptions = Container.with(
            Property.named("data_class", Scalars.phpClass),
            Property.named("validation_groups", Sequence.of(Scalars.any)),
            Property.named("extra_fields_message", Scalars.any)
        );

        final Container orderBy = Container.with(
            Property.any(Scalars.strictChoices("ASC", "DESC")).withKeyElement(Scalars.field(new PropertyPath("oro_api", "entities", "$this")))
        );

        final Element action = OneOf.from(
            Container.with(
                Property.named("exclude", Scalars.bool),
                Property.named("description", Scalars.any),
                Property.named("documentation", Scalars.any),
                Property.named("acl_resource", Scalars.any),
                Property.named("max_results", Scalars.integer),
                Property.named("order_by", orderBy),
                Property.named("page_size", Scalars.integer),
                Property.named("disable_sorting", Scalars.bool),
                Property.named("disable_inclusion", Scalars.bool),
                Property.named("disable_fieldset", Scalars.bool),
                Property.named("form_type", Scalars.formType),
                Property.named("form_options", formOptions),
                Property.named("status_codes", Container.with(
                    Property.any(Container.with(
                        Property.named("exclude", Scalars.bool),
                        Property.named("description", Scalars.any)
                    )).withKeyElement(Scalars.choices("200", "201", "202", "203", "204", "205", "206", "400", "401", "403", "404", "406", "417"))
                )),
                Property.named("fields", Container.with(
                    Property.any(Container.with(
                        Property.named("exclude", Scalars.bool),
                        Property.named("form_type", Scalars.formType),
                        Property.named("form_options", formOptions)
                    )).withKeyElement(Scalars.field(new PropertyPath("oro_api", "entities", "$this")))
                ))
            ),
            Scalars.bool
        );

        return new Schema(new FilePathMatcher(FilePathPatterns.API), Container.with(
            Property.named("oro_api", Container.with(
                Property.named("entities", Container.with(
                    Property.any(Container.with(
                        Property.named("exclude", Scalars.bool),
                        Property.named("inherit", Scalars.bool),
                        Property.named("exclusion_policy", Scalars.choices("all", "none")),
                        Property.named("max_results", Scalars.integer),
                        Property.named("order_by", orderBy),
                        Property.named("disable_inclusion", Scalars.bool),
                        Property.named("disable_fieldset", Scalars.bool),
                        Property.named("hints", Sequence.of(
                            OneOf.from(Scalars.any, Container.with(
                                Property.named("name", Scalars.any),
                                Property.named("value", Scalars.any)
                            ))
                        )),
                        Property.named("identifier_field_names", Sequence.of(Scalars.field(new PropertyPath("oro_api", "entities", "$this")))),
                        Property.named("post_serialize", Sequence.of(Scalars.callable)),
                        Property.named("delete_handler", Scalars.service),
                        Property.named("form_type", Scalars.formType),
                        Property.named("form_options", formOptions),
                        Property.named("fields", apiFields(new PropertyPath("oro_api", "entities", "$this"))),
                        Property.named("filters", apiFilters(new PropertyPath("oro_api", "entities", "$this"))),
                        Property.named("sorters", apiSorters(new PropertyPath("oro_api", "entities", "$this"))),
                        Property.named("actions", Container.with(
                            Property.named("get", action),
                            Property.named("get_list", action),
                            Property.named("create", action),
                            Property.named("update", action),
                            Property.named("delete", action),
                            Property.named("delete_list", action)
                        )),
                        Property.named("subresources", Container.with(
                            Property.any(Container.with(
                                Property.named("exclude", Scalars.bool),
                                Property.named("target_class", Scalars.fullEntity),
                                Property.named("target_type", Scalars.choices("to-one", "to-many", "collection")),
                                Property.named("actions", Container.with(
                                    Property.named("get", action),
                                    Property.named("get_list", action),
                                    Property.named("create", action),
                                    Property.named("update", action),
                                    Property.named("delete", action),
                                    Property.named("delete_list", action),
                                    Property.named("get_subresource", action),
                                    Property.named("get_relationship", action),
                                    Property.named("add_relationship", action),
                                    Property.named("update_relationship", action),
                                    Property.named("delete_relationship", action)
                                )),
                                Property.named("filters", apiFilters(new PropertyPath("oro_api", "entity", "$this")))
                            ))
                        ))
                    )).withKeyElement(Scalars.fullEntity)
                )),
                Property.named("relations", Container.with(
                    Property.any(Container.with(
                        Property.named("fields", apiFields(new PropertyPath("oro_api", "relations", "$this"))),
                        Property.named("filters", apiFilters(new PropertyPath("oro_api", "relations", "$this"))),
                        Property.named("sorters", apiSorters(new PropertyPath("oro_api", "relations", "$this")))
                    ).allowExtraProperties()).withKeyElement(Scalars.fullEntity)
                ))
            ))
        ));
    }

    private static Schema actions() {
        final Element conditions = Repeated.atAnyLevel(
            Container.with(Property.any(Scalars.any).withKeyElement(Scalars.condition))
        );
        final Element actions = Sequence.of(Container.with(
            Property.any(Container.any).withKeyElement(Scalars.action)
        ));
        //TODO: what operations should be suggested? From all files or maybe only from this file?
        final Element operations = Scalars.choices("UPDATE", "DELETE");
        final Scalar acl = Scalars.acl;

        return new Schema(new FilePathMatcher(FilePathPatterns.ACTIONS), Container.with(
            Property.named("operations", Container.with(
                Property.any(
                    Container.with(
                        Property.named("name", Scalars.any),
                        Property.named("extends", operations),
                        Property.named("label", Scalars.any),
                        Property.named("substitute_operation", operations),
                        Property.named("button_options", Container.with(
                            Property.named("icon", Scalars.any),
                            Property.named("class", Scalars.any),
                            Property.named("group", Scalars.any),
                            //TODO: twig template, index?
                            Property.named("template", Scalars.twig),
                            Property.named("data", Container.any),
                            Property.named("page_component_module", Scalars.any),
                            Property.named("page_component_options", Scalars.any)
                        )),
                        Property.named("enabled", Scalars.bool),
                        Property.named("entities", Sequence.of(Scalars.fullEntity)),
                        Property.named("for_all_entities", Scalars.bool),
                        Property.named("exclude_entities", Sequence.of(Scalars.fullEntity)),
                        //TODO: index routes - how? from cache?
                        Property.named("routes", Sequence.of(Scalars.any)),
                        Property.named("groups", Sequence.of(Scalars.any)),
                        Property.named("datagrids", Sequence.of(Scalars.datagrid)),
                        Property.named("for_all_datagrids", Scalars.bool),
                        Property.named("exclude_datagrids", Sequence.of(Scalars.datagrid)),
                        Property.named("order", Scalars.integer),
                        Property.named("acl_resource", acl),
                        Property.named("frontend_options", Container.with(
                            //TODO: index twig templates
                            Property.named("template", Scalars.any),
                            Property.named("title", Scalars.any),
                            Property.named("options", Container.any),
                            //TODO: index messages?
                            Property.named("confirmation", Scalars.any),
                            Property.named("show_dialog", Scalars.bool)
                        )),
                        Property.named("preactions", actions),
                        Property.named("preconditions", conditions),
                        Property.named("attributes", Container.with(
                            Container.with(
                                Property.named("type", Scalars.any),
                                Property.named("label", Scalars.any),
                                Property.named("property_path", Scalars.any),
                                Property.named("options", Container.with(
                                    Property.named("class", Scalars.phpClass)
                                ))
                            )
                        )),
                        Property.named("datagrid_options", Container.with(
                            Property.named("mass_action_provider", Scalars.massActionProvider),
                            //TODO: the same as in datagrid?
                            Property.named("mass_action", Sequence.of(Scalars.any))
                        )),
                        Property.named("form_options", Container.with(
                            Property.named("attribute_fields", Container.with(
                                Container.with(
                                    Property.named("form_type", Scalars.formType),
                                    Property.named("options", Container.any)
                                )
                            )),
                            Property.named("attribute_default_values", Container.with(
                                Property.any(Scalars.any)
                                    .withKeyElement(Scalars.propertiesFromPath(new PropertyPath("operations", "$this", "form_options", "attribute_fields")))
                            ))
                        )),
                        Property.named("form_init", actions),
                        Property.named("conditions", conditions),
                        Property.named("actions", actions),
                        Property.named("replace", Scalars.any),
                        Property.named("applications", Sequence.of(Scalars.any))
                    )
                ).withKeyElement(Scalars.choices("UPDATE", "DELETE"))
            )),
            Property.named("action_groups", Container.with(
                Container.with(
                    Property.named("parameters", Container.with(
                        Container.with(
                            Property.named("type", OneOf.from(Scalars.fullEntity, Scalars.choices("string", "integer", "boolean"))),
                            Property.named("default", Scalars.any),
                            Property.named("required", Scalars.bool),
                            //TODO: translation message?
                            Property.named("message", Scalars.any)
                        )
                    )),
                    Property.named("actions", actions),
                    Property.named("conditions", conditions),
                    Property.named("acl_resource", acl)
                )
            )),
            Property.named("skipped_config", Container.any)
        ).allowExtraProperties());
    }

}
