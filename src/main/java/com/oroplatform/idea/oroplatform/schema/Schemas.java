package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class Schemas {

    private static final Element QUERY_JOIN = new Container(
        new Property("join", Scalar.any).required(),
        new Property("alias", Scalar.any).required(),
        new Property("conditionType", Scalar.any),
        new Property("condition", Scalar.strictChoices("ON", "WITH"))
    );

    public static final Collection<Schema> ALL = asList(
        new Schema("acl.yml", new Container(
            new Property(Pattern.compile(".+"),
                new OneOf(
                    new Container(
                        new Property("type", Scalar.strictChoices("entity")),
                        new Property("bindings", new Sequence(new Container(
                            new Property("class", Scalar.controller).required(),
                            new Property("method", Scalar.phpMethod("*Action")).required()
                        ))),
                        new Property("class", Scalar.entity).required(),
                        new Property("permission", Scalar.strictChoices("VIEW", "EDIT", "CREATE", "DELETE", "ASSIGN", "SHARE")).required(),
                        new Property("group_name", Scalar.any),
                        new Property("description", Scalar.any)
                    ),
                    new Container(
                        new Property("type", Scalar.strictChoices("action")),
                        new Property("label", Scalar.any, true).required(),
                        new Property("bindings", new Sequence(new Container(
                            //define as regular scalars in order to avoid double suggestions
                            new Property("class", Scalar.any).required(),
                            new Property("method", Scalar.any).required()
                        ))),
                        new Property("group_name", Scalar.any),
                        new Property("description", Scalar.any)
                    )
                )
            )
        )),
        new Schema("entity.yml", new Container(
            new Property("oro_entity",
                new Container(
                    new Property("exclusions", new Sequence(
                        new Container(
                            new Property("entity", Scalar.fullEntity).required(),
                            new Property("field", Scalar.field)
                        )
                    )),
                    new Property("entity_alias_exclusions", new Sequence(Scalar.fullEntity)),
                    new Property("entity_aliases", new Container(
                        new Property(Pattern.compile(".*"), new Container(
                            new Property("alias", Scalar.regexp("^[a-z][a-z0-9_]*$")),
                            new Property("plural_alias", Scalar.regexp("^[a-z][a-z0-9_]*$"))
                        )).withKeyElement(Scalar.fullEntity)
                    ))
                ).allowExtraProperties()
            ).required()
        )),
        new Schema("datagrid.yml", new Container(
            new Property("datagrid",
                new Container(
                    new Property(Pattern.compile(".*"), new Container(
                        new Property("extended_entity_name", new OneOf(Scalar.entity, Scalar.any)),
                        new Property("acl_resource", Scalar.any),
                        new Property("source", new Container(
                            new Property("type", Scalar.choices("orm", "search")),
                            new Property("acl_resource", Scalar.any),
                            new Property("query", new Container(
                                new Property("select", new Sequence(Scalar.any)),
                                new Property("from", new Sequence(new Container(
                                    new Property("table", Scalar.any),
                                    new Property("alias", Scalar.any)
                                ))),
                                new Property("join", new Container(
                                    new Property("left", new Sequence(QUERY_JOIN)),
                                    new Property("inner", new Sequence(QUERY_JOIN))
                                )),
                                new Property("where", new Container(
                                    new Property("and", new Sequence(Scalar.any)),
                                    new Property("or", new Sequence(Scalar.any))
                                )),
                                new Property("having", Scalar.any),
                                new Property("orderBy", new Container(
                                    new Property("column", Scalar.any),
                                    new Property("dir", Scalar.strictChoices("ASC", "DESC"))
                                )),
                                new Property("groupBy", Scalar.any),
                                new Property("distinct", Scalar.bool)
                            ))
                        )),
                        new Property("columns", new Container(
                            new Property(Pattern.compile(".*"),
                                new Container(
                                    new Property("label", Scalar.any),
                                    new Property("translatable", Scalar.bool),
                                    new Property("data_name", Scalar.any),
                                    new Property("frontend_type", Scalar.strictChoices("string", "html", "date", "time", "datetime", "integer", "number", "decimal", "percent", "currency", "boolean", "array", "simple_array", "row_array", "select", "multi-select", "phone", "relation")),
                                    new Property("choices", Scalar.any),
                                    new Property("template", Scalar.any),
                                    new Property("type", Scalar.strictChoices("field", "url", "link", "twig", "translatable", "callback", "localized_number")),
                                    new Property("renderable", Scalar.bool),
                                    new Property("editable", Scalar.bool),
                                    new Property("order", Scalar.integer),
                                    new Property("required", Scalar.bool),
                                    new Property("manageable", Scalar.bool),
                                    new Property("context", Scalar.any),
                                    new Property("inline_editing", new Container(
                                        new Property("enable", Scalar.bool),
                                        new Property("editor", new Container(
                                            new Property("component", Scalar.any),
                                            new Property("component_options", Container.any),
                                            new Property("view", Scalar.any),
                                            new Property("view_options", Container.any)
                                        )),
                                        new Property("save_api_accessor", Container.any),
                                        new Property("autocomplete_api_accessor", Container.any),
                                        new Property("validation_rules", Container.any)
                                    ))
                                )
                            )
                        )),
                        new Property("sorters", new Container(
                            new Property("columns", new Container(
                                new Property("data_name", Scalar.any),
                                new Property("disabled", Scalar.bool),
                                new Property("type", Scalar.any),
                                new Property("apply_callback", Scalar.any)
                            )),
                            new Property("default", Container.any),
                            new Property("multiple_sorting", Scalar.bool),
                            new Property("toolbar_sorting", Scalar.bool)
                        )),
                        new Property("filters", new Container(
                            new Property("columns", new Container(
                                new Property(Pattern.compile(".*"), new Container(
                                    new Property("type", Scalar.choices(
                                        "string", "selectrow", "number", "number-range", "percent", "currency", "choice",
                                        "single_choice", "entity", "boolean", "date", "datetime", "many-to-many", "choice-tree",
                                        "dictionary", "enum", "multi_enum"
                                    )),
                                    new Property("data_name", Scalar.any),
                                    new Property("filter_condition", Scalar.strictChoices("AND", "OR")),
                                    new Property("filter_by_having", Scalar.bool),
                                    new Property("enabled", Scalar.bool),
                                    new Property("translatable", Scalar.bool),
                                    new Property("options", Container.any)
                                ))
                            )),
                            new Property("default", Container.any)
                        )),
                        //TODO: type, route etc. Definition depending on a type?
                        new Property("properties", Scalar.any),
                        new Property("actions", new Container(
                            new Property(Pattern.compile(".*"), new Container(
                                new Property("label", Scalar.any),
                                new Property("type", Scalar.choices("navigate", "ajax", "delete", "ajaxdelete", "frontend")),
                                new Property("acl_resource", Scalar.any),
                                new Property("icon", Scalar.any),
                                new Property("link", Scalar.any),
                                new Property("rowAction", Scalar.bool),
                                new Property("selector", Scalar.any)
                            ))
                        )),
                        new Property("mass_action", new Container(
                            new Property(Pattern.compile(".*"), new Container(
                                new Property("label", Scalar.any),
                                new Property("type", Scalar.choices("frontend", "merge")),
                                new Property("data_identifier", Scalar.any),
                                new Property("icon", Scalar.any),
                                new Property("selector", Scalar.any)
                            ))
                        )),
                        new Property("totals", new Container(
                            new Property(Pattern.compile(".*"), new Container(
                                new Property("per_page", Scalar.bool),
                                new Property("hide_if_one_page", Scalar.bool),
                                new Property("extends", Scalar.any),
                                new Property("columns", new Container(
                                    new Property(Pattern.compile(".*"), new Container(
                                        new Property("label", Scalar.any),
                                        new Property("expr", Scalar.any),
                                        new Property("formatter", Scalar.strictChoices(
                                            "date", "datetime", "time", "decimal", "integer", "percent", "currency"
                                        ))
                                    ))
                                ))
                            ).allowExtraProperties())
                        )),
                        new Property("inline_editing", new Container(
                            new Property("enable", Scalar.bool),
                            new Property("entity_name", Scalar.entity),
                            new Property("behaviour", Scalar.strictChoices("enable_all", "enable_selected")),
                            new Property("plugin", Scalar.any),
                            new Property("default_editors", Scalar.any),
                            new Property("cell_editor", new Container(
                                new Property("component", Scalar.any),
                                new Property("component_options", Container.any)
                            )),
                            new Property("save_api_accessor", new Container(
                                new Property("route", Scalar.any),
                                new Property("http_method", Scalar.any),
                                new Property("headers", Scalar.any),
                                new Property("default_route_parameters", Container.any),
                                new Property("query_parameter_names", new Sequence(Scalar.any))
                            ))
                        )),
                        new Property("action_configuration", Scalar.any),
                        new Property("options", new Container(
                            new Property("entityHint", Scalar.any),
                            new Property("entity_pagination", Scalar.any),
                            new Property("toolbarOptions", new Container(
                                new Property("hide", Scalar.bool),
                                new Property("addResetAction", Scalar.bool),
                                new Property("addRefreshAction", Scalar.bool),
                                new Property("addColumnManager", Scalar.bool),
                                new Property("turnOffToolbarRecordsNumber", Scalar.integer),
                                new Property("pageSize", new Container(
                                    new Property("hide", Scalar.bool),
                                    new Property("default_per_page", Scalar.integer),
                                    new Property("items", new Sequence(Scalar.integer))
                                )),
                                new Property("pagination", new Container(
                                    new Property("hide", Scalar.bool),
                                    new Property("onePage", Scalar.bool)
                                )),
                                new Property("placement", new Container(
                                    new Property("top", Scalar.bool),
                                    new Property("bottom", Scalar.bool)
                                )),
                                new Property("columnManager", new Container(
                                    new Property("minVisibleColumnsQuantity", Scalar.integer)
                                ))
                            )),
                            new Property("export", new OneOf(
                                Scalar.bool,
                                new Container(
                                    new Property(Pattern.compile(".*"), new Container(
                                        new Property("label", Scalar.any)
                                    )
                                ))
                            )),
                            new Property("rowSelection", new Container(
                                new Property("dataField", Scalar.any),
                                new Property("columnName", Scalar.any),
                                new Property("selectors", Scalar.any)
                            )),
                            new Property("skip_count_walker", Scalar.bool),
                            new Property("requireJSModules", new Sequence(Scalar.any)),
                            new Property("routerEnabled", Scalar.bool)
                        ))
                    ))
                ))
            )
        )
    );

}
