package com.nga.xtendhr.fastHire.service;

import java.util.List;

import com.nga.xtendhr.fastHire.model.CodeListText;

public interface CodeListTextService {
	public List<CodeListText> findAll();
	public CodeListText findById(String codeListId,String language,String value);
	public CodeListText update(CodeListText item);
	public CodeListText create(CodeListText item);
	public List<CodeListText> findByCodeListIdLang(String codeListId,String language);
	public void deleteByObject(CodeListText item);
}
