package com.corremi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.corremi.controller.Utils;
import com.corremi.model.InfoTab;

import net.miginfocom.swing.MigLayout;

public class MainWindow {

	private static final Logger LOGGER = LogManager.getLogger(MainWindow.class);

	ActionListenerImpl actionListener;

	JFrame frmCorremi;
	JTabbedPane tabbedPane;
	JEditorPane editorPane;

	JButton buttonLoadFile;
	JButton buttonSave;
	JButton buttonQuit;
	JButton buttonCopyToClipboard;

	JMenu fileMenu;
	JMenu optionLangMenu;

	JMenuItem menuItemQuit;

	Map<String, String> mapLang = new HashMap<>();
	Map<String, String> mapLangPath = new HashMap<>();

	private static final String DEFAULT_FILE = "./data/corremi.txt";
	static final String CONFIG = "./data/config.dat";
	static final String LANGUAGE_CHANGE = "language_change";
	private String currentLanguage = "./data/lang_fr.txt";
	List<InfoTab> listInfoTabs;

	// TODO:construction du projet en maven
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmCorremi.setVisible(true);
				} catch (Exception e) {
					LOGGER.error("Error launching Corremi",e);
				}
			}
		});
	}

	/**
	 * Create the application. Loads the local file by default if present.
	 */
	public MainWindow() {
		this.initialize();
		if (new File(DEFAULT_FILE).exists()) {
			this.loadFromFile(DEFAULT_FILE);
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	void initialize() {
		this.actionListener = new ActionListenerImpl(this);
		this.frmCorremi = new JFrame();
		this.frmCorremi.setMinimumSize(new Dimension(800, 600));
		this.frmCorremi.setPreferredSize(new Dimension(800, 600));
		this.frmCorremi.setTitle("Corremi : Help to correct some papers");
		this.frmCorremi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ComponentListener componentListener = new ComponentListenerImpl(this);
		this.frmCorremi.addComponentListener(componentListener);
		this.frmCorremi.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow 60][grow 40][30px:n]"));

		// Initializing menu bar.
		JMenuBar menuBar = new JMenuBar();
		this.fileMenu = new JMenu("File");

		this.optionLangMenu = new JMenu("Language");
		this.fileMenu.add(this.optionLangMenu);

		ButtonGroup languageGroup = new ButtonGroup();

		this.menuItemQuit = new JMenuItem("Quit");
		this.menuItemQuit.addActionListener(this.actionListener);
		this.fileMenu.add(this.menuItemQuit);
		menuBar.add(this.fileMenu);
		this.frmCorremi.setJMenuBar(menuBar);

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.frmCorremi.getContentPane().add(this.tabbedPane, "cell 0 0,grow");

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setOpaque(false);
		this.tabbedPane.addTab("New tab", null, scrollPane, null);

		this.editorPane = new JEditorPane();
		this.frmCorremi.getContentPane().add(this.editorPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 255, 255));
		this.frmCorremi.getContentPane().add(panel, "cell 0 2,grow");
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.buttonLoadFile = new JButton("Load file");
		this.buttonLoadFile.addActionListener(this.actionListener);
		panel.add(this.buttonLoadFile);

		this.buttonSave = new JButton("Save");
		this.buttonSave.addActionListener(this.actionListener);
		panel.add(this.buttonSave);

		this.buttonCopyToClipboard = new JButton("Copy to clipboard");
		this.buttonCopyToClipboard.addActionListener(this.actionListener);
		panel.add(this.buttonCopyToClipboard);

		this.buttonQuit = new JButton("Quit");
		this.buttonQuit.addActionListener(this.actionListener);
		panel.add(this.buttonQuit);
		this.applyConfigForFrameAtStart();
		this.translateText();
		createLanguageMenuItems(languageGroup);
	}

	/**
	 * Creates dynamically menu items for all the languages in the data folder.
	 * @param languageGroup
	 */
	private void createLanguageMenuItems(ButtonGroup languageGroup) {
		File folderToSearchIn = new File("./data"); 
		File[] listOfFiles = folderToSearchIn.listFiles();
		for (File f : listOfFiles) { 
			if (f.isFile() && f.getName().startsWith("lang_") && f.getName().endsWith(".txt")) {
				try {
					for (String line : Files.readAllLines(f.toPath())) {
						if(line.startsWith("menuItem=")) {
							String value = line.substring(line.indexOf("=") + 1);
							boolean isSelected = value.equals(mapLang.get("menuItem")); //Verifies if this menuItem is the current language's.
							JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(value, isSelected);
							menuItem.addActionListener(this.actionListener);
							menuItem.setActionCommand(LANGUAGE_CHANGE);
							this.optionLangMenu.add(menuItem);
							languageGroup.add(menuItem);
							mapLangPath.put(value, f.getPath());
							break;
						}
					}
				} catch (IOException e) {
					LOGGER.error("Error in menuItem",e);
				}
			}
		}
	}

	/**
	 * Read dimension and position of the frame, also the current language. Applies a config (dimension and position) saved in a config.dat file before
	 * closing the application.
	 */
	private void applyConfigForFrameAtStart() {

		if (!new File(CONFIG).exists()) {
			return;
		}

		// Reading the content of the file
		try {
			int x = 10;
			int y = 10;
			int w = 800;
			int h = 600;

			for (String line : Files.readAllLines(Paths.get(CONFIG))) {
				if (line.startsWith("X=")) {
					x = Integer.parseInt(line.substring(2));
				} else if (line.startsWith("Y=")) {
					y = Integer.parseInt(line.substring(2));
				} else if (line.startsWith("W=")) {
					w = Integer.parseInt(line.substring(2));
				} else if (line.startsWith("H=")) {
					h = Integer.parseInt(line.substring(2));
				} else if (line.startsWith("lang=")) {
					this.currentLanguage = line.substring("lang=".length());
				}
			}
			this.frmCorremi.setBounds(x, y, w, h);

		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the frame's dimensions and position ; also writes the current
	 * language.
	 */
	public void writeConfigFile() {

		try {
			// Assigning the content of the file
			String text = "X=" + this.frmCorremi.getX() + "\nY=" + this.frmCorremi.getY() + "\nH="
					+ this.frmCorremi.getHeight() + "\nW=" + this.frmCorremi.getWidth() + "\nlang=" + this.currentLanguage;

			// Defining the file name of the file
			Path fileName = Paths.get(MainWindow.CONFIG);

			// Writing into the file
			Files.write(fileName, text.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initializes the text with the chosen language from a lang_file.
	 */
	private void initializeLang() {
		if (new File(this.currentLanguage).exists()) {
			try {
				for (String line : Files.readAllLines(Paths.get(this.currentLanguage))) {
					String key = line.substring(0, line.indexOf("="));
					String value = line.substring(line.indexOf("=") + 1);
					this.mapLang.put(key.trim(), value);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Applies the language chosen in the menu to the entire app.
	 */
	void translateText() {
		for (int i=0; i<optionLangMenu.getItemCount(); ++i) {
			JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) optionLangMenu.getItem(i);
			if (menuItem.isSelected()) {
				this.currentLanguage = this.mapLangPath.get(menuItem.getText());
			}
		}

		this.initializeLang();
		this.frmCorremi.setTitle(this.mapLang.get("title"));
		this.buttonCopyToClipboard.setText(this.mapLang.get("buttonCopyToClipboard"));
		this.buttonLoadFile.setText(this.mapLang.get("buttonLoadFile"));
		this.buttonQuit.setText(this.mapLang.get("buttonQuit"));
		this.buttonSave.setText(this.mapLang.get("buttonSave"));
		this.fileMenu.setText(this.mapLang.get("menuFile"));
		this.optionLangMenu.setText(this.mapLang.get("menuLanguage"));
		this.menuItemQuit.setText(this.mapLang.get("menuQuit"));
	}

	/**
	 * Creates tabs and checklists from the file's content
	 *
	 * @param filePath The correction file
	 */
	void loadFromFile(String filePath) {
		this.tabbedPane.removeAll();
		this.editorPane.setText("");
		this.listInfoTabs = Utils.parseFile(filePath);

		for (InfoTab infoTab : this.listInfoTabs) {
			if (infoTab.getListLines().size() == 0) {
				// No need to display an empty tab
				continue;
			}
			JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.tabbedPane.addTab(infoTab.getName(), scrollPane);
			JPanel panelPane = new JPanel();
			scrollPane.setViewportView(panelPane);
			panelPane.setOpaque(false);
			panelPane.setLayout(new BoxLayout(panelPane, BoxLayout.Y_AXIS));
			for (int i = 0; i < infoTab.getListLines().size(); i++) {
				Entry<JCheckBox, JTextField> entry = infoTab.getListLines().get(i);
				JPanel panelLine = new JPanel();
				panelLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
				panelLine.setLayout(new MigLayout("", "[][grow,fill]", "[top]"));

				panelLine.add(entry.getKey(), "flowy,cell 0 " + i + ", aligny top");
				panelLine.add(entry.getValue(), "cell 1 " + i + ",growx, aligny top");

				entry.getKey().addActionListener(this.actionListener);
				entry.getValue().addActionListener(this.actionListener);
				entry.getValue().getDocument().addDocumentListener(this.actionListener);

				panelPane.add(panelLine);
			}
			this.updateText();
		}
	}

	/**
	 * Updates the text in the editor pane by displaying the titles and checked
	 * lines
	 */
	void updateText() {
		this.editorPane.setText(Utils.getFinalText(this.listInfoTabs));
	}

}
