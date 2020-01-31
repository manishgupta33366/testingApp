package com.nga.xtendhr.workStoppage.service;

import java.util.List;

import com.nga.xtendhr.workStoppage.model.StoppageDocuments;

public interface StoppageDocumentsService {
	public List<StoppageDocuments> findAll();

	public StoppageDocuments update(StoppageDocuments item);

	public StoppageDocuments create(StoppageDocuments item);

	public List<StoppageDocuments> findByStoppageDetailsId(String id);
}
