package com.nga.xtendhr.fastHire.service;

import java.util.List;

import com.nga.xtendhr.fastHire.model.FieldGroupText;

public interface FieldGroupTextService {
	public List<FieldGroupText> findAll();
	public FieldGroupText update(FieldGroupText item);
	public FieldGroupText create(FieldGroupText item);
	public List<FieldGroupText> findByFieldGroupId(String fieldGroupId);
	public FieldGroupText findByFieldGroupLanguage(String fieldGroupId,String language);
	public void deleteByObject(FieldGroupText item);

}
