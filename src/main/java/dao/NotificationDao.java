package dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import entities.NotificationEntity;

public class NotificationDao
	extends DaoBase
{
	public void save(Session session, NotificationEntity notificationEntity) {
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			session.persist(notificationEntity);
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
	
	public Optional<NotificationEntity> findById(Session session, Long id) {
		Optional<NotificationEntity> retVar = null;
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			retVar = Optional.ofNullable(session.get(NotificationEntity.class, id));
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
	
	public List<NotificationEntity> findAll(Session session) {
		List<NotificationEntity> retVar = null;
		Transaction transaction = null;
		
		boolean joiningTransaction = existingTransaction(session);
		try {
			transaction = session.beginTransaction(); // begin or join a Transaction
			retVar = session.createQuery("SELECT a FROM NotificationEntity a", NotificationEntity.class).getResultList();
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
