package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.ConfigurableTables;

public interface ConfigurableTablesService {
	public ConfigurableTables create(ConfigurableTables item);

	public ConfigurableTables update(ConfigurableTables item);

	public void delete(ConfigurableTables item);

	public List<ConfigurableTables> findAll();

	public ConfigurableTables findById(String id);
}
