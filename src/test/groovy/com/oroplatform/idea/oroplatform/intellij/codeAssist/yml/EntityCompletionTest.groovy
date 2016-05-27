package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class EntityCompletionTest extends CompletionTest {

    @Override
    String fileName() {
        return Schemas.FilePathPatternss.ENTITY
    }

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

            ["entity", "field"]
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

}
