package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.DocTemplateDetails;

public interface DocTemplateDetailsService {
	public DocTemplateDetails create(DocTemplateDetails item);

	public DocTemplateDetails update(DocTemplateDetails item);

	public void delete(DocTemplateDetails item);

	public List<DocTemplateDetails> findAll();

	public List<DocTemplateDetails> findByName(String name);
}
