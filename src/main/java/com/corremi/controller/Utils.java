package com.corremi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.Document;

import com.corremi.model.InfoTab;

public class Utils {

  static final String CARRIAGE_RETURN = "<br>";
  static final String TOTAL = "<total>";

  /**
   * Returns a map of tabs each containing a list of lines
   *
   * @param filePath The path to the text file
   */
  public static List<InfoTab> parseFile(String filePath) {
    try {
      List<InfoTab> listInfoTabs = new ArrayList<>();
      InfoTab currentTab = null;
      List<String> lines = Files.readAllLines(Paths.get(filePath));

      for (String line : lines) {
        if (line.trim().length() == 0) {
          continue;
        }
        if (line.startsWith(">")) {
          if (currentTab != null) {
            listInfoTabs.add(currentTab);
          }
          currentTab = new InfoTab(line.substring(1));
        } else if (currentTab != null) {
          currentTab.addLine(line);
        }
      }
      listInfoTabs.add(currentTab);
      return listInfoTabs;
    } catch (IOException e) {
      e.printStackTrace();
      displayErrorPopup(e);
    }
    return null;
  }

  /**
   * Displays an error popup with the stack trace of an exception
   *
   * @param e the exception
   */
  public static void displayErrorPopup(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    JOptionPane.showMessageDialog(null, "Une erreur est survenue: " + sw.toString());
  }

  /**
   * Adds the titles of the tabs and the checked lines to create the text
   *
   * @param listInfoTabs The list of tabs
   * @return the final text
   */
  public static String getFinalText(List<InfoTab> listInfoTabs) {
    String text = "";
    int total = 0;

    for (InfoTab infoTab : listInfoTabs) {
      text += infoTab.getName() + "\n";
      for (Entry<JCheckBox, JTextField> entry : infoTab.getListLines()) {
        if (entry.getKey().isSelected()) {
          String line = entry.getValue().getText();
          line = line.replaceAll(CARRIAGE_RETURN, "\n");
          if (!line.contains(TOTAL) && line.contains("<") && line.contains(">")) {
          	// If there are partial grades
            String textPartialGrade = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
            try {
              total += Integer.parseInt(textPartialGrade);
            } catch (NumberFormatException nfe) {
              // Nothing to do
            }
            line = line.replaceAll("<", "").replaceAll(">", "");
          }
          text += line;
          if (!line.endsWith("\n")) {
          	text += " ";
          }
        }
      }
      text += "\n\n";
    }
    text = text.replace(TOTAL, Integer.toString(total));

    return text;
  }

  public static JCheckBox getJCheckBoxfromDocument(Document document, List<InfoTab> listInfoTabs) {
    for (InfoTab infoTab : listInfoTabs) {
      for (Entry<JCheckBox, JTextField> entry : infoTab.getListLines()) {
        if (document == entry.getValue().getDocument()) {
          return entry.getKey();
        }
      }
    }
    return null;
  }

}
