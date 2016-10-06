package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class ApiCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.API
    }

    def void "test: suggest root property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["api"]
        )
    }

    def void "test: suggest api properties"() {
        suggestions(
            """
            |api:
            |  <caret>
            """.stripMargin(),

            ["entities", "relations"]
        )
    }
}
