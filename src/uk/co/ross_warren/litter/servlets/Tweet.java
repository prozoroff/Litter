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
 * Servlet implementation class Tweet
 */
@WebServlet("/Tweet")
public class Tweet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> FormatsMap = new HashMap<String, Integer>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tweet() {
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
		// TODO Auto-generated method stub
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);

		switch (args.length){
			case 3:
				if (FormatsMap.containsKey(args[2])) {
					Integer IFormat= (Integer)FormatsMap.get(args[2]);
					UserStore lc = (UserStore)request.getAttribute("User");
					if (lc != null && lc.isloggedIn() == true)
					{
						switch((int)IFormat.intValue()){
							case 3:GetTweets(request, response,3,lc.getUserName()); //Only JSON implemented for now
							break;
						}
						
					}
				}
				else
				{
					GetTweets(request, response,0,args[2]);
				}
				break;
			
			case 4: if (FormatsMap.containsKey(args[3])){ //all authors in a format
						Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
						case 3:GetTweets(request, response,3,args[2]); //Only JSON implemented for now
					 		break;
						default:break;
						}
					}
					break;
			default: System.out.println("Wrong number of arguements in doGet Tweet "+request.getRequestURI()+" : "+args.length);
			break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		TweetStore tweet = new TweetStore();
		HttpSession session=request.getSession();
		UserStore lc =(UserStore)session.getAttribute("User");
		if (lc != null && lc.isloggedIn() == true)
		{
			tweet.setUser(lc.getUserName());
			tweet.setTweetID(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("TweetID")));
			tweet.setContent(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Content")));
			tweet.setReplyToUser(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("ReplyToUser")));
			try {
				TweetConnector connector = new TweetConnector();
				connector.addTweet(tweet);
			} catch (Exception e)
			{
				System.out.println("There was curious error in tweeting the tweet." + e);
			}
		}
	}
	
	public void GetTweets(HttpServletRequest request, HttpServletResponse response,int Format, String username) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		TweetConnector connect = new TweetConnector();
		List<TweetStore> tweets = connect.getTweets(username);
		if (tweets == null) return;
		Collections.sort(tweets);
		switch(Format){
			case 3: request.setAttribute("Data", tweets);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			default: System.out.println("Invalid Format in Return Tweets ");
		}
	}
}
