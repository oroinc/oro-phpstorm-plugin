package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class Schemas {

    private static final Element QUERY_JOIN = new Container(asList(
        new Property("join", new Scalar()).required(),
        new Property("alias", new Scalar()).required(),
        new Property("conditionType", new Scalar()),
        new Property("condition", new Scalar(new Scalar.Choices(asList("ON", "WITH"))))
    ));

    public static final Collection<Schema> ALL = asList(
        new Schema("acl.yml", new Container(Collections.singletonList(
            new Property(Pattern.compile(".+"),
                new OneOf(
                    new Container(asList(
                        new Property("type", new Scalar(asList("entity"))),
                        new Property("bindings", new Sequence(new Container(asList(
                            new Property("class", new Scalar(Scalar.PhpClass.controller())).required(),
                            new Property("method", new Scalar(new Scalar.PhpMethod("*Action"))).required()
                        )))),
                        new Property("class", new Scalar(Scalar.PhpClass.entity())).required(),
                        new Property("permission", new Scalar(asList("VIEW", "EDIT", "CREATE", "DELETE", "ASSIGN", "SHARE"))).required(),
                        new Property("group_name", new Scalar()),
                        new Property("description", new Scalar())
                    )),
                    new Container(asList(
                        new Property("type", new Scalar(asList("action"))),
                        new Property("label", new Scalar(), true).required(),
                        new Property("bindings", new Sequence(new Container(asList(
                            //define as regular scalars in order to avoid double suggestions
                            new Property("class", new Scalar()).required(),
                            new Property("method", new Scalar()).required()
                        )))),
                        new Property("group_name", new Scalar()),
                        new Property("description", new Scalar())
                    ))
                )
            )
        ))),
        new Schema("entity.yml", new Container(asList(
            new Property("oro_entity",
                new Container(asList(
                    new Property("exclusions", new Sequence(
                        new Container(asList(
                            new Property("entity", new Scalar(Scalar.PhpClass.entity(false))).required(),
                            new Property("field", new Scalar(new Scalar.PhpField()))
                        ))
                    )),
                    new Property("entity_alias_exclusions", new Sequence(new Scalar(Scalar.PhpClass.entity(false)))),
                    new Property("entity_aliases", new Container(asList(
                        new Property(Pattern.compile(".*"), new Container(asList(
                            new Property("alias", new Scalar(new Scalar.Regexp(Pattern.compile("^[a-z][a-z0-9_]*$")))),
                            new Property("plural_alias", new Scalar(new Scalar.Regexp(Pattern.compile("^[a-z][a-z0-9_]*$"))))
                        ))).withKeyElement(new Scalar(Scalar.PhpClass.entity(false)))
                    )))
                )).allowExtraProperties()
            ).required()
        ))),
        new Schema("datagrid.yml", new Container(asList(
            new Property("datagrid",
                new Container(asList(
                    new Property(Pattern.compile(".*"), new Container(asList(
                        new Property("extended_entity_name", new OneOf(
                            new Scalar(Scalar.PhpClass.entity()),
                            new Scalar()
                        )),
                        new Property("acl_resource", new Scalar()),
                        new Property("source", new Container(asList(
                            new Property("type", new Scalar(new Scalar.Choices(asList("orm", "search")).allowExtraChoices())),
                            new Property("acl_resource", new Scalar()),
                            new Property("query", new Container(asList(
                                new Property("select", new Sequence(new Scalar())),
                                new Property("from", new Sequence(new Container(asList(
                                    new Property("table", new Scalar()),
                                    new Property("alias", new Scalar())
                                )))),
                                new Property("join", new Container(asList(
                                    new Property("left", new Sequence(QUERY_JOIN)),
                                    new Property("inner", new Sequence(QUERY_JOIN))
                                ))),
                                new Property("where", new Container(asList(
                                    new Property("and", new Sequence(new Scalar())),
                                    new Property("or", new Sequence(new Scalar()))
                                ))),
                                new Property("having", new Scalar()),
                                new Property("orderBy", new Container(asList(
                                    new Property("column", new Scalar()),
                                    new Property("dir", new Scalar(new Scalar.Choices(asList("ASC", "DESC"))))
                                ))),
                                new Property("groupBy", new Scalar()),
                                new Property("distinct", new Scalar(Scalar.Boolean))
                            )))
                        ))),
                        new Property("columns", new Container(asList(
                            new Property(Pattern.compile(".*"),
                                new Container(asList(
                                    new Property("label", new Scalar()),
                                    new Property("translatable", new Scalar(Scalar.Boolean)),
                                    new Property("data_name", new Scalar()),
                                    new Property("frontend_type", new Scalar(new Scalar.Choices(asList("string", "html", "date", "time", "datetime", "integer", "number", "decimal", "percent", "currency", "boolean", "array", "simple_array", "row_array", "select", "multi-select", "phone", "relation")))),
                                    new Property("choices", new Scalar()),
                                    new Property("template", new Scalar()),
                                    new Property("type", new Scalar(new Scalar.Choices(asList("field", "url", "link", "twig", "translatable", "callback", "localized_number")))),
                                    new Property("renderable", new Scalar(Scalar.Boolean)),
                                    new Property("editable", new Scalar(Scalar.Boolean)),
                                    new Property("order", new Scalar(Scalar.Integer)),
                                    new Property("required", new Scalar(Scalar.Boolean)),
                                    new Property("manageable", new Scalar(Scalar.Boolean)),
                                    new Property("context", new Scalar()),
                                    new Property("inline_editing", new Container(asList(
                                        new Property("enable", new Scalar(Scalar.Boolean)),
                                        new Property("editor", new Container(asList(
                                            new Property("component", new Scalar()),
                                            new Property("component_options", new Container(Collections.<Property>emptyList()).allowExtraProperties()),
                                            new Property("view", new Scalar()),
                                            new Property("view_options", new Container(Collections.<Property>emptyList()).allowExtraProperties())
                                        ))),
                                        new Property("save_api_accessor", new Container(Collections.<Property>emptyList()).allowExtraProperties()),
                                        new Property("autocomplete_api_accessor", new Container(Collections.<Property>emptyList()).allowExtraProperties()),
                                        new Property("validation_rules", new Container(Collections.<Property>emptyList()).allowExtraProperties())
                                    )))
                                ))
                            )
                        ))),
                        new Property("sorters", new Container(asList(
                            new Property("columns", new Container(asList(
                                new Property("data_name", new Scalar()),
                                new Property("disabled", new Scalar(Scalar.Boolean)),
                                new Property("type", new Scalar()),
                                new Property("apply_callback", new Scalar())
                            ))),
                            new Property("default", new Container(Collections.<Property>emptyList()).allowExtraProperties()),
                            new Property("multiple_sorting", new Scalar(Scalar.Boolean)),
                            new Property("toolbar_sorting", new Scalar(Scalar.Boolean))
                        ))),
                        new Property("filters", new Container(asList(
                            new Property("columns", new Container(asList(
                                new Property(Pattern.compile(".*"), new Container(asList(
                                    new Property("type", new Scalar(new Scalar.Choices(asList(
                                        "string", "selectrow", "number", "number-range", "percent", "currency", "choice",
                                        "single_choice", "entity", "boolean", "date", "datetime", "many-to-many", "choice-tree",
                                        "dictionary", "enum", "multi_enum"
                                    )).allowExtraChoices())),
                                    new Property("data_name", new Scalar()),
                                    new Property("filter_condition", new Scalar(new Scalar.Choices(asList("AND", "OR")))),
                                    new Property("filter_by_having", new Scalar(Scalar.Boolean)),
                                    new Property("enabled", new Scalar(Scalar.Boolean)),
                                    new Property("translatable", new Scalar(Scalar.Boolean)),
                                    new Property("options", new Container(Collections.<Property>emptyList()).allowExtraProperties())
                                )))
                            ))),
                            new Property("default", new Container(Collections.<Property>emptyList()).allowExtraProperties())
                        ))),
                        //TODO: type, route etc. Definition depending on a type?
                        new Property("properties", new Scalar()),
                        new Property("actions", new Container(asList(
                            new Property(Pattern.compile(".*"), new Container(asList(
                                new Property("label", new Scalar()),
                                new Property("type", new Scalar(new Scalar.Choices(asList("navigate", "ajax", "delete", "ajaxdelete", "frontend")).allowExtraChoices())),
                                new Property("acl_resource", new Scalar()),
                                new Property("icon", new Scalar()),
                                new Property("link", new Scalar()),
                                new Property("rowAction", new Scalar(Scalar.Boolean)),
                                new Property("selector", new Scalar())
                            ))))
                        )),
                        new Property("mass_action", new Container(asList(
                            new Property(Pattern.compile(".*"), new Container(asList(
                                new Property("label", new Scalar()),
                                new Property("type", new Scalar(new Scalar.Choices(asList("frontend", "merge")).allowExtraChoices())),
                                new Property("data_identifier", new Scalar()),
                                new Property("icon", new Scalar()),
                                new Property("selector", new Scalar())
                            )))
                        ))),
                        new Property("totals", new Container(asList(
                            new Property(Pattern.compile(".*"), new Container(asList(
                                new Property("per_page", new Scalar(Scalar.Boolean)),
                                new Property("hide_if_one_page", new Scalar(Scalar.Boolean)),
                                new Property("extends", new Scalar()),
                                new Property("columns", new Container(asList(
                                    new Property(Pattern.compile(".*"), new Container(asList(
                                        new Property("label", new Scalar()),
                                        new Property("expr", new Scalar()),
                                        new Property("formatter", new Scalar(new Scalar.Choices(asList(
                                            "date", "datetime", "time", "decimal", "integer", "percent", "currency"
                                        ))))
                                    )))
                                )))
                            )).allowExtraProperties())
                        ))),
                        new Property("inline_editing", new Container(asList(
                            new Property("enable", new Scalar(Scalar.Boolean)),
                            new Property("entity_name", new Scalar(Scalar.PhpClass.entity())),
                            new Property("behaviour", new Scalar(new Scalar.Choices(asList("enable_all", "enable_selected")))),
                            new Property("plugin", new Scalar()),
                            new Property("default_editors", new Scalar()),
                            new Property("cell_editor", new Container(asList(
                                new Property("component", new Scalar()),
                                new Property("component_options", new Container(Collections.<Property>emptyList()).allowExtraProperties())
                            ))),
                            new Property("save_api_accessor", new Container(asList(
                                new Property("route", new Scalar()),
                                new Property("http_method", new Scalar()),
                                new Property("headers", new Scalar()),
                                new Property("default_route_parameters", new Container(Collections.<Property>emptyList()).allowExtraProperties()),
                                new Property("query_parameter_names", new Sequence(new Scalar()))
                            )))
                        ))),
                        new Property("action_configuration", new Scalar()),
                        new Property("options", new Container(asList(
                            new Property("entityHint", new Scalar()),
                            new Property("entity_pagination", new Scalar()),
                            new Property("toolbarOptions", new Container(asList(
                                new Property("hide", new Scalar(Scalar.Boolean)),
                                new Property("addResetAction", new Scalar(Scalar.Boolean)),
                                new Property("addRefreshAction", new Scalar(Scalar.Boolean)),
                                new Property("addColumnManager", new Scalar(Scalar.Boolean)),
                                new Property("turnOffToolbarRecordsNumber", new Scalar(Scalar.Integer)),
                                new Property("pageSize", new Container(asList(
                                    new Property("hide", new Scalar(Scalar.Boolean)),
                                    new Property("default_per_page", new Scalar(Scalar.Integer)),
                                    new Property("items", new Sequence(new Scalar(Scalar.Integer)))
                                ))),
                                new Property("pagination", new Container(asList(
                                    new Property("hide", new Scalar(Scalar.Boolean)),
                                    new Property("onePage", new Scalar(Scalar.Boolean))
                                ))),
                                new Property("placement", new Container(asList(
                                    new Property("top", new Scalar(Scalar.Boolean)),
                                    new Property("bottom", new Scalar(Scalar.Boolean))
                                ))),
                                new Property("columnManager", new Container(asList(
                                    new Property("minVisibleColumnsQuantity", new Scalar(Scalar.Integer))
                                )))
                            ))),
                            new Property("export", new OneOf(
                                new Scalar(Scalar.Boolean),
                                new Container(asList(
                                    new Property(Pattern.compile(".*"), new Container(asList(
                                        new Property("label", new Scalar())
                                    ))
                                )))
                            )),
                            new Property("rowSelection", new Container(asList(
                                new Property("dataField", new Scalar()),
                                new Property("columnName", new Scalar()),
                                new Property("selectors", new Scalar())
                            ))),
                            new Property("skip_count_walker", new Scalar(Scalar.Boolean)),
                            new Property("requireJSModules", new Sequence(new Scalar())),
                            new Property("routerEnabled", new Scalar(Scalar.Boolean))
                        )))
                    )))
                ))
            )
        )))
    );

}
