package com.nga.xtendhr.fastDoc.service;

import java.util.List;

import com.nga.xtendhr.fastDoc.model.ConfigurableColumns;

public interface ConfigurableColumnsService {
	public ConfigurableColumns create(ConfigurableColumns item);

	public ConfigurableColumns update(ConfigurableColumns item);

	public void delete(ConfigurableColumns item);

	public List<ConfigurableColumns> findByTableID(String id);

	public List<String> getColumnNamesByTableID(String id);
}
