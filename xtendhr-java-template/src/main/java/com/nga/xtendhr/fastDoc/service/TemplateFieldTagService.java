package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.TemplateFieldTag;

public interface TemplateFieldTagService {
	public TemplateFieldTag create(TemplateFieldTag item);

	public TemplateFieldTag update(TemplateFieldTag item);

	public void delete(TemplateFieldTag item);

	public List<TemplateFieldTag> findAll();
}
