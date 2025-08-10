package dao;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;

public abstract class DaoBase {
	
	protected boolean existingTransaction(Session session) {
		// article https://stackoverflow.com/questions/4854746/hibernate-active-transaction
		
		return ((SessionImpl)session).isTransactionInProgress();
	}

}
