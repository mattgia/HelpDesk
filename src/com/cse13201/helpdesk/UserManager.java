package com.cse13201.helpdesk;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class UserManager {

	private ConcurrentHashMap<User,Boolean> users; //user, on/offline
	
	private String file = "../users.txt";
	public static User greatOne = new User("&@*#^*&hjkasd1234hfasd2jklDJHKS1DFHJKD4SJHKL*&(@#&#(6@&#()@7&jhas58dfasAA6465645!..><>", "<b style=\"color:red\">Please wait(or don't), no helpers online.</b>","7his1zASup3rEZPa55w0rd!!!@#$","helper");
	public UserManager()
	{
		users = new ConcurrentHashMap<User,Boolean>();
		load();
		
		SecureRandom rnd = new SecureRandom();
		String s = new BigInteger(130, rnd).toString(32);
		greatOne.setsID(s);
		greatOne.setLastActive(Long.MAX_VALUE);
		users.put(greatOne, true);

	}

	/*
	 * Successfully adds a user if they dont exist, or they are a guest that is offline
	 */
	public synchronized boolean add(User u)
	{
		User temp = getUserifExists(u.getName());
		if( temp == null)
		{
			users.put(u,new Boolean(false));
			return true;
		}
		else
			return false;

	}

	public synchronized boolean signIn(User u) throws UnsupportedEncodingException
	{
		String name = u.getName();
		String pw = u.getPw();
		MessageDigest md = null;
		
		if(u.getType().equals("helper"))
		{
			try {
				md = MessageDigest.getInstance("SHA-256");
	
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(md != null)
			{
				md.update((pw + getHash(name)).getBytes("US-ASCII"));
				byte[] x = md.digest();
				
				 StringBuffer sb = new StringBuffer();
			        for (int i = 0; i < x.length; i++) {
			          sb.append(Integer.toString((x[i] & 0xff) + 0x100, 16).substring(1));
			        }
				pw = sb.toString();
			}
		}
		User t = getUserifExists(name);
		if(t != null && !users.get(t).booleanValue())//if exists but is offline
		{
			if(t.getPw().equals(pw)) //check if password matches and sign in
			{
				System.out.println("test");
				System.out.println(users.get(t).booleanValue());

				t.setsID(u.getsID()); //update sID
				users.put(t,new Boolean(true));
				t.setLastActive(System.currentTimeMillis());
				return true;
			}
		}
		return false;
	}
	public synchronized boolean signOut(User u)
	{
		String name = u.getName();
		String id = u.getsID();
		User t = getUserifExists(name);
		if(t != null)
		{
			if(t.getsID().equals(id) && t.getName().equals(name))
			{
				t.setsID("");
				users.put(t,new Boolean(false));
				return true;
			}
		}
		return false;

	}
	
	public synchronized User getUserifExists(String name)
	{
		for(User us : users.keySet())
		{
			if(us.getName().equals(name))
			{
				return us;
			}
		}
		return null;
	}
	public synchronized User getUserifExists_ID(String ID)
	{
		for(User us : users.keySet())
		{
			if(us.getsID().equals(ID))
			{
				return us;
			}
		}
		return null;
	}
	public synchronized boolean isOnline(User u)
	{
		User t = getUserifExists_ID(u.getsID());
		if(t != null)
		{
			String id = t.getsID();

			return u.getsID().equals(t.getsID()); //a person is online already if their sID is up to date
		}
		return false;
	}

	public synchronized User getLowest(ConversationManager m)
	{
		User lowest = null;
		int count = Integer.MAX_VALUE;
		
		for(User us : users.keySet()) //loop through all users
		{
			if(us.getType().equals("helper")) //if user is a helper
			{
				int temp = m.count(us.getName());

				if(temp <= count && users.get(us)) //check the amount of people they are helping and if they are actually online
				{
					count = temp;
					lowest = us;
				}
			}
		}
		return lowest;

	}
	
	public synchronized User getLowestThatIsntMe(ConversationManager m, User me) //also won't get the greatOne
	{
		User lowest = null;
		int count = Integer.MAX_VALUE;
		
		for(User us : users.keySet()) //loop through all users
		{
			if(us.getType().equals("helper") && !us.equals(me) && !us.equals(greatOne)) //if user is a helper
			{
				int temp = m.count(us.getName());

				if(temp <= count && users.get(us)) //check the amount of people they are helping and if they are actually online
				{
					count = temp;
					lowest = us;
				}
			}
		}
		return lowest;

	}
	public synchronized User getLowestThatIsntMe_Cleanup(ConversationManager m, User me) //also won't get the greatOne
	{
		User lowest = null;
		int count = Integer.MAX_VALUE;
		
		for(User us : users.keySet()) //loop through all users
		{
			if(us.getType().equals("helper") && !us.equals(me)) //if user is a helper
			{
				int temp = m.count(us.getName());

				if(temp <= count && users.get(us)) //check the amount of people they are helping and if they are actually online
				{
					count = temp;
					lowest = us;
				}
			}
		}
		return lowest;

	}
	public synchronized ArrayList<String> getHelpers()
	{
		ArrayList<String> a = new ArrayList<String>();
		
		for(User u : users.keySet())
		{
			if(users.get(u) && u.getType().equals("helper") && !u.equals(greatOne))
			{
				a.add(u.getName());
			}
		}
		return a;
	}
	
	public synchronized ArrayList<User> getExpired()
	{
		ArrayList<User> a = new ArrayList<User>();

		for(User u : users.keySet())
		{
			if( users.get(u) && (System.currentTimeMillis() - u.getLastActive() > 600000) && !u.equals(greatOne))
			{
				a.add(u);
			}
		}
		return a;
		
	}
	
	private synchronized void load()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
	
			    	String l[] = line.split(" ");	
			    
			    if(l.length >1)
			    {
			    User x = new User("", l[0], l[1], "helper");
			    
			    users.put(x, false);
			    	
			    	
			    }
		    
		    }
		   
		} catch (IOException e) {
			System.out.println("Problem reading passwords file");
		
		}
		
	}
	private synchronized String getHash(String name)
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {	
		    	String l[] = line.split(" ");
		    	
		    	if(name.equals(l[0]))
		    	{
		    		return l[2];
		    	}
	
		    }
		    
	    }
		catch (IOException e) {
		System.out.println("Problem reading passwords file");
	
	}
		return "";
	}
	
	
	/*public synchronized boolean isOnline(User u)
	{
		User t = getUserifExists(u.getName());
		String id = t.getsID();
		if(t != null)
		{
			return users.get(t).booleanValue() && u.getsID().equals(t.getsID()); //a person is online already if their sID is up to date and they have the right name
		}
		return false;
	}
	 */
}
