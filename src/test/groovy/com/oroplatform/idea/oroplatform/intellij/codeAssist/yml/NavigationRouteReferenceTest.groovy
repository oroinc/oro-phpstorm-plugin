package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas

class NavigationRouteReferenceTest extends ReferenceTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.NAVIGATION
    }

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
            |       'oro_route1' => array(1 => array('_controller' => 'Oro\\\\Controller::createAction')),
            |       'oro_route2' => array(1 => array('_controller' => 'Oro\\\\Controller::viewAction')),
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

            ["oro_route1", "oro_route2"]
        )
    }

    def void "test: suggest routes as oro_title properties in quotes"() {
        suggestions(
            """
            |oro_titles:
            |  '<caret>'
            """.stripMargin(),

            ["oro_route1", "oro_route2"]
        )
    }

    def void "test: detect route references as oro_title properties"() {
        checkReference(
            """
            |oro_titles:
            |  oro_rou<caret>te1: ~
            """.stripMargin(),

            ["createAction"]
        )
    }

    def void "test: detect route references as oro_title properties in quotes"() {
        checkReference(
            """
            |oro_titles:
            |  "oro_rou<caret>te1": ~
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

            ["oro_route1", "oro_route2"]
        )
    }
}
