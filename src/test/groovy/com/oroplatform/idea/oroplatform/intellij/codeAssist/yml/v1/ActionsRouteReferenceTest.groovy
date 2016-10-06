package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ActionsRouteReferenceTest extends ReferenceTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
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
            |       'oro_business_unit_create' => array(1 => array('_controller' => 'Oro\\\\Controller::createAction')),
            |       'oro_business_unit_view' => array(1 => array('_controller' => 'Oro\\\\Controller::viewAction')),
            |   };
            |}
            """.stripMargin()
        )

        configureByText(
            "vars/cache/dev/appDevUrlGenerator.php",
            """
            |<?php
            |
            |class appDevUrlGenerator extends Symfony\\Component\\Routing\\Generator\\UrlGenerator {
            |   public function __construct(Symfony\\Component\\Routing\\RequestContext \$context, Psr\\Log\\LoggerInterface \$logger = null) {
            |       \$this->context = \$context;
            |       \$this->logger = \$logger;
            |       if (null === self::\$declaredRoutes) {
            |           self::\$declaredRoutes = array(
            |               'oro_business_unit_list' => array(1 => array('_controller' => 'Oro\\\\Controller::listAction')),
            |           };
            |       }
            |   }
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
            |    public function listAction(){}
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

    def void "test: detect route reference"() {
        checkReference(
            """
`           |operations:
            |  op1:
            |    routes:
            |      - oro_business_unit<caret>_create
            """.stripMargin(),
            ["createAction"]
        )
    }

    def void "test: suggest routes from symfony3 url generator"() {
        suggestions(
            """
`           |operations:
            |  op1:
            |    routes:
            |      - <caret>
            """.stripMargin(),
            ["oro_business_unit_list"]
        )
    }
}
