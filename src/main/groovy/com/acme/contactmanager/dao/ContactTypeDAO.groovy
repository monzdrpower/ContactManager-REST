package com.acme.contactmanager.dao;

// test

import java.util.List;

import com.acme.contactmanager.domain.ContactType;

interface ContactTypeDAO {

	void addContactType(ContactType contactType)

	List<ContactType> listContactTypes()

	void removeContactType(Integer id)

	ContactType getDefault();
	
}
