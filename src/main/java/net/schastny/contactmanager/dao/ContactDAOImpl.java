package net.schastny.contactmanager.dao;

import java.util.List;

import net.schastny.contactmanager.domain.Contact;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.acme.contactmanager.dao.ContactTypeDAO;

@Repository
public class ContactDAOImpl implements ContactDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ContactTypeDAO contactTypeDAO;
	
	public void addContact(Contact contact) {
		
		if(contact.getContacttype() == null){
			contact.setContacttype(contactTypeDAO.getDefault());
		}
		
		sessionFactory.getCurrentSession().save(contact);
	}

	@SuppressWarnings("unchecked")
	public List<Contact> listContact() {

		List list = sessionFactory.getCurrentSession().createQuery("from Contact")
			.list();
		return list;
	}

	public void removeContact(Integer id) {
		Contact contact = (Contact) sessionFactory.getCurrentSession().load(
				Contact.class, id);
		if (null != contact) {
			sessionFactory.getCurrentSession().delete(contact);
		}

	}
}
