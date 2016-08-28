package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.oroplatform.idea.oroplatform.symfony.Route;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class RouteExternalizer implements DataExternalizer<Route> {
    private final DataExternalizer<String> stringExternalizer = new EnumeratorStringDescriptor();

    @Override
    public void save(@NotNull DataOutput out, Route value) throws IOException {
        if(value != null) {
            stringExternalizer.save(out, value.getControllerName());
            stringExternalizer.save(out, value.getAction());
        }
    }

    @Override
    public Route read(@NotNull DataInput in) throws IOException {
        try {
            final String controller = stringExternalizer.read(in);
            final String action = stringExternalizer.read(in);
            return new Route(controller, action);
        } catch (IOException ex) {
            return null;
        }
    }
}
