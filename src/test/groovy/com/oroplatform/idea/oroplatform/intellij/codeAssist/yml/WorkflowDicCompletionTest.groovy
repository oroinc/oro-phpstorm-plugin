package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas

class WorkflowDicCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.WORKFLOW
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="condition1_id">
            |      <tag name="oro_workflow.condition" alias="condition1"/>
            |    </service>
            |    <service id="condition2_id">
            |      <tag name="oro_workflow.condition" alias="condition2|condition2b"/>
            |    </service>
            |    <service id="some_service">
            |      <tag name="xxx" alias="some"/>
            |    </service>
            |    <service id="action1_id">
            |      <tag name="oro_workflow.action" alias="action1"/>
            |    </service>
            |    <service id="action2_id">
            |      <tag name="oro_workflow.action" alias="action2|action2b"/>
            |    </service>
            |  </services>
            |</container>
          """.stripMargin()
        )

        configureByText("Resources/config/services.yml",
            """
            |services:
            |  condition3_id:
            |    tags:
            |      - { name: oro_workflow.condition, alias: condition3 }
            |  action3_id:
            |    tags:
            |      - { name: oro_workflow.action, alias: action3 }
            """.stripMargin()
        )
    }

    def void "test: suggest conditions defined in xml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          <caret>
            """.stripMargin(),
            ["@condition1", "@condition2", "@condition2b"],
            ["@some"]
        )
    }

    def void "test: suggest conditions defined in yml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          <caret>
            """.stripMargin(),
            ["@condition3"]
        )
    }

    def void "test: does not suggest conditions as property value"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          @not: <caret>
            """.stripMargin(),
            [],
            ["@condition1", "@condition2", "@condition2b"]
        )
    }

    def void "test: suggest actions after new line defined in xml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          -
            |            <caret>
            """.stripMargin(),
            ["@action1", "@action2", "@action2b"],
            ["@some"]
        )
    }

    def void "test: suggest actions in the same line defined in xml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - <caret>
            """.stripMargin(),
            ["@action1", "@action2", "@action2b"],
            ["@some"]
        )
    }

    def void "test: suggest actions defined in yml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          -
            |            <caret>
            """.stripMargin(),
            ["@action3"]
        )
    }

    def void "test: complete conditions as keys"() {
        completion(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          2b<caret>
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          @condition2b: <caret>
            """.stripMargin(),
        )
    }

    def void "test: complete quoted conditions as keys"() {
        completion(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '2b<caret>'
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@condition2b': <caret>
            """.stripMargin(),
        )
    }

    def void "test: complete quoted at beginning conditions as keys"() {
        completion(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '2b<caret>
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@condition2b': <caret>
            """.stripMargin(),
        )
    }
}
