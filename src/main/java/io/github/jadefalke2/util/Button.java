package io.github.jadefalke2.util;

//TODO still not happy with this, as it handles both frontend and backend...
//toString used for displaying, name for script files

public enum Button {
	KEY_A,
	KEY_B,
	KEY_X,
	KEY_Y,

	KEY_ZR,
	KEY_ZL,

	KEY_R,
	KEY_L,

	KEY_PLUS,
	KEY_MINUS,

	KEY_DLEFT,
	KEY_DRIGHT,
	KEY_DUP,
	KEY_DDOWN,

	KEY_LSTICK,
	KEY_RSTICK,

	KEY_M,
	KEY_MU,
	KEY_MD,
	KEY_ML,
	KEY_MR,

	KEY_MUU,
	KEY_MDD,
	KEY_MLL,
	KEY_MRR;


	@Override
	public String toString() {
		return name().replace("KEY_", "");
	}
}
