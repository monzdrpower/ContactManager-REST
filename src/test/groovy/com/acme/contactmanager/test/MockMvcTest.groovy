package com.acme.contactmanager.test


import net.schastny.contactmanager.domain.Contact
import net.schastny.contactmanager.service.ContactService

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = [ TestConfig.class ] )
class MockMvcTest {

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

//		увы, это пока не работает				
//		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
//		Authentication auth = new UsernamePasswordAuthenticationToken("user1", "1111", authorities);
//		SecurityContextHolder.getContext().setAuthentication(auth);
		
	}

	@After
	public void teardown() {
		SecurityContextHolder.clearContext()
	}

	
	// ----------------------- HOME TESTS --------------------------

	@Test
	public void home_user1() {
		mockMvc.perform(MockMvcRequestBuilders.get("/")
				.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))
	}

	@Test
	public void home_admin() {
		mockMvc.perform(MockMvcRequestBuilders.get("/")
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))
	}

	@Test
	public void home_na() {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/")
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				)
		result.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))
	}


	// ----------------------- INDEX TESTS --------------------------

	@Test
	public void index_user1() {
		mockMvc.perform(MockMvcRequestBuilders.get("/index")
				.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.andExpect(MockMvcResultMatchers.view().name("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactList"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactTypeList"))

	}

	@Test
	public void index_admin() {
		mockMvc.perform(MockMvcRequestBuilders.get("/index")
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andExpect(MockMvcResultMatchers.view().name("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactList"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactTypeList"))
	}

	@Test
	public void index_na() {
		mockMvc.perform(MockMvcRequestBuilders.get("/index")
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				)
				.andExpect(MockMvcResultMatchers.view().name("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contact"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactList"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("contactTypeList"))
	}

	// ----------------------- ADD TESTS --------------------------

	@Test
	public void add_user1() {

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
				.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))

		contacts = contactService.listContact()

		assert contacts
		assert contacts[0].id

		contactService.removeContact(contacts[0].id)

	}

	@Test
	public void add_admin() {

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
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))

		contacts = contactService.listContact()

		assert contacts
		assert contacts[0].id

		contactService.removeContact(contacts[0].id)

	}

	@Test
	public void add_na() {

		def contactTypes = contactService.listContactType()
		assert contactTypes

		mockMvc.perform(MockMvcRequestBuilders.post("/add")
				.param("firstname",'firstname')
				.param("lastname",'lastname')
				.param("email",'firstname.lastname@gmail.com')
				.param("telephone",'555-1234')
				.param("contacttype.id", contactTypes[0].id.toString())
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				)
				.andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login.jsp"))

	}

	// ----------------------- DELETE TESTS --------------------------

	@Test
	public void delete_user1() {

		// можно даже не создавать контакт, потому что обращение к урлу /delete/{id} с ролью ROLE_USER даст нам 403 ошибку
		mockMvc.perform(MockMvcRequestBuilders.get("/delete/1")
				.with(SecurityRequestPostProcessors.userDetailsService(USER1)))
				.andExpect(MockMvcResultMatchers.forwardedUrl("/error403.jsp"))

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

		mockMvc.perform(MockMvcRequestBuilders.get("/delete/${contact.id}")
				.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/index"))

		contacts = contactService.listContact()
		assert !(contact.id in contacts.id)

	}

	@Test
	public void delete_na() {

		// можно даже не создавать контакт, потому что неавторизованное обращение к урлу /delete/{id} отправит нас на страницу логина
		mockMvc.perform(MockMvcRequestBuilders.get("/delete/1")
				//.with(SecurityRequestPostProcessors.userDetailsService(ADMIN)) убираем сведения об авторизации
				).andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login.jsp"))

	}

}

