package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;

import javax.swing.*;

public class DurationAction implements Action{
	private final Script script;
	private final int row;
	private int oldValue;
	private int redoValue;
	private int newDuration;

	public DurationAction(Script script, int row) {
		this.script = script;
		this.row = row;
	}

	@Override
	public void execute() {

		oldValue = script.getLine(row).getDuration();

		int newDuration;
		try {
			//open popup to ask for duration
			newDuration = Integer.parseInt(JOptionPane.showInputDialog("Enter new duration:"));
			if (newDuration < 1) return;
			redoValue = newDuration;
			this.newDuration = newDuration;
		} catch (NumberFormatException e) {
			return;
		}

		script.setDuration(row, newDuration);


	}

	@Override
	public void revert() {
		script.setDuration(row, oldValue);
	}

	public void redo() {
			script.setDuration(row, redoValue);
	}

	@Override
	public String toString() {
		return "Duration Action, at frames: "+row+"->" +newDuration;
	}
}
