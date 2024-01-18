package com.corremi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.corremi.controller.Utils;
import com.corremi.model.InfoTab;

import net.miginfocom.swing.MigLayout;

public class MainWindow {

  ActionListenerImpl actionListener;

  JFrame frmCorremi;
  JTabbedPane tabbedPane;
  JEditorPane editorPane;

  JButton buttonLoadFile;
  JButton buttonSave;
  JButton buttonQuit;
  JButton buttonCopyToClipboard;

  private static final String DEFAULT_FILE = "./data/corremi.txt";
  List<InfoTab> listInfoTabs;

  // TODO: Save preferences, fix smaller size, language files
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
          e.printStackTrace();
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
  private void initialize() {
    this.actionListener = new ActionListenerImpl(this);
    this.frmCorremi = new JFrame();
    this.frmCorremi.setTitle("Corremi");
    this.frmCorremi.setExtendedState(this.frmCorremi.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    this.frmCorremi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frmCorremi.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow 60][200px:n,grow 40][30px:n]"));

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

    this.buttonLoadFile = new JButton("Charger fichier");
    this.buttonLoadFile.addActionListener(this.actionListener);
    panel.add(this.buttonLoadFile);

    this.buttonSave = new JButton("Sauvegarder");
    this.buttonSave.addActionListener(this.actionListener);
    panel.add(this.buttonSave);

    this.buttonCopyToClipboard = new JButton("Copier dans le presse-papier");
    this.buttonCopyToClipboard.addActionListener(this.actionListener);
    panel.add(this.buttonCopyToClipboard);

    this.buttonQuit = new JButton("Quitter");
    this.buttonQuit.addActionListener(this.actionListener);
    panel.add(this.buttonQuit);
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
