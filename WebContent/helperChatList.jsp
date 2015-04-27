<%@page import="java.util.ArrayList"%>
<%@page import="com.cse13201.helpdesk.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
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

<script src="/HelpDesk/js/jq.js"></script>
<%
	String token = (String) request.getSession().getAttribute("CSRFToken");
%>
<script>
	function update() {
		//location.reload();
		//window.location.href = "/HelpDesk/helperChatList.jsp"
		
			$.get("/HelpDesk/helperChatList.jsp", function(result) {
				$('#myConvs').html($(result).filter("#myConvs").html());

			});
	}
	$("#myConvs").submit(function(e){
	    return false;
	});
	setInterval(function() {
		update()
	}, 500);
</script>
<body >
	<h4>Select a conversation..</h4>
	<%
		ConversationManager con = (ConversationManager) request.getServletContext().getAttribute("conversations");
		UserManager man = (UserManager) request.getServletContext().getAttribute("users");
			
			User u = null;
			ArrayList<Conversation> list = null;
			String myName = null;
			
			if(man != null && con != null)
			{
				u = man.getUserifExists_ID(request.getSession().getId().toString());
				if(u != null)
				myName = u.getName();
				
				list = con.myConversations(myName);
			}
	%>

	<form method="post" action="Servlet" id="myConvs">
	
		<%
			if(u != null && u.getType().equals("helper"))
			{
				if(list != null)
					{
					for (Conversation s : list) {
						
						String tempName = "";
						if(s.getOne().equals(myName))
						{
							tempName = s.getTwo();
						}
						else
						{
							tempName = s.getOne();
						}
						
						out.write("<input type=\"submit\" value=\"" + tempName + "\"" + " name=\"" + tempName + "\">" );
						if(!Conversation.getLastMessageUser(s).equals(myName))
								{
								out.write("--not answered");
								}
						out.write("<br>");
					}
				}
			}
			else
			{
			out.write("You do not have permission to see this page.");
			}
		%>
	<input type="hidden" name="HiddenCSRF" value="<%=token%>">
		
	</form>




</body>
</html>