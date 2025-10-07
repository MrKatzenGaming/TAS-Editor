package io.github.jadefalke2.components;

import io.github.jadefalke2.util.Button;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumnModel;
import java.awt.Component;
import java.awt.Point;


import static io.github.jadefalke2.util.Util.getFixedColumnCount;

public class ColumnRightClickMenu extends JPopupMenu {

	private final JCheckBoxMenuItem[] items;

	public ColumnRightClickMenu(PianoRoll pianoRoll){
		TableColumnModel model = pianoRoll.getColumnModel();
		items = new JCheckBoxMenuItem[Button.values().length + getFixedColumnCount()];

		items[0] = new JCheckBoxMenuItem("Frame");
		if (getFixedColumnCount()==4) {
			items[1] = new JCheckBoxMenuItem("Duration");
			items[2] = new JCheckBoxMenuItem("L-Stick");
			items[3] = new JCheckBoxMenuItem("R-Stick");
		}
		else {
			items[1] = new JCheckBoxMenuItem("L-Stick");
			items[2] = new JCheckBoxMenuItem("R-Stick");
		}
		for(int i = getFixedColumnCount(); i < items.length; i++){
			items[i] = new JCheckBoxMenuItem(Button.values()[i-getFixedColumnCount()].toString());
		}

		for(int i=0; i<model.getColumnCount(); i++) {
			items[i].setSelected(model.getColumn(pianoRoll.convertColumnIndexToView(i)).getMaxWidth() != 0);
			items[i].addActionListener(e -> {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				int index = -1;
				for(int j=0; j<items.length; j++){
					if(items[j] == item){
						index = j;
						break;
					}
				}
				if(index == -1) {
					return;
				}
				int modelIndex = pianoRoll.convertColumnIndexToView(index);
				if(items[index].isSelected()){
					model.getColumn(modelIndex).setMinWidth(15);
					model.getColumn(modelIndex).setMaxWidth(Integer.MAX_VALUE);
				} else {
					model.getColumn(modelIndex).setMinWidth(0);
					model.getColumn(modelIndex).setMaxWidth(0);
				}
				pianoRoll.adjustColumnWidth();
			});
		}

		for(JCheckBoxMenuItem item : items){
			add(item);
		}

		pack();
	}

	/**
	 * opens the popup menu with the line actions
	 * @param point the point at which the menu "spawns"
	 */
	public void openPopUpMenu(Point point, Component invoker){
		show(invoker,(int)point.getX(),(int)point.getY());
	}
}
