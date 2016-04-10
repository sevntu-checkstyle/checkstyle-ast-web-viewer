package com.puppycrawl.tools.checkstyle.ast.web.viewer;

import java.io.StringReader;
import java.util.Comparator;
import java.util.Set;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration.ClassList;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.grammars.CommentListener;
import com.puppycrawl.tools.checkstyle.grammars.GeneratedJavaLexer;
import com.puppycrawl.tools.checkstyle.grammars.GeneratedJavaRecognizer;
import com.vaadin.Application;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import antlr.RecognitionException;
import antlr.TokenStreamException;

public class Main extends Application {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {

        Server server = new Server();

        WebAppContext servletContextHandler = new WebAppContext();
        servletContextHandler.setContextPath("/");
       servletContextHandler.setResourceBase("src/main/resources/webapp");
        ClassList clist = ClassList.setServerDefault(server);
        clist.addBefore(JettyWebXmlConfiguration.class.getName(), AnnotationConfiguration.class.getName());
        servletContextHandler.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*(/target/classes/|.*.jar)");
        servletContextHandler.setParentLoaderPriority(true);
        servletContextHandler.setInitParameter("useFileMappedBuffer", "false");

        ResourceHandler staticResourceHandler = new ResourceHandler();
        staticResourceHandler.setDirectoriesListed(false);
        Resource staticResources = Resource.newClassPathResource("webapp");
        staticResourceHandler.setBaseResource(staticResources);
        staticResourceHandler.setWelcomeFiles(new String[] { "html/index.html" });

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { staticResourceHandler, servletContextHandler });

        server.setHandler(handlers);
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8080);

        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(8080);
        server.setConnectors(new Connector[] { http });

        server.start();
        server.join();
    }

    private Window window;

    private TextArea codeArea;

    private AstTreeTable tree;

    @Override
    public void init() {
        window = new Window("Checkstyle Runner");
        window.setSizeFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        codeArea = new TextArea("Input");
        codeArea.setSizeFull();
        codeArea.setImmediate(true);
        codeArea.setInputPrompt("Please enter the code here");

        mainLayout.addComponent(codeArea);

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setWidth("140px");
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, true, false);

        Button displayTreeButton = new Button("Display Tree");
        displayTreeButton.setImmediate(true);
        displayTreeButton.addListener(new ParsingClickListener());
        buttonLayout.addComponent(displayTreeButton);

        Button expandByOneLevelButton = new Button("Expand by one level");
        expandByOneLevelButton.setImmediate(true);
        expandByOneLevelButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                tree.expandByOneLevel();
            }
        });
        buttonLayout.addComponent(expandByOneLevelButton);

        Button expandAllButton = new Button("Expand all");
        expandAllButton.setImmediate(true);
        expandAllButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                tree.expandAllItemsRecursively();
            }
        });
        buttonLayout.addComponent(expandAllButton);

        Button collapseAllButton = new Button("Collapse all");
        collapseAllButton.setImmediate(true);
        collapseAllButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                for (Object id : tree.getItemIds()) {
                    tree.setCollapsed(id, true);
                }
            }
        });
        buttonLayout.addComponent(collapseAllButton);

        mainLayout.addComponent(buttonLayout);

        tree = new AstTreeTable("Result AST");
        tree.addListener(new ItemClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton() == com.vaadin.event.MouseEvents.ClickEvent.BUTTON_LEFT) {
                    int tokenLine = (int) event.getItem().getItemProperty(AstTreeTable.LINE_PROPERTY).getValue();
                    int tokenColumn = (int) event.getItem().getItemProperty(AstTreeTable.COLUMN_PROPERTY).getValue();
                    // codeArea.setSelectionRange(tokenLine * tokenColumn, 5);
                }
            }
        });
        mainLayout.addComponent(tree);

        mainLayout.setExpandRatio(codeArea, 100);
        mainLayout.setExpandRatio(buttonLayout, 1);
        mainLayout.setExpandRatio(tree, 100);

        window.setContent(mainLayout);
        setMainWindow(window);
    }

    private final class ParsingClickListener implements Button.ClickListener {
        private static final long serialVersionUID = 1L;

        public void buttonClick(ClickEvent event) {
            String fullText = codeArea.getValue().toString();

            DetailAST astTree = null;
            try {
                astTree = parse(fullText);
                fillWithAstTree(tree, astTree);
            } catch (RecognitionException | TokenStreamException e) {
                displayErrorDialog(e, "Error", "Parsing Error", e.toString());
            }
        }

        private void displayErrorDialog(Exception e, String dialogTitle, String messageTitle, String errorText) {
            final Window dialog = new Window(dialogTitle);
            dialog.setModal(true);
            dialog.setWidth("500px");
            dialog.setHeight("350px");
            window.addWindow(dialog);

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();

            TextArea errorTextArea = new TextArea(messageTitle);
            errorTextArea.setSizeFull();
            errorTextArea.setValue(errorText);
            layout.addComponent(errorTextArea);

            Button gotItButton = new Button("Got it", new Button.ClickListener() {
                private static final long serialVersionUID = 1L;

                public void buttonClick(ClickEvent event) {
                    window.removeWindow(dialog);
                }
            });

            layout.addComponent(gotItButton);
            layout.setExpandRatio(gotItButton, 1);
            layout.setExpandRatio(errorTextArea, 100);

            dialog.setContent(layout);
        }

        private DetailAST parse(String fullText) throws RecognitionException, TokenStreamException {

            GeneratedJavaLexer lexer = new GeneratedJavaLexer(new StringReader(fullText));
            lexer.setFilename("test");

            // Ugly hack to skip comments support for now
            lexer.setCommentListener(new CommentListener() {
                @Override
                public void reportSingleLineComment(String aType, int aStartLineNo, int aStartColNo) {
                }

                @Override
                public void reportBlockComment(String type, int startLineNo, int startColNo, int endLineNo, int endColNo) {
                }
            });
            lexer.setTreatAssertAsKeyword(true);
            lexer.setTreatEnumAsKeyword(true);

            final GeneratedJavaRecognizer parser = new GeneratedJavaRecognizer(lexer);
            parser.setFilename("file");
            parser.setASTNodeClass(DetailAST.class.getName());
            parser.compilationUnit();

            return (DetailAST) parser.getAST();
        }

        private void fillWithAstTree(AstTreeTable tree, DetailAST astTree) {

            tree.removeAllItems();

            for (DetailAST token : collectTokensInHumanReadOrder(astTree)) {

                addItem(tree, token);

                DetailAST parent = token.getParent();
                if (parent != null) {
                    addIfNotExist(tree, parent);
                    tree.setParent(token, parent);
                }
            }

            tree.makeLeafItemsNonExpandable();
        }

        private Set<DetailAST> collectTokensInHumanReadOrder(DetailAST astTree) {

            Set<DetailAST> allTokens = Sets.newTreeSet(new Comparator<DetailAST>() {
                @Override
                public int compare(DetailAST o1, DetailAST o2) {
                    int result = Integer.compare(o1.getLineNo(), o2.getLineNo());
                    if (result == 0) {
                        result = Integer.compare(o1.getColumnNo(), o2.getColumnNo());
                    }
                    if (result == 0) {
                        result = o1.toString().compareTo(o2.toString());
                    }
                    return result;
                }
            });

            // Iterate over the tree and add all tokens to the plain set

            DetailAST curNode = astTree;
            while (curNode != null) {

                allTokens.add(curNode);

                DetailAST toVisit = curNode.getFirstChild();
                while ((curNode != null) && (toVisit == null)) {
                    toVisit = curNode.getNextSibling();
                    if (toVisit == null) {
                        curNode = curNode.getParent();
                    }
                }
                curNode = toVisit;
            }

            return allTokens;
        }

        private void addIfNotExist(TreeTable tree, DetailAST node) {
            if (!tree.containsId(node)) {
                addItem(tree, node);
            }
        }

        private void addItem(TreeTable tree, DetailAST node) {
            Object[] cells = new Object[] { node.toString(), TokenTypes.getTokenName(node.getType()), node.getLineNo(),
                    node.getColumnNo(), node.getText() };
            tree.addItem(cells, node);
        }

//        private String astTreeToString(DetailAST astTree) {
//            StringBuilder sb = new StringBuilder();
//            DetailAST curNode = astTree;
//            while (curNode != null) {
//                sb.append(curNode.toString());
//                sb.append("\n");
//                DetailAST toVisit = curNode.getFirstChild();
//                while ((curNode != null) && (toVisit == null)) {
//                    toVisit = curNode.getNextSibling();
//                    if (toVisit == null) {
//                        curNode = curNode.getParent();
//                    }
//                }
//                curNode = toVisit;
//            }
//            return sb.toString();
//        }

    }
}
