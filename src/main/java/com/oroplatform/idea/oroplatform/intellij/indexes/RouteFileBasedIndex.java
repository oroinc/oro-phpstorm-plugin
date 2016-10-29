package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Route;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;

public class RouteFileBasedIndex extends FileBasedIndexExtension<String, Route> {
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final DataExternalizer<Route> valueExternalizer = new RouteExternalizer();
    public static final ID<String, Route> KEY = ID.create("com.oroplatform.idea.oroplatform.routes");

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getPath().contains("/cache/dev/appDevUrlGenerator.php");
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public ID<String, Route> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Route, FileContent> getIndexer() {
        return new DataIndexer<String, Route, FileContent>() {
            @NotNull
            @Override
            public Map<String, Route> map(@NotNull FileContent inputData) {
                final THashMap<String, Route> index = new THashMap<>();

                if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                    return index;
                }

                final PhpFile file = (PhpFile) inputData.getPsiFile();

                for (PhpNamedElement element : file.getTopLevelDefs().values()) {
                    if(element instanceof PhpClass) {
                        final PhpClass phpClass = (PhpClass) element;

                        //for symfony <2.8
                        final Collection<Field> fields = phpClass.getOwnFieldMap().get("declaredRoutes");
                        for (Field field : fields) {
                            indexRoutes(index, field.getDefaultValue());
                        }

                        //for symfony >=2.8
                        final Method constructor = phpClass.getConstructor();

                        if(constructor == null) continue;

                        for (FieldReference fieldReference : PsiTreeUtil.collectElementsOfType(constructor, FieldReference.class)) {
                            if(!"declaredRoutes".equals(fieldReference.getCanonicalText())) continue;
                            if(!(fieldReference.getParent() instanceof AssignmentExpression)) continue;

                            PhpPsiElement value = ((AssignmentExpression) fieldReference.getParent()).getValue();
                            if(!(value instanceof ArrayCreationExpression)) continue;

                            indexRoutes(index, value);
                        }
                    }
                }

                return index;
            }

            private void indexRoutes(final THashMap<String, Route> index, final PsiElement value) {
                toStream(value)
                    .flatMap(elementFilter(ArrayCreationExpression.class))
                    .flatMap(arrayConstructor -> toStream(arrayConstructor.getHashElements()))
                    .filter(hash -> hash.getKey() != null)
                    .forEach(hash -> indexRoute(index, hash));
            }

            private void indexRoute(final THashMap<String, Route> index, final ArrayHashElement hash) {
                final String name = getKeyText(hash);
                if(!name.isEmpty()) {
                    Stream.of(hash.getValue().getChildren())
                        .flatMap(elementFilter(ArrayHashElement.class))
                        .flatMap(routeDefinitionHash -> toStream(routeDefinitionHash.getValue()))
                        .flatMap(elementFilter(ArrayCreationExpression.class))
                        .flatMap(arrayConstructor -> toStream(arrayConstructor.getHashElements()))
                        .filter(arrayHashElement -> getKeyText(arrayHashElement).equals("_controller"))
                        .map(arrayHashElement -> {
                            final String controller = getValueText(arrayHashElement);
                            final String[] controllerParts = controller.split("::");
                            final Route route = controllerParts.length == 2 ? new Route(controllerParts[0].replace("\\\\", "\\"), controllerParts[1]) : null;
                            return Pair.create(name, route);
                        })
                        .forEach(pair -> index.put(pair.first, pair.second));
                }
            }

            @NotNull
            private String getValueText(ArrayHashElement arrayHashElement) {
                return StringUtil.stripQuotesAroundValue(arrayHashElement.getValue().getText());
            }

            @NotNull
            private String getKeyText(ArrayHashElement arrayHashElement) {
                return StringUtil.stripQuotesAroundValue(arrayHashElement.getKey().getText());
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Route> getValueExternalizer() {
        return valueExternalizer;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
