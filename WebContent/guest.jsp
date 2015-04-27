
<%@page import="com.cse13201.helpdesk.*"%>
<%@ page import="java.io.*, java.net.*"%>
<html>
<head>
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



<script src="/HelpDesk/js/jq.js"></script>

<script>
	function update() {
		$.get(document.URL, function(result) {
			$('#textBox').html($(result).find("#textBox").html());
			$('#convTitle').html($(result).find("#convTitle").html());
		});

	}

	setInterval(function() {
		update();
	}, 1000);
	</script>
<script>
	function down() {
		window.scrollTo(0,document.body.scrollHeight);
	}
</script>
	
</head>
<body onload='down();'>
<%
	ConversationManager con = (ConversationManager) request.getServletContext().getAttribute("conversations");
	UserManager man = (UserManager) request.getServletContext().getAttribute("users");
	
	
%>
<div id="wrapper">
	<div id="Titlediv">
		<h2 id="convTitle">
		<%
		Conversation c = null;
		if(man != null && con != null)
		{
			c = con.findFirst((String) request.getSession().getAttribute("guestName"));
			if(c != null)
			{
				if(c.getOne().matches((String) request.getSession().getAttribute("guestName")))
				{
					out.write(c.getTwo());
				}
				else
					out.write(c.getOne());
			}
		}
		
		%>
		</h2>
	</div>

	<div id="textBox">
	<%
		if(c != null)
		out.write(c.getConversationFormattedString());
		%>
	</div>
</div>
</body>

</html>