package com.oroplatform.idea.oroplatform.intellij.indexes;

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
                if(value != null && value instanceof ArrayCreationExpression) {
                    final Iterable<ArrayHashElement> hashes = ((ArrayCreationExpression) value).getHashElements();
                    for (ArrayHashElement hash : hashes) {
                        if(hash.getKey() != null) {
                            indexRoute(index, hash);
                        }
                    }
                }
            }

            private void indexRoute(final THashMap<String, Route> index, final ArrayHashElement hash) {
                final String name = StringUtil.stripQuotesAroundValue(hash.getKey().getText());
                if(!name.isEmpty()) {
                    for (PsiElement element : hash.getValue().getChildren()) {
                        if(element instanceof ArrayHashElement) {
                            final ArrayHashElement routeDefinitionHash = (ArrayHashElement) element;
                            if(routeDefinitionHash.getValue() instanceof ArrayCreationExpression) {
                                for (ArrayHashElement arrayHashElement : ((ArrayCreationExpression) routeDefinitionHash.getValue()).getHashElements()) {
                                    final String key = StringUtil.stripQuotesAroundValue(arrayHashElement.getKey().getText());
                                    if(key.equals("_controller")) {
                                        final String controller = StringUtil.stripQuotesAroundValue(arrayHashElement.getValue().getText());
                                        final String[] controllerParts = controller.split("::");
                                        index.put(name, controllerParts.length == 2 ? new Route(controllerParts[0].replace("\\\\", "\\"), controllerParts[1]) : null);
                                    }
                                }
                            }
                        }
                    }
                }
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
