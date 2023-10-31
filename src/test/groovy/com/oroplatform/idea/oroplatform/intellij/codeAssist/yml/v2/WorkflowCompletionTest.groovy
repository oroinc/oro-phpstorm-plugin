package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class WorkflowCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.WORKFLOW
    }

    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }

    def void "test: support suggestions for imported file"() {
        def imported1 = randomIdentifier("imported1")

        configureByText(SchemasV2.FilePathPatterns.WORKFLOW,
            """
            |imports:
            |  - { resource: '${imported1}.yml' }
            """.stripMargin()
        )

        suggestions(
            "Resources/config/oro/${imported1}.yml",
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }
}
