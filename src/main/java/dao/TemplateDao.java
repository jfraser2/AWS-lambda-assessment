package dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import entities.TemplateEntity;

public class TemplateDao {
	
	public void save(Session session, TemplateEntity templateEntity) {
	    Transaction transaction = session.beginTransaction();
	    session.persist(templateEntity);
	    transaction.commit(); // session is flushed by default
	}
	
	public Optional<TemplateEntity> findById(Session session, Long id) {
		
		Optional<TemplateEntity> retVar = null;
		
	    Transaction transaction = session.beginTransaction();
	    retVar = Optional.ofNullable(session.get(TemplateEntity.class, id));
	    transaction.commit(); // session is flushed by default
	    
	    return retVar;
	}	
	
	public List<TemplateEntity> findAll(Session session) {
		
		List<TemplateEntity> retVar = null;
		
	    Transaction transaction = session.beginTransaction();
	    retVar = session.createQuery("SELECT a FROM Templates a", TemplateEntity.class).getResultList();
	    transaction.commit(); // session is flushed by default
	    
	    return retVar;
	}	
	
}
