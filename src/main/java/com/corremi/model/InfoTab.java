package com.corremi.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class InfoTab {
  private String tabName;
  private List<Entry<JCheckBox, JTextField>> listLines = new ArrayList<>();

  public InfoTab(String tabName) {
    this.tabName = tabName;
  }

  public void addLine(String line) {
    this.listLines.add(new SimpleEntry<>(new JCheckBox(), new JTextField(line)));
  }

  public String getName() {
    return this.tabName;
  }

  public List<Entry<JCheckBox, JTextField>> getListLines() {
    return this.listLines;
  }
}
