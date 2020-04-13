package com.nga.xtendhr.workStoppage.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nga.xtendhr.workStoppage.model.StoppageDetails;

@Transactional
@Component
public class StoppageDetailsServiceImp implements StoppageDetailsService {
	@PersistenceContext
	EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDetails> findAll() {
		Query query = em.createNamedQuery("StoppageDetails.findAll");
		List<StoppageDetails> items = query.getResultList();
		return items;
	}

	@Override
	@Transactional
	public StoppageDetails update(StoppageDetails item) {
		em.merge(item);
		return item;
	}

	@Override
	@Transactional
	public StoppageDetails create(StoppageDetails item) {
		em.persist(item);
		return item;
	}

	@Override
	public StoppageDetails findById(String id) {
		StoppageDetails item = em.find(StoppageDetails.class, id);
		return item;
	}

	@Override
	@Transactional
	public void deleteByObject(StoppageDetails item) {
		em.remove(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDetails> findByEmployeeId(String employeeId) {
		Query query;
		List<StoppageDetails> items;
		query = em.createNamedQuery("StoppageDetails.findByEmployeeId").setParameter("employeeId", employeeId);
		items = query.getResultList();
		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDetails> findAllApproved() {
		Query query = em.createNamedQuery("StoppageDetails.findAllApproved").setParameter("isApproved", true);
		List<StoppageDetails> items = query.getResultList();
		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDetails> findAllRejected() {
		Query query = em.createNamedQuery("StoppageDetails.findAllRejected").setParameter("isRejected", true);
		List<StoppageDetails> items = query.getResultList();
		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StoppageDetails> findAllNotApproved() { // All those are available for approval
		Query query = em.createNamedQuery("StoppageDetails.findAllNotApproved").setParameter("isApproved", false)
				.setParameter("isRejected", false);
		List<StoppageDetails> items = query.getResultList();
		return items;
	}

}
