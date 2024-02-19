package com.corremi.test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.junit.Assert;
import org.junit.Test;

import com.corremi.controller.Utils;
import com.corremi.model.InfoTab;

public class UtilsTest {
	/**
	 * - testing that if the checkbox is selected, the text appears and if it's not, nothing appears.
	 * - testing if the <br> becomes a carriage return
	 * - testing that the total equals the sum of all the partial grades.
	 * 
	 */
	@Test
	public void testGetFinalText() {
		List<InfoTab> tabs = new ArrayList<>();
		InfoTab tab1 = new InfoTab("tab1");
		InfoTab tab2 = new InfoTab("tab2");
		JCheckBox selectedBox = new JCheckBox();
		JCheckBox unselectedBox = new JCheckBox();
		selectedBox.setSelected(true);
		unselectedBox.setSelected(false);
		tab1.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(selectedBox, new JTextField("Line1 <br>")));
		tab1.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(selectedBox,new JTextField("Line12 <2>.")));
		tab1.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(selectedBox,new JTextField("Line123 <4>.")));
		tab1.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(selectedBox, new JTextField("line1234 <total>.")));
		tab1.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(selectedBox, new JTextField("line12345 <a>.")));
		tab1.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(selectedBox, new JTextField("line123456 <>.")));
		tab2.getListLines().add(new SimpleEntry<JCheckBox, JTextField>(unselectedBox, new JTextField("line2")));		
		tabs.add(tab1);
		tabs.add(tab2);
		String finalText = Utils.getFinalText(tabs);
		System.out.println(finalText);
		String expectedText = "tab1\n"
				+ "Line1 \n"
				+ "Line12 2. Line123 4. line1234 6. line12345 a. line123456 . \n"
				+ "\n"
				+ "tab2\n"
				+ "\n"
				+ "\n"
				+ "";
		Assert.assertEquals(finalText,expectedText);
		
	}
}