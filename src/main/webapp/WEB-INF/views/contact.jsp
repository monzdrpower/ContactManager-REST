<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title><spring:message code="label.title" /></title>
</head>
<body>

	<a href="<c:url value="/logout" />"> <spring:message
			code="label.logout" />
	</a>
	<br/>
	<a href="<c:url value="/index" />"> <spring:message
			code="label.refresh" />
	</a>

	<h2>
		<spring:message code="label.title" />
	</h2>

	<form:form method="post" action="add" commandName="contact">

		<table>
			<tr>
				<td><form:label path="firstname">
						<spring:message code="label.firstname" />
					</form:label></td>
				<td><form:input path="firstname" /></td>
			</tr>
			<tr>
				<td><form:label path="lastname">
						<spring:message code="label.lastname" />
					</form:label></td>
				<td><form:input path="lastname" /></td>
			</tr>
			<tr>
				<td><form:label path="email">
						<spring:message code="label.email" />
					</form:label></td>
				<td><form:input path="email" /></td>
			</tr>
			<tr>
				<td><form:label path="telephone">
						<spring:message code="label.telephone" />
					</form:label></td>
				<td><form:input path="telephone" /></td>
			</tr>
			<tr>
				<td><form:label path="contacttype.id">
						<spring:message code="label.contacttype" />
					</form:label></td>
				<td><form:select path="contacttype.id">
						<form:option value="0">
							<spring:message code="label.selecttype" />
						</form:option>
						<form:options items="${contactTypeList}" itemValue="id"	itemLabel="name" />
					</form:select></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="<spring:message code="label.addcontact"/>" /></td>
			</tr>
		</table>
	</form:form>

	<h3>
		<spring:message code="label.contacts" />
	</h3>
	<c:if test="${!empty contactList}">
		<table class="data">
			<tr>
				<th><spring:message code="label.firstname" /></th>
				<th><spring:message code="label.email" /></th>
				<th><spring:message code="label.telephone" /></th>
				<th><spring:message code="label.contacttype" /></th>
				<th>&nbsp;</th>
			</tr>
			<c:forEach items="${contactList}" var="contact">
				<tr>
					<td>${contact.lastname}, ${contact.firstname}</td>
					<td>${contact.email}</td>
					<td>${contact.telephone}</td>
					<td>${contact.contacttype.name}</td>
					<td><a href="delete/${contact.id}"><spring:message
								code="label.delete" /></a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>

</body>
</html>