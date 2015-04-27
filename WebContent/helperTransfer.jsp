<%@page import="java.util.ArrayList"%>
<%@page import="com.cse13201.helpdesk.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<style>
input {
 border: 2px solid #000;
 backgroud-color: #A9A9A9;
 padding: 5px;
 margin: 5px;
 color: #000;

}
</style>
</head>
<script>
function update() {
	//location.reload();
	window.location.href = "/HelpDesk/helperTransfer.jsp"
}
</script>

<%
	String token = (String) request.getSession().getAttribute("CSRFToken");
%>
<body>
	<%
		ConversationManager con = (ConversationManager) request.getServletContext().getAttribute("conversations");
		UserManager man = (UserManager) request.getServletContext().getAttribute("users");
		
		if(man != null && con != null)
		{
			ArrayList<String> helpers = man.getHelpers();
		
			User u = man.getUserifExists_ID(request.getSession().getId());
			if(u != null && helpers != null)
			{
				helpers.remove(u.getName());//remove myself from the list
					
				if(u.getType().equals("helper"))
				{
				out.write("<form method=\"post\" action=\"Servlet\" id=\"transferForm\">");

				out.write("<select name=\"transfers\" form=\"transferForm\" >");
				
						for (String s : helpers) {
							out.write("<option name=\"" + s + "\">" + s + "</option>");
						}
				out.write("</select>");
				out.write("<input type=\"button\" value=\"Refresh\" onclick=\"update()\">");
				out.write("<br>");
				out.write("<input type=\"submit\" value=\"Transfer\" name=\"transferForm\">");
				out.write("<input type=\"submit\" value=\"New Conversation\" name=\"newConversation\">");
				out.write("<input type=\"hidden\" name=\"HiddenCSRF\" value=\"" + token + "\">");

				out.write("</form>");
				
				
				out.write("<form method=\"post\" action=\"Servlet\" id=\"logOutForm\">");

				out.write("<input type=\"submit\" value=\"Log Out\" name=\"logOutButton\">");

				out.write("<input type=\"hidden\" name=\"HiddenCSRF\" value=\"" + token + "\">");

				out.write("</form>");

				}
				else
					out.write("You do not have permission to access this page.");
				}
		}
	%>





</body>
</html>