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
             "@subtractOption", "@replaceOption", "@removeOption", "@changeBlockType", "@setBlockTheme", "@clear"]
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
}
