package com.corremi.view;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * This component listener updates the config file when the frame is resized or moved.
 */
public class ComponentListenerImpl implements ComponentListener {

	private MainWindow mainWindow;

	public ComponentListenerImpl(MainWindow mw) {
		this.mainWindow = mw;
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		this.mainWindow.writeConfigFile();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		this.mainWindow.writeConfigFile();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// nothing to do
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// nothing to do
	}
	
}
