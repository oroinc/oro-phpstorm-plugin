package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ActionsTranslationsCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText(
            "some/app/cache/dev/translations/catalogue.en.fff.php",
            """
            |<?php
            |use Symfony\\Component\\Translation\\MessageCatalogue;
            |\$catalogue = new MessageCatalogue('en', array (
            |   'some_domain' => array(
            |       'trans1' => 'en trans1',
            |       'trans2' => 'en trans2',
            |   ),
            |));
            """.stripMargin()
        )
    }

    def void "test: suggest translation messages"() {
        suggestions(
            """
            |operations:
            |  op1:
            |    frontend_options:
            |      confirmation: <caret>
            """.stripMargin(),
            ["trans1", "trans2"]
        )
    }
}
