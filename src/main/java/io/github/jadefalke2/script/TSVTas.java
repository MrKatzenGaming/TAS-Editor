package io.github.jadefalke2.script;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class TSVTas {

    public static void write(Script script, File file) throws IOException {
        Logger.log("saving script to " + file.getAbsolutePath());
        Util.writeFile(write(script), file);
    }

	public static String write(Script script) {
		InputLine[] inputLines = script.getLines().clone();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < inputLines.length; i++) {
			// Move special buttons from the next frame to the current frame
			if (i < inputLines.length - 1) {
				EnumSet<Button> currentButtons = inputLines[i].buttons;
				EnumSet<Button> nextButtons = inputLines[i + 1].buttons;

				if (nextButtons.contains(Button.KEY_M)) {
					currentButtons.add(Button.KEY_M);
					nextButtons.remove(Button.KEY_M);
				}
				if (nextButtons.contains(Button.KEY_MUU)) {
					currentButtons.add(Button.KEY_MUU);
					nextButtons.remove(Button.KEY_MUU);
				}
				if (nextButtons.contains(Button.KEY_MDD)) {
					currentButtons.add(Button.KEY_MDD);
					nextButtons.remove(Button.KEY_MDD);
				}
				if (nextButtons.contains(Button.KEY_MLL)) {
					currentButtons.add(Button.KEY_MLL);
					nextButtons.remove(Button.KEY_MLL);
				}
				if (nextButtons.contains(Button.KEY_MRR)) {
					currentButtons.add(Button.KEY_MRR);
					nextButtons.remove(Button.KEY_MRR);
				}
				if (nextButtons.contains(Button.KEY_MU)) {
					currentButtons.add(Button.KEY_MU);
					nextButtons.remove(Button.KEY_MU);
				}
				if (nextButtons.contains(Button.KEY_MD)) {
					currentButtons.add(Button.KEY_MD);
					nextButtons.remove(Button.KEY_MD);
				}
				if (nextButtons.contains(Button.KEY_ML)) {
					currentButtons.add(Button.KEY_ML);
					nextButtons.remove(Button.KEY_ML);
				}
				if (nextButtons.contains(Button.KEY_MR)) {
					currentButtons.add(Button.KEY_MR);
					nextButtons.remove(Button.KEY_MR);
				}
			}

			// Append the current line's data
			sb.append(convertButtons(inputLines[i].getButtonsString())).append("\t")
				.append(convertStick(inputLines[i].getStickL(), true)).append("\t")
				.append(convertStick(inputLines[i].getStickR(), false)).append("\n");

		}
		int i = 0;
		while (i < inputLines.length) {
			InputLine curLine = inputLines[i];
			InputLine prevLine = i-1 < 0 ? InputLine.getEmpty() : inputLines[i-1];
			i = moveMotion(i, curLine, prevLine);
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

		if (settings.is2PMode.get()) {
			header.append("$is2P = true\n");
		}

		header.append("//\tAuthor: ").append(settings.authorName.get()).append("\n");

		String combined = combineDuplicateLines(sb.toString());
		sb.delete(0, sb.length()); // Clear the StringBuilder before appending
		sb.append(header);
		sb.append(combined);

		return sb.toString();
	}

	private static String combineDuplicateLines(String input) {
		String[] lines = input.split("\n");
		StringBuilder result = new StringBuilder();

		String previousLine = null;
		int count = 0;

		for (String line : lines) {
			if (line.equals(previousLine)) {
				count++;
			} else {
				if (previousLine != null) {
					result.append(count).append("\t").append(previousLine).append("\n");
				}
				previousLine = line;
				count = 1;
			}
		}

		// Append the last line
		if (previousLine != null) {
			result.append(count).append("\t").append(previousLine).append("\n");
		}

		return result.toString();
	}

	private static int moveMotion(int i, InputLine curLine, InputLine prevLine) {
		if (prevLine.buttons.contains(Button.KEY_MUU)) {
			prevLine.buttons.remove(Button.KEY_MUU);
			curLine.buttons.add(Button.KEY_MUU);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_MDD)) {
			prevLine.buttons.remove(Button.KEY_MDD);
			curLine.buttons.add(Button.KEY_MDD);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_MLL)) {
			prevLine.buttons.remove(Button.KEY_MLL);
			curLine.buttons.add(Button.KEY_MLL);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_MRR)) {
			prevLine.buttons.remove(Button.KEY_MRR);
			curLine.buttons.add(Button.KEY_MRR);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_M)) {
			prevLine.buttons.remove(Button.KEY_M);
			curLine.buttons.add(Button.KEY_M);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_MU)) {
			prevLine.buttons.remove(Button.KEY_MU);
			curLine.buttons.add(Button.KEY_MU);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_MD)) {
			prevLine.buttons.remove(Button.KEY_MD);
			curLine.buttons.add(Button.KEY_MD);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_ML)) {
			prevLine.buttons.remove(Button.KEY_ML);
			curLine.buttons.add(Button.KEY_ML);
			i++;
		}
		if (prevLine.buttons.contains(Button.KEY_MR)) {
			prevLine.buttons.remove(Button.KEY_MR);
			curLine.buttons.add(Button.KEY_MR);
			i++;
		}
		i++;
		return i;
	}

	private static String convertButtons(String buttons) {
        StringBuilder sb = new StringBuilder();
        for (String button : buttons.split("\t")) {
            if (!button.isEmpty()) {
				button = button.replace("KEY_", "");
                switch (button) {
					case "M" -> button = "m";
					case "MU" -> button = "m-u";
					case "MD" -> button = "m-d";
					case "ML" -> button = "m-l";
					case "MR" -> button = "m-r";
					case "MUU" -> button = "m-uu";
					case "MDD" -> button = "m-dd";
					case "MLL" -> button = "m-ll";
					case "MRR" -> button = "m-rr";
					case "R" -> button = "r";
					case "L" -> button = "l";
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
					case "DUP" -> button = "dp-u";
					case "DDOWN" -> button = "dp-d";
					case "DLEFT" -> button = "dp-l";
					case "DRIGHT" -> button = "dp-r";
				}
				sb.append(button).append("\t");
            }
        }
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1); // Remove trailing tab
        }
        return sb.toString();
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
			i = moveMotion(i, curLine, prevLine);
		}

		return new Script(inputLines.toArray(new InputLine[0]), 0);
	}

	public static InputLine readLine(String full) throws CorruptedScriptException {
		if (full == null || full.isEmpty()) {
			return InputLine.getEmpty();
		}

		String[] components = full.split("\t");
		int len = components.length;
		boolean radialStick = false;
		boolean RhasR = false;
		boolean LhasR = false;
		int sticklidx = 0;
		int stickridx = 0;

		if (Arrays.toString(components).contains("lsx(") || Arrays.toString(components).contains("rsx(")) {
			sticklidx = indexOf(components, "lsx(");
			stickridx = indexOf(components, "rsx(");
		} else if (Arrays.toString(components).contains("ls(") || Arrays.toString(components).contains("rs(")) {
			radialStick = true;
			sticklidx = indexOf(components, "ls(");
			stickridx = indexOf(components, "rs(");

			if (sticklidx != -1) {
				if (components[sticklidx].contains(";")) {
					LhasR = true;
				}
			}
			if (stickridx != -1) {
				if (components[stickridx].contains(";")) {
					RhasR = true;
				}
			}
		} else {
			sticklidx = -1;
			stickridx = -1;
		}

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
				Collections.addAll(curLine.buttons, readCovertButton(components[i]));
			}
		}
		if (radialStick) {
			if (!LhasR && sticklidx != -1) {
				String stickL = components[sticklidx].split("\\(")[1].split("\\)")[0].replace(" ", "");
				curLine.setStickL(new StickPosition(Double.parseDouble(stickL)));
			} else if (sticklidx != -1) {
				String[] stickL = components[sticklidx].split("\\(")[1].split("\\)")[0].replace(" ", "").split(";");
				curLine.setStickL(new StickPosition(Float.parseFloat(stickL[1]), Double.parseDouble(stickL[0])));
			}
			if (!RhasR && stickridx != -1) {
				String stickR = components[stickridx].split("\\(")[1].split("\\)")[0].replace(" ", "");
				curLine.setStickR(new StickPosition(Double.parseDouble(stickR)));
			} else if (stickridx != -1) {
				String[] stickR = components[stickridx].split("\\(")[1].split("\\)")[0].replace(" ", "").split(";");
				curLine.setStickR(new StickPosition(Float.parseFloat(stickR[1]), Double.parseDouble(stickR[0])));
			}

		} else {
			if (sticklidx != -1) {
				String[] stickL = components[sticklidx].split("\\(")[1].split("\\)")[0].replace(" ", "").split(";");
				curLine.setStickL(new StickPosition(Integer.parseInt(stickL[0]), Integer.parseInt(stickL[1])));
			}

			if (stickridx != -1) {
				String[] stickR = components[stickridx].split("\\(")[1].split("\\)")[0].replace(" ", "").split(";");
				curLine.setStickR(new StickPosition(Integer.parseInt(stickR[0]), Integer.parseInt(stickR[1])));
			}
		}
		return curLine;
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
			case "+" -> Button.KEY_PLUS;
			case "-" -> Button.KEY_MINUS;
			case "dp-u" -> Button.KEY_DUP;
			case "dp-d" -> Button.KEY_DDOWN;
			case "dp-l" -> Button.KEY_DLEFT;
			case "dp-r" -> Button.KEY_DRIGHT;
			default -> throw new IllegalArgumentException("Unknown button: " + button);
		};
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
}
