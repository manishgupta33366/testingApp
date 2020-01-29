package com.nga.xtendhr.workStoppage.service;

import java.util.List;

import com.nga.xtendhr.workStoppage.model.StoppageDetails;

public interface StoppageDetailsService {
	public List<StoppageDetails> findAll();

	public StoppageDetails update(StoppageDetails item);

	public StoppageDetails create(StoppageDetails item);

	public StoppageDetails findById(String id);

	public void deleteByObject(StoppageDetails item);

	public List<StoppageDetails> findByEmployeeId(String employeeId);

	public List<StoppageDetails> findAllApproved();

	public List<StoppageDetails> findAllNotApproved();
}
