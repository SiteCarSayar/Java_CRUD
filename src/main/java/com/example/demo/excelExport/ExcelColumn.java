package com.example.demo.excelExport;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface ExcelColumn {
	String name() default "";
	int order() default 0;

}
