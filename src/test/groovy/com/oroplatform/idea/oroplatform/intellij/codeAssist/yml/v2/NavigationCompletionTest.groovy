package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class NavigationCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.NAVIGATION
    }


    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["navigation"]
        )
    }

    def void "test: suggest oro_menu_config properties"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    <caret>
            """.stripMargin(),

            ["templates", "items", "tree"]
        )
    }

    def void "test: suggest template properties"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    templates:
            |      some_menu:
            |        <caret>
            """.stripMargin(),

            ["template", "clear_matcher", "depth", "current_as_link", "current_class", "ancestor_class", "first_class",
             "last_class", "compressed", "block", "root_class", "is_dropdown"]
        )
    }

    def void "test: suggest items properties"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    items:
            |      some_item:
            |        <caret>
            """.stripMargin(),

            ["acl_resource_id", "translate_domain", "translate_parameters", "translate_disabled", "label", "name", "uri", "route",
             "route_parameters", "attributes", "linkAttributes", "label_attributes", "children_attributes",
             "show_non_authorized", "display", "display_children"]
        )
    }

    def void "test: suggest tree item properties"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    tree:
            |      some_menu:
            |        <caret>
            """.stripMargin(),

            ["type", "merge_strategy", "extras", "children"]
        )
    }

    def void "test: suggest properties for linkAttributes"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    items:
            |      some_item:
            |        linkAttributes:
            |          <caret>
            """.stripMargin(),

            ["class", "id", "target", "type"]
        )

    }

    def void "test: suggest tree extras properties"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    tree:
            |      some_menu:
            |        extras:
            |          <caret>
            """.stripMargin(),

            ["brand", "brandLink"]
        )
    }

    def void "test: suggest tree children at the first level"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    items:
            |      item1: ~
            |      item2: ~
            |      item3: ~
            |    tree:
            |      some_menu:
            |        children:
            |          <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest position as property of tree children"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    tree:
            |      some_menu:
            |        children:
            |          <caret>
            """.stripMargin(),

            ["position"]
        )
    }

    def void "test: suggest tree children at the second level"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    items:
            |      item1: ~
            |      item2: ~
            |      item3: ~
            |    tree:
            |      some_menu:
            |        children:
            |          some_child:
            |            children:
            |              <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest tree children at the third level"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    items:
            |      item1: ~
            |      item2: ~
            |      item3: ~
            |    tree:
            |      some_menu:
            |        children:
            |          some_child:
            |            children:
            |              some_child2:
            |                children:
            |                  <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest navigation_elements properties"() {
        suggestions(
            """
            |navigation:
            |  navigation_elements:
            |    some_element:
            |      <caret>
            """.stripMargin(),

            ["routes", "default"]
        )
    }
}
