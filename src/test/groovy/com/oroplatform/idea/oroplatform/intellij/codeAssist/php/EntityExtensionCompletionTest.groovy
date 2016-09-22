package com.oroplatform.idea.oroplatform.intellij.codeAssist.php

import com.intellij.testFramework.PsiTestUtil
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


class EntityExtensionCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return "test.php"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText(
            "app/cache/dev/oro_entities/Extend/Entity/EX_OroAcmeBundle_User.php",
            """
            |<?php
            |class Ex_OroAcmeBundle_User {
            |  public function getFullName() {}
            |}
            """.stripMargin()
        )

        PsiTestUtil.addExcludedRoot(myFixture.getModule(), myFixture.getFile().getParent().getVirtualFile())

        configureByText(
            "classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class User {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )
    }

    def void "test: suggest extension method for variable"() {
        suggestions(
            """
            |<?php
            |\$user = new Oro\\Bundle\\AcmeBundle\\Entity\\User();
            |\$user-><caret>
            """.stripMargin(),
            ["getFullName()"]
        )
    }
}
