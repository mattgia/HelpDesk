package com.cse13201.helpdesk;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.*;
/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static ServletContext c;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Servlet() {
		super();
		// TODO Auto-generated constructor stub
		c = null;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	   public void init() {
		
		c = getServletContext();

		UserManager um = new UserManager();
		ConversationManager cm = new ConversationManager();
		c.setAttribute("users", um);
		c.setAttribute("conversations", cm);	
		
		CleanupThread clnUp = new CleanupThread();
		clnUp.start();
		
		MoveFromNobodyThread mv = new MoveFromNobodyThread();
		mv.start();
		
		
		
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);

		
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		try{

			/*if(c == null)
			{
				c = request.getServletContext();

				UserManager um = new UserManager();
				ConversationManager cm = new ConversationManager();

				c.setAttribute("users", um);
				c.setAttribute("conversations", cm);
			}*/
			ArrayList<String> parameterNames = new ArrayList<String>();
			Enumeration enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String parameterName = (String) enumeration.nextElement();
				parameterNames.add(parameterName);
			}
			

			if(!((UserManager)request.getSession().getServletContext().getAttribute("users")).isOnline(new User(request.getSession().getId().toString(),"","","")))
			{
				if(request.getParameter("guestLogin") != null && request.getParameter("guestName").matches("^[A-Za-z0-9]{1,10}$") && request.getParameter("cappy") != null &&  captcha != null && ((String)request.getParameter("cappy")).equals(captcha.getAnswer()) )
				{
					HttpSession s = request.getSession();
					s.setAttribute("guestName", request.getParameter("guestName"));
					System.out.println("guestName set to: " + s.getAttribute("guestName"));

					User u = new User(s.getId().toString(), request.getParameter("guestName"), "", "guest");

					UserManager man = (UserManager) c.getAttribute("users");
					
					request.getSession().setAttribute("CSRFToken", (new TokenGenerator()).generateToken());

					man.add(u);
					boolean x = man.signIn(u);
					if(x)
					{
						ConversationManager con = (ConversationManager) c.getAttribute("conversations");
						User lowest = man.getLowest(con);
						while(lowest == null)
						{
							System.out.println("waiting for helper...");
							Thread.sleep(1000);
							lowest = man.getLowest(con);
						}
						con.add(new Conversation(u.getName(), lowest.getName()));

						if( request.getHeader("referer").split("/").length >= 3)
							response.addCookie(new Cookie("login_location", request.getHeader("referer").split("/")[2]));

						response.sendRedirect("/HelpDesk/guest.html");
						//request.getRequestDispatcher("/guest.html").forward(request, response);
					}
					else
					{
						System.out.println("guest login failed");
						response.sendRedirect("/HelpDesk/index.jsp");			
						}

				}
				else if(request.getParameter("helperLogin") != null && request.getParameter("userName").matches("^[A-Za-z0-9]{1,10}$"))
				{

					HttpSession s = request.getSession();
					s.setAttribute("helperName", (String) request.getParameter("userName"));
					

					
						


					User u = new User(s.getId().toString(), request.getParameter("userName"), request.getParameter("password"), "helper");

					UserManager man = (UserManager) c.getAttribute("users");
					
					request.getSession().setAttribute("CSRFToken", (new TokenGenerator()).generateToken());

					//man.add(u);
					boolean x = man.signIn(u);
					if(x)
					{
						ConversationManager con = (ConversationManager) c.getAttribute("conversations");

						con.add(new Conversation(u.getName(), u.getName()));
						if( request.getHeader("referer").split("/").length >= 3)
							response.addCookie(new Cookie("login_location", request.getHeader("referer").split("/")[2]));

						
						response.sendRedirect("/HelpDesk/helper.jsp");
						//request.getRequestDispatcher("/helper.jsp").forward(request, response);
					}
					else
					{
						System.out.println("helper login failed");
						response.sendRedirect("/HelpDesk/index.jsp");	
					}

				}
				else
				{
					response.sendRedirect("/HelpDesk/index.jsp");

				}
			}
			else if(request.getHeader("referer").split("/").length >= 3 )
			{
				String login_location = "";
				Cookie[] cookies = (Cookie[]) request.getCookies();
				for(Cookie c : cookies )
				{
					if(c.getName().equals("login_location"))
						login_location = c.getValue();
						
				}
				System.out.println("login_location");
				if(request.getHeader("referer").split("/")[2].equals(login_location))
				{
					//System.out.println("here");
					if(request.getParameter("mySend") != null && request.getParameter("HiddenCSRF") != null && request.getParameter("HiddenCSRF").equals(request.getSession().getAttribute("CSRFToken")))
					{
						String msg = (String) request.getParameter("message");
						if(msg != null && msg.length() > 0 && msg.length() <= 10000)
						{
							HttpSession s = request.getSession();
							String name = (String) s.getAttribute("guestName");

							ConversationManager cons = (ConversationManager) c.getAttribute("conversations");
							Conversation conv = cons.findFirst(name);
							if(conv.getOne().matches(name))
							{
								conv.oneMessage(msg);
							}
							else
							{
								conv.twoMessage(msg);
							}
							System.out.println("Guest sent message");
						}
						response.sendRedirect("/HelpDesk/guestSend.jsp");

					}
					else if(request.getParameter("myHelperSend") != null && request.getParameter("HiddenCSRF") != null && request.getParameter("HiddenCSRF").equals(request.getSession().getAttribute("CSRFToken")))
					{
						String msg = (String) request.getParameter("helperMessage");
						if(msg != null && msg.length() > 0 && msg.length() <= 10000)
						{
							HttpSession s = request.getSession();

							String name = (String) s.getAttribute("guestName");
							String name2 = (String) s.getAttribute("helperName");

							if(name != null && name2 != null && name.length() > 1 && name2.length() > 1)
							{
								ConversationManager cons = (ConversationManager) c.getAttribute("conversations");
								UserManager man = (UserManager) c.getAttribute("users");

								User u = man.getUserifExists_ID(s.getId().toString());

								Conversation conv = cons.getConversation(name, name2);
								if( conv != null)
								{
									if(conv.getOne().equals(u.getName()))
									{
										conv.oneMessage(msg);
									}
									else if(conv.getTwo().equals(u.getName()))
									{
										conv.twoMessage(msg);
									}
								}
								System.out.println("Helper sent message");

							}
						}
						response.sendRedirect("/HelpDesk/helperSend.jsp");

					}
					else if(request.getParameter("transferForm") != null && request.getParameter("HiddenCSRF") != null && request.getParameter("HiddenCSRF").equals(request.getSession().getAttribute("CSRFToken")))
					{
						//System.out.println(request.getParameter("transfers"));

						if(request.getParameter("transfers") != null)
						{

							String myName = ((UserManager)c.getAttribute("users")).getUserifExists_ID(request.getSession().getId().toString()).getName();
							String currentGuy = null;

							String gName = (String) request.getSession().getAttribute("guestName");
							String hName = (String) request.getSession().getAttribute("helperName");

							if(gName != null && gName.equals(myName))
							{
								currentGuy = (String)request.getSession().getAttribute("helperName");
							}
							else if(hName != null && hName.equals(myName))
							{
								currentGuy = (String)request.getSession().getAttribute("guestName");
							}
							if( currentGuy != null && !request.getParameter("transfers").equals(currentGuy))
							{

								ConversationManager cm = (ConversationManager) c.getAttribute("conversations");
								Conversation v = cm.getConversation(myName, currentGuy);

								cm.transfer(v, currentGuy, (String) request.getParameter("transfers"));
							}
						}
						response.sendRedirect("/HelpDesk/helperTransfer.jsp");

					}
					else if(request.getParameter("newConversation") != null && request.getParameter("HiddenCSRF") != null && request.getParameter("HiddenCSRF").equals(request.getSession().getAttribute("CSRFToken")))
					{
						String myName = ((UserManager)c.getAttribute("users")).getUserifExists_ID(request.getSession().getId().toString()).getName();
						String otherGuy = (String) request.getParameter("transfers");



						ConversationManager cm = (ConversationManager) c.getAttribute("conversations");
						//System.out.println(myName);
						//System.out.println(otherGuy);

						if(myName != null && otherGuy != null)
						{
							if(cm.getConversation(myName, otherGuy) == null)
							{
								cm.add(new Conversation(myName,otherGuy));
							}

						}
						response.sendRedirect("/HelpDesk/helperTransfer.jsp");

					}
					else if(request.getParameter("logOutButton") != null && request.getParameter("HiddenCSRF") != null && request.getParameter("HiddenCSRF").equals(request.getSession().getAttribute("CSRFToken")))
					{
						UserManager uu = (UserManager) c.getAttribute("users");
						ConversationManager cc = (ConversationManager) c.getAttribute("conversations");
						
						User u = uu.getUserifExists_ID(request.getSession().getId().toString());
						Conversation toRemove = null;
						if(u.getType().equals("helper"))
						{
							toRemove = cc.findFirst(u.getName());
							
							while(toRemove != null) //give all of the conversations to somebody else
							{
								if(toRemove.getOne().equals(u.getName()))
								{
									if(uu.getHelpers().contains(toRemove.getTwo())) //if it's a conversation between two helpers, just remove it
									{
										System.out.println("Removed conversation between: " + toRemove.getOne() + " " + toRemove.getTwo());
										cc.remove(toRemove);
									}
									else
									{
										System.out.print("Cleanup transfered: " + toRemove.getOne() + " and " + toRemove.getTwo() + "->");
										cc.transfer(toRemove, uu.getLowestThatIsntMe_Cleanup(cc,u).getName(), toRemove.getTwo() );
										System.out.println(toRemove.getOne() + " and " + toRemove.getTwo());
									}
								}
								else if(toRemove.getTwo().equals(u.getName()))
								{
									if(uu.getHelpers().contains(toRemove.getOne()))
									{
										System.out.println("Removed conversation between: " + toRemove.getOne() + " " + toRemove.getTwo());
										cc.remove(toRemove);

									}
									else
									{
										System.out.print("Cleanup transfered: " + toRemove.getOne() + " and " + toRemove.getTwo() + "->");
										cc.transfer(toRemove, toRemove.getOne(),  uu.getLowestThatIsntMe_Cleanup(cc, u).getName());
										System.out.println(toRemove.getOne() + " and " + toRemove.getTwo());

									}
								}
								
								toRemove = cc.findFirst(u.getName());
							}
							
							System.out.println("Signed out " + u.getName());
							uu.signOut(u);
							response.sendRedirect("/HelpDesk/helperTransfer.jsp");

						}
						
					}
					else
					{
						String s = parameterNames.get(0);
						if(s != null)
						{
							if(request.getParameter(s) != null)
							{
								if(((UserManager)request.getServletContext().getAttribute("users")).getUserifExists(s) != null && request.getParameter("HiddenCSRF") != null && request.getParameter("HiddenCSRF").equals(request.getSession().getAttribute("CSRFToken")))
								{
									String myName = ((UserManager)request.getServletContext().getAttribute("users")).getUserifExists_ID(request.getSession().getId().toString()).getName();

									String currentName1 = (String) request.getSession().getAttribute("guestName");
									String currentName2 = (String) request.getSession().getAttribute("helperName");

									if(currentName1.equals(myName))
									{
										request.getSession().setAttribute("helperName", s);
									}
									else
										request.getSession().setAttribute("guestName", s);

								}
							}
						}
						response.sendRedirect("/HelpDesk/helperChatList.jsp");

					}
				}
				try{
					response.sendRedirect("/HelpDesk");
				}
				catch(Throwable x)
				{//be quiet!
				}
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	class MoveFromNobodyThread extends Thread
	{
		MoveFromNobodyThread()
		{
			
		}
		
		public void run()
		{
			while(c == null)
			{
				//wait for c to not be null
			}
			
			UserManager uu = (UserManager) c.getAttribute("users");
			ConversationManager cc = (ConversationManager) c.getAttribute("conversations");
			User u = UserManager.greatOne;
			Conversation toRemove = cc.findFirst(u.getName());
			
			while(true)
			{
				try{
	
				while(toRemove != null) //give all of the conversations to somebody else
				{
					if(toRemove.getOne().equals(u.getName()))
					{
						if(uu.getHelpers().contains(toRemove.getTwo())) //if it's a conversation between two helpers, just remove it
						{
							System.out.println("Removed conversation between(mvr thrd1): " + toRemove.getOne() + " " + toRemove.getTwo());
							cc.remove(toRemove);
						}
						else
						{
							System.out.print("Mover Thread transfered: " + toRemove.getOne() + " and " + toRemove.getTwo() + "->");
							cc.transfer(toRemove, uu.getLowestThatIsntMe(cc,u).getName(), toRemove.getTwo() );
							System.out.println(toRemove.getOne() + " and " + toRemove.getTwo());
						}
					}
					else if(toRemove.getTwo().equals(u.getName()))
					{
						if(uu.getHelpers().contains(toRemove.getOne()))
						{
							System.out.println("Removed conversation between(mvr thrd2): " + toRemove.getOne() + " " + toRemove.getTwo());
							cc.remove(toRemove);

						}
						else
						{
							System.out.print("Mover Thread transfered: " + toRemove.getOne() + " and " + toRemove.getTwo() + "->");
							cc.transfer(toRemove, toRemove.getOne(),  uu.getLowestThatIsntMe(cc, u).getName());
							System.out.println(toRemove.getOne() + " and " + toRemove.getTwo());

						}
					}
					
					toRemove = cc.findFirst(u.getName());
				}
				try {
					System.out.println("Moving thread going to sleep for 5s");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				toRemove = cc.findFirst(UserManager.greatOne.getName());
				
			}
				catch(Throwable t)
				{
					System.out.println("Probably null pointer exception because there is only 1 helper left and getLowestThatIsntMe returned null ");
					try {
						System.out.println("Moving thread going to sleep for 5s");
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

		}
	}
	}
	
	class CleanupThread extends Thread
	{
		CleanupThread()
		{
			
		}
		
		public void run()
		{
			while(c == null)
			{
				//wait for c to not be null
			}
		
			UserManager uu = (UserManager) c.getAttribute("users");
			ConversationManager cc = (ConversationManager) c.getAttribute("conversations");
			
			ArrayList<User> peeps;
			Conversation toRemove = null;
			while(true)
			{	try{
				
				peeps = uu.getExpired();
				
				for(User u : peeps)
				{
					if(u.getType().equals("guest"))
					{
						toRemove = cc.findFirst(u.getName());
						
						cc.remove(toRemove);
						uu.signOut(u);
						System.out.println("Removed "+ u.getName());
						
					}
					else if(u.getType().equals("helper"))
					{
						toRemove = cc.findFirst(u.getName());
						
						while(toRemove != null) //give all of the conversations to somebody else
						{
							if(toRemove.getOne().equals(u.getName()))
							{
								if(uu.getHelpers().contains(toRemove.getTwo())) //if it's a conversation between two helpers, just remove it
								{
									System.out.println("Removed conversation between: " + toRemove.getOne() + " " + toRemove.getTwo());
									cc.remove(toRemove);
								}
								else
								{
									System.out.print("Cleanup transfered: " + toRemove.getOne() + " and " + toRemove.getTwo() + "->");
									cc.transfer(toRemove, uu.getLowestThatIsntMe_Cleanup(cc,u).getName(), toRemove.getTwo() );
									System.out.println(toRemove.getOne() + " and " + toRemove.getTwo());
								}
							}
							else if(toRemove.getTwo().equals(u.getName()))
							{
								if(uu.getHelpers().contains(toRemove.getOne()))
								{
									System.out.println("Removed conversation between: " + toRemove.getOne() + " " + toRemove.getTwo());
									cc.remove(toRemove);

								}
								else
								{
									System.out.print("Cleanup transfered: " + toRemove.getOne() + " and " + toRemove.getTwo() + "->");
									cc.transfer(toRemove, toRemove.getOne(),  uu.getLowestThatIsntMe_Cleanup(cc, u).getName());
									System.out.println(toRemove.getOne() + " and " + toRemove.getTwo());

								}
							}
							
							toRemove = cc.findFirst(u.getName());
						}
						
						System.out.println("Signed out " + u.getName());
						uu.signOut(u);
					}
				}
				
				
				
				try {
					System.out.println("Cleanup thread going to sleep for 30s");
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch(Throwable t)
			{
				System.out.println("Probably null pointer exception because there is only 1 helper left and getLowestThatIsntMe returned null ");
				try {
					System.out.println("Cleanup thread going to sleep for 30s");
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			}
			
			
		}
		
		
		
	}
}
