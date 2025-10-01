package com.example.demo.common;

import java.util.List;

public class PageResponse<T> {
	 private List<T> content;
	    private long totalElements;

	    public PageResponse(List<T> content, long totalElements) {
	        this.content = content;
	        this.totalElements = totalElements;
	    }

	    public List<T> getContent() {
	        return content;
	    }

	    public long getTotalElements() {
	        return totalElements;
	    }

}
