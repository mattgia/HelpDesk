<%@page import="com.cse13201.helpdesk.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%UserManager man = (UserManager) request.getServletContext().getAttribute("users"); %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<style>
#helperMessage {
 width:75% !important;
 display: block;
 min-height: 50px;
 float: left;
 height:95% !important;
}

#characters {
 border: 2px solid white;
 backgroud-color: #000;
 color: #000;
 display: block;
  float: left;
 width: 18%;

 text-align: right;
 margin: 0 2%;
}

#subButton {
 border: 2px solid #000;
 backgroud-color: #A9A9A9;
 padding: 5px;
 display: block;
  float: left;
 color: #000;
 width: 18%;
 height: 76%;
 margin: 0 3%;
 min-height: 25px;
}
</style>

</head>
<script src="/HelpDesk/js/jq.js"></script>
<%
	String token = (String) request.getSession().getAttribute("CSRFToken");
%>
<script type='text/javascript'>//<![CDATA[ 
$(window).load(function(){
$('#helperMessage').keyup(function() {
    var cs = $(this).val().length;
    $('#characters').text(cs + "/10000");
    
   if(cs > 10000)
	   {
		document.getElementById("subButton").style.display = "none";
	   }
   else
	   {
		document.getElementById("subButton").style.display = "inline";

	   }
});
});//]]> 
</script>

<body style="text-align: center;">

<form method="post" action="Servlet" id="myHelperSend">
		<input type="hidden" name="HiddenCSRF" value="<%=token%>">
		<textarea name="helperMessage" id="helperMessage" form="myHelperSend"></textarea>
		<input type="submit" name="myHelperSend"  id="subButton" value="SEND"/>
		<span id="characters">0/10000</span>
		
	</form>
</body>
</html>