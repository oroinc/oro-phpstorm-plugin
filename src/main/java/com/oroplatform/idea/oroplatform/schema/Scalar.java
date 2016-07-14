package com.oroplatform.idea.oroplatform.schema;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.schema.requirements.PatternRequirement;
import com.oroplatform.idea.oroplatform.schema.requirements.Requirement;
import com.oroplatform.idea.oroplatform.schema.requirements.StrictLookupsRequirement;

import java.util.*;
import java.util.regex.Pattern;

public class Scalar implements Element {

    private final Value value;

    public Scalar() {
        this(new Any());
    }

    public Scalar(Value value) {
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        value.accept(visitor);
    }

    public Value getValue() {
        return value;
    }

    static Scalar strictChoices(final String... choices) {
        return choices(Collections.singletonList(new StrictLookupsRequirement(Arrays.asList(choices))), choices);
    }

    private static Scalar choices(Collection<? extends Requirement> requirements, final String[] choices) {
        return new Scalar(new Lookup(requirements) {
            @Override
            public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.choices(Arrays.asList(choices), insertHandler);
            }
        });
    }

    static Scalar choices(final String... choices) {
        return choices(Collections.<Requirement>emptyList(), choices);
    }

    static Scalar propertiesFromPath(final PropertyPath path, final String prefix) {
        return new Scalar(new Lookup() {
            @Override
            public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.propertiesFromPath(path, prefix, insertHandler);
            }
        });
    }

    static Scalar propertiesFromPath(PropertyPath path) {
        return propertiesFromPath(path, "");
    }

    static Scalar phpMethod(final String pattern) {
        return new Scalar(new Reference() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.phpMethod(pattern, insertHandler);
            }
        });
    }

    static Scalar regexp(String pattern) {
        return new Scalar(new Scalar.Any(Collections.singletonList(new PatternRequirement(Pattern.compile(pattern)))));
    }

    final static Scalar condition = new Scalar(new Lookup() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.condition(insertHandler);
        }
    });

    final static Scalar action = new Scalar(new Lookup() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.action(insertHandler);
        }
    });

    final static Scalar formType = new Scalar(new Lookup() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.formType(insertHandler);
        }
    });

    final static Scalar datagrid = new Scalar(new Lookup() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.datagrid(insertHandler);
        }
    });

    final static Scalar file = new Scalar(new Reference() {
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.filePath(insertHandler);
        }
    });

    final static Scalar any = new Scalar();

    final static Scalar fullEntity = phpClass(Scalar.PhpClass.entity(false));

    final static Scalar entity = phpClass(Scalar.PhpClass.entity(true));

    final static Scalar controller = phpClass(Scalar.PhpClass.controller());

    final static Scalar phpClass = phpClass(Scalar.PhpClass.any());

    private static Scalar phpClass(final Scalar.PhpClass phpClass) {
        return new Scalar(new Reference() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.phpClass(phpClass, insertHandler);
            }
        });
    }

    final static Scalar phpCallback = new Scalar(new Reference() {
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.phpCallback(insertHandler);
        }
    });

    final static Scalar field = new Scalar(new Reference() {
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.phpField(insertHandler);
        }
    });

    final static Scalar bool = strictChoices("true", "false");

    final static Scalar integer = regexp("^\\d+$");

    public interface Value {
        void accept(Visitor visitor);
        Collection<Requirement> getRequirements();
    }

    private static abstract class AbstractValue implements Value {
        private final Collection<Requirement> requirements = new LinkedList<Requirement>();

        AbstractValue(Collection<? extends Requirement> requirements) {
            this.requirements.addAll(requirements);
        }

        AbstractValue() {
            this(Collections.<Requirement>emptyList());
        }

        @Override
        public Collection<Requirement> getRequirements() {
            return Collections.unmodifiableCollection(requirements);
        }
    }

    public static class Any extends AbstractValue {
        private Any() {}

        private Any(Collection<? extends Requirement> requirements) {
            super(requirements);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarAnyValue(this);
        }
    }

    public static class PhpClass {
        private final String namespacePart;
        private final boolean allowDoctrineShortcutNotation;

        private static PhpClass controller() {
            return new PhpClass("Controller", false);
        }

        private static PhpClass entity(boolean allowDoctrineShortcutNotation) {
            return new PhpClass("Entity", allowDoctrineShortcutNotation);
        }

        public static PhpClass any() {
            return new PhpClass(null, false);
        }

        private PhpClass(String namespacePart, boolean allowDoctrineShortcutNotation) {
            this.namespacePart = namespacePart;
            this.allowDoctrineShortcutNotation = allowDoctrineShortcutNotation;
        }

        public boolean allowDoctrineShortcutNotation() {
            return allowDoctrineShortcutNotation;
        }

        public String getNamespacePart() {
            return namespacePart;
        }
    }

    public static class PhpMethod {
        private final Pattern pattern;

        /**
         * @param pattern Simple pattern, use * for any string
         */
        public PhpMethod(String pattern) {
            this.pattern = Pattern.compile("^"+pattern.replace("*", ".*")+"$");
        }

        public boolean matches(String name) {
            return pattern.matcher(name).matches();
        }
    }

    public abstract static class Lookup extends AbstractValue {
        Lookup(Collection<? extends Requirement> requirements) {
            super(requirements);
        }

        Lookup() {
            this(Collections.<Requirement>emptyList());
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarLookupValue(this);
        }

        public abstract CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler);
    }

    public abstract static class Reference extends AbstractValue {
        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarReferenceValue(this);
        }

        public abstract PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler);
    }

}
