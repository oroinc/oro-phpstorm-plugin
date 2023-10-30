package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ActionsCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
    }

    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["operations", "action_groups"]
        )
    }

    def void "test: suggest properties for operation"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    <caret>
            """.stripMargin(),

            ["name", "extends", "label", "substitute_operation", "enabled", "entities", "for_all_entities",
             "exclude_entities", "routes", "groups", "datagrids", "for_all_datagrids", "exclude_datagrids", "order",
             "acl_resource", "frontend_options", "preactions", "preconditions", "attributes", "datagrid_options",
             "form_options", "form_init", "conditions", "actions", "button_options"]
        )
    }

    def void "test: suggest groups defined in other operations"() {
        suggestions(
            """
            |operations:
            |  some_op1:
            |    groups: [<caret>]
            |  some_op2:
            |    groups: [group1, group2]
            """.stripMargin(),

            ["group1", "group2"]
        )
    }

    def void "test: suggest properties for button_options"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    button_options:
            |      <caret>
            """.stripMargin(),

            ["icon", "class", "group", "template", "data", "page_component_module", "page_component_options"]
        )
    }

    def void "test: suggest properties for frontend_options"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    frontend_options:
            |      <caret>
            """.stripMargin(),

            ["template", "title", "options", "confirmation", "show_dialog"]
        )
    }

    def void "test: suggest properties for attributes"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    attributes:
            |      attr_name:
            |        <caret>
            """.stripMargin(),

            ["type", "label", "property_path", "options"]
        )
    }

    def void "test: suggest properties for attributes.options"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    attributes:
            |      attr_name:
            |        options:
            |          <caret>
            """.stripMargin(),

            ["class", "multiple"]
        )
    }

    def void "test: suggest properties for datagrid_options"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    datagrid_options:
            |      <caret>
            """.stripMargin(),

            ["mass_action_provider", "mass_action", "data"]
        )
    }

    def void "test: suggest properties for datagrid_options.data"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    datagrid_options:
            |      data:
            |        <caret>
            """.stripMargin(),

            ["entity", "importProcessor", "importJob", "importValidateProcessor", "importValidateJob", "exportProcessor",
             "exportJob", "exportLabel", "exportTemplateProcessor", "exportTemplateJob", "exportTemplateLabel"]
        )
    }

    def void "test: suggest batch jobs for datagrid_options.data.importJob"() {

        def batchJob = randomIdentifier("batchJob")

        configureByText("Resources/config/batch_jobs.yml",
            """
            |connector:
            |  jobs:
            |    $batchJob: ~
            """.stripMargin()
        )

        suggestions(
            """
            |operations:
            |  some_op:
            |    datagrid_options:
            |      data:
            |        importJob: <caret>
            """.stripMargin(),

            [batchJob]
        )
    }

    def void "test: suggest properties for form_options"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    form_options:
            |      <caret>
            """.stripMargin(),

            ["attribute_fields", "attribute_default_values"]
        )
    }

    def void "test: suggest default operations"() {
        suggestions(
            """
            |operations:
            |  <caret>
            """.stripMargin(),

            ["UPDATE", "DELETE"]
        )
    }

    def void "test: suggest default operations in extends"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    extends: <caret>
            """.stripMargin(),

            ["UPDATE", "DELETE"]
        )
    }

    def void "test: suggest properties for action_groups"() {
        suggestions(
            """
            |action_groups:
            |  some_group:
            |    <caret>
            """.stripMargin(),

            ["parameters", "conditions", "actions", "acl_resource"]
        )
    }

    def void "test: suggest properties for action_groups.parameters"() {
        suggestions(
            """
            |action_groups:
            |  some_group:
            |    parameters:
            |      param1:
            |        <caret>
            """.stripMargin(),

            ["type", "default", "required", "message"]
        )
    }

    def void "test: suggest properties for attribute_fields"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    form_options:
            |      attribute_fields:
            |        field1:
            |          <caret>
            |
            """.stripMargin(),

            ["form_type", "options"]
        )
    }

    def void "test: suggest attributes for attribute_fields"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    attributes:
            |      attr1: ~
            |      attr2: ~
            |    form_options:
            |      attribute_fields:
            |        <caret>
            |  some_op2:
            |    attributes:
            |      attr3: ~
            |
            """.stripMargin(),

            ["attr1", "attr2"],
            ["attr3"]
        )
    }

    def void "test: suggest attributes for attribute_default_values"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    form_options:
            |      attribute_fields:
            |        attr1: ~
            |        attr2: ~
            |      attribute_default_values:
            |        <caret>
            |
            """.stripMargin(),

            ["attr1", "attr2"]
        )
    }

    def void "test: suggest datagrids for datagrid related properties"() {
        configureByText(
            "some/"+SchemasV1.FilePathPatterns.DATAGRID,
            """
            |datagrid:
            |  grid1: ~
            |  grid2: ~
            """.stripMargin()
        )

        suggestions(
            """
            |operations:
            |  some_op:
            |    datagrids:
            |      - <caret>
            |
            """.stripMargin(),

            ["grid1", "grid2"]
        )
    }

    def void "test: suggest acl_resources for acl related properties"() {
        configureByText(
            "some/"+SchemasV1.FilePathPatterns.ACL,
            """
            |acl1: ~
            |acl2: ~
            """.stripMargin()
        )

        suggestions(
            """
            |operations:
            |  some_op:
            |    acl_resource: <caret>
            |
            """.stripMargin(),

            ["acl1", "acl2"]
        )
    }

    def void "test: suggest datagrid for mass_action"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    datagrid_options:
            |      mass_action:
            |        <caret>
            |
            """.stripMargin(),

            ["type", "label", "route"]
        )
    }

    def void "test: suggest operation from different files"() {
        configureByText(
            "some/"+SchemasV1.FilePathPatterns.ACTIONS,
            """
            |operations:
            |  op1: ~
            |  op2: ~
            """.stripMargin()
        )

        suggestions(
            """
            |operations:
            |  op3:
            |    extends: <caret>
            |
            """.stripMargin(),

            ["op1", "op2"]
        )
    }

    def void "test: suggest operation from the same file"() {
        suggestions(
            """
            |operations:
            |  op4: ~
            |  op3:
            |    extends: <caret>
            |
            """.stripMargin(),

            ["op4"]
        )
    }
}
