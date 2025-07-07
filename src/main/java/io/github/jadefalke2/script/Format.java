package io.github.jadefalke2.script;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.CorruptedScriptException;

import java.io.File;
import java.io.IOException;

public enum Format {

	STAS,
	nxTAS,
	TSVTAS;

	public static Script read(File file, Format format) throws IOException, CorruptedScriptException {
		switch (format) {
			case nxTAS -> {return NXTas.read(file);}
			case STAS -> {return STas.read(file);}
			case TSVTAS -> {return TSVTas.read(file);}
			default -> throw new IllegalStateException("Unexpected value: " + format);
		}
	}
	public static void write(Script script, File file, Format format) throws IOException {
		switch (format) {
			case nxTAS -> NXTas.write(script, file);
			case STAS -> STas.write(script, file);
			case TSVTAS -> TSVTas.write(script, file);
			default -> throw new IllegalStateException("Unexpected value: " + format);
		}
	}

}
