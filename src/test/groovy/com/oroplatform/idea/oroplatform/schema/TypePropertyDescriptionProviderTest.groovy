package com.oroplatform.idea.oroplatform.schema

import org.junit.Test
import static org.junit.Assert.*

class TypePropertyDescriptionProviderTest {

    @Test
    def void "should use property value type as description"() {
        final PropertyDescriptionProvider provider = new TypePropertyDescriptionProvider();
        assertEquals("object", provider.getDescription(new Property("some", new Container())))
        assertEquals("string", provider.getDescription(new Property("some", new Scalar())))
        assertEquals("integer", provider.getDescription(new Property("some", Scalar.integer)))
        assertEquals("boolean", provider.getDescription(new Property("some", Scalar.bool)))
        assertEquals("string[]", provider.getDescription(new Property("some", new Sequence(Scalar.any))))
        assertEquals("object[]", provider.getDescription(new Property("some", new Sequence(new Container()))))
        assertEquals("integer or boolean", provider.getDescription(new Property("some", new OneOf(Scalar.integer, Scalar.bool))))
        assertEquals("integer", provider.getDescription(new Property("some", new OneOf(Scalar.integer, Scalar.integer))))
        assertEquals("boolean", provider.getDescription(new Property("some", Scalar.bool)))
        assertEquals("boolean", provider.getDescription(new Property("some", Repeated.atAnyLevel(Scalar.bool))))
    }
}
