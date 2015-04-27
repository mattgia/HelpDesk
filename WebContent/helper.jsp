<HTML>
<HEAD>
<TITLE>HelpDesk alpha 0.1</TITLE>

<script src="/HelpDesk/js/jq.js"></script>
<script>
function update() {
	$.get("/HelpDesk", function(result) {
		if(typeof $(result).find('div').html() != 'undefined')
			{
			window.location.replace("/HelpDesk");
			}
		
	});

}

setInterval(function() {
	update();
}, 5000);
</script>
</HEAD>
<frameset>
	<frameset rows="80%,20%">
		<frameset cols="75%,25%">
			<frame src="helperChat.jsp">
			<frame src="helperChatList.jsp">
		</frameset>
		<frameset cols="75%,25%">
		<frame src="helperSend.jsp"></frame>
		<frame src="helperTransfer.jsp"></frame>
		</frameset>
	</frameset>	
</frameset>
</HTML>