package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class AclCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.ACL
    }

    def void "test: suggest acls as top property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["acls"]
        )
    }

    def void "test: suggest key in new line"() {
        suggestions(
            """
            |acls:
            |  some_id:
            |    <caret>
            """.stripMargin(),

            ["type", "label"]
        )
    }
}
