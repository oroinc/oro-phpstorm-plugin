package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class NavigationRouteReferenceTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.NAVIGATION
    }

    def route1 = randomIdentifier("route1")
    def route2 = randomIdentifier("route2")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText(
            "app/cache/dev/appDevUrlGenerator.php",
            """
            |<?php
            |
            |class appDevUrlGenerator extends Symfony\\Component\\Routing\\Generator\\UrlGenerator {
            |   private static \$declaredRoutes = array(
            |       '$route1' => array(1 => array('_controller' => 'Oro\\\\Controller::createAction')),
            |       '$route2' => array(1 => array('_controller' => 'Oro\\\\Controller::viewAction')),
            |   };
            |}
            """.stripMargin()
        )

        configureByText(
            "classes.php",
            """
            |<?php
            |namespace Oro;
            |class Controller {
            |    public function createAction(){}
            |    public function viewAction(){}
            |}
            """.stripMargin()
        )
    }

    def void "test: suggest routes as oro_title properties"() {
        suggestions(
            """
            |oro_titles:
            |  <caret>
            """.stripMargin(),

            [route1, route2]
        )
    }

    def void "test: suggest routes as oro_title properties in quotes"() {
        suggestions(
            """
            |oro_titles:
            |  '<caret>'
            """.stripMargin(),

            [route1, route2]
        )
    }

    def void "test: detect route references as oro_title properties"() {
        checkReference(
            """
            |oro_titles:
            |  ${insertSomewhere(route1, "<caret>")}: ~
            """.stripMargin(),

            ["createAction"]
        )
    }

    def void "test: detect route references as oro_title properties in quotes"() {
        checkReference(
            """
            |oro_titles:
            |  "${insertSomewhere(route1, "<caret>")}": ~
            """.stripMargin(),

            ["createAction"]
        )
    }

    def void "test: suggest routes in oro_navigation_elements"() {
        suggestions(
            """
            |oro_navigation_elements:
            |  some_element:
            |    routes:
            |      <caret>
            """.stripMargin(),

            [route1, route2]
        )
    }
}
