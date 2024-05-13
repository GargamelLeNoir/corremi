package com.corremi.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;



class LangTest {

	@Test
	void withDynamicFile() throws IOException {
		MainWindow mw = new MainWindow();
		//Arrange - Copy the test lang file at the application data folder.
		Files.copy(Paths.get("./src/test/resources/data/lang_blooby.txt"), Paths.get("./data/lang_blooby.txt"), StandardCopyOption.REPLACE_EXISTING);

		//Act - j'initialise la frame
		mw.initialize();

		//Assert - je dois avoir un menu item "blooby"

		Assertions.assertEquals(4, mw.optionLangMenu.getItemCount());
		Assertions.assertEquals("Vieilz franceis", mw.optionLangMenu.getItem(0).getText());
		Assertions.assertEquals("Français", mw.optionLangMenu.getItem(3).getText());
		Assertions.assertEquals("English", mw.optionLangMenu.getItem(2).getText());
		Assertions.assertEquals("Blooby", mw.optionLangMenu.getItem(1).getText());


		//Clean up
		new File("./data/lang_blooby.txt").delete();
		Files.copy(Paths.get("./src/test/resources/data/config.dat"), Paths.get("./data/config.dat"), StandardCopyOption.REPLACE_EXISTING);
	}

}