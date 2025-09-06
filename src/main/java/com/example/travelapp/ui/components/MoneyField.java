package com.example.travelapp.ui.components;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class MoneyField extends JFormattedTextField {

	public MoneyField() {
		super(integerFormatter());
		setColumns(12);
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	private static NumberFormatter integerFormatter() {
		NumberFormatter nf = new NumberFormatter(NumberFormat.getIntegerInstance());
		nf.setAllowsInvalid(false);
		nf.setMinimum(0L);
		nf.setCommitsOnValidEdit(true);
		return nf;
	}

	public BigDecimal getBigDecimal() {
		Object v = getValue();
		return (v instanceof Number n) ? BigDecimal.valueOf(n.longValue()) : null;
	}

	public void setBigDecimal(BigDecimal bd) {
		setValue(bd);
	}
}
