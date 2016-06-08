package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;

import static java.util.Arrays.asList;

public class Schemas {

    public static class FilePathPatterns {
        public final static String ACL = "Resources/config/acl.yml";
        public final static String ENTITY = "Resources/config/oro/entity.yml";
        public final static String DATAGRID = "Resources/config/datagrid.yml";
        public final static String WORKFLOW = "Resources/config/workflow.yml";
    }

    public static final Collection<Schema> ALL = asList(acl(), entity(), datagrid(), workflow());

    private static Schema acl() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ACL), Container.with(
            OneOf.from(
                Container.with(
                    Property.named("type", Scalar.strictChoices("entity")),
                    Property.named("bindings", Sequence.of(Container.with(
                        Property.named("class", Scalar.controller).required(),
                        Property.named("method", Scalar.phpMethod("*Action")).required()
                    ))),
                    Property.named("class", Scalar.entity).required(),
                    Property.named("permission", Scalar.strictChoices("VIEW", "EDIT", "CREATE", "DELETE", "ASSIGN", "SHARE")).required(),
                    Property.named("group_name", Scalar.any),
                    Property.named("description", Scalar.any)
                ),
                Container.with(
                    Property.named("type", Scalar.strictChoices("action")),
                    Property.named("label", Scalar.any).required(),
                    Property.named("bindings", Sequence.of(Container.with(
                        //define as regular scalars in order to avoid double suggestions
                        Property.named("class", Scalar.any).required(),
                        Property.named("method", Scalar.any).required()
                    ))),
                    Property.named("group_name", Scalar.any),
                    Property.named("description", Scalar.any)
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
                            Property.named("entity", Scalar.fullEntity).required(),
                            Property.named("field", Scalar.field)
                        )
                    )),
                    Property.named("entity_alias_exclusions", Sequence.of(Scalar.fullEntity)),
                    Property.named("entity_aliases", Container.with(
                        Property.any(Container.with(
                            Property.named("alias", Scalar.regexp("^[a-z][a-z0-9_]*$")),
                            Property.named("plural_alias", Scalar.regexp("^[a-z][a-z0-9_]*$"))
                        )).withKeyElement(Scalar.fullEntity)
                    ))
                ).allowExtraProperties()
            ).required()
        ));
    }

    private static Schema datagrid() {
        final Element queryJoin = Container.with(
            Property.named("join", Scalar.any).required(),
            Property.named("alias", Scalar.any).required(),
            Property.named("conditionType", Scalar.any),
            Property.named("condition", Scalar.strictChoices("ON", "WITH"))
        );

        return new Schema(new FilePathMatcher(FilePathPatterns.DATAGRID), Container.with(
            Property.named("datagrid",
                Container.with(
                    Container.with(
                        Property.named("extended_entity_name", OneOf.from(Scalar.entity, Scalar.any)),
                        Property.named("acl_resource", Scalar.any),
                        Property.named("source", Container.with(
                            Property.named("type", Scalar.choices("orm", "search")),
                            Property.named("acl_resource", Scalar.any),
                            Property.named("query", Container.with(
                                Property.named("select", Sequence.of(Scalar.any)),
                                Property.named("from", Sequence.of(Container.with(
                                    Property.named("table", Scalar.entity),
                                    Property.named("alias", Scalar.any)
                                ))),
                                Property.named("join", Container.with(
                                    Property.named("left", Sequence.of(queryJoin)),
                                    Property.named("inner", Sequence.of(queryJoin))
                                )),
                                Property.named("where", Container.with(
                                    Property.named("and", Sequence.of(Scalar.any)),
                                    Property.named("or", Sequence.of(Scalar.any))
                                )),
                                Property.named("having", Scalar.any),
                                Property.named("orderBy", Container.with(
                                    Property.named("column", Scalar.any),
                                    Property.named("dir", Scalar.strictChoices("ASC", "DESC"))
                                )),
                                Property.named("groupBy", Scalar.any),
                                Property.named("distinct", Scalar.bool)
                            ))
                        ).allowExtraProperties()),
                        Property.named("columns", Container.with(
                            Container.with(
                                Property.named("label", Scalar.any),
                                Property.named("translatable", Scalar.bool),
                                Property.named("data_name", Scalar.any),
                                Property.named("frontend_type", Scalar.choices("string", "html", "date", "time", "datetime", "integer", "number", "decimal", "percent", "currency", "boolean", "array", "simple_array", "row_array", "select", "multi-select", "phone", "relation")),
                                Property.named("choices", Scalar.any),
                                Property.named("template", Scalar.any),
                                Property.named("type", Scalar.choices("field", "url", "link", "twig", "translatable", "callback", "localized_number")),
                                Property.named("renderable", Scalar.bool),
                                Property.named("editable", Scalar.bool),
                                Property.named("order", Scalar.integer),
                                Property.named("required", Scalar.bool),
                                Property.named("manageable", Scalar.bool),
                                Property.named("context", Scalar.any),
                                Property.named("inline_editing", Container.with(
                                    Property.named("enable", Scalar.bool),
                                    Property.named("editor", Container.with(
                                        Property.named("component", Scalar.any),
                                        Property.named("component_options", Container.any),
                                        Property.named("view", Scalar.any),
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
                                    Property.named("data_name", Scalar.any),
                                    Property.named("disabled", Scalar.bool),
                                    Property.named("type", Scalar.any),
                                    Property.named("apply_callback", Scalar.any)
                                )
                            )),
                            Property.named("default", Container.any),
                            Property.named("multiple_sorting", Scalar.bool),
                            Property.named("toolbar_sorting", Scalar.bool)
                        )),
                        Property.named("filters", Container.with(
                            Property.named("columns", Container.with(
                                Container.with(
                                    Property.named("type", Scalar.choices(
                                        "string", "selectrow", "number", "number-range", "percent", "currency", "choice",
                                        "single_choice", "entity", "boolean", "date", "datetime", "many-to-many", "choice-tree",
                                        "dictionary", "enum", "multi_enum"
                                    )),
                                    Property.named("data_name", Scalar.any),
                                    Property.named("filter_condition", Scalar.strictChoices("AND", "OR")),
                                    Property.named("filter_by_having", Scalar.bool),
                                    Property.named("enabled", Scalar.bool),
                                    Property.named("translatable", Scalar.bool),
                                    Property.named("options", Container.any)
                                ).allowExtraProperties()
                            )),
                            Property.named("default", Container.any)
                        )),
                        Property.named("properties", Container.with(
                            Container.with(
                                Property.named("type", Scalar.choices("url", "callback")),
                                Property.named("route", Scalar.any),
                                Property.named("params", Sequence.of(Scalar.any))
                            ).allowExtraProperties()
                        )),
                        Property.named("actions", Container.with(
                            Container.with(
                                Property.named("label", Scalar.any),
                                Property.named("type", Scalar.choices("navigate", "ajax", "delete", "ajaxdelete", "frontend")),
                                Property.named("acl_resource", Scalar.any),
                                Property.named("icon", Scalar.any),
                                Property.named("link", Scalar.any),
                                Property.named("rowAction", Scalar.bool),
                                Property.named("selector", Scalar.any)
                            ).allowExtraProperties()
                        )),
                        Property.named("mass_action", Container.with(
                            Container.with(
                                Property.named("label", Scalar.any),
                                Property.named("type", Scalar.choices("frontend", "merge")),
                                Property.named("data_identifier", Scalar.any),
                                Property.named("icon", Scalar.any),
                                Property.named("selector", Scalar.any)
                            )
                        )),
                        Property.named("totals", Container.with(
                            Container.with(
                                Property.named("per_page", Scalar.bool),
                                Property.named("hide_if_one_page", Scalar.bool),
                                Property.named("extends", Scalar.any),
                                Property.named("columns", Container.with(
                                    Container.with(
                                        Property.named("label", Scalar.any),
                                        Property.named("expr", Scalar.any),
                                        Property.named("formatter", Scalar.strictChoices(
                                            "date", "datetime", "time", "decimal", "integer", "percent", "currency"
                                        ))
                                    )
                                ))
                            ).allowExtraProperties()
                        )),
                        Property.named("inline_editing", Container.with(
                            Property.named("enable", Scalar.bool),
                            Property.named("entity_name", Scalar.fullEntity),
                            Property.named("behaviour", Scalar.strictChoices("enable_all", "enable_selected")),
                            Property.named("plugin", Scalar.any),
                            Property.named("default_editors", Scalar.any),
                            Property.named("cell_editor", Container.with(
                                Property.named("component", Scalar.any),
                                Property.named("component_options", Container.any)
                            )),
                            Property.named("save_api_accessor", Container.with(
                                Property.named("route", Scalar.any),
                                Property.named("http_method", Scalar.any),
                                Property.named("headers", Scalar.any),
                                Property.named("default_route_parameters", Container.any),
                                Property.named("query_parameter_names", Sequence.of(Scalar.any))
                            ))
                        )),
                        Property.named("action_configuration", Scalar.any),
                        Property.named("options", Container.with(
                            Property.named("entityHint", Scalar.any),
                            Property.named("entity_pagination", Scalar.bool),
                            Property.named("toolbarOptions", Container.with(
                                Property.named("hide", Scalar.bool),
                                Property.named("addResetAction", Scalar.bool),
                                Property.named("addRefreshAction", Scalar.bool),
                                Property.named("addColumnManager", Scalar.bool),
                                Property.named("turnOffToolbarRecordsNumber", Scalar.integer),
                                Property.named("pageSize", Container.with(
                                    Property.named("hide", Scalar.bool),
                                    Property.named("default_per_page", Scalar.integer),
                                    Property.named("items", Sequence.of(Scalar.integer))
                                )),
                                Property.named("pagination", Container.with(
                                    Property.named("hide", Scalar.bool),
                                    Property.named("onePage", Scalar.bool)
                                )),
                                Property.named("placement", Container.with(
                                    Property.named("top", Scalar.bool),
                                    Property.named("bottom", Scalar.bool)
                                )),
                                Property.named("columnManager", Container.with(
                                    Property.named("minVisibleColumnsQuantity", Scalar.integer)
                                ))
                            )),
                            Property.named("export", OneOf.from(
                                Scalar.bool,
                                Container.with(
                                    Container.with(Property.named("label", Scalar.any))
                                )
                            )),
                            Property.named("rowSelection", Container.with(
                                Property.named("dataField", Scalar.any),
                                Property.named("columnName", Scalar.any),
                                Property.named("selectors", Scalar.any)
                            )),
                            Property.named("skip_count_walker", Scalar.bool),
                            Property.named("requireJSModules", Sequence.of(Scalar.any)),
                            Property.named("routerEnabled", Scalar.bool)
                        ).allowExtraProperties())
                    ).allowExtraProperties())
                )
            )
        );
    }

    private static Schema workflow() {
        final Container acl = Container.with(
            Property.named("update", Scalar.bool),
            Property.named("delete", Scalar.bool)
        );
        final Element entityAcl = OneOf.from(acl, Container.with(acl));

        //TODO: implement conditions
        final Element conditions = Scalar.service("oro_workflow.condition", "@");

        final Container attribute = Container.with(
            Property.named("type", Scalar.strictChoices("boolean", "bool", "integer", "int", "float", "string", "array", "object", "entity")),
            Property.named("label", Scalar.any),
            Property.named("entity_acl", entityAcl),
            Property.named("property_path", Scalar.any),
            Property.named("options", Container.with(
                Property.named("class", Scalar.any), //TODO: support for any php class reference
                Property.named("multiple", Scalar.bool)
            ))
        );

        final Container step = Container.with(
            Property.named("label", Scalar.any),
            Property.named("order", Scalar.integer),
            Property.named("is_final", Scalar.bool),
            Property.named("entity_acl", entityAcl),
            Property.named("allowed_transitions", Sequence.of(Scalar.any))
        );

        final Container transition = Container.with(
            Property.named("step_to", Scalar.any),
            Property.named("transition_definition", Scalar.any),
            Property.named("is_start", Scalar.bool),
            Property.named("is_hidden", Scalar.bool),
            Property.named("is_unavailable_hidden", Scalar.bool),
            Property.named("acl_resource", Scalar.any),
            Property.named("acl_message", Scalar.any),
            Property.named("message", Scalar.any),
            Property.named("display_type", Scalar.any),
            Property.named("page_template", Scalar.any),
            Property.named("dialog_template", Scalar.any),
            Property.named("frontend_options", Container.with(
                Property.named("class", Scalar.any),
                Property.named("icon", Scalar.any)
            )),
            Property.named("form_options", Container.any),
            Property.named("label", Scalar.any)
        );

        final Container transitionDefinition = Container.with(
            Property.named("pre_conditions", conditions),
            Property.named("conditions", conditions),
            Property.named("post_actions", conditions),
            Property.named("init_actions", conditions)
        );

        return new Schema(new WorkflowMatcher(), Container.with(
            Property.named("imports", Sequence.of(Container.with(
                Property.named("resource", Scalar.file)
            ))),
            Property.named("workflows", Container.with(
                Container.with(
                    Property.named("label", Scalar.any),
                    Property.named("entity", Scalar.fullEntity),
                    Property.named("entity_attribute", Scalar.field),
                    Property.named("is_system", Scalar.bool),
                    Property.named("start_step", Scalar.any),
                    Property.named("steps_display_ordered", Scalar.bool),
                    Property.named("attributes", OneOf.from(
                        Container.with(attribute),
                        Sequence.of(attribute.andWith(Property.named("name", Scalar.any)))
                    )),
                    Property.named("steps", OneOf.from(
                        Container.with(step),
                        Sequence.of(step.andWith(Property.named("name", Scalar.any)))
                    )),
                    Property.named("transitions", OneOf.from(
                        Container.with(transition),
                        Sequence.of(transition.andWith(Property.named("name", Scalar.any)))
                    )),
                    Property.named("transition_definitions", OneOf.from(
                        Container.with(transitionDefinition),
                        Sequence.of(transitionDefinition.andWith(Property.named("name", Scalar.any)))
                    ))
                )
            ))
        ));
    }
}
