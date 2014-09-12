package com.puppycrawl.tools.checkstyle.ast.web.viewer;

import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang.exception.ExceptionUtils;

import antlr.ANTLRException;
import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.grammars.CommentListener;
import com.puppycrawl.tools.checkstyle.grammars.GeneratedJavaLexer;
import com.puppycrawl.tools.checkstyle.grammars.GeneratedJavaRecognizer;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Main extends Application
{

	private static final long serialVersionUID = 1L;

	private Window window;

	private TextArea resultArea;

	private TextArea codeArea;

	@Override
	public void init()
	{
		window = new Window("Checkstyle Runner");
		window.setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		codeArea = new TextArea("Input");
		codeArea.setSizeFull();
		codeArea.setImmediate(true);
		codeArea.setInputPrompt("Please enter the code here");

		layout.addComponent(codeArea);

		Button button = new Button("Process");
		button.addListener(new ParsingClickListener());
		button.setImmediate(true);
		layout.addComponent(button);

		resultArea = new TextArea();
		resultArea.setSizeFull();
		resultArea.setInputPrompt("Result will be here");
		layout.addComponent(resultArea);

		layout.setExpandRatio(codeArea, 100);
		layout.setExpandRatio(button, 1);
		layout.setExpandRatio(resultArea, 100);

		window.setContent(layout);
		setMainWindow(window);

	}

	private final class ParsingClickListener implements Button.ClickListener {
		private static final long serialVersionUID = 1L;

		public void buttonClick(ClickEvent event) {
			String fullText = codeArea.getValue().toString();

			DetailAST astTree = null;
			try {
				astTree = parse(fullText);
				resultArea.setValue(astTreeToString(astTree));
			} catch (RecognitionException | TokenStreamException e) {
				resultArea.setValue(ExceptionUtils.getFullStackTrace(e));
			}
		}

		/**
		 * Parses a file and returns the parse tree.
		 * 
		 * @return the root node of the parse tree
		 * @throws TokenStreamException
		 * @throws RecognitionException
		 * @throws ANTLRException
		 *             if the file is not a Java source
		 */
		private DetailAST parse(String fullText) throws RecognitionException, TokenStreamException {

			final Reader sr = new StringReader(fullText);
			final GeneratedJavaLexer lexer = new GeneratedJavaLexer(sr);
			lexer.setFilename("test");

			// Ugly hack to skip comments support for now
			lexer.setCommentListener(new CommentListener() {
				@Override
				public void reportSingleLineComment(String aType, int aStartLineNo, int aStartColNo) {
				}

				@Override
				public void reportBlockComment(String aType, int aStartLineNo, int aStartColNo, int aEndLineNo,
						int aEndColNo) {
				}
			});
			lexer.setTreatAssertAsKeyword(true);
			lexer.setTreatEnumAsKeyword(true);

			final GeneratedJavaRecognizer parser = new GeneratedJavaRecognizer(lexer);
			parser.setFilename("test");
			parser.setASTNodeClass(DetailAST.class.getName());
			parser.compilationUnit();

			return (DetailAST) parser.getAST();
		}

		private String astTreeToString(DetailAST astTree) {
			StringBuilder sb = new StringBuilder();
			DetailAST curNode = astTree;
			while (curNode != null) {
				sb.append(curNode.toString());
				sb.append("\n");
				DetailAST toVisit = curNode.getFirstChild();
				while ((curNode != null) && (toVisit == null)) {
					toVisit = curNode.getNextSibling();
					if (toVisit == null) {
						curNode = curNode.getParent();
					}
				}
				curNode = toVisit;
			}
			return sb.toString();
		}

	}
}
