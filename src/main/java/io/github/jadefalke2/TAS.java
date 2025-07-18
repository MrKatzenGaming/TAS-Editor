package io.github.jadefalke2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import io.github.jadefalke2.components.MainEditorWindow;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TAS {

	private MainEditorWindow mainEditorWindow;

	public static void main(String[] args) {
		new TAS();
	}

	public TAS() {
		startProgram();
	}

	/**
	 * starts the program by opening a new window with the two options of either creating a new script or loading in a preexisting one. After this it will start the editor.
	 */

	public void startProgram() {
		Logger.log("boot up");

		// initialise preferences
		setLookAndFeel(Settings.INSTANCE.darkTheme.get());
		Settings.INSTANCE.darkTheme.attachListener(this::setLookAndFeel);


		mainEditorWindow = new MainEditorWindow(this);
		mainEditorWindow.openScript(Script.getEmptyScript(10));
		mainEditorWindow.setVisible(true);

		UIManager.put("FileChooser.useSystemExtensionHiding", false);
		UIManager.put("FileChooser.readOnly", true);
	}

	// set look and feels

	public void setLookAndFeel(boolean darkTheme){
		Logger.log("Changing theme: " + (darkTheme ? "Dark theme" : "Light theme"));

		try {
			UIManager.setLookAndFeel(darkTheme ? new FlatDarkLaf() : new FlatLightLaf());
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("Unsupported Look and Feel: " + e.getMessage());
		}
	}

	public void exit() {
		Logger.log("exiting program");
		mainEditorWindow.dispose();
	}

	public void newWindow() {
		Logger.log("opening new window");
		new TAS(); // TODO not the right way, as for example settings won't sync properly
	}

	public boolean closeAllScripts(){
		return mainEditorWindow.closeAllScripts();
	}

	public void convertAndSend() {
		Runtime rt = Runtime.getRuntime();
		Settings settings = Settings.INSTANCE;
		Script script = mainEditorWindow.getActiveScriptTab().getScript();
		String tsvpath = settings.tsvtaspath.get().getPath();
		try {
			script.saveFile();
			Process p = rt.exec(new String[]{
				"cmd.exe", "/c", "start", "/wait", "/D",
				"\"" + String.join("\\", tsvpath) + "\"",
				"py",
				"tsv-tas.py",
				"-f",
				"\"" + script.getPath() + "\"",
				script.getNameNoExtension()
			});
			p.waitFor();
			System.out.println(tsvpath + "\\" + script.getNameNoExtension());
			File file = new File(tsvpath + "\\" + script.getNameNoExtension());
			file.delete();


		} catch (IOException e) {
			System.out.println("Error while converting script: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
