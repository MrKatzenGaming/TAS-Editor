package io.github.jadefalke2.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class ButtonIconLoader {

    private static final Map<Button, ImageIcon> buttonIcons = new EnumMap<>(Button.class);

    static {
		for (Button button : Button.values()) {
			String iconPath = "/icons/" + button.toString().toLowerCase() + ".png";
			try {
				ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(ButtonIconLoader.class.getResource(iconPath)));
				Image scaledImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

				buttonIcons.put(button, new ImageIcon(scaledImage));
			} catch (Exception e) {
				System.err.println("Failed to load icon for button: " + button + " at " + iconPath);
			}
		}
    }

    public static ImageIcon getIcon(Button button) {
        return buttonIcons.get(button);
    }
}
