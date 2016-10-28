package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class SystemConfigurationDicCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.SYSTEM_CONFIGURATION
    }

    def form1 = randomIdentifier("form1")
    def form2 = randomIdentifier("form2")
    def form3 = randomIdentifier("form3")
    def unknown1 = randomIdentifier("unknown1")
    def unknown2 = randomIdentifier("unknown2")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="from1_id">
            |      <tag name="form.type" alias="$form1"/>
            |    </service>
            |    <service id="form2_id">
            |      <tag name="form.type" alias="$form2"/>
            |    </service>
            |    <service id="some_service">
            |      <tag name="xxx" alias="$unknown1"/>
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
            |      - { name: form.type, alias: $form3 }
            |  some_service_2:
            |    tags:
            |      - { name: xxx, alias: $unknown2 }
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

            [form1, form2],
            [unknown1]
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

            [form3],
            [unknown2]
        )
    }
}
