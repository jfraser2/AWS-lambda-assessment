package dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import entities.NotificationEntity;

public class NotificationDao {
	public void save(Session session, NotificationEntity notificationEntity) {
	    Transaction transaction = session.beginTransaction();
	    session.persist(notificationEntity);
	    transaction.commit(); // session is flushed by default
	}
	
	public Optional<NotificationEntity> findById(Session session, Long id) {
		
		Optional<NotificationEntity> retVar = null;
		
	    Transaction transaction = session.beginTransaction();
	    retVar = Optional.ofNullable(session.get(NotificationEntity.class, id));
	    transaction.commit(); // session is flushed by default
	    
	    return retVar;
	}
	
	public List<NotificationEntity> findAll(Session session) {
		
		List<NotificationEntity> retVar = null;
		
	    Transaction transaction = session.beginTransaction();
	    retVar = session.createQuery("SELECT a FROM Notifications a", NotificationEntity.class).getResultList();
	    transaction.commit(); // session is flushed by default
	    
	    return retVar;
	}	

}
