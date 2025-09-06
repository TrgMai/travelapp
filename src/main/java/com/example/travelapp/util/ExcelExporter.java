package com.example.travelapp.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JTable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExcelExporter {
	private ExcelExporter() {
	}

	public static void exportTable(JTable table, Path file, String sheetName) throws IOException {
		try (Workbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet(sheetName);
			Row header = sheet.createRow(0);
			for (int c = 0; c < table.getColumnCount(); c++) {
				Cell cell = header.createCell(c);
				cell.setCellValue(table.getColumnName(c));
			}
			for (int r = 0; r < table.getRowCount(); r++) {
				Row row = sheet.createRow(r + 1);
				for (int c = 0; c < table.getColumnCount(); c++) {
					Cell cell = row.createCell(c);
					Object v = table.getValueAt(r, c);
					if (v instanceof Number n) {
						cell.setCellValue(n.doubleValue());
					} else {
						cell.setCellValue(v != null ? v.toString() : "");
					}
				}
			}
			for (int c = 0; c < table.getColumnCount(); c++) {
				sheet.autoSizeColumn(c);
			}
			try (OutputStream out = Files.newOutputStream(file)) {
				wb.write(out);
			}
		}
	}
}
