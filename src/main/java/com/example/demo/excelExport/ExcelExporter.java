package com.example.demo.excelExport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;

public class ExcelExporter {

	public static <T> void exportStudentList(HttpServletResponse response, List<T> entity, String sheetName)
			throws IOException {
		if (entity == null || entity.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			response.getWriter().write("No data Found");
			return;
		}
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=" + sheetName + ".xlsx");

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			
			//filtering to export only fields with @ExcelColumn annotation and sorting based on order
			T first = entity.get(0);
			Field[] fields = Arrays.stream(first.getClass().getDeclaredFields())
					.filter(f -> f.isAnnotationPresent(ExcelColumn.class))
					.sorted(Comparator.comparingInt(f -> f.getAnnotation(ExcelColumn.class).order()))
					.toArray(Field[]::new);

			// header row
			Row header = sheet.createRow(0);
			for (int i = 0; i < fields.length; i++) {
				ExcelColumn column = fields[i].getAnnotation(ExcelColumn.class);
				header.createCell(i).setCellValue(column.name().isEmpty() ? fields[i].getName() : column.name());
			}

			// Data row
			for (int i = 0; i < entity.size(); i++) {
				Row row = sheet.createRow(i + 1);
				T studentData = entity.get(i);
				for (int r = 0; r < fields.length; r++) {
					fields[r].setAccessible(true);
					Object value = fields[r].get(studentData);
					row.createCell(r).setCellValue(value != null ? value.toString() : "");
				}
			}
			workbook.write(response.getOutputStream());
			response.flushBuffer();

		} catch (IllegalAccessException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
