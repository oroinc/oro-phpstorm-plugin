package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class DashboardCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.DASHBOARD
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["oro_dashboard_config"]
        )
    }

    def void "test: suggest oro_dashboard_config properties"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  <caret>
            """.stripMargin(),
            ["widgets", "dashboards", "widgets_configuration"]
        )
    }

    def void "test: suggest widget properties"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      <caret>
            """.stripMargin(),
            ["icon", "label", "description", "acl", "route", "route_parameters", "isNew", "items", "configuration"]
        )
    }

    def void "test: suggest widgets_configuration properties"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  widgets_configuration:
            |    param:
            |      <caret>
            """.stripMargin(),
            ["type", "options", "show_on_widget"]
        )
    }

    def void "test: suggest properties for widget configuration"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      configuration:
            |        param:
            |          <caret>
            """.stripMargin(),
            ["type", "options", "show_on_widget"]
        )
    }

    def void "test: suggest properties for dashboards"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  dashboards:
            |    some_dashboard:
            |      <caret>
            """.stripMargin(),
            ["twig"]
        )
    }

    def void "test: suggest oro_dashboard_grid as route"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      route: <caret>
            """.stripMargin(),
            ["oro_dashboard_grid"]
        )
    }

    def void "test: suggest items for widget"() {
        suggestions(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      items:
            |        some_item:
            |          <caret>
            """.stripMargin(),
            ["label", "route", "acl", "position"]
        )
    }
}
