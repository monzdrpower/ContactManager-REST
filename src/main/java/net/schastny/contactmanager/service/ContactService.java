package net.schastny.contactmanager.service;

import java.util.List;

import net.schastny.contactmanager.domain.Contact;

import com.acme.contactmanager.domain.ContactType;

public interface ContactService {

	public void addContact(Contact contact);

	public List<Contact> listContact();
	
	public List<ContactType> listContactType();

	public void removeContact(Integer id);
}
