package io.github.jadefalke2.util;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.script.Format;
import io.github.jadefalke2.stickRelatedClasses.SmoothTransitionDialog;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	public static final Settings INSTANCE = new Settings(Preferences.userRoot().node(TAS.class.getName()));
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				INSTANCE.storeSettings();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}));
	}

	public final ObservableProperty<File> directory;
	public final ObservableProperty<Boolean> darkTheme;
	public final ObservableProperty<Integer> lastStickPositionCount;
	public final ObservableProperty<JoystickPanelPosition> joystickPanelPosition;
	public final ObservableProperty<SmoothTransitionDialog.SmoothTransitionType> smoothTransitionType;
	public final ObservableProperty<RedoKeybind> redoKeybind;
	public final ObservableProperty<Format> defaultScriptFormat;
	public final ObservableProperty<String> authorName;

	public final ObservableProperty<String> practiceStageName;
	public final ObservableProperty<Integer> practiceScenarioNo;
	public final ObservableProperty<String> practiceEntranceName;

	public final ObservableProperty<Float> startPositionX;
	public final ObservableProperty<Float> startPositionY;
	public final ObservableProperty<Float> startPositionZ;

	public final ObservableProperty<File> tsvtaspath;


	private final Preferences backingPrefs;

	private Settings(Preferences prefs) {
		this.backingPrefs = prefs;

		File yuzuDir = new File(System.getProperty("user.home")+"/AppData/Roaming/yuzu/tas");

		directory = new ObservableProperty<>(new File(prefs.get("directory", System.getProperty("user.home")+(yuzuDir.exists() ? "/AppData/Roaming/yuzu/tas" : ""))));
		darkTheme = new ObservableProperty<>(prefs.get("darkTheme", "false").equals("true"));
		lastStickPositionCount = new ObservableProperty<>(Integer.parseInt(prefs.get("lastStickPositionCount", "3")));
		joystickPanelPosition = new ObservableProperty<>(JoystickPanelPosition.valueOf(prefs.get("joystickPanelPosition", "RIGHT")));
		smoothTransitionType = new ObservableProperty<>(SmoothTransitionDialog.SmoothTransitionType.valueOf(prefs.get("smoothTransitionType", "ANGULAR_CLOSEST")));
		redoKeybind = new ObservableProperty<>(RedoKeybind.valueOf(prefs.get("redoKeybind", "CTRL_SHIFT_Z")));
		defaultScriptFormat = new ObservableProperty<>(Format.valueOf(prefs.get("defaultScriptFormat", "nxTAS")));
		authorName = new ObservableProperty<>(prefs.get("authorName", ""));

		practiceStageName = new ObservableProperty<>(prefs.get("practiceStage", "CityWorldHomeStage"));
		practiceScenarioNo = new ObservableProperty<>(Integer.parseInt(prefs.get("practiceScenario", "1")));
		practiceEntranceName = new ObservableProperty<>(prefs.get("practiceEntrance", "start"));

		startPositionX = new ObservableProperty<>(Float.parseFloat(prefs.get("startPositionX", "0")));
		startPositionY = new ObservableProperty<>(Float.parseFloat(prefs.get("startPositionY", "0")));
		startPositionZ = new ObservableProperty<>(Float.parseFloat(prefs.get("startPositionZ", "0")));

		tsvtaspath = new ObservableProperty<>(new File(prefs.get("tsvtaspath", System.getProperty("user.dir"))));

	}

	public void storeSettings() throws BackingStoreException {
		backingPrefs.clear();

		backingPrefs.put("directory", directory.get() + "");
		backingPrefs.put("darkTheme", darkTheme.get() + "");
		backingPrefs.put("lastStickPositionCount", lastStickPositionCount.get() + "");
		backingPrefs.put("joystickPanelPosition", joystickPanelPosition.get() + "");
		backingPrefs.put("smoothTransitionType", smoothTransitionType.get() + "");
		backingPrefs.put("redoKeybind", redoKeybind.get() + "");
		backingPrefs.put("defaultScriptFormat", defaultScriptFormat.get() + "");
		backingPrefs.put("authorName", authorName.get());
		backingPrefs.put("practiceStage", practiceStageName.get() + "");
		backingPrefs.put("practiceScenario", practiceScenarioNo.get() + "");
		backingPrefs.put("practiceEntrance", practiceEntranceName.get() + "");
		backingPrefs.put("startPositionX", startPositionX.get() + "");
		backingPrefs.put("startPositionY", startPositionY.get() + "");
		backingPrefs.put("startPositionZ", startPositionZ.get() + "");
		backingPrefs.put("tsvtaspath", tsvtaspath.get() + "");

		backingPrefs.flush();
	}

	public enum JoystickPanelPosition {
		LEFT, RIGHT
	}

	public enum RedoKeybind {
		CTRL_SHIFT_Z, CTRL_Y
	}

}
