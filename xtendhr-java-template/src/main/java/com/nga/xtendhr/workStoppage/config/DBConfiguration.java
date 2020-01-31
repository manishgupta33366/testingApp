package com.nga.xtendhr.workStoppage.config;

/*
 * DataBase configuration file 
 * Used to dynamically pick schema and the artifact table path
 * 
 * @author	:	Manish Gupta  
 * @email	:	manish.g@ngahr.com
 * @version	:	0.0.1
 */

public class DBConfiguration {

	public static final String SCHEMA_NAME = "WORK_STOPPAGE";
	public static final String ARTIFACT_PATH = "com.nga.workStoppage";

	public static final String TABLE_PATH = ".db::Table.";

	public static final String STOPPAGE_TYPE = "\"" + ARTIFACT_PATH + TABLE_PATH + "WSD_STOPPAGE_TYPE\"";
	public static final String STOPPAGE_DETAILS = "\"" + ARTIFACT_PATH + TABLE_PATH + "WSR_STOPPAGE_DETAILS\"";
	public static final String STOPPAGE_DOCUMENTS = "\"" + ARTIFACT_PATH + TABLE_PATH + "WSR_STOPPAGE_DOCUMENTS\"";
}
