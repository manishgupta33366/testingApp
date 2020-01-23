package com.nga.xtendhr.workStoppage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nga.xtendhr.workStoppage.config.DBConfiguration;

@Entity
@Table(name = DBConfiguration.STOPPAGE_TYPE, schema = DBConfiguration.SCHEMA_NAME)
@NamedQueries({ @NamedQuery(name = "StoppageType.findAll", query = "SELECT ST FROM StoppageType ST") })
public class StoppageType {
	@Id
	@Column(name = "\"ID\"", columnDefinition = "VARCHAR(32)")
	private String id;

	@Column(name = "\"NAME\"", columnDefinition = "VARCHAR(64)")
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
