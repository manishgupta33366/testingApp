package com.nga.xtendhr.fastHire.service;

import java.util.List;

import com.nga.xtendhr.fastHire.model.SFConstants;

public interface SFConstantsService {
	public List<SFConstants> findAll();
	public SFConstants update(SFConstants item);
	public SFConstants create(SFConstants item);
	public SFConstants findById(String technicalName);
	public void deleteByObject(SFConstants item);

}
