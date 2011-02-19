package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.UserConnector;
import uk.co.ross_warren.litter.stores.FollowereeStore;
import uk.co.ross_warren.litter.stores.UserStore;

/**
 * Servlet implementation class Follow
 */
@WebServlet("/Follow")
public class Follow extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> FormatsMap = new HashMap<String, Integer>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Follow() {
        super();
        FormatsMap.put("Jsp", 0);
		FormatsMap.put("xml", 1);
		FormatsMap.put("rss", 2);
		FormatsMap.put("json",3);
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);

		switch (args.length){
			case 3:
				if (FormatsMap.containsKey(args[2])) {
					Integer IFormat= (Integer)FormatsMap.get(args[2]);
					HttpSession session=request.getSession();
					UserStore sessionUser = (UserStore)session.getAttribute("User");
					if (sessionUser != null && sessionUser.isloggedIn() == true)
					{
						switch((int)IFormat.intValue()){
							case 3:GetFollowers(request, response,3,sessionUser.getUserName()); //Only JSON implemented for now
							break;
						}
						
					}
				}
				break;
			
			case 4: if (FormatsMap.containsKey(args[3])){ //all authors in a format
						Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
						case 3:GetFollowers(request, response,3,args[2]); //Only JSON implemented for now
					 		break;
						default:break;
						}
					}
					break;
			default: System.out.println("Wrong number of arguements in doGet Author "+request.getRequestURI()+" : "+args.length);
			break;
		}
	
	}
		
	public void GetFollowers(HttpServletRequest request, HttpServletResponse response,int Format, String username) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		HttpSession session=request.getSession();
		session.setAttribute("followers", null);
		session.setAttribute("followees", null);
		UserConnector connect = new UserConnector();
		List<FollowereeStore> followers = connect.getFollowers(username);
		List<FollowereeStore> followerList = new LinkedList<FollowereeStore>();
		if (followers != null && followers.size() > 0)
		{
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
				System.out.println("Followed by" + follow.getUsername());
			}
		}
		switch(Format){
			case 3: request.setAttribute("Data", followerList);
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
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);
		if (args.length == 3)
		{
			String usernameToFollow = args[2]; //args 2 is the person you want to follow
			UserConnector connector = new UserConnector();
			HttpSession session=request.getSession();
			UserStore sessionUser =(UserStore)session.getAttribute("User");
			if (sessionUser == null || sessionUser.isloggedIn() == false)
			{
				//
			} else {
				if (connector.addFollower(sessionUser.getUserName(), usernameToFollow) == true)
				{
					System.out.println("Follow success");
					if (connector.addFollowee(sessionUser.getUserName(), usernameToFollow))
					{
						System.out.println("Followee success");
					}
				}
				else {
					try
					{
						if (connector.removeFollower(sessionUser.getUserName(), usernameToFollow) && connector.removeFollowee(sessionUser.getUserName(), usernameToFollow))
						{
							System.out.println("UnfollowSuccess");
						}
					} catch (Exception e)
					{
						System.out.println("Unfollow fail: " + e);
					}
				}
			}
		}
	}
}
