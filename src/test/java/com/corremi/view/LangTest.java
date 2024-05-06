package com.corremi.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
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
		Assert.assertEquals(3, mw.optionLangMenu.getItemCount());
		Assert.assertEquals("Français", mw.optionLangMenu.getItem(2).getText());
		Assert.assertEquals("English", mw.optionLangMenu.getItem(1).getText());
		Assert.assertEquals("Blooby", mw.optionLangMenu.getItem(0).getText());
		
		//Clean up
		new File("./data/lang_blooby.txt").delete();
		Files.copy(Paths.get("./src/test/resources/data/config.dat"), Paths.get("./data/config.dat"), StandardCopyOption.REPLACE_EXISTING);
	}

}
