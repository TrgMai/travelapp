package com.example.travelapp.ui.theme;

import java.awt.*;

public final class ThemeTokens {
    private ThemeTokens() {
    }

    public static final String FONT_FAMILY = ThemeManager.str("tt.font.family");
    public static final int FONT_SIZE_XS = ThemeManager.intval("tt.font.size.xs");
    public static final int FONT_SIZE_SM = ThemeManager.intval("tt.font.size.sm");
    public static final int FONT_SIZE_BASE = ThemeManager.intval("tt.font.size.md");
    public static final int FONT_SIZE_LG = ThemeManager.intval("tt.font.size.lg");
    public static final int FONT_SIZE_XL = ThemeManager.intval("tt.font.size.xl");
    public static final int FONT_WEIGHT_REGULAR = ThemeManager.intval("tt.font.weight.regular");
    public static final int FONT_WEIGHT_MEDIUM = ThemeManager.intval("tt.font.weight.medium");
    public static final int FONT_WEIGHT_BOLD = ThemeManager.intval("tt.font.weight.bold");

    public static final int SPACE_4 = ThemeManager.intval("tt.space.4");
    public static final int SPACE_6 = ThemeManager.intval("tt.space.6");
    public static final int SPACE_8 = ThemeManager.intval("tt.space.8");
    public static final int SPACE_12 = ThemeManager.intval("tt.space.12");
    public static final int SPACE_16 = ThemeManager.intval("tt.space.16");
    public static final int SPACE_20 = ThemeManager.intval("tt.space.20");
    public static final int SPACE_24 = ThemeManager.intval("tt.space.24");

    public static Color PRIMARY() {
        return ThemeManager.color("tt.PRIMARY");
    }

    public static Color PRIMARY_HOVER() {
        return ThemeManager.color("tt.PRIMARY_HOVER");
    }

    public static Color PRIMARY_PRESSED() {
        return ThemeManager.color("tt.PRIMARY_PRESSED");
    }

    public static Color ON_PRIMARY() {
        return ThemeManager.color("tt.ON_PRIMARY");
    }

    public static Color SURFACE() {
        return ThemeManager.color("tt.SURFACE");
    }

    public static Color SURFACE_ALT() {
        return ThemeManager.color("tt.SURFACE_ALT");
    }

    public static Color BORDER() {
        return ThemeManager.color("tt.BORDER");
    }

    public static Color HOVER() {
        return ThemeManager.color("tt.HOVER");
    }

    public static Color TABLE_STRIPE() {
        return ThemeManager.color("tt.TABLE_STRIPE");
    }

    public static Color TEXT() {
        return ThemeManager.color("tt.TEXT");
    }

    public static Color TEXT_MUTED() {
        return ThemeManager.color("tt.TEXT_MUTED");
    }

    public static Color MUTED() {
        return ThemeManager.color("tt.MUTED");
    }

    public static Color SUCCESS() {
        return ThemeManager.color("tt.SUCCESS");
    }

    public static Color WARNING() {
        return ThemeManager.color("tt.WARNING");
    }

    public static Color DANGER() {
        return ThemeManager.color("tt.DANGER");
    }

    public static Color SHADOW() {
        return ThemeManager.color("tt.SHADOW");
    }

    public static Color[] CHART_PALETTE() {
        return new Color[] {
                PRIMARY(), SUCCESS(), WARNING(), DANGER(),
                tint(PRIMARY(), .25f), tint(SUCCESS(), .25f),
                shade(WARNING(), .15f), shade(PRIMARY(), .15f),
                tint(DANGER(), .25f), shade(SUCCESS(), .15f)
        };
    }

    private static Color tint(Color c, float f) {
        int r = c.getRed() + Math.round((255 - c.getRed()) * f);
        int g = c.getGreen() + Math.round((255 - c.getGreen()) * f);
        int b = c.getBlue() + Math.round((255 - c.getBlue()) * f);
        return new Color(r, g, b);
    }

    private static Color shade(Color c, float f) {
        int r = Math.round(c.getRed() * (1 - f));
        int g = Math.round(c.getGreen() * (1 - f));
        int b = Math.round(c.getBlue() * (1 - f));
        return new Color(r, g, b);
    }

}
