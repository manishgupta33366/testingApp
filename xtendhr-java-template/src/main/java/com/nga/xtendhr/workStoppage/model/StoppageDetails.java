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
@NamedQueries({ @NamedQuery(name = "StoppageDetails.findAll", query = "SELECT SD FROM StoppageDetails SD") })
public class StoppageDetails {
	@Id
	@Column(name = "\"ID\"", columnDefinition = "VARCHAR(32)")
	private String id;

	@Column(name = "\"EMPOYEE_ID\"", columnDefinition = "VARCHAR(32)")
	private String employeeId;

	@Column(name = "\"STOPPAGE_TYPE.ID\"", columnDefinition = "VARCHAR(32)")
	private String stoppageType;

	@Column(name = "\"START_DATE\"", columnDefinition = "SECONDDATE")
	private Date startDate;

	@Column(name = "\"END_DATE\"", columnDefinition = "SECONDDATE")
	private Date endDate;

	@Column(name = "\"DOCUMENT\"", columnDefinition = "BLOB")
	private byte[] document;

	@Column(name = "\"DOCUMENT_TYPE\"", columnDefinition = "String(32)")
	private String documentType;

	@Column(name = "\"IS_APPROVED\"", columnDefinition = "BOOLEAN")
	private Boolean isApproved;

	@Column(name = "\"APPROVED_BY\"", columnDefinition = "String(32)")
	private String approvedBy;

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

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

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}
}
