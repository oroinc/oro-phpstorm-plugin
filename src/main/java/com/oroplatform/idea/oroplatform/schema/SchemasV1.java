package com.oroplatform.idea.oroplatform.schema;

import com.oroplatform.idea.oroplatform.Functions;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ElementRootDirsFinder;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.LayoutAssetsCssOutputChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PublicResourcesRootDirsFinder;
import com.oroplatform.idea.oroplatform.symfony.Service;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public class SchemasV1 {

    public static class FilePathPatterns {
        public final static String ACL = "Resources/config/acl.yml";
        public final static String ENTITY = "Resources/config/oro/entity.yml";
        public final static String DATAGRID = "Resources/config/datagrid.yml";
        public final static String WORKFLOW = "Resources/config/workflow.yml";
        public final static String SYSTEM_CONFIGURATION = "Resources/config/system_configuration.yml";
        public final static String API = "Resources/config/oro/api.yml";
        public final static String ACTIONS = "Resources/config/oro/actions.yml";
        public final static String DASHBOARD = "Resources/config/dashboard.yml";
        public final static String NAVIGATION = "Resources/config/navigation.yml";
        public final static String SEARCH = "Resources/config/search.yml";
        public final static String LAYOUT_UPDATE = "Resources/views/layouts/**/*.yml";
        public final static String THEME = "Resources/views/layouts/*/theme.yml";
        public final static String ASSETS = "Resources/views/layouts/*/config/assets.yml";
        public final static String LAYOUT_REQUIRE_JS = "Resources/views/layouts/*/config/requirejs.yml";
        public final static String REQUIRE_JS = "Resources/config/requirejs.yml";
        public final static String IMAGES = "Resources/views/layouts/*/config/images.yml";
    }

    static final Collection<Schema> ALL = asList(
        acl(), entity(), datagrid(), workflow(), systemConfiguration(), api(), actions(), dashboard(), navigation(), search(), layoutUpdate(),
        theme(), assets(), requirejs(), images()
    );

    private static Schema acl() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ACL), aclElementProperties());
    }

    @NotNull
    static Container aclElementProperties() {
        return Container.with(
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
                    Property.named("description", Scalars.trans)
                ),
                Container.with(
                    Property.named("type", Scalars.strictChoices("action")),
                    Property.named("label", Scalars.trans).required(),
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
        );
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
                        )).withKeyElement(Scalars.fullEntity(new PropertyPath("oro_entity", "exclusions", "*", "entity").pointsToValue()))
                    ))
                ).allowExtraProperties()
            ).required()
        ));
    }

    private static Schema datagrid() {
        return new Schema(new FilePathMatcher(FilePathPatterns.DATAGRID), Container.with(
                Property.named("imports", Sequence.of(Container.with(
                    Property.named("resource", Scalars.filePath)
                ))),
                Property.named("datagrid",
                    datagridElementProperties()
                )
            )
        );
    }

    @NotNull
    static Container datagridElementProperties() {
        final Element queryJoin = Container.with(
            Property.named("join", Scalars.any).required(),
            Property.named("alias", Scalars.any).required(),
            Property.named("conditionType", Scalars.strictChoices("ON", "WITH")),
            Property.named("condition", Scalars.any)
        );

        return Container.with(
            Container.with(
                Property.named("extended_entity_name", OneOf.from(Scalars.entity, Scalars.any)),
                Property.named("acl_resource", Scalars.acl),
                Property.named("mixins", Sequence.of(Scalars.datagrid)),
                Property.named("source", Container.with(
                    Property.named("type", Scalars.choices("orm", "search")),
                    Property.named("acl_resource", Scalars.acl),
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
                        Property.named("label", Scalars.trans),
                        Property.named("translatable", Scalars.bool),
                        Property.named("data_name", Scalars.any),
                        Property.named("frontend_type", Scalars.choices("string", "html", "date", "time", "datetime", "integer", "number", "decimal", "percent", "currency", "boolean", "array", "simple_array", "row_array", "select", "multi-select", "phone", "relation")),
                        Property.named("choices", OneOf.from(Sequence.of(Scalars.any), Container.with(Property.any(Scalars.any)))),
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
                        Property.named("route", Scalars.route),
                        Property.named("params", Sequence.of(Scalars.any))
                    ).allowExtraProperties()
                )),
                Property.named("actions", Container.with(
                    Container.with(
                        Property.named("label", Scalars.trans),
                        Property.named("type", Scalars.choices("navigate", "ajax", "delete", "ajaxdelete", "frontend")),
                        Property.named("acl_resource", Scalars.acl),
                        Property.named("icon", Scalars.any),
                        Property.named("link", Scalars.any),
                        Property.named("rowAction", Scalars.bool),
                        Property.named("selector", Scalars.any)
                    ).allowExtraProperties()
                )),
                Property.named("mass_action", Container.with(
                    massAction()
                )),
                Property.named("totals", Container.with(
                    Container.with(
                        Property.named("per_page", Scalars.bool),
                        Property.named("hide_if_one_page", Scalars.bool),
                        Property.named("extends", Scalars.any),
                        Property.named("columns", Container.with(
                            Container.with(
                                Property.named("label", Scalars.trans),
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
                        Property.named("route", Scalars.route),
                        Property.named("http_method", Scalars.any),
                        Property.named("headers", Scalars.any),
                        Property.named("default_route_parameters", Container.any),
                        Property.named("query_parameter_names", Sequence.of(Scalars.any))
                    ))
                )),
                Property.named("action_configuration", OneOf.from(Scalars.any, Sequence.of(Scalars.any))),
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
                            Container.with(Property.named("label", Scalars.trans))
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
            ).allowExtraProperties());
    }

    private static Element massAction() {
        return Container.with(
            Property.named("label", Scalars.trans),
            Property.named("type", Scalars.choices("frontend", "merge")),
            Property.named("data_identifier", Scalars.any),
            Property.named("icon", Scalars.any),
            Property.named("selector", Scalars.any),
            Property.named("entity_name", Scalars.any),
            Property.named("data_identifier", Scalars.any),
            Property.named("route", Scalars.route),
            Property.named("frontend_options", Container.any)
        ).allowExtraProperties();
    }

    private static Schema workflow() {
        return new Schema(new WorkflowMatcher("workflow.yml"), workflowElement());
    }

    @NotNull
    static Container workflowElement() {
        final Container acl = Container.with(
            Property.named("update", Scalars.bool),
            Property.named("delete", Scalars.bool)
        );
        final Element entityAcl = OneOf.from(acl, Container.with(acl));

        final Element attributesElement = Scalars.propertiesFromPath(new PropertyPath("workflows", "$this", "attributes").pointsToValue(), "$");
        final Element workflowAttributes = OneOf.from(attributesElement, Sequence.of(attributesElement));
        final Element conditions = conditions(workflowAttributes);

        final Element actions = actions(Sequence.of(conditions), workflowAttributes, 5);

        final Container attribute = Container.with(
            Property.named("type", Scalars.strictChoices("boolean", "bool", "integer", "int", "float", "string", "array", "object", "entity")),
            Property.named("label", Scalars.trans),
            Property.named("entity_acl", entityAcl),
            Property.named("property_path", Scalars.any),
            Property.named("options", Container.with(
                Property.named("class", Scalars.phpClass),
                Property.named("multiple", Scalars.bool)
            ))
        );

        final Container step = Container.with(
            Property.named("label", Scalars.trans),
            Property.named("order", Scalars.integer),
            Property.named("is_final", Scalars.bool),
            Property.named("entity_acl", entityAcl),
            Property.named("allowed_transitions", Sequence.of(choicesFromPropertyPath(new PropertyPath("workflows", "$this", "transitions"))))
        );

        final Container transition = Container.with(
            Property.named("step_to", choicesFromPropertyPath(new PropertyPath("workflows", "$this", "steps"))),
            Property.named("transition_definition", Scalars.propertiesFromPath(new PropertyPath("workflows", "$this", "transition_definitions").pointsToValue())),
            Property.named("is_start", Scalars.bool),
            Property.named("is_hidden", Scalars.bool),
            Property.named("is_unavailable_hidden", Scalars.bool),
            Property.named("acl_resource", Scalars.acl),
            Property.named("acl_message", Scalars.trans),
            Property.named("message", Scalars.trans),
            Property.named("display_type", Scalars.any),
            Property.named("page_template", Scalars.twig),
            Property.named("dialog_template", Scalars.twig),
            Property.named("init_entities", Sequence.of(Scalars.fullEntity)),
            Property.named("init_routes", Sequence.of(Scalars.route)),
            Property.named("init_context_attribute", Scalars.any),
            Property.named("init_datagrids", Sequence.of(Scalars.datagrid)),
            Property.named("frontend_options", Container.with(
                Property.named("class", Scalars.any),
                Property.named("icon", Scalars.any)
            )),
            Property.named("form_options", Container.with(
                Property.named("attribute_fields", Container.with(
                    Property.any(Container.with(
                        Property.named("form_type", Scalars.formType),
                        Property.named("options", Container.any),
                        Property.named("label", Scalars.trans)
                    )).withKeyElement(Scalars.propertiesFromPath(new PropertyPath("workflows", "$this", "attributes").pointsToValue()))
                ))
            )),
            Property.named("form_type", Scalars.formType),
            Property.named("triggers", Sequence.of(Container.with(
                Property.named("entity_class", Scalars.fullEntity),
                Property.named("event", Scalars.strictChoices("create", "update", "delete")),
                Property.named("field", Scalars.field(new PropertyPath("workflows", "$this", "transitions", "$this", "triggers", "$this", "entity_class").pointsToValue())),
                Property.named("queue", Scalars.bool),
                Property.named("require", Scalars.any),
                Property.named("relation", Scalars.field(new PropertyPath("workflows", "$this", "transitions", "$this", "triggers", "$this", "entity_class").pointsToValue())),
                Property.named("queued", Scalars.bool),
                Property.named("cron", Scalars.any),
                Property.named("filter", Scalars.any)
            ))),
            Property.named("label", Scalars.trans)
        );

        final Container transitionDefinition = Container.with(
            Property.named("pre_conditions", conditions).deprecated(),
            Property.named("preconditions", conditions),
            Property.named("conditions", conditions),
            Property.named("post_actions", actions).deprecated(),
            Property.named("preactions", actions),
            Property.named("actions", actions),
            Property.named("init_actions", actions),
            Property.named("form_init", actions)
        );

        final Function<String, String> getSimpleClassName = PhpClassUtil::getSimpleName;

        final Element stepReference = choicesFromPropertyPath(new PropertyPath("workflows", "$this", "steps"));

        return Container.with(
            Property.named("imports", Sequence.of(Container.with(
                Property.named("resource", Scalars.filePath)
            ))),
            Property.named("workflows", Container.with(
                Container.with(
                    Property.named("label", Scalars.trans),
                    Property.named("entity", Scalars.fullEntity),
                    Property.named("entity_attribute", Scalars.any)
                        .withKeyElement(Scalars.any(new DefaultValueDescriptor(new PropertyPath("workflows", "$this", "entity").pointsToValue(), getSimpleClassName.andThen(Functions::snakeCase)))),
                    Property.named("is_system", Scalars.bool),
                    Property.named("start_step", stepReference),
                    Property.named("priority", Scalars.integer),
                    Property.named("exclusive_active_groups", Sequence.of(Scalars.any)),
                    Property.named("exclusive_record_groups", Sequence.of(Scalars.any)),
                    Property.named("defaults", Container.with(
                        Property.named("active", Scalars.bool)
                    ).allowExtraProperties()),
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
                    )),
                    Property.named("entity_restrictions", Container.with(
                        Container.with(
                            Property.named(
                                "attribute",
                                choicesFromPropertyPath(
                                    new PropertyPath("workflows", "$this", "attributes"),
                                    new PropertyPath.Condition(new PropertyPath("type").pointsToValue(), "entity")
                                )
                            ),
                            Property.named("field", Scalars.any),
                            Property.named("mode", Scalars.strictChoices("full", "disallow", "allow")),
                            Property.named("values", Sequence.of(Scalars.any)),
                            Property.named("step", stepReference)
                        )
                    )),
                    Property.named("scopes", Sequence.of(Container.with(
                        Property.any(Scalars.integer).withKeyElement(Scalars.workflowScope)
                    ))),
                    Property.named("datagrids", Sequence.of(Scalars.datagrid)),
                    Property.named("disable_operations", Container.with(
                        Property.any(Sequence.of(Scalars.fullEntity)).withKeyElement(Scalars.operation)
                    ))
                ).allowExtraProperties()
            ))
        );
    }

    private static Element action(Element conditions, Element workflowAttributes, int maxDepth) {
        final Container initializationOptions = Container.with(Property.any(Scalars.any).withKeyElement(Scalars.objectInitializationOptions));
        return OneOf.from(
            initializationOptions,
            Container.with(
                Property.named("parameters", initializationOptions),
                Property.named("actions", maxDepth == 0 ? Container.any : actions(conditions, workflowAttributes, maxDepth - 1)),
                Property.named("conditions", conditions),
                Property.named("break_on_failure", Scalars.bool)
            )
        );
    }

    private static Element actions(Element conditions, Element workflowAttributes, int maxDepth) {
        return Sequence.of(
            Container.with(
                Property.any(OneOf.from(action(conditions, workflowAttributes, maxDepth), workflowAttributes, Container.with(workflowAttributes))).withKeyElement(Scalars.action)
            )
        );
    }

    private static Element conditions(Element workflowAttributes) {
        // use Repeated.atAnyLevel instead of complex recursive structure because of performance
        final Element condition =  Repeated.atAnyLevel(
            Container.with(
                Property.any(Container.any).withKeyElement(Scalars.ifCompositeCondition(Scalars.condition)),
                Property.named("parameters", Container.any),
                Property.named("message", Scalars.trans),
                Property.any(workflowAttributes),
                Property.any(Scalars.any).withKeyElement(Scalars.objectInitializationOptions)
            )
        );

        return Container.with(
            Property.any(condition).withKeyElement(Scalars.condition),
            Property.any(workflowAttributes)
        );
    }

    private static Element choicesFromPropertyPath(PropertyPath propertyPath) {
        return choicesFromPropertyPath(propertyPath, null);
    }

    private static Element choicesFromPropertyPath(PropertyPath propertyPath, PropertyPath.Condition condition) {
        return OneOf.from(
            Scalars.propertiesFromPath(propertyPath.pointsToValue().withCondition(condition != null ? condition.updatePath(path -> path.prepend("*")) : null)),
            Scalars.propertiesFromPath(propertyPath.add("*", "name").pointsToValue().withCondition(condition != null ? condition.updatePath(path -> path.prepend("..")) : null))
        );
    }

    private static Schema systemConfiguration() {
        return new Schema(new FilePathMatcher(FilePathPatterns.SYSTEM_CONFIGURATION), Container.with(
            Property.named("oro_system_configuration", systemConfigurationElementProperties("oro_system_configuration"))
        ));
    }

    @NotNull
    static Container systemConfigurationElementProperties(final String rootElementName) {
        return Container.with(
            Property.named("groups", Container.with(
                Container.with(
                    Property.named("icon", Scalars.any),
                    Property.named("title", Scalars.trans),
                    Property.named("page_reload", Scalars.bool),
                    Property.named("priority", Scalars.integer),
                    Property.named("description", Scalars.trans),
                    Property.named("tooltip", Scalars.any),
                    Property.named("configurator", OneOf.from(Scalars.phpCallback, Sequence.of(Scalars.phpCallback))),
                    Property.named("handler", Scalars.any)
                )
            )),
            Property.named("fields", Container.with(
                Container.with(
                    Property.named("type", Scalars.formType),
                    Property.named("options", Container.any),
                    Property.named("acl_resource", Scalars.acl),
                    Property.named("priority", Scalars.integer),
                    Property.named("ui_only", Scalars.bool),
                    Property.named("data_type", dataType()),
                    Property.named("tooltip", Scalars.any),
                    Property.named("page_reload", Scalars.bool),
                    Property.named("property_path", Scalars.any)
                )
            )),
            Property.named("tree", Container.with(
                systemConfigurationTree(rootElementName, 10)
            )),
            Property.named("api_tree", Container.with(
                Repeated.atAnyLevel(
                    Container.with(
                        Property.any(Container.any).withKeyElement(Scalars.propertiesFromPath(new PropertyPath(rootElementName, "fields").pointsToValue()))
                    )
                )
            ))
        );
    }

    private static Scalar dataType() {
        return Scalars.choices("boolean", "integer", "float", "double", "string", "array");
    }

    private static Element systemConfigurationTree(String rootElementName, int deep) {
        if(deep == 0) return Scalars.any;

        return Container.with(
            Property.any(Container.with(
                Property.named("priority", Scalars.integer),
                Property.named("children", OneOf.from(
                    systemConfigurationTree(rootElementName, deep - 1),
                    Sequence.of(Scalars.propertiesFromPath(new PropertyPath(rootElementName, "fields").pointsToValue()))
                ))
            )).withKeyElement(Scalars.propertiesFromPath(new PropertyPath(rootElementName, "groups").pointsToValue()))
        );
    }

    private static Container apiFilters(PropertyPath entityPropertyPath) {
        return Container.with(
            Property.named("exclusion_policy", Scalars.choices("all", "none")),
            Property.named("fields", Container.with(
                Property.any(Container.with(
                    Property.named("exclude", Scalars.bool),
                    Property.named("description", Scalars.trans),
                    Property.named("property_path", Scalars.any),
                    Property.named("data_type", apiDataType()),
                    Property.named("allow_array", Scalars.bool),
                    Property.named("type", apiDataType()),
                    Property.named("options", Container.any),
                    Property.named("operators", Sequence.of(Scalars.any))
                )).withKeyElement(Scalars.field(entityPropertyPath))
            ))
        );
    }

    static Container apiFields(PropertyPath entityPropertyPath) {
        return Container.with(
            Property.any(Container.with(
                Property.named("exclude", Scalars.bool),
                Property.named("description", Scalars.trans),
                Property.named("property_path", Scalars.any),
                Property.named("data_transformer", OneOf.from(Scalars.service, Scalars.phpClass, Sequence.of(Scalars.callable))),
                Property.named("collapse", Scalars.bool),
                Property.named("form_type", Scalars.apiFormType),
                Property.named("form_type", Scalars.formType(servicesIndex -> Optional.of(servicesIndex.findStandardApiFormTypes()))),
                Property.named("form_options", Container.any),
                Property.named("data_type", apiDataType()),
                Property.named("meta_property", Scalars.bool),
                Property.named("target_class", Scalars.fullEntity),
                Property.named("target_type", Scalars.choices("to-one", "to-many", "collection"))
            ).allowExtraProperties()).withKeyElement(Scalars.field(entityPropertyPath))
        );
    }

    static Element apiDataType() {
        return OneOf.from(dataType(), Scalars.choices("association:manyToOne", "association:manyToMany", "association:multipleManyToOne"));
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
        return new Schema(new FilePathMatcher(FilePathPatterns.API), apiElement("oro_api", new Container(), new Container()));
    }

    @NotNull
    static Container apiElement(final String rootElementName, final Container apiExtension, final Container actionExtension) {
        final Container formOptions = Container.with(
            Property.named("data_class", Scalars.phpClass),
            Property.named("validation_groups", Sequence.of(Scalars.any)),
            Property.named("extra_fields_message", Scalars.any),
            Property.named("mapped", Scalars.bool)
        ).allowExtraProperties();

        final Container orderBy = Container.with(
            Property.any(Scalars.strictChoices("ASC", "DESC")).withKeyElement(Scalars.field(new PropertyPath(rootElementName, "entities", "$this")))
        );

        final Element action = OneOf.from(
            Container.with(
                Property.named("exclude", Scalars.bool),
                Property.named("description", Scalars.trans),
                Property.named("documentation", Scalars.resource("**.md")),
                Property.named("acl_resource", Scalars.acl),
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
                    )).withKeyElement(Scalars.field(new PropertyPath(rootElementName, "entities", "$this")))
                ))
            ).andWith(actionExtension.getProperties()),
            Scalars.bool
        );

        return Container.with(
            Property.named(rootElementName, Container.with(
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
                        Property.named("identifier_field_names", Sequence.of(Scalars.field(new PropertyPath(rootElementName, "entities", "$this")))),
                        Property.named("delete_handler", Scalars.service),
                        Property.named("form_type", Scalars.formType),
                        Property.named("form_options", formOptions),
                        Property.named("fields", apiFields(new PropertyPath(rootElementName, "entities", "$this"))),
                        Property.named("filters", apiFilters(new PropertyPath(rootElementName, "entities", "$this"))),
                        Property.named("sorters", apiSorters(new PropertyPath(rootElementName, "entities", "$this"))),
                        Property.named("actions", OneOf.from(Scalars.bool, Container.with(
                            Property.named("get", action),
                            Property.named("get_list", action),
                            Property.named("create", action),
                            Property.named("update", action),
                            Property.named("delete", action),
                            Property.named("delete_list", action),
                            Property.named("get_subresource", action),
                            Property.named("get_relationship", action),
                            Property.named("update_relationship", action),
                            Property.named("add_relationship", action),
                            Property.named("delete_relationship", action)
                        ))),
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
                                Property.named("filters", apiFilters(new PropertyPath(rootElementName, "entity", "$this"))),
                                Property.named("acl_resource", Scalars.acl)
                            ))
                        ))
                    ).allowExtraProperties()).withKeyElement(Scalars.fullEntity)
                )),
                Property.named("relations", Container.with(
                    Property.any(Container.with(
                        Property.named("fields", apiFields(new PropertyPath(rootElementName, "relations", "$this"))),
                        Property.named("filters", apiFilters(new PropertyPath(rootElementName, "relations", "$this"))),
                        Property.named("sorters", apiSorters(new PropertyPath(rootElementName, "relations", "$this")))
                    ).allowExtraProperties()).withKeyElement(Scalars.fullEntity)
                ))
            ).andWith(apiExtension.getProperties()))
        ).allowExtraProperties();
    }

    private static Schema actions() {
        final Element conditions = Repeated.atAnyLevel(
            Container.with(Property.any(Scalars.any).withKeyElement(Scalars.condition))
        );
        final Element actions = Sequence.of(Container.with(
            Property.any(Container.any).withKeyElement(Scalars.action)
        ));
        final Element operations = OneOf.from(Scalars.choices("UPDATE", "DELETE"), Scalars.operation);
        final Element acl = OneOf.from(Scalars.acl, Sequence.of(Scalars.acl));

        final Function<String, Predicate<Service>> importExportProcessorPredicate = processorType -> {
            return service -> service.getTags().stream().anyMatch(tag -> "oro_importexport.processor".equals(tag.getName()) && processorType.equals(tag.getType()));
        };

        return new Schema(new FilePathMatcher(FilePathPatterns.ACTIONS), Container.with(
            Property.named("operations", Container.with(
                Property.any(
                    Container.with(
                        Property.named("name", Scalars.any),
                        Property.named("extends", operations),
                        Property.named("label", Scalars.trans).required(),
                        Property.named("substitute_operation", operations),
                        Property.named("button_options", Container.with(
                            Property.named("icon", Scalars.any),
                            Property.named("class", Scalars.any),
                            Property.named("group", Scalars.any),
                            Property.named("template", Scalars.twig),
                            Property.named("data", Container.any),
                            Property.named("page_component_module", Scalars.any),
                            Property.named("page_component_options", Scalars.any)
                        )),
                        Property.named("enabled", Scalars.bool),
                        Property.named("entities", Sequence.of(Scalars.fullEntity)),
                        Property.named("for_all_entities", Scalars.bool),
                        Property.named("exclude_entities", Sequence.of(Scalars.fullEntity)),
                        Property.named("routes", Sequence.of(Scalars.route)),
                        Property.named("groups", Sequence.of(Scalars.propertiesFromPath(new PropertyPath("operations", "*", "groups", "*").pointsToValue()))),
                        Property.named("datagrids", Sequence.of(Scalars.datagrid)),
                        Property.named("for_all_datagrids", Scalars.bool),
                        Property.named("exclude_datagrids", Sequence.of(Scalars.datagrid)),
                        Property.named("order", Scalars.integer),
                        Property.named("acl_resource", acl),
                        Property.named("frontend_options", Container.with(
                            Property.named("template", Scalars.twig),
                            Property.named("title", Scalars.trans),
                            Property.named("title_parameters", Container.with(
                                Property.named("allowMaximize", Scalars.bool),
                                Property.named("allowMinimize", Scalars.bool),
                                Property.named("modal", Scalars.bool),
                                Property.named("dblclick", Scalars.choices("maximize", "minimize", "restore")),
                                Property.named("maximizedHeightDecreaseBy", Scalars.choices("minimize-bar", "false")),
                                Property.named("titlebar", Scalars.choices("transparent", "false")),
                                Property.named("width", Scalars.integer),
                                Property.named("height", Scalars.integer),
                                Property.named("minWidth", Scalars.integer)
                            )),
                            Property.named("options", Container.any),
                            Property.named("confirmation", OneOf.from(Scalars.trans, Container.with(
                                Property.named("title", Scalars.trans),
                                Property.named("message", Scalars.trans),
                                Property.named("message_parameters", Container.any)
                            ))),
                            Property.named("show_dialog", Scalars.bool)
                        ).allowExtraProperties()),
                        Property.named("preactions", actions),
                        Property.named("preconditions", conditions),
                        Property.named("attributes", Container.with(
                            Container.with(
                                Property.named("type", Scalars.strictChoices("bool", "boolean", "int", "integer", "float", "string", "array", "object", "entity")),
                                Property.named("label", Scalars.trans),
                                Property.named("property_path", Scalars.any),
                                Property.named("options", Container.with(
                                    Property.named("class", Scalars.phpClass),
                                    Property.named("multiple", Scalars.bool)
                                ))
                            )
                        )),
                        Property.named("datagrid_options", Container.with(
                            Property.named("mass_action_provider", Scalars.massActionProvider),
                            Property.named("mass_action", massAction()),
                            Property.named("data", Container.with(
                                Property.named("entity", Scalars.fullEntity),
                                Property.named("importProcessor", Scalars.service(importExportProcessorPredicate.apply("import"))),
                                Property.named("importJob", Scalars.batchJob),
                                Property.named("importValidateProcessor", Scalars.service(importExportProcessorPredicate.apply("import_validation"))),
                                Property.named("importValidateJob", Scalars.batchJob),
                                Property.named("exportProcessor", Scalars.service(importExportProcessorPredicate.apply("export"))),
                                Property.named("exportJob", Scalars.batchJob),
                                Property.named("exportLabel", Scalars.trans),
                                Property.named("exportTemplateProcessor", Scalars.service(importExportProcessorPredicate.apply("export_template"))),
                                Property.named("exportTemplateJob", Scalars.batchJob),
                                Property.named("exportTemplateLabel", Scalars.trans)
                            ))
                        )),
                        Property.named("form_options", Container.with(
                            Property.named("attribute_fields", Container.with(
                                Property.any(
                                    Container.with(
                                        Property.named("form_type", Scalars.formType),
                                        Property.named("options", Container.any)
                                    )
                                ).withKeyElement(Scalars.propertiesFromPath(new PropertyPath("operations", "$this", "attributes").pointsToValue()))
                            )),
                            Property.named("attribute_default_values", Container.with(
                                Property.any(Scalars.any)
                                    .withKeyElement(Scalars.propertiesFromPath(new PropertyPath("operations", "$this", "form_options", "attribute_fields").pointsToValue()))
                            ))
                        )),
                        Property.named("form_init", actions),
                        Property.named("conditions", conditions),
                        Property.named("actions", actions),
                        Property.named("replace", OneOf.from(Scalars.any, Sequence.of(Scalars.any))),
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
                            Property.named("message", Scalars.trans)
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

    private static Schema dashboard() {
        return new Schema(new FilePathMatcher(FilePathPatterns.DASHBOARD), dashboardElement("oro_dashboard_config"));
    }

    @NotNull
    static Container dashboardElement(final String rootElement) {
        final Element configuration = Container.with(
            Container.with(
                Property.named("type", Scalars.formType),
                Property.named("options", Container.with(
                    Property.named("required", Scalars.bool),
                    Property.named("label", Scalars.trans)
                ).allowExtraProperties()),
                Property.named("show_on_widget", Scalars.bool),
                Property.named("converter_attributes", OneOf.from(Container.any, Sequence.of(Scalars.any))),
                Property.named("aclClass", Scalars.phpClass),
                Property.named("aclPermission", Scalars.choices("VIEW", "EDIT", "CREATE", "DELETE", "ASSIGN", "SHARE"))
            )
        );

        final Container dataItem = Container.with(
            Property.named("label", Scalars.trans).required(),
            //TODO: what to suggest as data_provider?
            Property.named("data_provider", Scalars.any).required(),
            Property.named("template", Scalars.twig).required(),
            Property.named("acl", Scalars.acl),
            Property.named("enabled", Scalars.bool),
            Property.named("applicable", Scalars.any),
            Property.named("position", Scalars.integer)
        );

        return Container.with(
            Property.named(rootElement, Container.with(
                Property.named("widgets", Container.with(
                    Container.with(
                        Property.named("icon", Scalars.fileRelativeToAppIn("../web", "png", "jpg", "jpeg")),
                        Property.named("label", Scalars.trans),
                        Property.named("description", Scalars.trans),
                        Property.named("acl", Scalars.acl),
                        Property.named("route", OneOf.from(Scalars.route, Scalars.choices("oro_dashboard_grid"))),
                        Property.named("route_parameters", Container.any),
                        Property.named("isNew", Scalars.bool),
                        Property.named("items", Container.with(
                            Container.with(
                                Property.named("label", Scalars.trans),
                                Property.named("acl", Scalars.acl),
                                Property.named("route", Scalars.route),
                                Property.named("position", Scalars.integer),
                                Property.named("icon", Scalars.any),
                                Property.named("class", Scalars.any),
                                Property.named("applicable", Scalars.any),
                                Property.named("enabled", Scalars.bool),
                                Property.named("route_parameters", Container.any)
                            )
                        )),
                        Property.named("configuration", configuration),
                        Property.named("enabled", Scalars.bool),
                        Property.named("applicable", Scalars.any),
                        Property.named("configuration_dialog_options", Container.with(
                            Property.named("resizable", Scalars.bool),
                            Property.named("minWidth", Scalars.integer),
                            Property.named("minHeight", Scalars.integer),
                            Property.named("title", Scalars.any)
                        )),
                        Property.named("data_items", OneOf.from(
                            Container.with(dataItem),
                            Sequence.of(dataItem.andWith(Property.named("name", Scalars.any)))
                        ))
                    )
                )),
                Property.named("widgets_configuration", configuration),
                Property.named("dashboards", Container.with(
                    Container.with(
                        Property.named("twig", Scalars.twig)
                    )
                ))
            ))
        );
    }

    private static Schema navigation() {
        final Container attributes = Container.with(
            Property.named("class", Scalars.any),
            Property.named("id", Scalars.any)
        );

        return new Schema(new FilePathMatcher(FilePathPatterns.NAVIGATION), Container.with(
            Property.named("oro_" +"menu_config", Container.with(
                Property.named("templates", Container.with(
                    Container.with(
                        Property.named("template", Scalars.twig),
                        Property.named("clear_matcher", Scalars.bool),
                        Property.named("depth", Scalars.integer),
                        Property.named("currentAsLink", Scalars.bool),
                        Property.named("currentClass", Scalars.any),
                        Property.named("ancestorClass", Scalars.any),
                        Property.named("firstClass", Scalars.any),
                        Property.named("lastClass", Scalars.any),
                        Property.named("compressed", Scalars.bool),
                        Property.named("block", Scalars.any),
                        Property.named("rootClass", Scalars.any),
                        Property.named("isDropdown", Scalars.bool),
                        Property.named("allow_safe_labels", Scalars.bool)
                    )
                )),
                Property.named("items", Container.with(
                    Container.with(
                        Property.named("aclResourceId", Scalars.acl),
                        Property.named("translateDomain", Scalars.transDomain),
                        Property.named("translateParameters", Container.any),
                        Property.named("label", Scalars.trans),
                        Property.named("name", Scalars.any),
                        Property.named("uri", Scalars.any),
                        Property.named("route", Scalars.route),
                        Property.named("routeParameters", Container.any),
                        Property.named("attributes", attributes),
                        Property.named("linkAttributes", attributes.andWith(
                            Property.named("target", Scalars.any),
                            Property.named("title", Scalars.any),
                            Property.named("rel", Scalars.any),
                            Property.named("type", Scalars.any),
                            Property.named("name", Scalars.any)
                        )),
                        Property.named("labelAttributes", attributes),
                        Property.named("childrenAttributes", attributes),
                        Property.named("showNonAuthorized", Scalars.bool),
                        Property.named("display", Scalars.bool),
                        Property.named("displayChildren", Scalars.bool),
                        Property.named("extras", Container.any)
                    )
                )),
                Property.named("tree", Container.with(
                    Property.any(Container.with(
                        Property.named("type", Scalars.any),
                        Property.named("merge_strategy", Scalars.strictChoices("append", "replace", "move")),
                        Property.named("extras", Container.with(
                            Property.named("brand", Scalars.any),
                            Property.named("brandLink", Scalars.any)
                        )),
                        Property.named("children", navigationTree(10, new PropertyPath("oro_menu_config", "items").pointsToValue()))
                    ))
                ))
            )),
            Property.named("oro_" +"titles", Container.with(
                Property.any(Scalars.any).withKeyElement(Scalars.route)
            )),
            Property.named("oro_" +"navigation_elements", Container.with(
                Container.with(
                    Property.named("routes", Container.with(
                        Property.any(Scalars.bool).withKeyElement(Scalars.route)
                    )),
                    Property.named("default", Scalars.bool)
                )
            ))
        ));
    }

    private static Element navigationTree(int deep, PropertyPath itemsPath) {
        if(deep == 0) return Scalars.any;

        return Container.with(
            Property.any(navigationTree(deep - 1, itemsPath))
                .withKeyElement(Scalars.propertiesFromPath(itemsPath)),
            Property.named("position", Scalars.integer)
        );
    }

    private static Schema search() {
        return new Schema(new FilePathMatcher(FilePathPatterns.SEARCH), searchElementProperties(new PropertyPath()));
    }

    @NotNull
    static Container searchElementProperties(final PropertyPath pathToRoot) {
        final Element targetType = Scalars.strictChoices("text", "integer", "decimal", "datetime");
        final PropertyPath classPropertyPath = pathToRoot.add("$this");
        final Element field = Scalars.field(classPropertyPath);
        final Element targetFields = Sequence.of(field);

        return Container.with(
            Property.any(Container.with(
                Property.named("alias", Scalars.any),
                Property.named("search_template", Scalars.twig),
                Property.named("label", Scalars.trans),
                Property.named("route", Container.with(
                    Property.named("name", Scalars.route),
                    Property.named("parameters", Container.any)
                )),
                Property.named("mode", Scalars.strictChoices("normal", "with_descendants", "only_descendants")),
                Property.named("title_fields", Sequence.of(field)),
                Property.named("fields", Sequence.of(Container.with(
                    Property.named("name", field),
                    Property.named("target_type", targetType),
                    Property.named("target_fields", targetFields),
                    Property.named("relation_type", Scalars.choices("many-to-one", "many-to-many", "one-to-many", "one-to-one")),
                    Property.named("relation_fields", Sequence.of(Container.with(
                        Property.named("name", Scalars.fieldOfFieldTypeClass(classPropertyPath, pathToRoot.add("$this", "fields", "$this", "name").pointsToValue())),
                        Property.named("target_type", targetType),
                        Property.named("target_fields", targetFields)
                    )))
                )))
            )).withKeyElement(Scalars.fullEntity)
        );
    }

    private static Schema layoutUpdate() {
        final Element option = OneOf.from(
            Sequence.of(Scalars.any),
            Container.with(
                Property.named("id", Scalars.any).required(),
                Property.named("optionName", Scalars.any).required(),
                Property.named("optionValue", Scalars.any).required()
            )
        );
        final Scalar importId = Scalars.filePathRelativeToElementIn("imports", 1);

        final FileMatcher matcher = new AndFileMatcher(
            new NotFileMatcher(new FilePathMatcher(FilePathPatterns.THEME)),
            new NotFileMatcher(new FilePathMatcher("Resources/views/layouts/*/config/*.yml")),
            new FilePathMatcher(FilePathPatterns.LAYOUT_UPDATE)
        );

        final Element template = OneOf.from(Scalars.twig("**/Resources/views/layouts/**"), Scalars.relativeFile(new ElementRootDirsFinder(), "twig"));
        final Element setTheme = OneOf.from(
            Sequence.of(Scalars.any),
            Container.with(
                Property.named("themes", OneOf.from(template, Sequence.of(template))).required(),
                Property.named("id", Scalars.any)
            )
        );

        return new Schema(matcher, Container.with(
            Property.named("layout", Container.with(
                Property.named("actions", Sequence.of(Container.with(
                    Property.named("@add", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("id", Scalars.any).required(),
                            Property.named("parentId", Scalars.any).required(),
                            Property.named("blockType", Scalars.any).required(),
                            Property.named("options", Container.any),
                            Property.named("siblingId", Scalars.any),
                            Property.named("prepend", Scalars.bool)
                        )
                    )),
                    Property.named("@addTree", Container.with(
                        Property.named("items", Container.with(
                            Container.with(
                                Property.named("blockType", Scalars.any),
                                Property.named("options", Container.any)
                            )
                        )),
                        Property.named("tree", Container.with(
                            Property.any(layoutUpdateTree(10))
                        ))
                    )),
                    Property.named("@remove", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("id", Scalars.any).required()
                        )
                    )),
                    Property.named("@move", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("id", Scalars.any).required(),
                            Property.named("parentId", Scalars.any),
                            Property.named("siblingId", Scalars.any),
                            Property.named("prepend", Scalars.bool)
                        )
                    )),
                    Property.named("@addAlias", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("alias", Scalars.any).required(),
                            Property.named("id", Scalars.any).required()
                        )
                    )),
                    Property.named("@removeAlias", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("alias", Scalars.any).required()
                        )
                    )),
                    Property.named("@setOption", option),
                    Property.named("@appendOption", option),
                    Property.named("@subtractOption", option),
                    Property.named("@replaceOption", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("id", Scalars.any).required(),
                            Property.named("optionName", Scalars.any).required(),
                            Property.named("oldOptionValue", Scalars.any).required(),
                            Property.named("newOptionValue", Scalars.any).required()
                        )
                    )),
                    Property.named("@removeOption", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("id", Scalars.any).required(),
                            Property.named("optionName", Scalars.any).required()
                        )
                    )),
                    Property.named("@changeBlockType", OneOf.from(
                        Sequence.of(Scalars.any),
                        Container.with(
                            Property.named("id", Scalars.any).required(),
                            Property.named("blockType", Scalars.any).required(),
                            Property.named("optionsCallback", Scalars.any)
                        )
                    )),
                    Property.named("@setBlockTheme", setTheme),
                    Property.named("@setFormTheme", setTheme),
                    Property.named("@clear", Container.any)
                ).allowExtraProperties())),
                Property.named("conditions", Container.any),
                Property.named("imports", Sequence.of(OneOf.from(importId, Container.with(
                    Property.named("id", importId),
                    Property.named("namespace", Scalars.any),
                    Property.named("root", Scalars.any)
                ))))
            ))
        ));
    }

    private static Element layoutUpdateTree(int deep) {
        if(deep == 0) return Container.any;

        return Container.with(
            Property.any(layoutUpdateTree(deep - 1)).withKeyElement(Scalars.propertiesFromPath(new PropertyPath("layout", "actions", "$this", "$this", "items").pointsToValue()))
        );
    }

    private static Schema theme() {
        return new Schema(new FilePathMatcher(FilePathPatterns.THEME), Container.with(
            Property.named("label", Scalars.trans),
            Property.named("logo", Scalars.filePathRelativeToAppIn("../web")),
            Property.named("screenshot", Scalars.filePathRelativeToAppIn("../web")),
            Property.named("directory", Scalars.any),
            Property.named("parent", Scalars.filePathRelativeToElementIn("..", 1)),
            Property.named("groups", OneOf.from(Scalars.any, Sequence.of(Scalars.any)))
        ).allowExtraProperties());
    }

    private static Schema assets() {
        return new Schema(new FilePathMatcher(FilePathPatterns.ASSETS), Container.with(
            Property.named("styles", Container.with(
                Property.named("inputs", Sequence.of(Scalars.file(new PublicResourcesRootDirsFinder(), "css", "less", "sass"))),
                Property.named("output", Scalars.choices(new LayoutAssetsCssOutputChoicesProvider())),
                Property.named("filters", Sequence.of(Scalars.assetsFilter))
            ))
        ));
    }

    private static Schema requirejs() {
        return new Schema(new OrFileMatcher(new FilePathMatcher(FilePathPatterns.LAYOUT_REQUIRE_JS), new FilePathMatcher(FilePathPatterns.REQUIRE_JS)), Container.with(
            Property.named("config", Container.with(
                Property.named("build_path", Scalars.any),
                Property.named("shim", Container.with(
                    Property.any(
                        Container.with(
                            Property.named("deps", Sequence.of(Scalars.requirejs)),
                            Property.named("exports", Sequence.of(Scalars.any))
                        )
                    ).withKeyElement(Scalars.propertiesFromPath(new PropertyPath("config", "paths").pointsToValue()))
                )),
                Property.named("map", Container.with(
                    Property.any(Container.with(
                        Property.any(Scalars.requirejs)
                    )).withKeyElement(Scalars.requirejs)
                )),
                Property.named("paths", Container.with(
                    Property.any(Scalars.file(new PublicResourcesRootDirsFinder(), "js"))
                )),
                Property.named("appmodules", Sequence.of(Scalars.requirejs))
            )),
            Property.named("build", Container.with(
                Property.named("paths", Container.with(
                    Property.any(Scalars.any)
                ))
            ))
        ));
    }

    private static Schema images() {
        return new Schema(new FilePathMatcher(FilePathPatterns.IMAGES), Container.with(
            Property.named("types", Container.with(
                Container.with(
                    Property.named("label", Scalars.trans),
                    Property.named("dimensions", Sequence.of(Scalars.any)),
                    Property.named("max_number", Scalars.integer)
                )
            ))
        ));
    }
}
