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
            |  </services>
            |</container>
          """.stripMargin()
        )
    }

    def void "test: suggest condition"() {
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
}
