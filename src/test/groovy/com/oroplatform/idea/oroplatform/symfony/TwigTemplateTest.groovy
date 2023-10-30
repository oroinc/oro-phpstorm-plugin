package groovy.com.oroplatform.idea.oroplatform.symfony

import com.oroplatform.idea.oroplatform.symfony.Bundle
import com.oroplatform.idea.oroplatform.symfony.Resource
import org.junit.Test
import static org.junit.Assert.*

class TwigTemplateTest {
    def bundle = new Bundle("Oro\\Bundle\\AcmeBundle")

    @Test
    def void "should create twig template from resource"() {
        assertEquals(Optional.of("OroAcmeBundle::index.html.twig"), TwigTemplate.from(new Resource(bundle, "views/index.html.twig")).map { it.getName() })
        assertEquals(Optional.of("OroAcmeBundle:Admin:index.html.twig"), TwigTemplate.from(new Resource(bundle, "views/Admin/index.html.twig")).map { it.getName() })
        assertEquals(Optional.of("OroAcmeBundle:Admin:some/index.html.twig"), TwigTemplate.from(new Resource(bundle, "views/Admin/some/index.html.twig")).map { it.getName() })
    }
}
