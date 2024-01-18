package com.corremi.view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.corremi.controller.Utils;

public class ActionListenerImpl implements ActionListener, DocumentListener {
  MainWindow mainWindow;

  public ActionListenerImpl(MainWindow mw) {
    this.mainWindow = mw;
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    try {
      if (ae.getSource() == this.mainWindow.buttonLoadFile) {
        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File("./data/"));

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
          this.mainWindow.loadFromFile(fc.getSelectedFile().getAbsolutePath());
        }
      } else if (ae.getSource() == this.mainWindow.buttonSave) {
        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File("./data/"));

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
          Files.write(fc.getSelectedFile().toPath(), this.mainWindow.editorPane.getText().getBytes(),
              StandardOpenOption.CREATE);
        }
      } else if (ae.getSource() == this.mainWindow.buttonQuit) {
        this.mainWindow.frmCorremi
            .dispatchEvent(new WindowEvent(this.mainWindow.frmCorremi, WindowEvent.WINDOW_CLOSING));
      } else if (ae.getSource() == this.mainWindow.buttonCopyToClipboard) {
        // We copy the content of the editor pane to the clipboard
        StringSelection stringSelection = new StringSelection(this.mainWindow.editorPane.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
      } else if (ae.getSource() instanceof JCheckBox) {
        this.mainWindow.updateText();
      } else if (ae.getSource() instanceof JTextField) {
        this.checkBoxCorrespondingToDocument(((JTextField) ae.getSource()).getDocument());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Utils.displayErrorPopup(e);
    }
  }

  /**
   * Gets the JCheckbox corresponding to the JTextField that was just modified and
   * checks it if it wasn't done already
   *
   * @param document the document of the JTextField
   */
  public void checkBoxCorrespondingToDocument(Document document) {
    Utils.getJCheckBoxfromDocument(document, this.mainWindow.listInfoTabs).setSelected(true);
    this.mainWindow.updateText();
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    this.checkBoxCorrespondingToDocument(e.getDocument());
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    this.checkBoxCorrespondingToDocument(e.getDocument());
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    this.checkBoxCorrespondingToDocument(e.getDocument());
  }

}
