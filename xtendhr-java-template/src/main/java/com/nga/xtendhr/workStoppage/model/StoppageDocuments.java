package com.nga.xtendhr.workStoppage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nga.xtendhr.workStoppage.config.DBConfiguration;

@Entity
@Table(name = DBConfiguration.STOPPAGE_DOCUMENTS, schema = DBConfiguration.SCHEMA_NAME)
@NamedQueries({ @NamedQuery(name = "StoppageDocuments.findAll", query = "SELECT SD FROM StoppageDocuments SD"),
		@NamedQuery(name = "StoppageDocuments.findByStoppageDetailsId", query = "SELECT SD FROM StoppageDocuments SD WHERE SD.stoppageDetailsId = :stoppageDetailsId") })
public class StoppageDocuments {
	@Id
	@Column(name = "\"ID\"", columnDefinition = "VARCHAR(32)")
	private String id;

	@Column(name = "\"STOPPAGE_DETAILS.ID\"", columnDefinition = "VARCHAR(32)")
	private String stoppageDetailsId;

	@Column(name = "\"DOCUMENT\"", columnDefinition = "BLOB")
	private byte[] document;

	@Column(name = "\"DOCUMENT_TYPE\"", columnDefinition = "String(32)")
	private String documentType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStoppageDetailsId() {
		return stoppageDetailsId;
	}

	public void setStoppageDetailsId(String stoppageDetailsId) {
		this.stoppageDetailsId = stoppageDetailsId;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

}
