package io.github.jadefalke2.script;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class TSVTas {

    public static void write(Script script, File file) throws IOException {
        Logger.log("saving script to " + file.getAbsolutePath());
        Util.writeFile(write(script), file);
    }

	public static String write(Script script) {
		InputLine[] inputLines = script.getLines().clone();
		StringBuilder sb = new StringBuilder();
		int duration = 1;

		for (int i = 0; i < inputLines.length; i++) {
			// Move special buttons from the next frame to the current frame
			if (i < inputLines.length - 1) {
				EnumSet<Button> currentButtons = inputLines[i].buttons;
				EnumSet<Button> nextButtons = inputLines[i + 1].buttons;

				if (nextButtons.contains(Button.KEY_L)) {
					currentButtons.add(Button.KEY_L);
					nextButtons.remove(Button.KEY_L);
				}
				if (nextButtons.contains(Button.KEY_DUP)) {
					currentButtons.add(Button.KEY_DUP);
					nextButtons.remove(Button.KEY_DUP);
				}
				if (nextButtons.contains(Button.KEY_DDOWN)) {
					currentButtons.add(Button.KEY_DDOWN);
					nextButtons.remove(Button.KEY_DDOWN);
				}
				if (nextButtons.contains(Button.KEY_DLEFT)) {
					currentButtons.add(Button.KEY_DLEFT);
					nextButtons.remove(Button.KEY_DLEFT);
				}
				if (nextButtons.contains(Button.KEY_DRIGHT)) {
					currentButtons.add(Button.KEY_DRIGHT);
					nextButtons.remove(Button.KEY_DRIGHT);
				}
			}

			if (i > 0 && inputLines[i].equals(inputLines[i - 1])) {
				duration++;
			} else {
				if (i > 0) {
					sb.append(duration).append("\t")
						.append(convertButtons(inputLines[i - 1].getButtonsString())).append("\t")
						.append(convertStickL(inputLines[i - 1].getStickL())).append("\t")
						.append(convertStickR(inputLines[i - 1].getStickR())).append("\n");
				}
				duration = 1;
			}
		}

		if (inputLines.length > 0) {
			sb.append(duration).append("\t")
				.append(convertButtons(inputLines[inputLines.length - 1].getButtonsString())).append("\t")
				.append(convertStickL(inputLines[inputLines.length - 1].getStickL())).append("\t")
				.append(convertStickR(inputLines[inputLines.length - 1].getStickR())).append("\n");
		}

		int i = 0;
		while (i < inputLines.length) {
			InputLine curLine = inputLines[i];
			InputLine prevLine = i-1 < 0 ? InputLine.getEmpty() : inputLines[i-1];
			if (prevLine.buttons.contains(Button.KEY_DUP)) {
				prevLine.buttons.remove(Button.KEY_DUP);
				curLine.buttons.add(Button.KEY_DUP);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_DDOWN)) {
				prevLine.buttons.remove(Button.KEY_DDOWN);
				curLine.buttons.add(Button.KEY_DDOWN);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_DLEFT)) {
				prevLine.buttons.remove(Button.KEY_DLEFT);
				curLine.buttons.add(Button.KEY_DLEFT);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_DRIGHT)) {
				prevLine.buttons.remove(Button.KEY_DRIGHT);
				curLine.buttons.add(Button.KEY_DRIGHT);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_L)) {
				prevLine.buttons.remove(Button.KEY_L);
				curLine.buttons.add(Button.KEY_L);
				i++;
			}
			i++;
		}
		Settings settings = Settings.INSTANCE;

		StringBuilder header = new StringBuilder();
		if (settings.practiceStageName.get() != null && !settings.practiceStageName.get().isEmpty() && !settings.practiceStageName.get().equals("None")) {
			header.append("$stage = ").append(settings.practiceStageName.get()).append("\n");
			header.append("$entr = ").append(settings.practiceEntranceName.get()).append("\n");
			header.append("$scen = ").append(settings.practiceScenarioNo.get()).append("\n");
		}
		if (settings.startPositionX.get() != 0.0 && settings.startPositionY.get() != 0.0 && settings.startPositionZ.get() != 0.0) {
			header.append("$pos = (").append(settings.startPositionX.get()).append("; ").append(settings.startPositionY.get()).append("; ").append(settings.startPositionZ.get()).append(")\n");
		}
		header.append("//\tAuthor: ").append(settings.authorName.get()).append("\n");

		sb.insert(0, header);

		return sb.toString();
	}

    private static String convertButtons(String buttons) {
        StringBuilder sb = new StringBuilder();
        for (String button : buttons.split("\t")) {
            if (!button.isEmpty()) {
				button = button.replace("KEY_", "");
                switch (button) {
					case "L" -> button = "m-d";
					case "DUP" -> button = "m-uu";
					case "DDOWN" -> button = "m-dd";
					case "DLEFT" -> button = "m-ll";
					case "DRIGHT" -> button = "m-rr";
					case "R" -> button = "r";
					case "A" -> button = "a";
					case "B" -> button = "b";
					case "X" -> button = "x";
					case "Y" -> button = "y";
					case "ZL" -> button = "zl";
					case "ZR" -> button = "zr";
					case "LSTICK" -> button = "ls";
					case "RSTICK" -> button = "rs";
					case "PLUS" -> button = "+";
					case "MINUS" -> button = "-";
				}
				sb.append(button).append("\t");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Remove trailing tab
        }
        return sb.toString();
    }

    private static String convertStickL(StickPosition stickL) {
        if (stickL.getX() == 0 && stickL.getY() == 0) {
            return "";
        }
        return "lsx(" + stickL.getX() + ";" + stickL.getY() + ")";
    }

    private static String convertStickR(StickPosition stickR) {
        if (stickR.getX() == 0 && stickR.getY() == 0) {
            return "";
        }
        return "rsx(" + stickR.getX() + ";" + stickR.getY() + ")";
    }

    public static Script read(File file) throws CorruptedScriptException, IOException {
        Script s = read(Util.fileToString(file));
        s.setFile(file, Format.TSVTAS);
        return s;
    }

	public static Script read(String script) throws CorruptedScriptException {
		List<InputLine> inputLines = new ArrayList<>();
		String[] lines = script.split("\n");

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().isEmpty()) {
				continue;
			}
			if (line.startsWith("$")) {
				continue;
			}
			if (line.startsWith("//")) {
				continue;
			}

			InputLine currentInputLine = readLine(line);

			int duration = Integer.parseInt(line.split("\t")[0]);

			for (int j = 0; j < duration; j++) {
				inputLines.add(currentInputLine.clone());
			}
		}
		int i = 0;
		while (i < inputLines.size()) {
			InputLine curLine = inputLines.get(i);
			InputLine prevLine = i-1 < 0 ? InputLine.getEmpty() :inputLines.get(i - 1);
			if (prevLine.buttons.contains(Button.KEY_DUP)) {
				prevLine.buttons.remove(Button.KEY_DUP);
				curLine.buttons.add(Button.KEY_DUP);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_DDOWN)) {
				prevLine.buttons.remove(Button.KEY_DDOWN);
				curLine.buttons.add(Button.KEY_DDOWN);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_DLEFT)) {
				prevLine.buttons.remove(Button.KEY_DLEFT);
				curLine.buttons.add(Button.KEY_DLEFT);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_DRIGHT)) {
				prevLine.buttons.remove(Button.KEY_DRIGHT);
				curLine.buttons.add(Button.KEY_DRIGHT);
				i++;
			}
			if (prevLine.buttons.contains(Button.KEY_L)) {
				prevLine.buttons.remove(Button.KEY_L);
				curLine.buttons.add(Button.KEY_L);
				i++;
			}
			i++;
		}

		return new Script(inputLines.toArray(new InputLine[0]), 0);
	}

	public static InputLine readLine(String full) throws CorruptedScriptException {
		if (full == null || full.isEmpty()) {
			return InputLine.getEmpty();
		}

		String[] components = full.split("\t");
		int len = components.length;
		int sticklidx = indexOf(components, "lsx");
		int stickridx = indexOf(components, "rsx");
		int numButtons;

		if (sticklidx != -1) {
			numButtons = sticklidx - 1;
		} else if (stickridx != -1) {
			numButtons = stickridx - 1;
		} else {
			numButtons = len - 1;
		}

		InputLine curLine = new InputLine();

		for (int i = 1; i <= numButtons; i++) {
			if (!components[i].isEmpty()) {
				curLine.buttons.add(readCovertButton(components[i]));
			}
		}
		if (sticklidx != -1) {
			String[] stickL = components[sticklidx].split("\\(")[1].split("\\)")[0].replace(" ", "").split(";");
			curLine.setStickL(new StickPosition(Integer.parseInt(stickL[0]), Integer.parseInt(stickL[1])));
		}
		if (stickridx != -1) {
			String[] stickR = components[stickridx].split("\\(")[1].split("\\)")[0].replace(" ", "").split(";");
			curLine.setStickR(new StickPosition(Integer.parseInt(stickR[0]), Integer.parseInt(stickR[1])));
		}
		return curLine;
	}

	private static Button readCovertButton(String button) {
		switch (button.toLowerCase()) {
			case "m-d" -> {
				return Button.KEY_L;
			}
			case "m-uu" -> {
				return Button.KEY_DUP;
			}
			case "m-dd" -> {
				return Button.KEY_DDOWN;
			}
			case "m-ll" -> {
				return Button.KEY_DLEFT;
			}
			case "m-rr" -> {
				return Button.KEY_DRIGHT;
			}
			case "r" -> {
				return Button.KEY_R;
			}
			case "a" -> {
				return Button.KEY_A;
			}
			case "b" -> {
				return Button.KEY_B;
			}
			case "x" -> {
				return Button.KEY_X;
			}
			case "y" -> {
				return Button.KEY_Y;
			}
			case "zl" -> {
				return Button.KEY_ZL;
			}
			case "zr" -> {
				return Button.KEY_ZR;
			}
			case "ls" -> {
				return Button.KEY_LSTICK;
			}
			case "rs" -> {
				return Button.KEY_RSTICK;
			}
			case "+" -> {
				return Button.KEY_PLUS;
			}
			case "-" -> {
				return Button.KEY_MINUS;
			}
		}
		throw new IllegalArgumentException("Unknown button: " + button);
	}

	public static int indexOf(String[] array, String element) {
		if (array == null || element == null) {
			return -1;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i].startsWith(element)) {
				return i;
			}
		}
		return -1;
	}
	public static int countElementsBetween(String[] array, int index1, int index2) {
		if (array == null || index1 < 0 || index2 < 0 || index1 >= array.length || index2 >= array.length || index1 >= index2) {
			return -1;
		}
		return index2 - index1 - 1;
	}
}
