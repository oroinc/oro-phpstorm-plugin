package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest

public class AclPhpReferenceTest extends PhpReferenceTest {

    @Override
    String fileName() {
        return "acl.yml"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()


        myFixture.configureByText("classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Controller {
            |  class AdminController {
            |    public function editAction() {}
            |    public function someFunc() {}
            |  }
            |  class RegionController {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Controller\\Api {
            |  class AddressApiController {}
            |  class RegionController {}
            |}
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Address extends AbstractEntity {}
            |  abstract class AbstractEntity {}
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: php class should be inside class key"() {
        checkPhpReference(
            """
            |some:
            |  class: "Oro\\Bundle\\AcmeBundle\\Entity\\Address<caret>"
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: detect php class with escaped namespace separator"() {
        checkPhpReference(
            """
            |some:
            |  class: "Oro\\\\Bundle\\\\AcmeBundle\\\\Entity\\\\Address<caret>"
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: detect php entity in shortcut notation"() {
        checkPhpReference(
            """
            |some:
            |  class: OroAcmeBundle:Addre<caret>ss
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: should not suggest entity class by full name"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            [],
            ["Address", "AdminController"]
        )
    }

    def void "test: should complete entity shourtcut even when first part is already given"() {
        completion(
            """
            |some:
            |  class: OroAcmeBundle:Addre<caret>
            """.stripMargin(),
            """
            |some:
            |  class: OroAcmeBundle:Address
            """.stripMargin(),
        )
    }

    def void "test: should complete entity class even when namespace is already given"() {
        completion(
            """
            |some:
            |  class: Oro\\Bundle\\AcmeBundle\\Entity\\Addre<caret>
            """.stripMargin(),
            """
            |some:
            |  class: OroAcmeBundle:Address
            """.stripMargin()
        )
    }

    def void "test: should suggest abstract classes as entity"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            ["OroAcmeBundle:AbstractEntity"]
        )
    }

    def void "test: should suggest entity shortcut class name"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            ["OroAcmeBundle:Address"]
        )
    }

    def void "test: should suggest controllers also in subnamespaces of Controller"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: <caret> }
            """.stripMargin(),
            ["AddressApiController"]
        )
    }

    def void "test: should suggest classes with the same names"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: <caret> }
            """.stripMargin(),
            ["RegionController", "RegionController"]
        )
    }

    def void "test: should complete fq class name"() {
        completion(
            """
            |some:
            |  class: Addr<caret>
            """.stripMargin(),
            """
            |some:
            |  class: OroAcmeBundle:Address
            """.stripMargin()
        )
    }

    def void "test: should complete fq class name - eof"() {
        completion(
            """
            |some:
            |  class: Addr<caret>""".stripMargin(),
            """
            |some:
            |  class: OroAcmeBundle:Address""".stripMargin()
        )
    }

    def void "test: should complete entity shortcut class name in quotes"() {
        completion(
            """
            |some:
            |  class: "Addr<caret>"
            """.stripMargin(),
            """
            |some:
            |  class: "OroAcmeBundle:Address"
            """.stripMargin()
        )
    }

    def void "test: should suggest controller class in bindings"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: <caret> }
            """.stripMargin(),

            ["AdminController"],
            ["Address"]
        )
    }

    def void "test: should suggest controller action in bindings"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: "Oro\\\\Bundle\\\\AcmeBundle\\\\Controller\\\\AdminController", method: <caret> }
            """.stripMargin(),

            ["editAction"],
            ["someFunc"]
        )
    }

    def void "test: detect php method reference"() {
        checkPhpReference(
            """
            |some:
            |  bindings:
            |    - { class: "Oro\\\\Bundle\\\\AcmeBundle\\\\Controller\\\\AdminController", method: editActi<caret>on }
            """.stripMargin(),

            ["editAction"]
        )
    }

}
