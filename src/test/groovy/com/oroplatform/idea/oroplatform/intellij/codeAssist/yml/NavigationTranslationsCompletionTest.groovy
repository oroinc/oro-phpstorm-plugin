package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas

class NavigationTranslationsCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.NAVIGATION
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
            |   'domain1' => array(
            |       'trans1' => 'en trans1',
            |       'trans2' => 'en trans2',
            |   ),
            |   'domain2' => array(
            |       'trans3' => 'en trans3',
            |       'trans4' => 'en trans4',
            |   ),
            |));
            """.stripMargin()
        )
    }

    def void "test: suggest translation domains"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    some_item:
            |      translateDomain: <caret>
            """.stripMargin(),

            ["domain1", "domain2"]
        )
    }
}
