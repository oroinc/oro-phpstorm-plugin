package com.oroplatform.idea.oroplatform.schema

import org.junit.Test
import static org.junit.Assert.*

class TypePropertyDescriptionProviderTest {

    @Test
    def void "should use property value type as description"() {
        final PropertyDescriptionProvider provider = new TypePropertyDescriptionProvider();
        assertEquals("object", provider.getDescription(new Property("some", new Container([]))))
        assertEquals("string", provider.getDescription(new Property("some", new Scalar())))
        assertEquals("integer", provider.getDescription(new Property("some", new Scalar(Scalar.Integer))))
        assertEquals("boolean", provider.getDescription(new Property("some", new Scalar(Scalar.Boolean))))
        assertEquals("string[]", provider.getDescription(new Property("some", new Sequence(new Scalar()))))
        assertEquals("object[]", provider.getDescription(new Property("some", new Sequence(new Container([])))))
        assertEquals("integer or boolean", provider.getDescription(new Property("some", new OneOf(new Scalar(Scalar.Integer), new Scalar(Scalar.Boolean)))))
        assertEquals("integer", provider.getDescription(new Property("some", new OneOf(new Scalar(Scalar.Integer), new Scalar(Scalar.Integer)))))
        assertEquals("boolean", provider.getDescription(new Property("some", new Scalar(Scalar.Boolean))))
    }
}
