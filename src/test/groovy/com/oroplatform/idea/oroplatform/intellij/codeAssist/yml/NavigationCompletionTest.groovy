package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas

class NavigationCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.NAVIGATION
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["oro_menu_config", "oro_titles"]
        )
    }

    def void "test: suggest oro_menu_config properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  <caret>
            """.stripMargin(),

            ["templates", "items", "tree"]
        )
    }

    def void "test: suggest template properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  templates:
            |    some_menu:
            |      <caret>
            """.stripMargin(),

            ["template", "clear_matcher", "depth", "currentAsLink", "currentClass", "ancestorClass", "firstClass",
             "lastClass", "compressed", "block", "rootClass", "isDropdown"]
        )
    }

    def void "test: suggest items properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    some_item:
            |      <caret>
            """.stripMargin(),

            ["aclResourceId", "translateDomain", "translateParameters", "label", "name", "uri", "route",
             "routeParameters", "attributes", "linkAttributes", "labelAttributes", "childrenAttributes",
             "showNonAuthorized", "display", "displayChildren"]
        )
    }

    def void "test: suggest tree item properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  tree:
            |    some_menu:
            |      <caret>
            """.stripMargin(),

            ["type", "merge_strategy", "extras", "children"]
        )
    }

    def void "test: suggest properties for linkAttributes"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    some_item:
            |      linkAttributes:
            |        <caret>
            """.stripMargin(),

            ["class", "id", "target", "type"]
        )

    }

    def void "test: suggest tree extras properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  tree:
            |    some_menu:
            |      extras:
            |        <caret>
            """.stripMargin(),

            ["brand", "brandLink"]
        )
    }

    def void "test: suggest tree children at the first level"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    item1: ~
            |    item2: ~
            |    item3: ~
            |  tree:
            |    some_menu:
            |      children:
            |        <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest position as property of tree children"() {
        suggestions(
            """
            |oro_menu_config:
            |  tree:
            |    some_menu:
            |      children:
            |        <caret>
            """.stripMargin(),

            ["position"]
        )
    }

    def void "test: suggest tree children at the second level"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    item1: ~
            |    item2: ~
            |    item3: ~
            |  tree:
            |    some_menu:
            |      children:
            |        some_child:
            |          children:
            |            <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest tree children at the third level"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    item1: ~
            |    item2: ~
            |    item3: ~
            |  tree:
            |    some_menu:
            |      children:
            |        some_child:
            |          children:
            |            some_child2:
            |              children:
            |                <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }
}
