package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class WorkflowCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.WORKFLOW
    }

    //the rest is tested for v1 version
    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }

    def void "test: support suggestions for imported file"() {
        configureByText(SchemasV2.FilePathPatterns.WORKFLOW,
            """
            |imports:
            |  - { resource: 'imported2.yml' }
            """.stripMargin()
        )

        suggestions(
            "Resources/config/oro/imported2.yml",
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }
}
