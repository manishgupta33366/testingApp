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
@NamedQueries({
		@NamedQuery(name = "StoppageDetails.findAll", query = "SELECT SD FROM StoppageDetails SD ORDER BY SD.startDate DESC"),
		@NamedQuery(name = "StoppageDetails.findAllApproved", query = "SELECT SD FROM StoppageDetails SD WHERE SD.isApproved = :isApproved ORDER BY SD.startDate DESC"),
		@NamedQuery(name = "StoppageDetails.findAllNotApproved", query = "SELECT SD FROM StoppageDetails SD WHERE SD.isApproved = :isApproved ORDER BY SD.startDate DESC"),
		@NamedQuery(name = "StoppageDetails.findByEmployeeId", query = "SELECT SD FROM StoppageDetails SD WHERE SD.employeeId = :employeeId ORDER BY SD.startDate DESC") })
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

	@Column(name = "\"IS_APPROVED\"", columnDefinition = "BOOLEAN")
	private Boolean isApproved;

	@Column(name = "\"APPROVED_BY\"", columnDefinition = "String(32)")
	private String approvedBy;

	@Column(name = "\"APPROVED_ON\"", columnDefinition = "SECONDDATE")
	private Date approvedOn;

	@Column(name = "\"IS_THERAPEUTIC\"", columnDefinition = "BOOLEAN")
	private Boolean isTherapeutic;

	@Column(name = "\"THERAPY_START_DATE\"", columnDefinition = "SECONDDATE")
	private Date therapyStartDate;

	@Column(name = "\"THERAPY_END_DATE\"", columnDefinition = "SECONDDATE")
	private Date therapyEndDate;

	@Column(name = "\"ACCIDENT_TYPE\"", columnDefinition = "String(32)")
	private String accidentType;

	@Column(name = "\"WITH_STOPPAGE\"", columnDefinition = "BOOLEAN")
	private Boolean withStoppage;

	@Column(name = "\"SICK_TYPE\"", columnDefinition = "String(32)")
	private String sickType;

	@Column(name = "\"PREGNANCY_RELATED\"", columnDefinition = "BOOLEAN")
	private Boolean pregnancyRelated;

	public String getAccidentType() {
		return accidentType;
	}

	public void setAccidentType(String accidentType) {
		this.accidentType = accidentType;
	}

	public Boolean getWithStoppage() {
		return withStoppage;
	}

	public void setWithStoppage(Boolean withStoppage) {
		this.withStoppage = withStoppage;
	}

	public String getSickType() {
		return sickType;
	}

	public void setSickType(String sickType) {
		this.sickType = sickType;
	}

	public Boolean getPregnancyRelated() {
		return pregnancyRelated;
	}

	public void setPregnancyRelated(Boolean pregnancyRelated) {
		this.pregnancyRelated = pregnancyRelated;
	}

	public Boolean getIsTherapeutic() {
		return isTherapeutic;
	}

	public void setIsTherapeutic(Boolean isTherapeutic) {
		this.isTherapeutic = isTherapeutic;
	}

	public Date getTherapyStartDate() {
		return therapyStartDate;
	}

	public void setTherapyStartDate(Date therapyStartDate) {
		this.therapyStartDate = therapyStartDate;
	}

	public Date getTherapyEndDate() {
		return therapyEndDate;
	}

	public void setTherapyEndDate(Date therapyEndDate) {
		this.therapyEndDate = therapyEndDate;
	}

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

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

}
