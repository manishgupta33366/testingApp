package com.nga.xtendhr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nga.xtendhr.config.DBConfiguration;

@Entity
@Table(name = DBConfiguration.FHD_COUNTRIES, schema = DBConfiguration.SCHEMA_NAME)
@NamedQueries({ @NamedQuery(name = "Country.findAll", query = "SELECT co FROM Country co") })
public class Country {

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
