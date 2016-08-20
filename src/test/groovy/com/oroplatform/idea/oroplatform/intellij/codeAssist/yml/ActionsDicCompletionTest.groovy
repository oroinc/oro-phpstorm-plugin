package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas

class ActionsDicCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.ACTIONS
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="service1">
            |      <tag name="oro_action.datagrid.mass_action_provider" alias="mass_action_provider1"/>
            |    </service>
            |    <service id="service2">
            |      <tag name="xxx" alias="some"/>
            |    </service>
            |    <service id="service6">
            |      <tag name="form.type" alias="form1"/>
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
            |      - { name: oro_action.datagrid.mass_action_provider, alias: mass_action_provider2 }
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

            ["mass_action_provider1", "mass_action_provider2"],
            ["some", "form1"]
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

            ["form1"]
        )
    }
}
