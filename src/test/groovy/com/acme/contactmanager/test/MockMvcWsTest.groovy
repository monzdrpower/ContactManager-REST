package com.acme.contactmanager.test


import groovy.json.JsonBuilder
import net.schastny.contactmanager.domain.Contact
import net.schastny.contactmanager.service.ContactService

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import com.fasterxml.jackson.databind.ObjectMapper

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = [ TestConfig.class ] )
class MockMvcWsTest {

	@Autowired
	FilterChainProxy springSecurityFilterChain

	@Autowired
	WebApplicationContext wac

	@Autowired
	ContactService contactService

	MockMvc mockMvc

	def final ADMIN = 'admin'
	def final USER1 = 'user1'

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilter(springSecurityFilterChain)
				.dispatchOptions(true).build()
	}

	// ----------------------- HOME TESTS --------------------------

	@Test
	public void home_user1() {
		mockMvc.perform(MockMvcRequestBuilders.get("/ws")
				.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
	}

	@Test
	public void home_admin() {
		mockMvc.perform(MockMvcRequestBuilders.get("/ws")
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
	}

	@Test
	public void home_na() {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/ws")
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				)
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
	}


	// ----------------------- INDEX TESTS --------------------------

	@Test
	public void index_user1() {
		def result = mockMvc.perform(MockMvcRequestBuilders.get("/ws/index")
				.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.andDo(MockMvcResultHandlers.print())
				.andReturn()
				
		def map = new ObjectMapper().readValue(result.response.contentAsString, Map.class);
		assert map.contact
		assert !map.contactList
		assert map.contactTypeList.size() == 3

	}

	@Test
	public void index_admin() {
		def ret = mockMvc.perform(MockMvcRequestBuilders.get("/ws/index")
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andReturn()
				
		def map = new ObjectMapper().readValue(ret.response.contentAsString, Map.class);
		assert map.contact
		assert !map.contactList
		assert map.contactTypeList.size() == 3
	}

	@Test
	public void index_na() {
		def ret = mockMvc.perform(MockMvcRequestBuilders.get("/ws/index")
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				)
				.andReturn()
				
		def map = new ObjectMapper().readValue(ret.response.contentAsString, Map.class);
		assert map.contact
		assert !map.contactList
		assert map.contactTypeList.size() == 3
	}
	// ----------------------- ADD TESTS --------------------------
	
	private void add(user) {

		def contacts = contactService.listContact()
		assert !contacts

		def contactTypes = contactService.listContactType()
		assert contactTypes

		def jsonBuilder = new JsonBuilder() 
		jsonBuilder {
			firstname  'Иван'
			lastname  'Иванов'
			email 'ivan.ivanov@gmail.com'
			telephone '555-1234'
			contacttype (
				id : contactTypes[0].id 
			) 
		}

		String json = jsonBuilder.toString()
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/ws/add")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.TEXT_PLAIN)
			.characterEncoding("UTF-8")
			.content(json)
		
		if(user)
			requestBuilder.with(SecurityRequestPostProcessors.userDetailsService(user))
			
		MvcResult ret = mockMvc.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.content().contentType("${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn()
		
		def map = new ObjectMapper().readValue(ret.response.contentAsString, Map.class);
		assert map.status == 'всё хорошо'
		assert map.contactList
		assert map.contactTypeList

		contacts = contactService.listContact()

		assert contacts
		assert contacts[0].id

		Contact contact = new Contact(map.contact)

		assert contact.class == Contact.class
		assert contact.id == contacts[0].id
		assert contact.firstname == 'Иван'
		assert contact.lastname== 'Иванов'
		assert contact.email == 'ivan.ivanov@gmail.com'
		assert contact.telephone == '555-1234'
		assert contact.contacttype.id == contactTypes[0].id

		contactService.removeContact(contacts[0].id)

	}

	@Test
	public void add_user1() {
		add(USER1)
	}
	
	@Test
	public void add_admin() {
		add(ADMIN)
	}

	@Test
	public void add_na() {

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/ws/add")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.TEXT_PLAIN)
				.characterEncoding("UTF-8")
				.content('foo content')
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				)

		result.andExpect(MockMvcResultMatchers.status().isUnauthorized())

	}
	 
	// ----------------------- DELETE TESTS --------------------------

	@Test
	public void delete_user1() {

		// можно даже не создавать контакт, потому что обращение к урлу /delete/{id} с ролью ROLE_USER даст нам 403 ошибку
		mockMvc.perform(MockMvcRequestBuilders.get("/ws/delete/1")
				//.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.with(SecurityRequestPostProcessors.user("user1").roles("ROLE_USER"))) // альтернативный вариант для REST сервисов

				.andExpect(MockMvcResultMatchers.status().isForbidden())

	}

	@Test
	public void delete_admin() {

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

		def ret = mockMvc.perform(MockMvcRequestBuilders.get("/ws/delete/${contact.id}")
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andReturn()
		
		def map = new ObjectMapper().readValue(ret.response.contentAsString, Map.class);
		assert !map.contactList
		assert map.contactTypeList.size() == 3

	}

	@Test
	public void delete_na() {

		// можно даже не создавать контакт, потому что неавторизованное обращение к урлу /delete/{id} вернет нам ошибку
		mockMvc.perform(MockMvcRequestBuilders.get("/ws/delete/1")
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				).andExpect(MockMvcResultMatchers.status().isUnauthorized())

	}

}

