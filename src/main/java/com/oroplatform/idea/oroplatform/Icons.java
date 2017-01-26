package com.oroplatform.idea.oroplatform;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.RowIcon;
import com.jetbrains.php.PhpIcons;

import javax.swing.*;

public class Icons {
    public static final Icon DOCTRINE = IconLoader.findIcon("icons/doctrine.png");
    public static final Icon TWIG = IconLoader.findIcon("icons/twig.png");
    public static final Icon ROUTE = IconLoader.findIcon("icons/route.png");
    public static final Icon ORO = IconLoader.findIcon("icons/oro.png");
    public static final Icon PUBLIC_METHOD;

    static {
        final RowIcon publicMethod = new RowIcon(2);
        publicMethod.setIcon(PhpIcons.METHOD_ICON, 0);
        publicMethod.setIcon(PhpIcons.PUBLIC_ICON, 1);

        PUBLIC_METHOD = publicMethod;
    }
}
