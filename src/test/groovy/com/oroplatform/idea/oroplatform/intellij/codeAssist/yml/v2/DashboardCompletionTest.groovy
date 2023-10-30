package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class DashboardCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.DASHBOARD
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["dashboards"]
        )
    }

    def void "test: suggest dashboards properties"() {
        suggestions(
            """
            |dashboards:
            |  <caret>
            """.stripMargin(),
            ["widgets", "dashboards", "widgets_configuration"]
        )
    }

    def void "test: suggest widget properties"() {
        suggestions(
            """
            |dashboards:
            |  widgets:
            |    some_widget:
            |      <caret>
            """.stripMargin(),
            ["icon", "label", "description", "acl", "route", "route_parameters", "isNew", "items", "configuration",
             "enabled", "applicable", "configuration_dialog_options", "data_items"]
        )
    }

    def void "test: suggest configuration_dialog_options properties"() {
        suggestions(
            """
            |dashboards:
            |  widgets:
            |    some_widget:
            |      configuration_dialog_options:
            |        <caret>
            """.stripMargin(),
            ["resizable", "minWidth", "minHeight", "title"]
        )
    }

    def void "test: suggest data_items properties"() {
        suggestions(
            """
            |dashboards:
            |  widgets:
            |    some_widget:
            |      data_items:
            |        item1:
            |          <caret>
            """.stripMargin(),
            ["label", "data_provider", "template", "acl", "enabled", "applicable", "position"],
            ["name"]
        )
    }

    def void "test: suggest data_items properties as sequence item"() {
        suggestions(
            """
            |dashboards:
            |  widgets:
            |    some_widget:
            |      data_items:
            |        - <caret>
            """.stripMargin(),
            ["label", "data_provider", "template", "acl", "enabled", "applicable", "position", "name"]
        )
    }

    def void "test: suggest properties for widget configuration"() {
        suggestions(
            """
            |dashboards:
            |  widgets:
            |    some_widget:
            |      configuration:
            |        param:
            |          <caret>
            """.stripMargin(),
            ["type", "options", "show_on_widget", "converter_attributes", "aclClass", "aclPermission"]
        )
    }

    def void "test: suggest items for widget"() {
        suggestions(
            """
            |dashboards:
            |  widgets:
            |    some_widget:
            |      items:
            |        some_item:
            |          <caret>
            """.stripMargin(),
            ["label", "route", "acl", "position", "class", "route_parameters", "enabled", "applicable"]
        )
    }

    def void "test: suggest widgets_configuration properties"() {
        suggestions(
            """
            |dashboards:
            |  widgets_configuration:
            |    param:
            |      <caret>
            """.stripMargin(),
            ["type", "options", "show_on_widget", "converter_attributes", "aclClass", "aclPermission"]
        )
    }
}
