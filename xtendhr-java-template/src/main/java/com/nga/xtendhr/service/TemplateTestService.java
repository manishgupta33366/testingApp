package com.nga.xtendhr.service;

import java.util.List;

import com.nga.xtendhr.model.TemplateTest;

public interface TemplateTestService {
	public TemplateTest create(TemplateTest item);

	public TemplateTest update(TemplateTest item);

	public void delete(TemplateTest item);

	public List<TemplateTest> findById(String item);
}
