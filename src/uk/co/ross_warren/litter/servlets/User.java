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
		UserStore output = (UserStore)session.getAttribute("User");

		switch (args.length){
			case 2:
				if (output != null && output.isloggedIn() == true)
				{
					ReturnAuthor(request, response,0, output.getUserName());
				}
				break;
			case 3: if (FormatsMap.containsKey(args[2])){ //Display an author
						Integer IFormat= (Integer)FormatsMap.get(args[2]);
						if (output != null && output.isloggedIn() == true)
						{
							 switch((int)IFormat.intValue()){
							 	
								 case 3:ReturnAuthor(request, response,3, output.getUserName()); //Only JSON implemented for now
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
						case 3:ReturnAuthor(request, response,3,args[2]); //Only JSON implemented for now
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
		UserConnector au = new UserConnector();
		UserStore Author;
		Author = au.getUserByUsername(username);
		if (Author==null){
			Author=new UserStore();
			Author.setEmail("");
			Author.setName("");
		} else {
			String Email = Author.getEmail();
			Author = au.getUserByEmail(Email);
			if (Author==null){
				Author=new UserStore();
				Author.setEmail("");
				Author.setName("");
			} else {
				Author.setUserName(username);
			}
		}
		
		HttpSession session=request.getSession();
		session.setAttribute("followers", null);
		session.setAttribute("followees", null);
		UserConnector connect = new UserConnector();
		List<FollowereeStore> followers = connect.getFollowers(Author.getUserName());
		if (followers != null && followers.size() > 0)
		{
			List<FollowereeStore> followerList = new ArrayList<FollowereeStore>();
			for (FollowereeStore follow : followers)
			{
				try 
				{
					follow.setAvatarUrl(connect.getUserByEmail(connect.getUserByUsername(follow.getUsername()).getEmail()).getAvatarUrl());
					
				}
				catch(Exception e)
				{
					System.out.println("Oh noes could not get the avatar URL" + e);
				}
				followerList.add(follow);
				System.out.println("Followed by: " + follow.getUsername());
			}
			session.setAttribute("followers", followerList);
		}
		List<FollowereeStore> followees = connect.getFollowees(Author.getUserName());
		if (followees != null && followees.size() > 0)
		{
			List<FollowereeStore> followeeList = new ArrayList<FollowereeStore>();
			for (FollowereeStore follow : followees)
			{
				try 
				{
					follow.setAvatarUrl(connect.getUserByEmail(connect.getUserByUsername(follow.getUsername()).getEmail()).getAvatarUrl());
					
				}
				catch(Exception e)
				{
					System.out.println("Oh noes could not get the avatar URL" + e);
				}
				followeeList.add(follow);
				System.out.println("Follows: " + follow.getUsername());
			}
			session.setAttribute("followees", followeeList);
			
		}
		request.setAttribute("Tweets", null);
		TweetConnector connector = new TweetConnector();
		List<TweetStore> tweets = connector.getTweets(Author.getUserName());
		Collections.sort(tweets);
		request.setAttribute("Tweets", tweets);
		
		System.out.println("Got Author "+Author.getName()+" : "+Format);
		System.out.flush();
		switch(Format){
			case 0: request.setAttribute("ViewUser", Author);
					RequestDispatcher rd=request.getRequestDispatcher("/RenderUser.jsp");
					//System.out.println("Added jsp to dispatcher");
					rd.forward(request,response);
					//System.out.println("We Shouldn't be here");
					break;
			case 3: request.setAttribute("Data", Author);
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
		UserStore Author =new UserStore();
		RequestDispatcher rd;
		HttpSession session=request.getSession();
		UserStore lc =(UserStore)session.getAttribute("User");
		if (lc==null){
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			rd.forward(request,response);
		}
		Author.setEmail(lc.getEmail());
		Author.setName(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Name")));
		Author.setUserName(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Username")));
		Author.setBio(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Bio")));
		Author.setAvatar(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Avatar")));
		UserConnector au = new UserConnector();
		if (au.addUser(Author)== true){
			lc = Author;
			lc.login(lc.getEmail());
			session.setAttribute("User", lc);
			try {
				response.sendRedirect("User/" + lc.getUserName());
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
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}