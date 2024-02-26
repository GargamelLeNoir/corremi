package com.corremi.test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.corremi.controller.Utils;
import com.corremi.model.InfoTab;

public class UtilsTest {
	@Before
	public void initializeTest() {
		Utils.isTesting = true;
	}

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

	/**
	 * Testing if the method returns null when no file is found.
	 */
	@Test
	public void testParseFileNotFound() {
		List<InfoTab> resultParseFile = Utils.parseFile("bla");
		Assert.assertNull(resultParseFile);
	}

	/**
	 * Testing the content of each infotab (title, text, number of lines).
	 */
	@Test
	public void testParseFile() {
		List<InfoTab> testListTabs = Utils.parseFile("src/test/resources/corremi.txt");
		for (InfoTab infoTab : testListTabs) {
			infoTab.toString();
			System.out.println(infoTab.getName());
			System.out.println(infoTab.getListLines().size());
		}
		
		Assert.assertEquals(4,testListTabs.size());
		
		Assert.assertEquals("Points positifs", testListTabs.get(0).getName());
		Assert.assertEquals(2, testListTabs.get(0).getListLines().size());
		Assert.assertEquals("Le style d'écriture est agréable.", testListTabs.get(0).getListLines().get(0).getValue().getText());
		Assert.assertEquals("L’écriture est globalement agréable, malgré quelques fautes.", testListTabs.get(0).getListLines().get(1).getValue().getText());
		
		Assert.assertEquals("Points à améliorer", testListTabs.get(1).getName());
		Assert.assertEquals(1, testListTabs.get(1).getListLines().size());
		Assert.assertEquals("Des soucis d'expression écrite, privilégiez les phrases courtes et avec une structure simple.", testListTabs.get(1).getListLines().get(0).getValue().getText());
		
		Assert.assertEquals("Note globale : <total>/20", testListTabs.get(2).getName());
		Assert.assertEquals(0, testListTabs.get(2).getListLines().size());
		
		Assert.assertEquals("Détail de la note :", testListTabs.get(3).getName());
		Assert.assertEquals(5, testListTabs.get(3).getListLines().size());
		Assert.assertEquals("Forme et facilité de lecture:  <>/ 2<br>", testListTabs.get(3).getListLines().get(0).getValue().getText());
		Assert.assertEquals("Introduction : <>/3<br>", testListTabs.get(3).getListLines().get(1).getValue().getText());
		Assert.assertEquals("Résumé : <>/4<br>", testListTabs.get(3).getListLines().get(2).getValue().getText());
		Assert.assertEquals("Méthodologie : <>/3<br>", testListTabs.get(3).getListLines().get(3).getValue().getText());
		Assert.assertEquals("Critique : <>/8<br>", testListTabs.get(3).getListLines().get(4).getValue().getText());
	}

	@After
	public void cleanUp() {
		Utils.isTesting = false;
	}
}