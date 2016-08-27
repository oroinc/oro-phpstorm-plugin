package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class ActionsRouteReferenceTest extends ReferenceTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.ACTIONS
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
            |       'oro_business_unit_create' => array(array ('_controller' => 'OroController:createAction')),
            |       'oro_business_unit_view' => array(array ('_controller' => 'OroController:viewAction')),
            |   };
            |}
            """.stripMargin()
        )

        configureByText(
            "classes.php",
            """
            |<?php
            |
            |class OroController {
            |    public function createAction(){}
            |    public function viewAction(){}
            |}
            """.stripMargin()
        )
    }

    def void "test: suggest routes"() {
        suggestions(
            """
`           |operations:
            |  op1:
            |    routes:
            |      - <caret>
            """.stripMargin(),
            ["oro_business_unit_create", "oro_business_unit_view"]
        )
    }
}
