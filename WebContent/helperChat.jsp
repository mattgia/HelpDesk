
<%@page import="com.cse13201.helpdesk.*"%>
<%@ page import="java.io.*, java.net.*"%>
<html>
<%
	ConversationManager con = (ConversationManager) request
			.getServletContext().getAttribute("conversations");
	UserManager man = (UserManager) request.getServletContext()
			.getAttribute("users");
%>

<script src="/HelpDesk/js/jq.js"></script>
<style>
body {
padding: 0;
margin: 0;
font-family: "Georgia", "Avenir Next", "Avenir", "Palantino Linotype", "Book Antiqua", "Open Sans";
}

#Titlediv {
background: #A9A9A9;
padding: 10px;
position: fixed;
width: 100%;
}

#convTitle {
 text-align: left;
 padding: 0;
 margin: 0 auto;
 }
 
 #textBox {
 text-align: left;
 padding: 10px;
 margin: 0 auto;
 padding-top: 50px;
 }

.name{
font-family:Arial; 
font-weight: bold; 
color:#425479; 
font-size:16;
}

.message{
font-family:Arial; 
font-size:14;
}

</style>
<script>
	function update() {
		$.get(document.URL, function(result) {
			$('#textBox').html($(result).find("#textBox").html());
			$('#convTitle').html($(result).find("#convTitle").html());

		});

	}

	setInterval(function() {
		update()
	}, 2000);
	function down() {
		
		window.scrollTo(0,document.body.scrollHeight);
	}
</script>

<body onload='down();'>
<div id="wrapper">
	<div id="Titlediv">

	<h2 id="convTitle">
		<%
		String gName = (String) request.getSession().getAttribute(
				"guestName");
		String hName = (String) request.getSession().getAttribute(
				"helperName");
		Conversation c = null;
		if (con != null && man != null) {
			if (gName != null && hName == null) {
				c = con.findFirst(gName);
				if (c != null) {
					if (c.getOne().matches(gName)) {
						out.write(c.getTwo());
						User temp = man.getUserifExists(c.getTwo());
						if( temp != null)
						{
							if(temp.getType().equals("helper"))
							{
								out.write("[HELPER]");
							}
						}
					} else{
						out.write(c.getOne());
						User temp = man.getUserifExists(c.getOne());
						if( temp != null)
						{
							if(temp.getType().equals("helper"))
							{
								out.write("[HELPER]");
							}
						}
					}
				} else
					out.write("nobody");
			} else if (gName == null && hName != null) {
				c = con.findFirst(hName);
				if (c != null) {
					if (c.getOne().matches(hName)) {
						out.write(c.getTwo());
						User temp = man.getUserifExists(c.getTwo());
						if( temp != null)
						{
							if(temp.getType().equals("helper"))
							{
								out.write("[HELPER]");
							}
						}
					} else
					{
						out.write(c.getOne());
						User temp = man.getUserifExists(c.getOne());
						if( temp != null)
						{
							if(temp.getType().equals("helper"))
							{
								out.write("[HELPER]");
							}
						}
					}
				} else
					out.write("nobody");
			} else if (gName != null && hName != null) {
				c = con.getConversation(gName, hName);
				if (c != null) {

					String myName = man.getUserifExists_ID(
							request.getSession().getId().toString())
							.getName();
					if (c.getOne().equals(myName)) {
						out.write(c.getTwo());
						User temp = man.getUserifExists(c.getTwo());
						if( temp != null)
						{
							if(temp.getType().equals("helper"))
							{
								out.write("[HELPER]");
							}
						}
					} else
					{
						out.write(c.getOne());
						User temp = man.getUserifExists(c.getOne());
						if( temp != null)
						{
							if(temp.getType().equals("helper"))
							{
								out.write("[HELPER]");
							}
						}
					}
				} else
					out.write("nobody");
			} else {
				out.write("nobody");
			}
		}
	%>
	</h2>
	</div>


<div id="textBox">
<%
			if (c != null) {
				String myName = man.getUserifExists_ID(
						request.getSession().getId().toString()).getName();
				if (c.getOne().equals(myName)) {
					request.getSession().setAttribute("helperName", c.getOne());
					request.getSession().setAttribute("guestName", c.getTwo());
				} else {

					request.getSession().setAttribute("guestName", c.getOne());
					request.getSession().setAttribute("helperName", c.getTwo());
				}

				out.write(c.getConversationFormattedString());
			} else
				out.write("No conversation.");
		%>
</div>



</div>
</body>
</html>