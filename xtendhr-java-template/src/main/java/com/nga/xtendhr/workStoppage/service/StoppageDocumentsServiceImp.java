package com.nga.xtendhr.workStoppage.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nga.xtendhr.workStoppage.model.StoppageDocuments;

@Transactional
@Component
public class StoppageDocumentsServiceImp implements StoppageDocumentsService {
	@PersistenceContext
	EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDocuments> findAll() {
		Query query = em.createNamedQuery("StoppageDetails.findAll");
		List<StoppageDocuments> items = query.getResultList();
		return items;
	}

	@Override
	@Transactional
	public StoppageDocuments update(StoppageDocuments item) {
		em.merge(item);
		return item;
	}

	@Override
	@Transactional
	public StoppageDocuments create(StoppageDocuments item) {
		em.persist(item);
		return item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDocuments> findByStoppageDetailsId(String id) {
		Query query = em.createNamedQuery("StoppageDocuments.findByStoppageDetailsId").setParameter("stoppageDetailsId",
				id);
		List<StoppageDocuments> items = query.getResultList();
		return items;
	}
}
