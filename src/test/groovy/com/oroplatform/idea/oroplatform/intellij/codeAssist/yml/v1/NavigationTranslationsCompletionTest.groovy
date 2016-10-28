package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class NavigationTranslationsCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.NAVIGATION
    }

    def trans1 = randomIdentifier("trans1")
    def trans2 = randomIdentifier("trans2")
    def trans3 = randomIdentifier("trans3")
    def trans4 = randomIdentifier("trans4")
    def domain1 = randomIdentifier("domain1")
    def domain2 = randomIdentifier("domain2")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText(
            "some/app/cache/dev/translations/catalogue.en.fff.php",
            """
            |<?php
            |use Symfony\\Component\\Translation\\MessageCatalogue;
            |\$catalogue = new MessageCatalogue('en', array (
            |   '$domain1' => array(
            |       '$trans1' => 'en trans1',
            |       '$trans2' => 'en trans2',
            |   ),
            |   '$domain2' => array(
            |       '$trans3' => 'en trans3',
            |       '$trans4' => 'en trans4',
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

            [domain1, domain2]
        )
    }
}
