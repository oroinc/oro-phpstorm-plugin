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
}
