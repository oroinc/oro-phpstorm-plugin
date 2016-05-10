package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


public class DatagridCompletionTest extends CompletionTest {

    @Override
    String fileName() {
        return "datagrid.yml"
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

            ["source", "columns", "sorters", "filters", "properties", "actions", "action_configuration", "options"]
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

    def void "test: suggest columns properties"() {
        suggestions(
            """
            |datagrid:
            |  some_grid:
            |    columns:
            |      title:
            |        <caret>
            """.stripMargin(),

            ["label", "frontend_type", "type", "template", "choices"]
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

            ["columns", "default"]
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

            ["entityHint", "entity_pagination"]
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

            ["select", "from", "join", "where", "groupBy"]
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
            |          <caret>
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
}