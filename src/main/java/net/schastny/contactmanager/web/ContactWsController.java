package net.schastny.contactmanager.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.schastny.contactmanager.domain.Contact;
import net.schastny.contactmanager.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = "/ws", produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8" )
public class ContactWsController {

	@Autowired
	private ContactService contactService;

	@RequestMapping(value = "/index")
	@ResponseBody 
	public Map<String, Object> listContacts() {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("contact", new Contact());
		map.put("contactList", contactService.listContact());
		map.put("contactTypeList", contactService.listContactType());
		return map;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody 
	public Map<String, Object> addContactWs(@RequestBody String json) {

		Contact contact = null;
		try {
			contact = new ObjectMapper().readValue(json, Contact.class);
			contactService.addContact(contact);
			
			Map<String, Object> mav = new HashMap<String, Object>();
			mav.put("status", "всё хорошо"); // проверяем кодировку
			mav.put("contact", contact);
			mav.put("contactList", contactService.listContact());
			mav.put("contactTypeList", contactService.listContactType());
			return mav;
		} catch (IOException e) {
			
			Map<String, Object> mav = new HashMap<String, Object>();
			mav.put("status", "error");
			mav.put("message", e.getMessage());
			return mav;
		}
		
	}

	@RequestMapping("/delete/{contactId}")
	@ResponseBody 
	public Map<String, Object> deleteContactWs(@PathVariable("contactId") Integer contactId) {

		contactService.removeContact(contactId);

		Map<String, Object> mav = new HashMap<String, Object>();
		mav.put("contactList", contactService.listContact());
		mav.put("contactTypeList", contactService.listContactType());
		return mav;
	}
	
}
