package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class SystemConfigurationDicCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.SYSTEM_CONFIGURATION
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="from1_id">
            |      <tag name="form.type" alias="form1"/>
            |    </service>
            |    <service id="form2_id">
            |      <tag name="form.type" alias="form2"/>
            |    </service>
            |    <service id="some_service">
            |      <tag name="xxx" alias="some"/>
            |    </service>
            |  </services>
            |</container>
          """.stripMargin()
        )

        configureByText("Resources/config/services.yml",
            """
            |services:
            |  form3_id:
            |    tags:
            |      - { name: form.type, alias: form3 }
            |  some_service_2:
            |    tags:
            |      - { name: xxx, alias: some2 }
            """.stripMargin()
        )
    }

    def void "test: suggest form types from xml file for field type"() {
        suggestions(
            """
            |oro_system_configuration:
            |  fields:
            |    field:
            |      type: <caret>
            """.stripMargin(),

            ["form1", "form2"],
            ["some"]
        )
    }

    def void "test: suggest form types from yml file for field type"() {
        suggestions(
            """
            |oro_system_configuration:
            |  fields:
            |    field:
            |      type: <caret>
            """.stripMargin(),

            ["form3"],
            ["some2"]
        )
    }
}
