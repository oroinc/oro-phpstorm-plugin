package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class SystemConfigurationCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.SYSTEM_CONFIGURATION
    }

    def void "test: suggest root node"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["oro_system_configuration"]
        )
    }

    def void "test: suggest properties under system configuration"() {
        suggestions(
            """
            |oro_system_configuration:
            |  <caret>
            """.stripMargin(),

            ["groups", "fields", "tree"]
        )
    }

    def void "test: suggest properties under groups"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      <caret>
            """.stripMargin(),

            ["title", "icon", "priority", "description", "tooltip", "configurator", "page_reload"]
        )
    }

    def void "test: suggest properties under fields"() {
        suggestions(
            """
            |oro_system_configuration:
            |  fields:
            |    field:
            |      <caret>
            """.stripMargin(),

            ["type", "data_type", "tooltip", "acl_resource", "priority", "ui_only", "options"]
        )
    }

    def void "test: suggest groups in first level of tree"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |  fields:
            |    field1: ~
            |    field2: ~
            |  tree:
            |    tree1:
            |      <caret>
            """.stripMargin(),

            ["group1", "group2"],
            ["field1", "field2"]
        )
    }

    def void "test: suggest children for tree element in first level"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |  tree:
            |    tree1:
            |      group1:
            |        <caret>
            """.stripMargin(),

            ["children", "priority"]
        )
    }

    def void "test: suggest groups for tree element in second level"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |  tree:
            |    tree1:
            |      group1:
            |        children:
            |          <caret>
            """.stripMargin(),

            ["group1", "group2"]
        )
    }

    def void "test: suggest groups for tree element in third level"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |    group3: ~
            |  tree:
            |    tree1:
            |      group1:
            |        children:
            |          group2:
            |            children:
            |              <caret>
            """.stripMargin(),

            ["group1", "group2", "group3"]
        )
    }


    def void "test: suggest fields for tree element in first level"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |    group3: ~
            |  fields:
            |    field1: ~
            |    field2: ~
            |    field3: ~
            |  tree:
            |    tree1:
            |      group1:
            |        children:
            |          - <caret>
            """.stripMargin(),

            ["field1", "field2", "field3"],
            ["group1", "group2", "group3"]
        )
    }

    def void "test: suggest fields for tree element in second level"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |    group3: ~
            |  fields:
            |    field1: ~
            |    field2: ~
            |    field3: ~
            |  tree:
            |    tree1:
            |      group1:
            |        children:
            |          group2:
            |            children:
            |              - <caret>
            """.stripMargin(),

            ["field1", "field2", "field3"],
            ["group1", "group2", "group3"]
        )
    }
}
