package net.whydah.uss.util.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public abstract class CRUDRepository<T, K> extends Repository<T, K> {
	
	 @Inject
	private DeletionHelper deletionHelper;
	 
	@Override
	public T insert(T t) {
		
		Objects.requireNonNull(t, "Object must not be null");

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			em.persist(t);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}
		return t;
	}
	
	@Override
	public List<T> insertAll(List<T> objs) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			final int numberOfRecords = objs.size();
            final int batchSize = 30; // same as the JDBC batch size "hibernate.jdbc.batch_size". Check map.put(AvailableSettings.STATEMENT_BATCH_SIZE, "30");
            for (int i = 1; i <= numberOfRecords; i++) {
                
                em.persist(objs.get(i-1));
 
                if (i % batchSize == 0 && i != numberOfRecords) {
                    em.flush();
                    em.clear();
                }
            }
			
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}
		return objs;
	}

	@Override
	public T update(T t) {
		
		Objects.requireNonNull(t, "Object must not be null");

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			em.merge(t);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}
		return t;
	}

	@Override
	public List<T> updateAll(List<T> objs) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			final int numberOfRecords = objs.size();
            final int batchSize = 30; // same as the JDBC batch size "hibernate.jdbc.batch_size". Check map.put(AvailableSettings.STATEMENT_BATCH_SIZE, "30");
            
            for (int i = 1; i <= numberOfRecords; i++) {
                
                em.merge(objs.get(i-1));
 
                if (i % batchSize == 0 && i != numberOfRecords) {
                    em.flush();
                    em.clear();
                }
            }
			
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}
		return objs;
	}
	
	@Override
	public Optional<T> findById(K key) {
		EntityManager em = emf.createEntityManager();
		T t = em.find(getClassType(), key);
		return Optional.ofNullable(t);
	}

	@Override
	public void delete(T obj) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();

			em.remove(em.contains(obj) ? obj : em.merge(obj));

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}
		
	}

	@Override
	public void deleteById(K key) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			try {
				Object ref = em.getReference(getClassType(), key);
				em.remove(ref);
			} catch (EntityNotFoundException e) {
				
			}
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}

		
	}
	
	@Override
	public void deleteAllById(List<K> keys) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			final int numberOfRecords = keys.size();
            final int batchSize = 30; // same as the JDBC batch size "hibernate.jdbc.batch_size". Check map.put(AvailableSettings.STATEMENT_BATCH_SIZE, "30");
            
            for (int i = 1; i <= numberOfRecords; i++) {
                
            	T t = em.find(getClassType(), keys.get(i - 1));
            	if(t !=null) {
            		em.remove(t);
            	}

            	
    		    if (i % batchSize == 0 && i != numberOfRecords) {
                    em.flush();
                    em.clear();
                }
            }
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}

		
	}
	
	
	@Override
	public void cascadeDeleteById(K key) {
		EntityManager em = emf.createEntityManager();
		deletionHelper.delete(em, getClassType(), key);
		
	}

	@Override
	public void deleteAll() {
		
	
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();

			List list = em.createQuery("Select t from " + getClassType().getSimpleName() + " t", getClassType()).getResultList();
			
			for(Object obj : new ArrayList<>(list)) {
				em.remove(obj);
			}
			

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e; // or display error message
		} finally {
			em.close();
		}
	}

	@Override
	public Long countWithQuery(String countJPQLQuery, Map<String, Object> parameters) {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery(countJPQLQuery);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return (long) query.getSingleResult(); 
	}
	
	@Override
	public Long countWithNativeQuery(String countSQLQuery, Map<String, Object> parameters) {
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery(countSQLQuery);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return (long) query.getSingleResult(); 
	}
	
	@Override
	public Long count() {
		
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> cqCount = builder.createQuery(Long.class);
		Root<T> entityRoot = cqCount.from(getClassType());
		cqCount.select(builder.count(entityRoot));
		return em.createQuery(cqCount).getSingleResult();
		
	}
	
	@Override
	public List<T> findWithNamedQuery(String queryName) {
		EntityManager em = emf.createEntityManager();
		return em.createNamedQuery(queryName, getClassType()).getResultList();
	}

	@Override
	public List<T> findWithNamedQuery(String queryName, int resultLimit) {
		EntityManager em = emf.createEntityManager();
		return em.createNamedQuery(queryName, getClassType()).
                setMaxResults(resultLimit).
                getResultList();
	}

	@Override
	public List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		 return findWithNamedQuery(namedQueryName, parameters, 0);
	}
	
	
	@Override
	public List<T> findWithQuery(String jpql) {
		EntityManager em = emf.createEntityManager();
		return em.createQuery(jpql, getClassType()).getResultList();
	}

	@Override
	public List<T> findWithQuery(String jpql, int resultLimit) {
		EntityManager em = emf.createEntityManager();
		return em.createQuery(jpql, getClassType()).
                setMaxResults(resultLimit).
                getResultList();
	}
	
	@Override
	public List<T> findWithQuery(String jpql, Map<String, Object> parameters) {
		return findWithQuery(jpql, parameters, 0);
	}
	
	//for reference
	//https://docs.jboss.org/hibernate/orm/4.3/devguide/en-US/html/ch11.html#d5e2714
	//https://thorben-janssen.com/jpql/
	@Override
	public List<T> findWithQuery(String jpql, Map<String, Object> parameters, int resultLimit) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> query = em.createQuery(jpql, getClassType());
		if(resultLimit > 0)
			query.setMaxResults(resultLimit);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList(); 
	}


	@Override
	public List<T> findWithNativeQuery(String sql) {
		EntityManager em = emf.createEntityManager();
		return em.createNativeQuery(sql, getClassType()).getResultList();
	}

	@Override
	public List<T> findWithNativeQuery(String sql, int resultLimit) {
		EntityManager em = emf.createEntityManager();
		return em.createNativeQuery(sql, getClassType()).
                setMaxResults(resultLimit).
                getResultList();
	}
	
	@Override
	public List<T> findWithNativeQuery(String jpql, Map<String, Object> parameters) {
		return findWithNativeQuery(jpql, parameters, 0);
	}
	
	@Override
	public List<T> findWithNativeQuery(String sql, Map<String, Object> parameters, int resultLimit) {
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery(sql, getClassType());
		if(resultLimit > 0)
			query.setMaxResults(resultLimit);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList(); 
	}

	@Override
	public List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> query = em.createNamedQuery(namedQueryName, getClassType());
		if(resultLimit > 0)
			query.setMaxResults(resultLimit);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	public List<T> findAll() {
		EntityManager em = emf.createEntityManager();
        return em.createQuery("Select t from " + getClassType().getSimpleName() + " t", getClassType()).getResultList();
	}
	
	
	


	
	
}
