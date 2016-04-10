package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


class EntityCompletionTest extends CompletionTest {

    def void "test: suggest oro_entity as top property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["oro_entity"]
        )
    }

    def void "test: suggest oro_entity properties"() {
        suggestions(
            """
            |oro_entity:
            |  <caret>
            """.stripMargin(),

            ["exclusions", "entity_alias_exclusions", "entity_aliases"]
        )
    }


    def void "test: suggest class and field in exclusions entry"() {
        suggestions(
            """
            |oro_entity:
            |  exclusions:
            |    - { <caret> }
            """.stripMargin(),

            ["class", "field"]
        )
    }

    def void "test: suggest entity_aliases items properties"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["alias", "plural_alias"]
        )
    }


    private def suggestions(String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions = []) {
        return super.suggestions("entity.yml", contents, expectedSuggestions, unexpectedSuggestions)
    }

    private def completion(String contents, String expected) {
        return super.completion("entity.yml", contents, expected)
    }
}
