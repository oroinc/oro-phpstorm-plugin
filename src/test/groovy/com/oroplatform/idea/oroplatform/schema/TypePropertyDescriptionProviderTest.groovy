package com.oroplatform.idea.oroplatform.schema

import org.junit.Test
import static org.junit.Assert.*

class TypePropertyDescriptionProviderTest {

    @Test
    def void "should use property value type as description"() {
        final PropertyDescriptionProvider provider = new TypePropertyDescriptionProvider();
        assertEquals("object", provider.getDescription(new Property("some", new Container())))
        assertEquals("string", provider.getDescription(new Property("some", Scalars.any)))
        assertEquals("integer", provider.getDescription(new Property("some", Scalars.integer)))
        assertEquals("boolean", provider.getDescription(new Property("some", Scalars.bool)))
        assertEquals("string[]", provider.getDescription(new Property("some", new Sequence(Scalars.any))))
        assertEquals("object[]", provider.getDescription(new Property("some", new Sequence(new Container()))))
        assertEquals("integer or boolean", provider.getDescription(new Property("some", new OneOf(Scalars.integer, Scalars.bool))))
        assertEquals("integer", provider.getDescription(new Property("some", new OneOf(Scalars.integer, Scalars.integer))))
        assertEquals("boolean", provider.getDescription(new Property("some", Scalars.bool)))
        assertEquals("boolean", provider.getDescription(new Property("some", Repeated.atAnyLevel(Scalars.bool))))
    }
}
