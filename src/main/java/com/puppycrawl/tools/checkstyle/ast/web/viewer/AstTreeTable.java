package com.puppycrawl.tools.checkstyle.ast.web.viewer;

import com.vaadin.ui.TreeTable;

public class AstTreeTable extends TreeTable {
    private static final long serialVersionUID = 1L;

    public static final String TREE_PROPERTY = "Tree";
    public static final String TYPE_PROPERTY = "Type";
    public static final String COLUMN_PROPERTY = "Column";
    public static final String LINE_PROPERTY = "Line";
    public static final String TEXT_PROPERTY = "Text";

    public AstTreeTable(String string) {
        super(string);
        setSizeFull();
        setImmediate(true);
        setSelectable(true);
        setSortDisabled(true);
        setColumnCollapsingAllowed(true);

        addContainerProperty(TREE_PROPERTY, String.class, "");
        addContainerProperty(TYPE_PROPERTY, String.class, "");
        addContainerProperty(LINE_PROPERTY, Integer.class, 0);
        addContainerProperty(COLUMN_PROPERTY, Integer.class, 0);
        addContainerProperty(TEXT_PROPERTY, String.class, "");

        setColumnExpandRatio(TREE_PROPERTY, 7);
        setColumnExpandRatio(TYPE_PROPERTY, 2);
        setColumnExpandRatio(LINE_PROPERTY, 1);
        setColumnExpandRatio(COLUMN_PROPERTY, 1);
        setColumnExpandRatio(TEXT_PROPERTY, 2);

        setCellStyleGenerator(new CellStyleGenerator() {

            private static final long serialVersionUID = 1L;

            @Override
            public String getStyle(Object itemId, Object propertyId) {
                return null;
            }
        });
    }

    public void expandByOneLevel() {
        for (Object id : getItemIds()) {
            setCollapsed(id, false);
        }
    }

    public void expandAllItemsRecursively() {
        for (Object id : rootItemIds()) {
            expandAllChildItemsRecursively(id);
        }
    }

    public void expandAllChildItemsRecursively(Object id) {
        setCollapsed(id, false);
        if (hasChildren(id)) {
            for (Object child : getChildren(id)) {
                expandAllChildItemsRecursively(child);
            }
        }
    }

    public void collapseAllItemsRecursively() {
        for (Object id : rootItemIds()) {
            collapseAllChildItemsRecursively(id);
        }
    }

    public void collapseAllChildItemsRecursively(Object id) {
        setCollapsed(id, true);
        if (hasChildren(id)) {
            for (Object child : getChildren(id)) {
                collapseAllChildItemsRecursively(child);
            }
        }
    }

    public void makeLeafItemsNonExpandable() {
        for (Object id : rootItemIds()) {
            makeLeafItemsNonExpandable(id);
        }
    }

    public void makeLeafItemsNonExpandable(Object id) {
        if (hasChildren(id)) {
            for (Object child : getChildren(id)) {
                makeLeafItemsNonExpandable(child);
            }
        } else {
            setChildrenAllowed(id, false);
        }
    }

}
