package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.ConfigurableColumns;
import com.nga.xtendhr.fastDoc.model.Countries;

public interface CountryFastDocService {

	public Countries create(Countries item);

	public Countries update(Countries item);

	public void delete(Countries item);

	public List<Countries> findAll();

	public Countries findById(String id);

	public List<Countries> dynamicSelect(List<ConfigurableColumns> requiredColumns);

	List<Countries> findAll(String locale);
}
