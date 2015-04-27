package com.cse13201.helpdesk;

import java.util.ArrayList;
import java.util.LinkedList;

public class ConversationManager {
	
	LinkedList<Conversation> list;
	public ConversationManager()
	{
		list = new LinkedList<Conversation>();
	}
	
	public synchronized void add(Conversation v)
	{
		list.add(v);
	}
	public synchronized void remove(Conversation v)
	{
		list.remove(v);
	}
	
	/* Make a new conversation containing the old conversations
	 * But with up 2 to new users...
	 * Most likely you will just have one new user..
	 */
	public synchronized void transfer(Conversation c, String newOne, String newTwo)
	{
		c.setOne(newOne);
		c.setTwo(newTwo);	
		
	}
	public synchronized Conversation getConversation(String user1, String user2)
	{
		for(Conversation c : list)
		{
			if(c.getOne().equals(user1) && c.getTwo().equals(user2))
				return c;
			if(c.getOne().equals(user2) && c.getTwo().equals(user1))
				return c;
		}
		return null;
	}
	public synchronized Conversation findFirst(String user1)
	{
		for(Conversation c : list)
		{
			//System.out.println(c.getOne() + " ; " + c.getTwo() );
			if((c.getOne().equals(user1) || c.getTwo().equals(user1)) && !c.getOne().equals(c.getTwo()))
				return c;
		}
		return null;
	}
	public synchronized int count(String name) //find amount of conversations a user is participating in
	{
		int res = 0;
		for(Conversation c : list)
		{
			if(c.getOne().equals(name) || c.getTwo().equals(name))
			{
				res++;
			}
		}
		return res;
	}
	public synchronized ArrayList<Conversation> myConversations(String me)
	{
		ArrayList<Conversation> s = new ArrayList<Conversation>();
		for(Conversation c : list)
		{
			if(c.getOne().equals(me) ^ c.getTwo().equals(me))
			{
				s.add(c);
			}
		}
		
		return s;
		
	}

}
