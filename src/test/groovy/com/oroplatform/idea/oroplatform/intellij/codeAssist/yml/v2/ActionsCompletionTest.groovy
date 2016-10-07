package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class ActionsCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.ACTIONS
    }

    def void "test: suggest acl_resources for acl related properties"() {
        configureByText(
            "some/"+SchemasV2.FilePathPatterns.ACL,
            """
            |acls:
            |  acl3: ~
            |  acl4: ~
            """.stripMargin()
        )

        suggestions(
            """
            |operations:
            |  some_op:
            |    acl_resource: <caret>
            |
            """.stripMargin(),

            ["acl3", "acl4"]
        )
    }
}
