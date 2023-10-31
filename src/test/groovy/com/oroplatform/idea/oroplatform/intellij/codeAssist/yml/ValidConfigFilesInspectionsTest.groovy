package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection;

class ValidConfigFilesInspectionsTest extends CodeInsightFixtureTestCase {

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: check workflows yml file"() {
        checkFile("Resources/config/oro/workflows.yml")
    }

    def void "test: check acls yml file"() {
        checkFile("Resources/config/oro/acls.yml")
    }

    def void "test: check datagrids yml file"() {
        checkFile("Resources/config/oro/datagrids.yml")
    }

    def void "test: check entity yml file"() {
        checkFile("Resources/config/oro/entity.yml")
    }

    def void "test: check system_configuration yml file"() {
        checkFile("Resources/config/oro/system_configuration.yml")
    }

    def void "test: check api yml file"() {
        checkFile("Resources/config/oro/api.yml")
    }

    def void "test: check actions yml file"() {
        checkFile("Resources/config/oro/actions.yml")
    }

    def void "test: check dashboards yml file"() {
        checkFile("Resources/config/oro/dashboards.yml")
    }

    def void "test: check navigation yml file"() {
        checkFile("Resources/config/oro/navigation.yml")
    }

    def void "test: check search yml file"() {
        checkFile("Resources/config/oro/search.yml")
    }

    private def void checkFile(String filepath) {
        myFixture.addFileToProject("oroplatformTests/"+filepath, new File(getTestResourcesPath()+"/inspections/"+filepath).getText("UTF-8"))
        myFixture.testHighlighting(true, false, true, "oroplatformTests/"+filepath)
    }

    @Override
    protected void tearDown() throws Exception {
        FileUtil.delete(new File(getTestDataPath()+"/oroplatformTests"))
        super.tearDown()
    }

    private static def String getTestResourcesPath() {
        new File("src/test/resources/com/oroplatform/idea/oroplatform/").getAbsolutePath()
    }
}
