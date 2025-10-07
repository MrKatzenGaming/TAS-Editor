package io.github.jadefalke2.actions;

import io.github.jadefalke2.Script;

import javax.swing.*;

public class DurationAction implements Action{
	private final Script script;
	private final int row;
	private int[] oldValues;
	private int newDuration;

	public DurationAction(Script script, int row) {
		this.script = script;
		this.row = row;
	}

	@Override
	public void execute() {
		oldValues = new int[1];
		for(int i=0; i<oldValues.length; i++) {
			oldValues[i] = script.getLine(row+i).getDuration();
		}
		int newDuration;
		try {
			//open popup to ask for duration
			newDuration = Integer.parseInt(JOptionPane.showInputDialog("Enter new duration:"));
			if (newDuration < 1) return; //invalid input
			this.newDuration = newDuration;
		} catch (NumberFormatException e) {
			return; //invalid input
		}
		//set new duration
		for(int i=row; i<=row; i++) {
			script.setDuration(i, newDuration);
		}
	}

	@Override
	public void revert() {
		for(int i=row; i<=row; i++) {
			script.setDuration(i,oldValues[i-row]);
		}
	}

	@Override
	public String toString() {
		return "Duration Action, at frames: "+row+";" +newDuration;
	}
}
