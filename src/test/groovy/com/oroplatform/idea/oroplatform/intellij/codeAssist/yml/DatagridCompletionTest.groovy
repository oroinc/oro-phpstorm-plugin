package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas


public class DatagridCompletionTest extends CompletionTest {

    @Override
    String fileName() {
        return Schemas.FilePathPatterns.DATAGRID
    }

    def void "test: suggest datagrid as top property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["datagrid"]
        )
    }

    def void "test: suggest datagrid properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    <caret>
            """.stripMargin(),

            ["extended_entity_name", "source", "columns", "sorters", "filters", "properties", "actions", "action_configuration", "options", "mass_action", "totals", "inline_editing", "acl_resource"]
        )
    }

    def void "test: suggest source properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      <caret>
            """.stripMargin(),

            ["type", "acl_resource", "query"]
        )
    }

    def void "test: suggest source types"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      type: <caret>
            """.stripMargin(),

            ["orm", "search"]
        )
    }

    def void "test: suggest columns properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns:
            |      title:
            |        <caret>
            """.stripMargin(),

            ["label", "translatable", "data_name", "frontend_type", "type", "template", "choices", "renderable",
             "editable", "order", "required", "manageable", "context", "inline_editing"]
        )
    }

    def void "test: suggest sorters properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    sorters:
            |      <caret>
            """.stripMargin(),

            ["columns", "default", "multiple_sorting", "toolbar_sorting"]
        )
    }

    def void "test: suggest 'columns' in 'sorters' property when 'columns' property already exists at the same level as 'sorters'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns: ~
            |    sorters:
            |      <caret>
            """.stripMargin(),

            ["columns", "default", "multiple_sorting", "toolbar_sorting"]
        )
    }

    def void "test: suggest 'columns' in 'sorters' property and skip duplicates when 'columns' property already exists at the same level as 'sorters'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns: ~
            |    sorters:
            |      default: xxx
            |      <caret>
            """.stripMargin(),

            ["columns", "multiple_sorting", "toolbar_sorting"],
            ["default"]
        )
    }

    def void "test: suggest 'columns' in 'sorters' property and skip duplicates for completed property when 'columns' property already exists at the same level as 'sorters'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns: ~
            |    sorters:
            |      default: xxx
            |      <caret>: ~
            """.stripMargin(),

            ["columns", "multiple_sorting", "toolbar_sorting"],
            ["default"]
        )
    }

    def void "test: suggest properties of 'columns' in 'sorters'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    sorters:
            |      columns:
            |        id:
            |          <caret>
            """.stripMargin(),

            ["data_name", "disabled", "type", "apply_callback"]
        )
    }

    def void "test: suggest options properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      <caret>
            """.stripMargin(),

            ["entityHint", "entity_pagination", "toolbarOptions", "export", "rowSelection", "skip_count_walker", "requireJSModules", "routerEnabled"]
        )
    }

    def void "test: suggest query properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        <caret>
            """.stripMargin(),

            ["select", "from", "join", "where", "groupBy", "distinct", "having", "orderBy"]
        )
    }

    def void "test: suggest boolean values for 'distinct' in 'query'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        distinct: <caret>
            """.stripMargin(),

            ["true", "false"]
        )
    }

    def void "test: suggest from query properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        from:
            |          - { <caret> }
            """.stripMargin(),

            ["table", "alias"]
        )
    }

    def void "test: suggest join query properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        join:
            |          <caret>
            """.stripMargin(),

            ["left", "inner"]
        )
    }

    def void "test: suggest left join properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        join:
            |          left:
            |            - { <caret> }
            """.stripMargin(),

            ["join", "alias", "conditionType", "condition"]
        )
    }

    def void "test: suggest conditions for joins"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        join:
            |          left:
            |            - { condition: <caret> }
            """.stripMargin(),

            ["ON", "WITH"]
        )
    }

    def void "test: suggest inner join properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        join:
            |          inner:
            |            - { <caret> }
            """.stripMargin(),

            ["join", "alias", "conditionType", "condition"]
        )
    }

    def void "test: suggest properties in 'where' section"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        where:
            |          <caret>
            """.stripMargin(),

            ["and", "or"]
        )
    }

    def void "test: suggest properties in 'orderBy' section"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        orderBy:
            |          <caret>
            """.stripMargin(),

            ["column", "dir"]
        )
    }

    def void "test: suggest directions in 'orderBy'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      query:
            |        orderBy:
            |          dir: <caret>
            """.stripMargin(),

            ["ASC", "DESC"]
        )
    }

    def void "test: suggest inline_editing in 'columns'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns:
            |      xxx:
            |        inline_editing:
            |          <caret>
            """.stripMargin(),

            ["enable", "editor", "save_api_accessor", "autocomplete_api_accessor", "validation_rules"]
        )
    }

    def void "test: suggest editor properties in 'inline_editing'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns:
            |      xxx:
            |        inline_editing:
            |          editor:
            |            <caret>
            """.stripMargin(),

            ["component", "component_options", "view", "view_options"]
        )
    }

    def void "test: suggest filter properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    filters:
            |      <caret>
            """.stripMargin(),

            ["columns", "default"]
        )
    }

    def void "test: suggest 'columns' properties in `filter`"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    filters:
            |      columns:
            |        name:
            |          <caret>
            """.stripMargin(),

            ["type", "data_name", "filter_condition", "filter_by_having", "enabled", "translatable", "options"]
        )
    }

    def void "test: suggest actions properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    actions:
            |      view:
            |        <caret>
            """.stripMargin(),

            ["label", "type", "acl_resource", "icon", "link", "rowAction", "selector"]
        )
    }

    def void "test: suggest mass_action properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    mass_action:
            |      view:
            |        <caret>
            """.stripMargin(),

            ["label", "type", "data_identifier", "icon", "selector"]
        )
    }

    def void "test: suggest totals properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    totals:
            |      view:
            |        <caret>
            """.stripMargin(),

            ["per_page", "hide_if_one_page", "extends", "columns"]
        )
    }

    def void "test: suggest 'columns' properties of 'total'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    totals:
            |      view:
            |        columns:
            |          name:
            |            <caret>
            """.stripMargin(),

            ["label", "expr", "formatter"]
        )
    }

    def void "test: suggest 'toolbarOptions' properties of 'options'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      toolbarOptions:
            |        <caret>
            """.stripMargin(),

            ["hide", "addResetAction", "addRefreshAction", "addColumnManager", "turnOffToolbarRecordsNumber", "pageSize", "pagination", "placement", "columnManager"]
        )
    }

    def void "test: suggest booleans for 'export' in 'options'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      export: <caret>
            """.stripMargin(),

            ["true", "false"]
        )
    }

    def void "test: suggest label for 'export' in 'options'"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      export:
            |        csv:
            |          <caret>
            """.stripMargin(),

            ["label"]
        )
    }


    def void "test: suggest properties for inline_editing"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    inline_editing:
            |      <caret>
            """.stripMargin(),

            ["enable", "entity_name", "behaviour", "plugin", "default_editors", "cell_editor", "save_api_accessor"]
        )
    }

    def void "test: suggest properties for properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    properties:
            |      id:
            |        <caret>
            """.stripMargin(),

            ["type", "route", "params"]
        )
    }
}