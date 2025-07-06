package io.github.jadefalke2.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

public class CustomPianoRollCellRenderer extends DefaultTableCellRenderer {

	public CustomPianoRollCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
												   boolean isSelected, boolean hasFocus, int row, int column) {

		try {
			Button button = Button.valueOf("KEY_" + value.toString());
			ImageIcon icon = ButtonIconLoader.getIcon(button);
			if (icon != null) {
				setIcon(icon);
				value = "";
			}
			else {
				setIcon(null);
			}

		} catch (Exception e) {
			setIcon(null);
		}

		return super.getTableCellRendererComponent(table, "<html>"+value+"</html>", isSelected, hasFocus, row, column);
	}
}
