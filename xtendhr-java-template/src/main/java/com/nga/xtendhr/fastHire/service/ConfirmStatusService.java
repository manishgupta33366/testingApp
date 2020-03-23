package com.nga.xtendhr.fastHire.service;

import java.util.List;

import com.nga.xtendhr.fastHire.model.ConfirmStatus;

public interface ConfirmStatusService {
	
	public List<ConfirmStatus> findAll();
	public ConfirmStatus findById(String id);
	public List<ConfirmStatus> findByCountryDepartment(String company , String department);
	public ConfirmStatus update(ConfirmStatus item);
	public ConfirmStatus create(ConfirmStatus item);
	public void deleteByObject(ConfirmStatus item);

}
