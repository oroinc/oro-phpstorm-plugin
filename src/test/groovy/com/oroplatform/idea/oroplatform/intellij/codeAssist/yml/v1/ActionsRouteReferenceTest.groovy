package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class ActionsRouteReferenceTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
    }

    def createRoute = randomIdentifier("create")
    def viewRoute = randomIdentifier("view")
    def listRoute = randomIdentifier("list")

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
            |       '$createRoute' => array(1 => array('_controller' => 'Oro\\\\Controller::createAction')),
            |       '$viewRoute' => array(1 => array('_controller' => 'Oro\\\\Controller::viewAction')),
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
            |               '$listRoute' => array(1 => array('_controller' => 'Oro\\\\Controller::listAction')),
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
            |operations:
            |  op1:
            |    routes:
            |      - <caret>
            """.stripMargin(),
            [createRoute, viewRoute]
        )
    }

    def void "test: detect route reference"() {
        checkReference(
            """
            |operations:
            |  op1:
            |    routes:
            |      - ${insertSomewhere(createRoute, "<caret>")}
            """.stripMargin(),
            ["createAction"]
        )
    }

    def void "test: suggest routes from symfony3 url generator"() {
        suggestions(
            """
            |operations:
            |  op1:
            |    routes:
            |      - <caret>
            """.stripMargin(),
            [listRoute]
        )
    }
}
