package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.Companies;

public interface CompaniesService {
	public Companies create(Companies item);

	public Companies update(Companies item);

	public void delete(Companies item);

	public List<Companies> findAll();
}
