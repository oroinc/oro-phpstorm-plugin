package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings

abstract class TestCase extends BasePlatformTestCase {

    abstract def String fileName()

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        OroPlatformSettings.getInstance(myFixture.project).setPluginEnabled(true)
    }

    protected def configureByText(String contents) {
        configureByText(fileName(), contents)
    }

    protected def configureByText(String fileName, String contents) {
        def file = myFixture.addFileToProject(fileName, contents)
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile())
    }
}
