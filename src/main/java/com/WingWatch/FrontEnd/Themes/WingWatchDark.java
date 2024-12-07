package com.WingWatch.FrontEnd.Themes;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class WingWatchDark extends FlatMacDarkLaf {
	public static final String NAME = "WingWatchDark";

	public static boolean setup() {
		return setup(new WingWatchDark());
	}

	public static void installLafInfo() {
		installLafInfo( NAME, WingWatchDark.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
