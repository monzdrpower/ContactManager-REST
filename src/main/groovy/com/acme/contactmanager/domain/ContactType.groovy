package com.acme.contactmanager.domain

import javax.persistence.Basic
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint

import net.schastny.contactmanager.domain.Contact

import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
@Table(name = "CONTACT_TYPES", uniqueConstraints = [@UniqueConstraint(columnNames = ["code", "name"])])
class ContactType {

	@Id
	@GeneratedValue
	Integer id

	@Basic
	@Column(unique = true, nullable = true)
	String code

	@Basic
	@Column(unique = true, nullable = true)
	String name

	@Basic
	@Column(nullable = true)
	Boolean defaulttype 

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH, CascadeType.MERGE], mappedBy = "contacttype")
	List<Contact> contacts = null

	@Override
	public String toString() {
		return "ContactType [id=" + id + ", code=" + code + ", name=" + name + ", defaulttype=" + defaulttype
				+ ", contacts=" + contacts + "]";
	}
	
	
}
