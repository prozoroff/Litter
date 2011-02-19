package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.TweetConnector;
import uk.co.ross_warren.litter.connectors.UserConnector;
import uk.co.ross_warren.litter.stores.FollowereeStore;
import uk.co.ross_warren.litter.stores.TweetStore;
import uk.co.ross_warren.litter.stores.UserStore;

/**
 * Servlet implementation class Author
 */
public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private HashMap<String, Integer> FormatsMap = new HashMap<String, Integer>();
    /**
     * Default constructor. 
     */
    public User() {
        // TODO Auto-generated constructor stub
    	 FormatsMap.put("Jsp", 0);
    	 FormatsMap.put("xml", 1);
    	 FormatsMap.put("rss", 2);
    	 FormatsMap.put("json",3);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("ViewUser", null);
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);
		HttpSession session=request.getSession();
		UserStore sessionUser = (UserStore)session.getAttribute("User");

		switch (args.length){
			case 2:
				if (sessionUser != null && sessionUser.isloggedIn() == true)
				{
					ReturnAuthor(request, response,0, sessionUser.getUserName());
				}
				break;
			case 3: 
				if (args[2].equals("Update"))
				{
					response.sendRedirect("/Litter/UpdateUser.jsp");
					return;
				}	
				if (FormatsMap.containsKey(args[2])){ //Display an author
						Integer IFormat= (Integer)FormatsMap.get(args[2]);
						if (sessionUser != null && sessionUser.isloggedIn() == true)
						{
							 switch((int)IFormat.intValue()){
							 	
								 case 3:ReturnAuthor(request, response,3, sessionUser.getUserName()); //Only JSON implemented for now
								 		break;
								 default:break;
							 }
						}
						else
						{
							response.sendRedirect("/Litter/");
						}
					}else{ //Must be a single Author request
						System.out.println("Call return Author");
						 ReturnAuthor(request, response,0,args[2]);
					}
					break;
			case 4: if (FormatsMap.containsKey(args[3])){ //Display an Author
						Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
						case 3:
							ReturnAuthor(request, response,3,args[2]); //Only JSON implemented for now
					 		break;
						default:break;
						}
					}
					break;
			default: System.out.println("Wrong number of arguements in doGet Author "+request.getRequestURI()+" : "+args.length);
					break;
		}
	}

	public void ReturnAuthor(HttpServletRequest request, HttpServletResponse response,int Format, String username) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */	
		UserConnector userConnect = new UserConnector();
		UserStore dataUser;
		dataUser = userConnect.getUserByUsername(username);
		if (dataUser==null){
			dataUser=new UserStore();
			dataUser.setEmail("");
			dataUser.setName("");
		} else {
			String Email = dataUser.getEmail();
			dataUser = userConnect.getUserByEmail(Email);
			if (dataUser==null){
				dataUser=new UserStore();
				dataUser.setEmail("");
				dataUser.setName("");
			} else {
				dataUser.setUserName(username);
				request.setAttribute("Tweets", null);
				TweetConnector tweetConnect = new TweetConnector();
				List<TweetStore> tweets = tweetConnect.getTweets(dataUser.getUserName());
				if (tweets != null && tweets.size() > 0) Collections.sort(tweets);
				request.setAttribute("Tweets", tweets);
				request.setAttribute("AtReplies", null);
				List<TweetStore> atReplies = tweetConnect.getAtReplies(dataUser.getUserName());
				if (atReplies != null && atReplies.size() > 0) Collections.sort(atReplies);
				request.setAttribute("AtReplies", atReplies);
		
			}
		}
		
		
		System.out.println("Got Author "+dataUser.getName()+" : "+Format);
		System.out.flush();
		switch(Format){
			case 0: request.setAttribute("ViewUser", dataUser);
					RequestDispatcher rd=request.getRequestDispatcher("/RenderUser.jsp");
					//System.out.println("Added jsp to dispatcher");
					rd.forward(request,response);
					//System.out.println("We Shouldn't be here");
					break;
			case 3: request.setAttribute("Data", dataUser);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			default: System.out.println("Invalid Format in ReturnAllAuthors ");
		}


	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		UserStore writeUser =new UserStore();
		RequestDispatcher rd;
		HttpSession session=request.getSession();
		UserStore sessionUser =(UserStore)session.getAttribute("User");
		if (sessionUser==null){
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			rd.forward(request,response);
		}
		writeUser.setEmail(sessionUser.getEmail());
		writeUser.setName(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Name")));
		writeUser.setUserName(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Username")));
		writeUser.setBio(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Bio")));
		writeUser.setAvatar(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Avatar")));
		UserConnector au = new UserConnector();
		if (au.addUser(writeUser)== true){
			sessionUser = writeUser;
			sessionUser.login(sessionUser.getEmail());
			session.setAttribute("User", sessionUser);
			try {
				response.sendRedirect("User/" + sessionUser.getUserName());
			} catch (Exception et) {
				System.out.println("Couldn't Forward to Show User");
			}
		}else{
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			rd.forward(request,response);
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Updating ze user ja");
		UserStore updateUser =new UserStore();
		RequestDispatcher rd;
		HttpSession session=request.getSession();
		UserStore sessionUser =(UserStore)session.getAttribute("User");
		if (sessionUser==null){
			rd=request.getRequestDispatcher("UpdateUser.jsp");
			rd.forward(request,response);
		}
		updateUser = sessionUser;
		updateUser.setName(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Name")));
		updateUser.setBio(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Bio")));
		System.out.println(updateUser.getBio());
		updateUser.setAvatar(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Avatar")));
		UserConnector connect = new UserConnector();
		if (connect.updateUser(updateUser)== true){
			sessionUser = updateUser;
			sessionUser.login(sessionUser.getEmail());
			session.setAttribute("User", sessionUser);
			try {
				response.sendRedirect("User/" + sessionUser.getUserName());
			} catch (Exception et) {
				System.out.println("Couldn't Forward to Show User");
			}
		}else{
			rd=request.getRequestDispatcher("UpdateUser.jsp");
			rd.forward(request,response);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		UserStore sessionUser = (UserStore)session.getAttribute("User");
		String username = sessionUser.getUserName();
		String email = sessionUser.getEmail();
		UserConnector connect = new UserConnector();
		List<FollowereeStore> followers = connect.getFollowers(username);
		List<FollowereeStore> followees = connect.getFollowees(username);
		connect.deleteUser(username, email, followers, followees);
	}
}