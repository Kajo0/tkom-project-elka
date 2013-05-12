package pl.edu.pw.elka.mmarkiew.tkom;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import pl.edu.pw.elka.mmarkiew.tkom.elements.TreeElement;

@SuppressWarnings("serial")
public class View extends JFrame {
	public static final int VIEW_WIDTH;
	public static final int VIEW_HEIGHT;

	private JButton processButton;
	private JLabel firstFile;
	private JLabel secondFile;

	static {
		VIEW_WIDTH = 400;
		VIEW_HEIGHT = 300;
	}

	public View() {
		super("TKOM");

		init();
	}

	private void init() {
		setBounds(
				(Toolkit.getDefaultToolkit().getScreenSize().width - VIEW_WIDTH) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - VIEW_HEIGHT) / 2,
				VIEW_WIDTH, VIEW_HEIGHT);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		// setAlwaysOnTop(true);
		setLayout(new GridLayout(3, 3));
		setVisible(true);

		Insets insets = Window.getWindows()[0].getInsets();
		setSize(VIEW_WIDTH + insets.left + insets.right, VIEW_HEIGHT
				+ insets.top + insets.bottom);

		initElements();

		// //
		// //
		// //
		// //
		// //
		// //
		// try {
		// Lexer lex;
		// Parser pars;
		// TreeElement first, second;
		// Linker link;
		//
		// lex = new Lexer(Utilities.readFileToString(firstFile.getText()));
		// lex.extractTokens();
		// pars = new Parser(lex.getTokens());
		// pars.parseTokens();
		// first = pars.getTree();
		//
		// lex = new Lexer(Utilities.readFileToString(secondFile.getText()));
		// lex.extractTokens();
		// pars = new Parser(lex.getTokens());
		// pars.parseTokens();
		// second = pars.getTree();
		//
		// link = new Linker(first, second);
		// try {
		// link.generateResult();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// System.out.println("---------------");
		// System.out.println(link.getResult());
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// //
		// dispose();
	}

	private void initElements() {
		/*
		 * START Process button
		 */
		processButton = new JButton("Process");

		processButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!(new File(firstFile.getText()).exists())
						|| !(new File(secondFile.getText()).exists())) {
					JOptionPane.showMessageDialog(View.this,
							"One of given files doesn't exist!");
					return;
				}

				try {
					Lexer lex;
					Parser pars;
					TreeElement first, second;
					Linker link;

					lex = new Lexer(Utilities.readFileToString(firstFile
							.getText()));
					lex.extractTokens();
					pars = new Parser(lex.getTokens());
					pars.parseTokens();
					first = pars.getTree();

					lex = new Lexer(Utilities.readFileToString(secondFile
							.getText()));
					lex.extractTokens();
					pars = new Parser(lex.getTokens());
					pars.parseTokens();
					second = pars.getTree();

					link = new Linker(first, second);
					link.generateResult();

					System.out
							.println("--------------------------------------------------------------------------------");
					System.out.println(link.getResult());
					System.out
							.println("--------------------------------------------------------------------------------");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		/*
		 * END Process button
		 */

		/*
		 * START File labels
		 */
		firstFile = new JLabel(
				"C:/Users/Kajo/Documents/Projects/Java/Tkom/src/examples/1.html");
		secondFile = new JLabel(
				"C:/Users/Kajo/Documents/Projects/Java/Tkom/src/examples/2.html");

		new FileDrop(firstFile, new FileDrop.Listener() {
			public void filesDropped(File[] files) {
				for (File file : files) {
					if (file.isDirectory())
						continue;

					firstFile.setText(file.getAbsolutePath());
				}
			}
		});

		new FileDrop(secondFile, new FileDrop.Listener() {
			public void filesDropped(File[] files) {
				for (File file : files) {
					if (file.isDirectory())
						continue;

					secondFile.setText(file.getAbsolutePath());
				}
			}
		});
		/*
		 * END File labels
		 */

		add(processButton);
		add(firstFile);
		add(secondFile);
		revalidate();
	}

}
