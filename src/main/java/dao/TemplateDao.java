package dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import entities.TemplateEntity;

public class TemplateDao
	extends DaoBase
{
	
	public void save(Session session, TemplateEntity templateEntity) {
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			session.persist(templateEntity);
			if (!joiningTransaction) {
				transaction.commit(); // session is flushed by default
			}	
		} catch (Exception e) {
			if (null != transaction) {
				if (!joiningTransaction) {
					transaction.rollback();
					session.flush(); // rollback does not flush the session automatically
				}	
			}
		}
	}
	
	public void merge(Session session, TemplateEntity templateEntity)
		throws Exception
	{
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			session.merge(templateEntity);
			if (!joiningTransaction) {
				transaction.commit(); // session is flushed by default
			}	
		} catch (Exception e) {
			if (null != transaction) {
				if (!joiningTransaction) {
					transaction.rollback();
					session.flush(); // rollback does not flush the session automatically
				}	
			}
			throw e;
		}
	}
	
	public Optional<TemplateEntity> findById(Session session, Long id) {
		Optional<TemplateEntity> retVar = null;
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			retVar = Optional.ofNullable(session.get(TemplateEntity.class, id));
			if (!joiningTransaction) {
				transaction.commit(); // session is flushed by default
			}	
		} catch (Exception e) {
			if (null != transaction) {
				if (!joiningTransaction) {
					transaction.rollback();
					session.flush(); // rollback does not flush the session automatically
				}	
			}
			retVar = Optional.empty();
		}
	    
	    return retVar;
	}	
	
	public List<TemplateEntity> findAll(Session session) {
		List<TemplateEntity> retVar = null;
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			retVar = session.createQuery("SELECT a FROM TemplateEntity a", TemplateEntity.class).getResultList();
			if (!joiningTransaction) {
				transaction.commit(); // session is flushed by default
			}	
		} catch (Exception e) {
			if (null != transaction) {
				if (!joiningTransaction) {
					transaction.rollback();
					session.flush(); // rollback does not flush the session automatically
				}	
			}
			retVar = null;
		}
	    
	    return retVar;
	}	
	
}
