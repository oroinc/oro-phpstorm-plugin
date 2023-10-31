package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class SearchCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.SEARCH
    }

    def void "test: suggest search as top property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["search"]
        )
    }

    def void "test: suggest entities properties"() {
        suggestions(
            """
            |search:
            |  stdClass:
            |    <caret>
            """.stripMargin(),
            ["alias", "title_fields", "search_template", "route", "fields", "label", "mode"]
        )
    }
}
