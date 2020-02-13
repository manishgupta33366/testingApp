package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.Fields;
import com.nga.xtendhr.fastDoc.model.TemplateCriteriaGeneration;

public interface TemplateCriteriaGenerationService {
	public TemplateCriteriaGeneration create(TemplateCriteriaGeneration item);

	public TemplateCriteriaGeneration update(TemplateCriteriaGeneration item);

	public void delete(TemplateCriteriaGeneration item);

	public List<TemplateCriteriaGeneration> findByTemplateID(String templateID);

	public List<Fields> getDistinctFields();
}
