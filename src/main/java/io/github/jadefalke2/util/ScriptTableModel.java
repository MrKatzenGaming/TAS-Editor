package io.github.jadefalke2.util;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import io.github.jadefalke2.Script;

import java.util.stream.IntStream;

import static io.github.jadefalke2.util.Util.getFixedColumnCount;

public class ScriptTableModel extends AbstractTableModel implements ScriptObserver {

	private final Script script;

	public ScriptTableModel(Script script) {
		this.script = script;
		script.attachObserver(this);
	}

	@Override
	public void onDirtyChange(boolean dirty) {
		fireTableChanged(new TableModelEvent(this, 0, getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}

	@Override
	public void onLengthChange(int length) {
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return script.getNumLines();
	}

	@Override
	public int getColumnCount() {
		return Button.values().length + getFixedColumnCount();  // frame, duration, left stick, right stick
	}


	@Override
	public String getColumnName(int column) {
		return switch (column) {
			case 0 -> "Frame";
			case 1 -> getFixedColumnCount() == 4 ? "Duration": "L-Stick";
			case 2 -> getFixedColumnCount() == 4 ? "L-Stick": "R-Stick";
			case 3 -> getFixedColumnCount() == 4 ? "R-Stick":Button.values()[column - 3].toString();
			default -> Button.values()[column - 4].toString();
		};
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return switch (columnIndex) {
			case 0 -> getFixedColumnCount() == 4?
						IntStream.range(0, rowIndex).map(i -> script.getLine(i).getDuration()).sum() : rowIndex;
			case 1 ->  getFixedColumnCount() == 4 ?
						script.getLine(rowIndex).getDuration(): script.getLine(rowIndex).getStickL().toCartString();
			case 2 ->  getFixedColumnCount() == 4 ?
						script.getLine(rowIndex).getStickL().toCartString(): script.getLine(rowIndex).getStickR().toCartString();
			case 3 ->  getFixedColumnCount() == 4 ?
						script.getLine(rowIndex).getStickR().toCartString(): script.getLine(rowIndex).buttons.contains(Button.values()[columnIndex - getFixedColumnCount()]) ? Button.values()[columnIndex - getFixedColumnCount()].toString() : "";
			default ->
				script.getLine(rowIndex).buttons.contains(Button.values()[columnIndex - getFixedColumnCount()]) ? Button.values()[columnIndex - getFixedColumnCount()].toString() : "";
		};
	}
}
