<%@page import="nl.captcha.Captcha"%>
<%@page import="com.cse13201.helpdesk.UserManager"
	import="com.cse13201.helpdesk.User"	%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<title>HelpDesk alpha 0.1</title>
<html id="headOfIndexPage">
<style>
body {
	text-align: center
}
</style>
<script>
	function switchPls() {

		if (document.getElementById("switchButton").innerHTML === "Switch to Helper login") {
			document.getElementById("guestLogin").style.display = "none"
			document.getElementById("helperLogin").style.display = "block"
			document.getElementById("switchButton").innerHTML = "Switch to guest login"
		} else {
			document.getElementById("guestLogin").style.display = "block"
			document.getElementById("helperLogin").style.display = "none"
			document.getElementById("switchButton").innerHTML = "Switch to Helper login"
		}
		document.getElementById("guestLogin").reset()
		document.getElementById("helperLogin").reset()

	}

	
	
	
	function redir() {
<%ServletContext c = request.getServletContext();
			UserManager man = (UserManager) c.getAttribute("users");

			if (man != null) {
				if (man.isOnline(new User(request.getSession().getId()
						.toString(), "", "", ""))) {

					if (man.getUserifExists_ID(
							request.getSession().getId().toString()).getType()
							.equals("guest")) {
						response.sendRedirect("/HelpDesk/guest.html");
					} else if (man
							.getUserifExists_ID(
									request.getSession().getId().toString())
							.getType().equals("helper")) {
						response.sendRedirect("/HelpDesk/helper.jsp");
					}

				}
			}%>
	}
</script>
<head>
</head>
<body bgcolor="#CCFFFF" id="leBody">
	<h1>Welcome to cse13201's HelpDesk WebApp</h1>
	<br>
	<br>
	<br>
	<br>
	
	<form method="post" action="Servlet" id="guestLogin">
		<h3>Anonymous guest sign in.</h3>
		<h4>Choose a nickname.</h4>
		<input placeholder="Nickname" name="guestName" />
		 
			<br/>
				    <img style="margin:15px" src="/HelpDesk/captcha" />
				    <br/>
					<input placeholder="Captcha Answer" name="cappy" />	
					<input type="submit" name="guestLogin" value="Login" />

			<div style="font-size:17px">
			You will be redirected back here if login fails.<br/><br/> Possible reasons may include:</br>- Name in use</br>- Name is not between 1 and 10 characters</br> - Name contains non alphanumeric characters<br/> -Captcha was incorrect
			</br></br></br>
			</div>
			<br>
			<span style="color:red; font-weight: bold; font-size: 2em">THIS APPLICATION DOES NOT WORK WITH INTERNET EXPLORER!</span>
			<br/>
			<span style="color:red; font-weight: bold; font-size: 1em">Opera also not recommended for helpers.... JUST STICK WITH FIREFOX AND CHROME!</span>
	</form>

	<form method="get" action="Servlet" style="display: none" id="helperLogin" name="myStupidForm">
		<h3>Helper sign in.</h3>
		<input placeholder="Username" name="userName" value=""/> <br> <br>
		<input placeholder="Password" type="password" name="password" value=""/> <br>
		<br> <input type="submit" name="helperLogin" value="Login"/>
	</form>

	<button style="margin-top: 500px" id="switchButton"
		onclick="switchPls()">Switch to Helper login</button>
</body>


</html>