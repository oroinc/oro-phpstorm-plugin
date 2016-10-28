package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class ActionsDicCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
    }

    def form1 = randomIdentifier("form1")
    def massActionProvider1 = randomIdentifier("massActionProvider1")
    def massActionProvider2 = randomIdentifier("massActionProvider2")
    def unknownService = randomIdentifier("unknown")
    def condition1 = randomIdentifier("condition1")
    def action1 = randomIdentifier("action1")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="service1">
            |      <tag name="oro_action.datagrid.mass_action_provider" alias="$massActionProvider1"/>
            |    </service>
            |    <service id="service2">
            |      <tag name="xxx" alias="$unknownService"/>
            |    </service>
            |    <service id="service6">
            |      <tag name="form.type" alias="$form1"/>
            |    </service>
            |    <service id="service7">
            |      <tag name="oro_workflow.condition" alias="$condition1"/>
            |    </service>
            |    <service id="service8">
            |      <tag name="oro_workflow.action" alias="$action1"/>
            |    </service>
            |  </services>
            |</container>
          """.stripMargin()
        )

        configureByText("Resources/config/services.yml",
            """
            |services:
            |  service3:
            |    tags:
            |      - { name: oro_action.datagrid.mass_action_provider, alias: $massActionProvider2 }
            """.stripMargin()
        )
    }

    def void "test: suggest mass_action_provider"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    datagrid_options:
            |      mass_action_provider: <caret>
            |
            """.stripMargin(),

            [massActionProvider1, massActionProvider2],
            [unknownService, form1]
        )
    }

    def void "test: suggest form_type"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    form_options:
            |      attribute_fields:
            |        field1:
            |          form_type: <caret>
            |
            """.stripMargin(),

            [form1]
        )
    }

    def void "test: suggest conditions in preconditions property"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    preconditions:
            |      <caret>
            |
            """.stripMargin(),

            ["@$condition1"],
            ["@$action1"]
        )
    }

    def void "test: suggest conditions in conditions property"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    conditions:
            |      <caret>
            |
            """.stripMargin(),

            ["@$condition1"],
            ["@$action1"]
        )
    }

    def void "test: suggest actions in preactions property"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    preactions:
            |      - <caret>
            |
            """.stripMargin(),

            ["@$action1"],
            ["@$condition1"]
        )
    }
}
