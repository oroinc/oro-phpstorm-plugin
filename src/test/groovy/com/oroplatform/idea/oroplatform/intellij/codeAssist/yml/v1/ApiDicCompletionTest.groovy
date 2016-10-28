package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ApiDicCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.API
    }

    def service1 = randomIdentifier("service1")
    def service2 = randomIdentifier("service2")
    def service3 = randomIdentifier("service3")
    def service4 = randomIdentifier("service4")
    def service5 = randomIdentifier("service5")

    @Override
    protected void setUp() throws Exception {
        super.setUp()


        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="$service1"></service>
            |    <service id="$service2"></service>
            |    <service id="$service3"></service>
            |  </services>
            |</container>
          """.stripMargin()
        )

        configureByText("Resources/config/services.yml",
            """
            |services:
            |  $service4:
            |    class: ~
            |  $service5:
            |    class: ~
            """.stripMargin()
        )
    }

    def void "test: suggest services from xml files as delete_handler"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      delete_handler: <caret>
            """.stripMargin(),
            [service1, service2, service3]
        )
    }

    def void "test: suggest services from yml files as delete_handler"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      delete_handler: <caret>
            """.stripMargin(),
            [service4, service5]
        )
    }
}
