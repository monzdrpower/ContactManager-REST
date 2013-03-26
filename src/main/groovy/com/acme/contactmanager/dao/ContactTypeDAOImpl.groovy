package com.acme.contactmanager.dao;

import org.hibernate.criterion.Restrictions;

import java.util.List;


import org.hibernate.Session
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional

import com.acme.contactmanager.domain.ContactType;

@Repository
public class ContactTypeDAOImpl implements ContactTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		sessionFactory.getCurrentSession()
	}

	@Override
	@Transactional
	void addContactType(ContactType contactType) {
		currentSession.save(contactType);
	}

	@Override
	@Transactional
	List<ContactType> listContactTypes() {
		def result = currentSession.createQuery("from ContactType").list();
		if(!result){
			def types = [
				[name:'Семья', code:'family', defaulttype: false],
				[name:'Работа', code:'job', defaulttype: false],
				[name:'Знакомые', code:'stuff', defaulttype: true]
			]

			types.each { type ->
				ContactType contactType = new ContactType( code: type.code, name : type.name, defaulttype : type.defaulttype )
				currentSession.save(contactType)
				result << contactType
			}
		}
		result
	}

	@Override
	@Transactional
	void removeContactType(Integer id) {
		ContactType contactType = currentSession.get(ContactType.class, id);
		if (contactType) {
			currentSession.delete(contactType);
		}
	}

	@Override
	@Transactional
	ContactType getDefault() {
		currentSession.createCriteria(ContactType.class)
				.add(Restrictions.eq('defaulttype', true))
				.uniqueResult()
	}
}
