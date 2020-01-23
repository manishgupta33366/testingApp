package com.nga.xtendhr.workStoppage.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nga.xtendhr.workStoppage.config.DBConfiguration;

@Entity
@Table(name = DBConfiguration.STOPPAGE_DETAILS, schema = DBConfiguration.SCHEMA_NAME)
@NamedQueries({ @NamedQuery(name = "StopageDetails.findAll", query = "SELECT SD FROM StopageDetails SD") })
public class StopageDetails {
	@Id
	@Column(name = "\"ID\"", columnDefinition = "VARCHAR(32)")
	private String id;

	@Column(name = "\"EMPOYEE_ID\"", columnDefinition = "VARCHAR(32)")
	private String employeeId;

	@Column(name = "\"STOPPAGE_TYPE\"", columnDefinition = "VARCHAR(32)")
	private String stoppageType;

	@Column(name = "\"START_DATE\"", columnDefinition = "SECONDDATE")
	private Date startDate;

	@Column(name = "\"END_DATE\"", columnDefinition = "SECONDDATE")
	private Date endDate;

	@Column(name = "\"TEMPLATE\"", columnDefinition = "BLOB")
	private byte[] template;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getStoppageType() {
		return stoppageType;
	}

	public void setStoppageType(String stoppageType) {
		this.stoppageType = stoppageType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}
}
