package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.TweetConnector;
import uk.co.ross_warren.litter.stores.TweetStore;
import uk.co.ross_warren.litter.stores.UserStore;

/**
 * Servlet implementation class Feed
 */
@WebServlet("/Feed")
public class Feed extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> FormatsMap = new HashMap<String, Integer>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Feed() {
        super();
        FormatsMap.put("Jsp", 0);
		FormatsMap.put("xml", 1);
		FormatsMap.put("rss", 2);
		FormatsMap.put("json",3);
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 
		// TODO Auto-generated method stub
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);

		switch (args.length){
			case 2:
				response.sendRedirect("/Litter/"); //If just requests /Feed then redirect to the home page as it is shown there.
				break;
			case 3:
				if (FormatsMap.containsKey(args[2])) {  //if the second part of the request is a format for example /Feed/json
					Integer IFormat= (Integer)FormatsMap.get(args[2]);
					HttpSession session=request.getSession();
					UserStore sessionUser = (UserStore)session.getAttribute("User");
					if (sessionUser != null && sessionUser.isloggedIn() == true) //Check the request user is logged in
					{
						switch((int)IFormat.intValue()){
							case 3:GetFeed(request, response,3,sessionUser.getUserName(), false); //If the request is for json, call the getfeed method in json mode
							break;
							case 0: response.sendRedirect("/Litter/"); // if jsp is wanted redirect to the home page as it is shown there
							break;
							default: response.sendRedirect("/Litter/"); //same here
							break;
						}
						
					}
				}
				break;
			case 4:
				if (FormatsMap.containsKey(args[3])) { 
					Integer IFormat= (Integer)FormatsMap.get(args[3]);
					HttpSession session=request.getSession();
					UserStore sessionUser = (UserStore)session.getAttribute("User");
					if (sessionUser != null && sessionUser.isloggedIn() == true)
					{
						switch((int)IFormat.intValue()){
							case 3:GetFeed(request, response,3,sessionUser.getUserName(), true); //Only JSON implemented for now
							break;
							case 0: response.sendRedirect("/Litter/");
							break;
							default: response.sendRedirect("/Litter/");
							break;
						}
						
					}
				}
				break;
			default: System.out.println("Wrong number of arguements in doGet Author "+request.getRequestURI()+" : "+args.length);
			break;
		}
			
		
		
	}
	
	public void GetFeed(HttpServletRequest request, HttpServletResponse response,int Format, String username, Boolean orderByLike) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		TweetConnector connect = new TweetConnector();
		List<TweetStore> feed = connect.getFeed(username);		
		if (orderByLike)
		{	
			for (TweetStore tweet: feed)
			{
				tweet.switchToLikeOrdering();
			}
			Collections.sort(feed);
		}
		switch(Format){
			case 3: request.setAttribute("Data", feed);
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
	}

}
