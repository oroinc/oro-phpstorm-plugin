package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


class ImagesCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return "src/Oro/AcmeBundle/Resources/views/layouts/base/config/images.yml"
    }

    def void "test: suggest root properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["types"]
        )
    }

    def void "test: suggest image properties"() {
        suggestions(
            """
            |types:
            |  img1:
            |    <caret>
            """.stripMargin(),
            ["label", "dimensions", "max_number"]
        )
    }
}
