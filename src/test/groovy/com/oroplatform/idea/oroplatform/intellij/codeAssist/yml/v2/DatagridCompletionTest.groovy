package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class DatagridCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.DATAGRID
    }

    def void "test: suggest datagrids as top property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["datagrids", "imports"]
        )
    }

    def void "test: suggest datagrids properties"() {
        suggestions(
            """
            |datagrids:
            |  some_grid:
            |    <caret>
            """.stripMargin(),

            ["extended_entity_name", "mixins", "source", "columns", "sorters", "filters", "properties", "actions", "action_configuration", "options", "mass_action", "totals", "inline_editing", "acl_resource"]
        )
    }

    def void "test: suggest datagrids as mixins"() {
        def grid1 = randomIdentifier("grid1")
        def grid2 = randomIdentifier("grid2")

        configureByText(
            "some/"+SchemasV2.FilePathPatterns.DATAGRID,
            """
            |datagrids:
            |  $grid1: ~
            |  $grid2: ~
            """.stripMargin()
        )

        suggestions(
            """
            |datagrids:
            |  grid3:
            |    mixins:
            |      - <caret>
            """.stripMargin(),

            [grid1, grid2]
        )
    }

    def void "test: suggest datagrid imports resource"() {
        suggestions(
            """
            |imports:
            | - { <caret> }
            """.stripMargin(),

            ["resource"]
        )
    }
}
