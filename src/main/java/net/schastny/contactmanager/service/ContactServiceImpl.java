package net.schastny.contactmanager.service;
 
import java.util.List;

import net.schastny.contactmanager.dao.ContactDAO;
import net.schastny.contactmanager.domain.Contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.contactmanager.dao.ContactTypeDAO;
import com.acme.contactmanager.domain.ContactType;
 
@Service
public class ContactServiceImpl implements ContactService {
 
    @Autowired
    private ContactDAO contactDAO;

    @Autowired
    private ContactTypeDAO contactTypeDAO;
    
    @Transactional
    public void addContact(Contact contact) {
        contactDAO.addContact(contact);
    }
 
    @Transactional
    public List<Contact> listContact() {
 
        return contactDAO.listContact();
    }
 
    @Transactional
    public void removeContact(Integer id) {
        contactDAO.removeContact(id);
    }

	@Override
	@Transactional
	public List<ContactType> listContactType() {
		return contactTypeDAO.listContactTypes();
	}
    
    
}
