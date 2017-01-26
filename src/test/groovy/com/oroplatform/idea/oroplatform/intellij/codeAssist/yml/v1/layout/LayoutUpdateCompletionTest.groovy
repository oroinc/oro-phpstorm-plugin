package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


class LayoutUpdateCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return "Resources/views/layouts/some_theme/some.yml"
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["layout"]
        )
    }

    def void "test: suggest layout properties"() {
        suggestions(
            """
            |layout:
            |  <caret>
            """.stripMargin(),
            ["actions", "conditions", "imports"]
        )
    }

    def void "test: suggest actions"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - <caret>
            """.stripMargin(),
            ["@add", "@addTree", "@remove", "@move", "@addAlias", "@removeAlias", "@setOption", "@appendOption",
             "@subtractOption", "@replaceOption", "@removeOption", "@changeBlockType", "@setBlockTheme", "@clear", "@setFormTheme"]
        )
    }

    def void "test: complete operations when quotes are missing"() {
        completion(
            """
            |layout:
            |  actions:
            |    - addTr<caret>
            """.stripMargin(),
            """
            |layout:
            |  actions:
            |    - '@addTree': <caret>
            """.stripMargin()
        )
    }

    def void "test: complete operations started with @ when quotes are missing"() {
        completion(
            """
            |layout:
            |  actions:
            |    - @addTr<caret>
            """.stripMargin(),
            """
            |layout:
            |  actions:
            |    - '@addTree': <caret>
            """.stripMargin()
        )
    }

    def void "test: complete operations in quotes"() {
        completion(
            """
            |layout:
            |  actions:
            |    - 'addTr<caret>'
            """.stripMargin(),
            """
            |layout:
            |  actions:
            |    - '@addTree': <caret>
            """.stripMargin()
        )
    }

    def void "test: complete operations started with @ in quotes"() {
        completion(
            """
            |layout:
            |  actions:
            |    - '@addTr<caret>'
            """.stripMargin(),
            """
            |layout:
            |  actions:
            |    - '@addTree': <caret>
            """.stripMargin()
        )
    }

    def void "test: suggest @add properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@add':
            |        <caret>
            """.stripMargin(),
            ["id", "parentId", "blockType", "options", "siblingId", "prepend"]
        )
    }

    def void "test: suggest @remove properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@remove':
            |        <caret>
            """.stripMargin(),
            ["id"]
        )
    }

    def void "test: suggest @move properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@move':
            |        <caret>
            """.stripMargin(),
            ["id", "parentId", "siblingId", "prepend"]
        )
    }

    def void "test: suggest @addAlias properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@addAlias':
            |        <caret>
            """.stripMargin(),
            ["alias", "id"]
        )
    }

    def void "test: suggest @removeAlias properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@removeAlias':
            |        <caret>
            """.stripMargin(),
            ["alias"]
        )
    }

    def void "test: suggest @setOption properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@setOption':
            |        <caret>
            """.stripMargin(),
            ["id", "optionName", "optionValue"]
        )
    }

    def void "test: suggest @appendOption properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@appendOption':
            |        <caret>
            """.stripMargin(),
            ["id", "optionName", "optionValue"]
        )
    }

    def void "test: suggest @replaceOption properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@replaceOption':
            |        <caret>
            """.stripMargin(),
            ["id", "optionName", "oldOptionValue", "newOptionValue"]
        )
    }

    def void "test: suggest @removeOption properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@removeOption':
            |        <caret>
            """.stripMargin(),
            ["id", "optionName"]
        )
    }

    def void "test: suggest @changeBlockType properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@changeBlockType':
            |        <caret>
            """.stripMargin(),
            ["id", "blockType", "optionsCallback"]
        )
    }

    def void "test: suggest @setBlockTheme properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        <caret>
            """.stripMargin(),
            ["id", "themes"]
        )
    }

    def void "test: suggest absolute theme for @setBlockTheme"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin("|")
        )
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some1.html.twig", "abc")

        suggestions(
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: <caret>
            """.stripMargin(),
            ["OroAcmeBundle:layouts:some1.html.twig"]
        )
    }

    def void "test: show gutter for relative template in twig file when it is used in layout update"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText(
            "src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: some1.html.twig
            """.stripMargin(),
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some1.html.twig", "abc")

        assertEquals(1, myFixture.findAllGutters().size())
    }

    def void "test: show gutter for relative template in twig file when it is used in layout update and defines in sequence"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText(
            "src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: [some1.html.twig]
            """.stripMargin(),
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some1.html.twig", "abc")

        assertEquals(1, myFixture.findAllGutters().size())
    }

    def void "test: not show gutter for relative template in twig file when it is not used in layout update"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText(
            "src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: some2.html.twig
            """.stripMargin(),
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some1.html.twig", "abc")

        assertEquals(0, myFixture.findAllGutters().size())
    }

    def void "test: show gutter for absolute template in twig file when it is used in layout update"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText(
            "Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: OroAcmeBundle:layouts:some_theme/some1.html.twig
            """.stripMargin(),
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some1.html.twig", "abc")

        assertEquals(1, myFixture.findAllGutters().size())
    }

    def void "test: not show gutter for absolute template in twig file when it is not used in layout update"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText(
            "Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: OroAcmeBundle:layouts:some_theme/some2.html.twig
            """.stripMargin(),
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some2.html.twig", "abc")
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some1.html.twig", "abc")

        assertEquals(0, myFixture.findAllGutters().size())
    }

    def void "test: not suggest absolute theme for @setBlockTheme if theme is not 'layouts' dir"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin("|")
        )
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/some1.html.twig", "abc")

        suggestions(
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: <caret>
            """.stripMargin(),
            [],
            ["OroAcmeBundle::some1.html.twig"]
        )
    }

    def void "test: suggest relative theme for @setBlockTheme"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin("|")
        )
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/relativeTemplate.html.twig", "abc")

        suggestions(
            "src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: <caret>
            """.stripMargin(),
            ["relativeTemplate.html.twig"],
            ["other.yml"]
        )
    }

    def void "test: complete relative theme for @setBlockTheme"() {
        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin("|")
        )
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/relativeTemplate.html.twig", "abc")

        completion(
            "src/Oro/Bundle/AcmeBundle/Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: rela<caret>
            """.stripMargin(),
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: relativeTemplate.html.twig
            """.stripMargin(),
        )
    }

    def void "test: suggest @setFormTheme properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@setFormTheme':
            |        <caret>
            """.stripMargin(),
            ["id", "themes"]
        )
    }

    def void "test: suggest more actions in the same item"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@setBlockTheme':
            |        themes: "some"
            |      <caret>
            """.stripMargin(),
            ["@changeBlockType"]
        )
    }

    def void "test: suggest @addTree properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@addTree':
            |        <caret>
            """.stripMargin(),
            ["items", "tree"]
        )
    }

    def void "test: suggest @addTree/items properties"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@addTree':
            |        items:
            |          some_block:
            |            <caret>
            """.stripMargin(),
            ["blockType", "options"]
        )
    }

    def void "test: suggest @addTree/tree properties at second level"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@addTree':
            |        items:
            |          block1: ~
            |          block2: ~
            |          block3: ~
            |        tree:
            |          root:
            |            <caret>
            """.stripMargin(),
            ["block1", "block2", "block3"]
        )
    }

    def void "test: suggest @addTree/tree properties at third level"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@addTree':
            |        items:
            |          block1: ~
            |          block2: ~
            |          block3: ~
            |        tree:
            |          root:
            |            block1:
            |              <caret>
            """.stripMargin(),
            ["block2", "block3"]
        )
    }


    def void "test: suggest @addTree/tree properties at fourth level"() {
        suggestions(
            """
            |layout:
            |  actions:
            |    - '@addTree':
            |        items:
            |          block1: ~
            |          block2: ~
            |          block3: ~
            |        tree:
            |          root:
            |            block1:
            |              block2:
            |                <caret>
            """.stripMargin(),
            ["block3"]
        )
    }

    def void "test: don't suggest properties for theme.yml"() {
        suggestions("Resources/views/layouts/some_theme/theme.yml",
            """
            |<caret>
            """.stripMargin(),
            [],
            ["layout"]
        )
    }

    def void "test: suggest properties for layout update in imports directory"() {
        suggestions("Resources/views/layouts/some_theme/imports/some_import/layout.yml",
            """
            |<caret>
            """.stripMargin(),
            ["layout"]
        )
    }

    def void "test: suggest properties for layout update in any nested directory"() {
        suggestions("Resources/views/layouts/some_theme/some_dir/another_dir/layout.yml",
            """
            |<caret>
            """.stripMargin(),
            ["layout"]
        )
    }

    def void "test: not suggest top level properties for theme file"() {
        suggestions(
            "Resources/views/layouts/some_theme/theme.yml",
            """
            |<caret>
            """.stripMargin(),
            [],
            ["layout"]
        )
    }

    def void "test: not suggest top level properties for config file"() {
        suggestions(
            "Resources/views/layouts/some_theme/config/assets.yml",
            """
            |<caret>
            """.stripMargin(),
            [],
            ["layout"]
        )
    }

    def void "test: suggest properties for imports"() {
        suggestions(
            """
            |layout:
            |  actions: []
            |  imports:
            |    -
            |      <caret>
            """.stripMargin(),
            ["id", "namespace", "root"]
        )
    }

    def void "test: suggest import ids"() {
        configureByText("Resources/views/layouts/some_theme/imports/some_import/layout.yml", "")

        suggestions(
            "Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions: []
            |  imports:
            |    -
            |      id: <caret>
            """.stripMargin(),
            ["some_import"]
        )
    }

    def void "test: suggest import ids as simple scalar"() {
        configureByText("Resources/views/layouts/some_theme/imports/some_import/layout.yml", "")

        suggestions(
            "Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions: []
            |  imports:
            |    - <caret>
            """.stripMargin(),
            ["some_import"]
        )
    }

    def void "test: not suggest filename as import ids"() {
        configureByText("Resources/views/layouts/some_theme/imports/some_import/layout.yml", "")

        suggestions(
            "Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions: []
            |  imports:
            |    -
            |      id: some_import/<caret>
            """.stripMargin(),
            [],
            ["layout.yml"]
        )
    }

    def void "test: suggest empty list when imports dir doesn't exist"() {
        configureByText("Resources/views/layouts/some_theme/some2.yml", "")

        suggestions(
            "Resources/views/layouts/some_theme/some.yml",
            """
            |layout:
            |  actions: []
            |  imports:
            |    -
            |      id: <caret>
            """.stripMargin(),
            [],
            ["some.yml", "some2.yml"]
        )
    }
}
