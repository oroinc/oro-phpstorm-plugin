package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class SearchCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.SEARCH
    }

    def void "test: suggest entities properties"() {
        suggestions(
            """
            |stdClass:
            |  <caret>
            """.stripMargin(),
            ["alias", "title_fields", "search_template", "route", "fields", "label", "mode"]
        )
    }

    def void "test: suggest route properties"() {
        suggestions(
            """
            |stdClass:
            |  route:
            |    <caret>
            """.stripMargin(),
            ["name", "parameters"]
        )
    }

    def void "test: suggest field properties"() {
        suggestions(
            """
            |stdClass:
            |  fields:
            |    -
            |      <caret>
            """.stripMargin(),
            ["name", "target_type", "target_fields", "relation_type", "relation_fields"]
        )
    }

    def void "test: suggest more field properties"() {
        suggestions(
            """
            |stdClass:
            |  fields:
            |    -
            |      name: some
            |      <caret>
            """.stripMargin(),
            ["target_type", "target_fields", "relation_type", "relation_fields"]
        )
    }

    def void "test: suggest target_type"() {
        suggestions(
            """
            |stdClass:
            |  fields:
            |    -
            |      target_type: <caret>
            """.stripMargin(),
            ["text", "integer", "decimal", "datetime"]
        )
    }

    def void "test: suggest relation_type"() {
        suggestions(
            """
            |stdClass:
            |  fields:
            |    -
            |      relation_type: <caret>
            """.stripMargin(),
            ["many-to-one", "many-to-many", "one-to-many"]
        )
    }

    def void "test: suggest relation_fields"() {
        suggestions(
            """
            |stdClass:
            |  fields:
            |    -
            |      relation_fields:
            |        -
            |          <caret>
            """.stripMargin(),
            ["name", "target_type", "target_fields"]
        )
    }
}
