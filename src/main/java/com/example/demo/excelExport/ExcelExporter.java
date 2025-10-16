package com.example.demo.excelExport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Student;

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

			// filtering to export only fields with @ExcelColumn annotation and sorting
			// based on order
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

	public static List<Student> importStudentList(MultipartFile file) throws IOException {
		List<Student> students = new ArrayList<>();

		try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				// Skip completely empty rows
				boolean hasData = false;
				for (int c = 0; c <= 10; c++) { // assuming 11 columns
					Cell cell = row.getCell(c);
					if (cell != null && !cell.toString().isBlank()) {
						hasData = true;
						break;
					}
				}
				if (!hasData)
					continue;

				Student student = new Student();
				student.setStudentID(row.getCell(0) != null ? (long) row.getCell(0).getNumericCellValue() : null);
				student.setStudentName(row.getCell(1) != null ? row.getCell(1).getStringCellValue().trim() : null);
				student.setFatherName(row.getCell(2) != null ? row.getCell(2).getStringCellValue().trim() : null);
				student.setDateOfBirth(
						row.getCell(3) != null ? row.getCell(3).getLocalDateTimeCellValue().toLocalDate() : null);

				if (row.getCell(4) != null) {
				    student.setGenderString(row.getCell(4).getStringCellValue().trim());
				} else {
				    student.setGenderString(null);
				}

				student.setNrcNo(row.getCell(5) != null ? row.getCell(5).getStringCellValue().trim() : null);

				if (row.getCell(6) != null) {
				    String gradeInput = row.getCell(6).getStringCellValue().trim();
				    student.setGradeString(gradeInput); // store the raw Excel string
				    student.setGrade(null);             // do not convert to enum yet
				}

				student.setNationality(row.getCell(7) != null ? row.getCell(7).getStringCellValue().trim() : null);
				student.setPhoneNumber(row.getCell(8) != null ? row.getCell(8).getStringCellValue().trim() : null);
				student.setEmail(row.getCell(9) != null ? row.getCell(9).getStringCellValue().trim() : null);
				student.setAddress(row.getCell(10) != null ? row.getCell(10).getStringCellValue().trim() : null);

				students.add(student);
			}
		}

		return students;
	}

}
