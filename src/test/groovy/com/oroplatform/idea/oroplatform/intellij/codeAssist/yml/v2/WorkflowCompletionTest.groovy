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
}
