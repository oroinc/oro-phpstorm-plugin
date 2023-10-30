package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest;
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.testFramework.LoggedErrorProcessor
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.apache.log4j.Logger
import org.jetbrains.annotations.NotNull


abstract class PhpReferenceTest extends CompletionTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        //turn off falling tests on internal errors because there is bug in php plugin during indexing class with field
//        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor() {
//            void processError(String message, Throwable t, String[] details, @NotNull Logger logger) {
//            }
//        })
    }

    def checkPhpReference(String content, List<String> expectedReferences) {
        assertEquals(expectedReferences, getPhpReference(content))
    }

    def List<String> getPhpReference(String content) {
        configureByText(content)

        myFixture.getProject().getBaseDir().refresh(false, true)

        def element = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
        def elements = [element, element.getParent(), element.getParent().getParent()]

        elements.collect { it.getReferences() }
            .flatten()
            .findAll { it instanceof PsiPolyVariantReferenceBase }
            .collect {  it as PsiPolyVariantReferenceBase }
            .collect { it.multiResolve(false) }
            .flatten()
            .collect { (it.getElement() as PhpNamedElement).getFQN().stripMargin('\\') }
            .unique()
            .toList()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
