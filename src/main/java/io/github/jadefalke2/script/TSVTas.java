package io.github.jadefalke2.script;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Consumer;

public class TSVTas {

    public static void write(Script script, File file) throws IOException {
        Logger.log("saving script to " + file.getAbsolutePath());
        Util.writeFile(write(script), file);
    }

	public static String write(Script script) {
		List<InputLine> inputLines = new ArrayList<>(List.of(deepCopyLines(script)));
		StringBuilder sb = new StringBuilder();

		Settings settings = Settings.INSTANCE;

		StringBuilder header = new StringBuilder();
		{
			if (settings.practiceStageName.get() != null && !settings.practiceStageName.get().isEmpty() && !settings.practiceStageName.get().equals("None")) {
				header.append("$stage = ").append(settings.practiceStageName.get()).append("\n");
				header.append("$entr = ").append(settings.practiceEntranceName.get()).append("\n");
				header.append("$scen = ").append(settings.practiceScenarioNo.get()).append("\n");
			}
			if (settings.startPositionX.get() != 0.0 && settings.startPositionY.get() != 0.0 && settings.startPositionZ.get() != 0.0) {
				header.append("$pos = (").append(settings.startPositionX.get()).append("; ").append(settings.startPositionY.get()).append("; ").append(settings.startPositionZ.get()).append(")\n");
			}

			if (settings.motionOffset.get() != 0) {
				header.append("$motionOffset = ").append(settings.motionOffset.get()).append("\n");
			}

			header.append("//\tAuthor: ").append(settings.authorName.get()).append("\n");
		}

		sb.append(header);

		for (InputLine line : inputLines) {
			// Append the current line's data
			sb.append(line.getDuration()).append("\t")
				.append(convertButtons(line.getButtonsString())).append("\t")
				.append(convertStick(line.getStickL(), true)).append("\t")
				.append(convertStick(line.getStickR(), false)).append("\n");
		}

		return sb.toString();

	}

	public static Script read(File file) throws IOException {
		Script s = read(Util.fileToString(file));
		s.setFile(file, Format.TSVTAS);
		return s;
	}

	public static Script read(String script) {
		List<InputLine> inputLines = new ArrayList<>();
		String[] lines = script.split("\n");

		for (String line : lines) {
			if (line.trim().isEmpty() || line.startsWith("$") || line.startsWith("//")) {
				continue;
			}

			InputLine curLine = readLine(line);
			InputLine curLineDur1 = curLine.clone();
			curLineDur1.setDuration(1);


			if (line.contains("/")) {
				// Alternate buttons for the given duration
				String[] buttons = line.split("\t")[1].split("/");
				for (int j = 0; j < curLine.getDuration(); j++) {
					InputLine alternateLine = curLineDur1.clone();
					alternateLine.buttons.clear();
					alternateLine.buttons.add(Button.valueOf("KEY_" + buttons[j % buttons.length].toUpperCase()));
					inputLines.add(alternateLine);
				}
			} else {
				// Add the same line for the given duration
				inputLines.add(curLine.clone());
			}
		}

		return new Script(inputLines.toArray(new InputLine[0]), 0);
	}

	public static InputLine readLine(String full) {
		if (full == null || full.isEmpty()) {
			return InputLine.getEmpty();
		}

		String[] components = full.split("\t");
		String componentsString = Arrays.toString(components);
		InputLine curLine = new InputLine();
		int[] stickIdx = {0,0}; // Left, Right
		boolean[] hasRadius = {false, false}; // Left, Right
		boolean isPolarStrick = false;
		int numButtons;

		curLine.setDuration(Integer.parseInt(components[0]));


		if (componentsString.contains("lsx(") || componentsString.contains("rsx(")) {
			stickIdx[0] = indexOf(components, "lsx(");
			stickIdx[1] = indexOf(components, "rsx(");
		} else if (componentsString.contains("ls(") || componentsString.contains("rs(")) {
			isPolarStrick = true;
			stickIdx[0] = indexOf(components, "ls(");
			stickIdx[1] = indexOf(components, "rs(");

			hasRadius[0] = stickIdx[0] != -1 && components[stickIdx[0]].contains(";");
			hasRadius[1] = stickIdx[1] != -1 && components[stickIdx[1]].contains(";");
		} else {
			stickIdx[0] = -1;
			stickIdx[1] = -1;
		}


		if (stickIdx[0] != -1) {
			numButtons = stickIdx[0] - 1;
		} else if (stickIdx[1] != -1) {
			numButtons = stickIdx[1] - 1;
		} else {
			numButtons = components.length - 1;
		}

		for (int i = 1; i <= numButtons; i++) {
			if (!components[i].isEmpty()) {
				Collections.addAll(curLine.buttons, readCovertButton(components[i]));
			}
		}
		if (isPolarStrick) {
			parseStickData(isPolarStrick, stickIdx[0], components, hasRadius[0], curLine::setStickL);
			parseStickData(isPolarStrick, stickIdx[1], components, hasRadius[1], curLine::setStickR);
		} else {
			parseStickData(isPolarStrick, stickIdx[0], components, false, curLine::setStickL);
			parseStickData(isPolarStrick, stickIdx[1], components, false, curLine::setStickR);
		}
		return curLine;
	}

	private static InputLine[] deepCopyLines(Script script) {
		InputLine[] originalLines = script.getLines();
		InputLine[] copiedLines = new InputLine[originalLines.length];

		for (int i = 0; i < originalLines.length; i++) {
			copiedLines[i] = originalLines[i].clone(); // Assuming InputLine has a clone() method
		}

		return copiedLines;
	}

	private static String convertButtons(String buttons) {
        StringBuilder sb = new StringBuilder();
        for (String button : buttons.split("\t")) {
            if (!button.isEmpty()) {
				button = button.replace("KEY_", "");
				String[] nxButtons = {"M", "MU", "MD", "ML", "MR", "MUU", "MDD", "MLL", "MRR","R", "L", "A", "B", "X", "Y", "ZL", "ZR", "LSTICK", "RSTICK", "PLUS", "MINUS", "DUP", "DDOWN", "DLEFT", "DRIGHT"};
				String[] tsvButtons = {"m", "m-u", "m-d", "m-l", "m-r", "m-uu", "m-dd", "m-ll", "m-rr","r", "l", "a", "b", "x", "y", "zl", "zr", "ls", "rs", "+", "-", "dp-u", "dp-d", "dp-l", "dp-r"};

				for (int j = 0; j < nxButtons.length; j++) {
					if (button.equals(nxButtons[j])) {
						button = tsvButtons[j];
						break;
					}
				}

				sb.append(button).append("\t");
            }
        }
        return sb.toString().trim();
    }

	private static String convertStick(StickPosition stick, boolean isLeft) {
		double r = stick.getRadius();
		double t = Math.toDegrees(stick.getTheta());
		if (r == 0 && t == 0) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#.###");
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		if (r == 1) {
			return String.format(Locale.US, "%s(%s)",isLeft ? "ls" : "rs", df.format(t));
		} else {
			return String.format(Locale.US, "%s(%s;%s)",isLeft ? "ls" : "rs", df.format(r), df.format(t));
		}
	}

	private static void parseStickData(boolean isPolarStrick, int stickIdx, String[] components, boolean hasRadius, Consumer<StickPosition> setStick) {
		if (stickIdx != -1) {
			String stickData = components[stickIdx].split("\\(")[1].split("\\)")[0].replace(" ", "");
			StickPosition stickPosition = isPolarStrick
				? parsePolarStick(stickData, hasRadius)
				: parseCartesianStick(stickData);
			setStick.accept(stickPosition);
		}
	}

	private static StickPosition parsePolarStick(String stickData, boolean hasRadius) {
		if (hasRadius) {
			String[] parts = stickData.split(";");
			return new StickPosition(Float.parseFloat(parts[1]), Double.parseDouble(parts[0]));
		} else {
			return new StickPosition(Double.parseDouble(stickData));
		}
	}

	private static StickPosition parseCartesianStick(String stickData) {
		String[] parts = stickData.split(";");
		return new StickPosition(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	private static Button[] readCovertButton(String button) {
		if (button.contains("/")) {
			String[] parts = button.split("/");
			Button[] buttons = new Button[parts.length];
			for (int i = 0; i < parts.length; i++) {
				buttons[i] = convertSingleButton(parts[i]);
			}
			return buttons;
		} else {
			return new Button[]{convertSingleButton(button)};
		}
	}

	private static Button convertSingleButton(String button) {
		return switch (button.toLowerCase()) {
			case "m" -> Button.KEY_M;
			case "m-u" -> Button.KEY_MU;
			case "m-d" -> Button.KEY_MD;
			case "m-l" -> Button.KEY_ML;
			case "m-r" -> Button.KEY_MR;
			case "m-uu" -> Button.KEY_MUU;
			case "m-dd" -> Button.KEY_MDD;
			case "m-ll" -> Button.KEY_MLL;
			case "m-rr" -> Button.KEY_MRR;
			case "r" -> Button.KEY_R;
			case "l" -> Button.KEY_L;
			case "a" -> Button.KEY_A;
			case "b" -> Button.KEY_B;
			case "x" -> Button.KEY_X;
			case "y" -> Button.KEY_Y;
			case "zl" -> Button.KEY_ZL;
			case "zr" -> Button.KEY_ZR;
			case "ls" -> Button.KEY_LSTICK;
			case "rs" -> Button.KEY_RSTICK;
			case "+", "plus" -> Button.KEY_PLUS;
			case "-", "minus" -> Button.KEY_MINUS;
			case "dp-u" -> Button.KEY_DUP;
			case "dp-d" -> Button.KEY_DDOWN;
			case "dp-l" -> Button.KEY_DLEFT;
			case "dp-r" -> Button.KEY_DRIGHT;
			default -> throw new IllegalArgumentException("Unknown button: " + button);
		};
	}

	private static int indexOf(String[] array, String element) {
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
}
