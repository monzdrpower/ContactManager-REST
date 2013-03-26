package com.acme.contactmanager.test


import net.schastny.contactmanager.domain.Contact
import net.schastny.contactmanager.service.ContactService

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = [ TestConfig.class ] )
class MockMvcNoSecTest {

	@Autowired
	WebApplicationContext wac

	@Autowired
	ContactService contactService
	
	MockMvc mockMvc
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
		.dispatchOptions(true).build()
	}

	@Test
	public void home() {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/")
		ResultActions result = mockMvc.perform(request)
		result.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))
	}

	@Test
	public void index() {
		mockMvc.perform(MockMvcRequestBuilders.get("/index"))
				.andExpect(MockMvcResultMatchers.view().name("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactList"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactTypeList"))
	}

	@Test
	public void add(){

		def contacts = contactService.listContact()
		assert !contacts

		def contactTypes = contactService.listContactType()
		assert contactTypes

		mockMvc.perform(MockMvcRequestBuilders.post("/add")
				.param("firstname",'firstname')
				.param("lastname",'lastname')
				.param("email",'firstname.lastname@gmail.com')
				.param("telephone",'555-1234')
				.param("contacttype.id", contactTypes[0].id.toString())
				)
		.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))

		contacts = contactService.listContact()

		assert contacts
		assert contacts[0].id

		contactService.removeContact(contacts[0].id)

	}

	@Test
	public void delete() {

		def contactTypes = contactService.listContactType()
		assert contactTypes

		Contact contact = new Contact(
				firstname : 'firstname',
				lastname : 'lastname',
				email : 'firstname.lastname@gmail.com',
				telephone : '555-1234',
				contacttype : contactTypes[0]
			)
		
		contactService.addContact(contact)
		assert contact.id

		def contacts = contactService.listContact()
		assert contact.id in contacts.id
		
		mockMvc.perform(MockMvcRequestBuilders.get("/delete/${contact.id}"))
			.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))

		contacts = contactService.listContact()
		assert !(contact.id in contacts.id)

	}

}

