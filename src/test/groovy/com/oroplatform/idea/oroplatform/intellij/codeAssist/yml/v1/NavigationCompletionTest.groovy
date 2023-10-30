package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class NavigationCompletionTest extends CompletionTest implements RandomIdentifiers {
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

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["oro_menu_config", "oro_titles", "oro_navigation_elements"]
        )
    }

    def void "test: suggest oro_menu_config properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  <caret>
            """.stripMargin(),

            ["templates", "items", "tree"]
        )
    }

    def void "test: suggest template properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  templates:
            |    some_menu:
            |      <caret>
            """.stripMargin(),

            ["template", "clear_matcher", "depth", "currentAsLink", "currentClass", "ancestorClass", "firstClass",
             "lastClass", "compressed", "block", "rootClass", "isDropdown"]
        )
    }

    def void "test: suggest items properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    some_item:
            |      <caret>
            """.stripMargin(),

            ["aclResourceId", "translateDomain", "translateParameters", "label", "name", "uri", "route",
             "routeParameters", "attributes", "linkAttributes", "labelAttributes", "childrenAttributes",
             "showNonAuthorized", "display", "displayChildren"]
        )
    }

    def void "test: suggest tree item properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  tree:
            |    some_menu:
            |      <caret>
            """.stripMargin(),

            ["type", "merge_strategy", "extras", "children"]
        )
    }

    def void "test: suggest properties for linkAttributes"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    some_item:
            |      linkAttributes:
            |        <caret>
            """.stripMargin(),

            ["class", "id", "target", "type"]
        )

    }

    def void "test: suggest tree extras properties"() {
        suggestions(
            """
            |oro_menu_config:
            |  tree:
            |    some_menu:
            |      extras:
            |        <caret>
            """.stripMargin(),

            ["brand", "brandLink"]
        )
    }

    def void "test: suggest tree children at the first level"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    item1: ~
            |    item2: ~
            |    item3: ~
            |  tree:
            |    some_menu:
            |      children:
            |        <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest position as property of tree children"() {
        suggestions(
            """
            |oro_menu_config:
            |  tree:
            |    some_menu:
            |      children:
            |        <caret>
            """.stripMargin(),

            ["position"]
        )
    }

    def void "test: suggest tree children at the second level"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    item1: ~
            |    item2: ~
            |    item3: ~
            |  tree:
            |    some_menu:
            |      children:
            |        some_child:
            |          children:
            |            <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest tree children at the third level"() {
        suggestions(
            """
            |oro_menu_config:
            |  items:
            |    item1: ~
            |    item2: ~
            |    item3: ~
            |  tree:
            |    some_menu:
            |      children:
            |        some_child:
            |          children:
            |            some_child2:
            |              children:
            |                <caret>
            """.stripMargin(),

            ["item1", "item2", "item3"]
        )
    }

    def void "test: suggest oro_navigation_elements properties"() {
        suggestions(
            """
            |oro_navigation_elements:
            |  some_element:
            |    <caret>
            """.stripMargin(),

            ["routes", "default"]
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
