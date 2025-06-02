package io.github.jadefalke2.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ButtonHeaderRenderer extends DefaultTableCellRenderer {

	private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

	private Color unselectedForeground;
	private Color unselectedBackground;


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
												   boolean isSelected, boolean hasFocus, int row, int column) {
		if (table == null) {
			return this;
		}

		Color fg = null;
		Color bg = null;

		JTable.DropLocation dropLocation = table.getDropLocation();
		if (dropLocation != null
			&& !dropLocation.isInsertRow()
			&& !dropLocation.isInsertColumn()
			&& dropLocation.getRow() == row
			&& dropLocation.getColumn() == column) {

			fg = UIManager.getColor("Table.dropCellForeground");
			bg = UIManager.getColor("Table.dropCellBackground");

			isSelected = true;
		}

		if (isSelected) {
			super.setForeground(fg == null ? table.getSelectionForeground()
				: fg);
			super.setBackground(bg == null ? table.getSelectionBackground()
				: bg);
		} else {
			Color background = unselectedBackground != null
				? unselectedBackground
				: table.getBackground();
			if (background == null || background instanceof javax.swing.plaf.UIResource) {
				Color alternateColor = UIManager.getColor("Table.alternateRowColor");
				if (alternateColor != null && row % 2 != 0) {
					background = alternateColor;
				}
			}
			super.setForeground(unselectedForeground != null
				? unselectedForeground
				: table.getForeground());
			super.setBackground(background);
		}

		setFont(table.getFont());

		if (hasFocus) {
			Border border = null;
			if (isSelected) {
				border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
			}
			if (border == null) {
				border = UIManager.getBorder("Table.focusCellHighlightBorder");
			}
			setBorder(border);

			if (!isSelected && table.isCellEditable(row, column)) {
				Color col;
				col = UIManager.getColor("Table.focusCellForeground");
				if (col != null) {
					super.setForeground(col);
				}
				col = UIManager.getColor("Table.focusCellBackground");
				if (col != null) {
					super.setBackground(col);
				}
			}
		} else {
			setBorder(getNoFocusBorder());
		}

		setValue(value);

		if (true) {
			try {
				Button button = Button.valueOf("KEY_" + value.toString());
				ImageIcon icon = ButtonIconLoader.getIcon(button);
				if (icon != null) {
					setIcon(icon);
					setText(null); // Clear text when icon is set
				}
				else {
					setIcon(null);
				}

			} catch (Exception e) {
				setIcon(null);
			}
		}

		return this;
	}

	private Border getNoFocusBorder() {
		Border border = UIManager.getBorder("Table.cellNoFocusBorder");
		if (System.getSecurityManager() != null) {
			if (border != null) return border;
			return SAFE_NO_FOCUS_BORDER;
		} else if (border != null) {
			if (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER) {
				return border;
			}
		}
		return noFocusBorder;
	}
//    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        JLabel label = new JLabel();
//		label.setHorizontalAlignment(SwingConstants.CENTER);
//		label.setFont(new Font("Arial", Font.PLAIN, 15));
//		if (row == -1 && false) {
//			try {
//				Button button = Button.valueOf("KEY_" + value.toString());
//				ImageIcon icon = ButtonIconLoader.getIcon(button);
//				if (icon != null) {
//					label.setIcon(icon);
//				} else {
//					label.setText(button.toString());
//				}
//
//			} catch (Exception e) {
//				label.setText(value.toString());
//			}
//		} else {
//			label.setText(value.toString());
//		}
//
//
//
//		return label;
//    }
}
