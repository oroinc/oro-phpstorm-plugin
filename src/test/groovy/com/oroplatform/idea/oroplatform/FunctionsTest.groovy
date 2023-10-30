package groovy.com.oroplatform.idea.oroplatform

import org.junit.Test
import static org.junit.Assert.*
import com.oroplatform.idea.oroplatform.Functions;

class FunctionsTest {

    @Test
    def void "transform camelCase to snake_case"() {
        assertEquals("my_custom_entity", Functions.snakeCase("MyCustomEntity"))
        assertEquals("entity", Functions.snakeCase("Entity"))
        assertEquals("a_b", Functions.snakeCase("AB"))
    }
}
