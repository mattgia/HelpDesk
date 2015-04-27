package com.cse13201.helpdesk;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Date;



import javax.servlet.ServletContext;

import org.apache.commons.lang3.*;


public class Conversation
{
	private String id1;
	private String id2;
	LinkedList<String> conversation;
	public LinkedList<String> sender;
	private ServletContext c = Servlet.c;

	public String getOne() {
		return id1;
	}
	public void setOne(String id1) {
		this.id1 = id1;
	}
	public String getTwo() {
		return id2;
	}
	public void setTwo(String id2) {
		this.id2 = id2;
	}
	public Conversation(String id1, String id2)
	{
		this.id1 = id1;
		this.id2 = id2;
		conversation = new LinkedList<String>();
		sender = new LinkedList<String>();
		

	}
	public Conversation(String id1, String id2, LinkedList<String> c, LinkedList<String> s)
	{
		this.id1 = id1;
		this.id2 = id2;
		conversation = c;
		sender = s;
	}


	public synchronized boolean oneMessage(String message)
	{
		if(message != null)
		{
				
				sender.add(id1 + "->" + id2);
				if(c != null)
				{
					UserManager m = (UserManager) c.getAttribute("users");
					if(m != null)
					{
						User u = m.getUserifExists(id1);
						u.setLastActive(System.currentTimeMillis());
						System.out.println("Updated last active of " + id1);
					}
				}
				
				
				conversation.add((StringEscapeUtils.escapeHtml4(message).trim()));
				if(sender.size() > 200)
				{
					sender.removeFirst();
					conversation.removeFirst();
				}
				return true;
			
		}
		else
			return false;
	}

	public synchronized boolean twoMessage(String message)
	{
		if(message != null)
		{
			
			
				sender.add(id2 + "->" + id1);
				if(c != null)
				{
					UserManager m = (UserManager) c.getAttribute("users");
					if(m != null)
					{
						User u = m.getUserifExists(id2);
						u.setLastActive(System.currentTimeMillis());
						System.out.println("Updated last active of " + id2);
					}
				}
				conversation.add((StringEscapeUtils.escapeHtml4(message).trim()));
				
				if(sender.size() > 200)
				{
					sender.removeFirst();
					conversation.removeFirst();
				}
				
				return true;
		}
		else
			return false;
	}

	public synchronized LinkedList<String> getConversation()
	{
		return this.conversation;
	}

	public synchronized String getConversationFormattedString()
	{
		LinkedList<String> x = getConversation();

		String ret = "";
		int i = 0;
		while(i < x.size())
		{

				ret += "<div class=\"name\" >";
				ret += sender.get(i);
				ret += "</div>";
				ret += "<div class=\"message\">";
				ret += x.get(i);
				ret += "</div>";
				ret += "<hr>";
			i++;
		}

		return ret;
	}

	public static synchronized String getLastMessageUser(Conversation c)
	{
		LinkedList<String> s =  c.sender;
		int size = s.size();
		if(size > 0){
			String last = s.get(size-1);
			String stuff[] = last.split("->");
			if(stuff.length > 0)
			{
				return stuff[0];
			}
		}
		return "";

	}
}