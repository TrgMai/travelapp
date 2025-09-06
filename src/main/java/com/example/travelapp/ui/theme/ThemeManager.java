package com.example.travelapp.ui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;

public final class ThemeManager {
	private ThemeManager() {
	}

	private static final String DEFAULT_THEME = "/themes/forest_light.properties";
	private static final Properties PROPS = new Properties();

	public static void applyTheme() {
		applyTheme(DEFAULT_THEME);
	}

	public static void applyTheme(String classpathProps) {
		try (InputStream in = ThemeManager.class.getResourceAsStream(classpathProps)) {
			if (in == null) {
				throw new IllegalArgumentException("Theme not found: " + classpathProps);
			}

			UIManager.setLookAndFeel(new FlatLightLaf());
			PROPS.clear();
			PROPS.load(in);

			putColor("Component.focusColor", prop("colors.focusColor"));
			putColor("Component.selectionBackground", prop("colors.selectionBackground"));
			putColor("Component.selectionForeground", prop("colors.selectionForeground"));

			putInt("Button.arc", propInt("buttons.arc"));
			putInt("Component.arc", propInt("components.arc"));
			putInt("Component.focusWidth", propInt("components.focusWidth"));
			putInt("ProgressBar.arc", propInt("progress.arc"));
			putInt("Slider.thumbArc", propInt("slider.thumbArc"));

			UIManager.put("Color.background", decode(prop("colors.background")));
			UIManager.put("Color.foreground", decode(prop("colors.foreground")));
			UIManager.put("Color.separator", decode(prop("colors.separatorColor")));
			UIManager.put("Color.border", decode(prop("colors.componentBorderColor")));

			mapAppToken("tt.PRIMARY");
			mapAppToken("tt.PRIMARY_HOVER");
			mapAppToken("tt.PRIMARY_PRESSED");
			mapAppToken("tt.ON_PRIMARY");

			mapAppToken("tt.SURFACE");
			mapAppToken("tt.SURFACE_ALT");
			mapAppToken("tt.BORDER");
			mapAppToken("tt.HOVER");
			mapAppToken("tt.TABLE_STRIPE");
			mapAppToken("tt.MUTED");
			mapAppToken("tt.TEXT");
			mapAppToken("tt.TEXT_MUTED");

			mapAppToken("tt.SUCCESS");
			mapAppToken("tt.WARNING");
			mapAppToken("tt.DANGER");

			UIManager.put("tt.SHADOW", decodeAlpha(prop("tt.SHADOW")));

			UIManager.put("tt.font.family", prop("typography.fontFamily"));
			UIManager.put("tt.font.size.xs", propInt("typography.sizeXS"));
			UIManager.put("tt.font.size.sm", propInt("typography.sizeSM"));
			UIManager.put("tt.font.size.md", propInt("typography.sizeMD"));
			UIManager.put("tt.font.size.lg", propInt("typography.sizeLG"));
			UIManager.put("tt.font.size.xl", propInt("typography.sizeXL"));
			UIManager.put("tt.font.weight.regular", propInt("typography.weightRegular"));
			UIManager.put("tt.font.weight.medium", propInt("typography.weightMedium"));
			UIManager.put("tt.font.weight.bold", propInt("typography.weightBold"));

			UIManager.put("tt.space.4", propInt("spacing.s4"));
			UIManager.put("tt.space.6", propInt("spacing.s6"));
			UIManager.put("tt.space.8", propInt("spacing.s8"));
			UIManager.put("tt.space.12", propInt("spacing.s12"));
			UIManager.put("tt.space.16", propInt("spacing.s16"));
			UIManager.put("tt.space.20", propInt("spacing.s20"));
			UIManager.put("tt.space.24", propInt("spacing.s24"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Color color(String key) {
		Object v = UIManager.get(key);
		if (v instanceof Color) {
			return (Color) v;
		}
		throw new IllegalStateException("Missing color key: " + key);
	}

	public static int intval(String key) {
		Object v = UIManager.get(key);
		if (v instanceof Integer) {
			return (Integer) v;
		}
		throw new IllegalStateException("Missing int key: " + key);
	}

	public static String str(String key) {
		Object v = UIManager.get(key);
		if (v != null) {
			return v.toString();
		}
		throw new IllegalStateException("Missing string key: " + key);
	}

	private static void mapAppToken(String key) {
		UIManager.put(key, decode(prop(key)));
	}

	private static void putColor(String key, String hex) {
		if (hex != null) {
			UIManager.put(key, decode(hex));
		}
	}

	private static void putInt(String key, Integer val) {
		if (val != null) {
			UIManager.put(key, val);
		}
	}

	private static String prop(String key) {
		String v = PROPS.getProperty(key);
		if (v == null) {
			int i = key.lastIndexOf('.');
			if (i >= 0) {
				v = PROPS.getProperty(key.substring(i + 1));
			}
		}
		if (v == null) {
			throw new IllegalStateException("Missing property: " + key);
		}
		return v.trim();
	}

	private static Integer propInt(String key) {
		String v = PROPS.getProperty(key);
		if (v == null) {
			int i = key.lastIndexOf('.');
			if (i >= 0) {
				v = PROPS.getProperty(key.substring(i + 1));
			}
		}
		if (v == null) {
			return null;
		}
		return Integer.parseInt(v.trim());
	}

	private static Color decode(String s) {
		if (s.startsWith("rgba")) {
			return decodeAlpha(s);
		}
		return Color.decode(s);
	}

	private static Color decodeAlpha(String rgba) {
		String t = rgba.replace("rgba(", "").replace(")", "");
		String[] p = t.split(",");
		int r = Integer.parseInt(p[0].trim());
		int g = Integer.parseInt(p[1].trim());
		int b = Integer.parseInt(p[2].trim());
		float a = Float.parseFloat(p[3].trim());
		return new Color(r, g, b, Math.round(a * 255));
	}
}
