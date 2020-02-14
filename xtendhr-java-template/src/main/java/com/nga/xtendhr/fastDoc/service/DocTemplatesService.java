package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.DocTemplates;

public interface DocTemplatesService {
	public DocTemplates create(DocTemplates item);

	public DocTemplates update(DocTemplates item);

	public void delete(DocTemplates item);

	public List<DocTemplates> findAll();

	public List<DocTemplates> findById(String id);
}
