package pl.edu.pw.elka.mmarkiew.tkom;

//import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import pl.edu.pw.elka.mmarkiew.tkom.elements.TreeElement;

/**
 * Class represents main app view
 * 
 * @author Mikolaj Markiewicz
 * 
 */
@SuppressWarnings("serial")
public class View extends JFrame {

	/**
	 * Frame width
	 */
	public static final int VIEW_WIDTH;

	/**
	 * Frame height
	 */
	public static final int VIEW_HEIGHT;

	/**
	 * Process button
	 */
	private JButton processButton;

	/**
	 * Button containing path to first file
	 */
	private JButton firstFile;

	/**
	 * Button containing path to second file
	 */
	private JButton secondFile;

	/**
	 * Button containing path to result file
	 */
	private JButton resultFile;

	/**
	 * Button to open result dir chooser
	 */
	private JButton chooseResultDirButton;

	/**
	 * Simple textarea console
	 */
	private JTextArea simpleConsole;

	static {
		VIEW_WIDTH = 400;
		VIEW_HEIGHT = 400;
	}

	/**
	 * C-tor
	 */
	public View() {
		super("TKOM");

		init();
	}

	/**
	 * Frame initialization
	 */
	private void init() {
		setBounds(
				(Toolkit.getDefaultToolkit().getScreenSize().width - VIEW_WIDTH) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - VIEW_HEIGHT) / 2,
				VIEW_WIDTH, VIEW_HEIGHT);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		// setAlwaysOnTop(true);
		setLayout(null);// new GridLayout(6, 1));
		setVisible(true);

		Insets insets = Window.getWindows()[0].getInsets();
		setSize(VIEW_WIDTH + insets.left + insets.right, VIEW_HEIGHT
				+ insets.top + insets.bottom);

		initElements();
	}

	/**
	 * Components initialization
	 */
	@SuppressWarnings("deprecation")
	private void initElements() {
		/*
		 * START Process button
		 */
		processButton = new JButton("Process");

		processButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!(new File(firstFile.getText()).exists())
						|| !(new File(secondFile.getText()).exists())
						|| !(new File(resultFile.getText()).exists())) {
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
					pars = new Parser(lex.extractTokens());
					first = pars.parseTokens();

					lex = new Lexer(Utilities.readFileToString(secondFile
							.getText()));
					pars = new Parser(lex.extractTokens());
					second = pars.parseTokens();

					link = new Linker(first, second);
					link.generateResult();

					// System.out
					// .println("--------------------------------------------------------------------------------");
					// System.out.println(link.getResult());
					// System.out
					// .println("--------------------------------------------------------------------------------");

					// Set conflict output
					String confl = "\tCOMPLETED, conflict counter:\n\n";
					for (Entry<Integer, Integer> entry : link
							.getConflictCounterMap().entrySet())
						confl += "Conflict no. " + entry.getKey() + "\t: "
								+ entry.getValue() + " times\n";

					confl += "\n\tTrace of used conflicts:\n";
					confl += link.getConflictTrace();

					simpleConsole.setText(confl);

					// Write result to file
					Utilities.writeToFile(resultFile.getText(), link
							.getResult().toString());

				} catch (Exception e) {
					simpleConsole.setText("\tERROR occurred, message:\n\n"
							+ e.getMessage());
				}
			}
		});
		/*
		 * END Process button
		 */

		/*
		 * START File labels
		 */
		firstFile = new JButton("First file");
		// "C:/Users/Kajo/Documents/Projects/Java/Tkom/src/examples/1.html");
		firstFile.setBorderPainted(false);
		firstFile.setContentAreaFilled(false);
		firstFile.setMargin(new Insets(0, 0, 0, 0));

		secondFile = new JButton("Second file");
		// "C:/Users/Kajo/Documents/Projects/Java/Tkom/src/examples/2.html");
		secondFile.setBorderPainted(false);
		secondFile.setContentAreaFilled(false);
		secondFile.setMargin(new Insets(0, 0, 0, 0));

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

		firstFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose file");

				if (chooser.showOpenDialog(View.this) == JFileChooser.APPROVE_OPTION)
					firstFile.setText(chooser.getSelectedFile().toString());
			}
		});

		secondFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose file");

				if (chooser.showOpenDialog(View.this) == JFileChooser.APPROVE_OPTION)
					secondFile.setText(chooser.getSelectedFile().toString());
			}
		});
		/*
		 * END File labels
		 */

		/*
		 * START Result file label
		 */
		resultFile = new JButton("Result file");
		// "C:/Users/Kajo/Documents/Projects/Java/Tkom/src/examples/3.html");
		resultFile.setBorderPainted(false);
		resultFile.setContentAreaFilled(false);
		resultFile.setMargin(new Insets(0, 0, 0, 0));

		new FileDrop(resultFile, new FileDrop.Listener() {
			public void filesDropped(File[] files) {
				for (File file : files) {
					if (file.isDirectory())
						resultFile.setText(file.getAbsolutePath()
								+ "/result.html");
					else
						resultFile.setText(file.getAbsolutePath());
				}
			}
		});

		resultFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose file");

				if (chooser.showOpenDialog(View.this) == JFileChooser.APPROVE_OPTION)
					resultFile.setText(chooser.getSelectedFile().toString());
			}
		});
		/*
		 * END Result file label
		 */

		/*
		 * START Result button
		 */

		chooseResultDirButton = new JButton(
				"Choose directory for result.html file");

		chooseResultDirButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose directory for result.html file");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(View.this) == JFileChooser.APPROVE_OPTION) {
					resultFile.setText(chooser.getSelectedFile()
							+ "/result.html");
				}
			}
		});
		/*
		 * END Result button
		 */

		/*
		 * START Simple console
		 */
		simpleConsole = new JTextArea();
		simpleConsole.setEditable(false);
		simpleConsole.setWrapStyleWord(true);
		simpleConsole.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(simpleConsole);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		/*
		 * END Simple console
		 */

		/*
		 * Set bounds
		 */
		int x = 0;
		int y = 0;
		int width = this.getWidth() - this.insets().left - this.insets().right;
		int height = 25;

		processButton.setBounds(x, y, width, height);
		y += height;
		firstFile.setBounds(x, y, width, height);
		y += height;
		secondFile.setBounds(x, y, width, height);
		y += height;
		chooseResultDirButton.setBounds(x, y, width, height);
		y += height;
		resultFile.setBounds(x, y, width, height);
		y += height;
		height = this.getHeight() - y - this.insets().top
				- this.insets().bottom;
		scrollPane.setBounds(x, y, width, height);

		/*
		 * Add to pane
		 */
		add(processButton);
		add(firstFile);
		add(secondFile);
		add(chooseResultDirButton);
		add(resultFile);
		add(scrollPane);

		repaint();
		// revalidate();
	}

}
