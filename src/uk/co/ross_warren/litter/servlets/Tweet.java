package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.TweetConnector;
import uk.co.ross_warren.litter.connectors.UserConnector;
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
					HttpSession session=request.getSession();
					UserStore sessionUser = (UserStore)session.getAttribute("User");
					if (sessionUser != null && sessionUser.isloggedIn() == true)
					{
						switch((int)IFormat.intValue()){
							case 3:GetTweets(request, response,3,sessionUser.getUserName()); //Only JSON implemented for now
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
		UserStore sessionUser =(UserStore)session.getAttribute("User");
		if (sessionUser != null && sessionUser.isloggedIn() == true)
		{
			String content = request.getParameter("Content");
			
			if (content.length() > 140 || content.length() < 2)
			{
				System.out.println("tweet malformed");
				response.sendRedirect("/Litter/");
				return;
			}
			StringTokenizer tokens = new StringTokenizer(content);
			tweet.setReplyToUser("");
			while (tokens.hasMoreElements())
			{
				String token = (String) tokens.nextElement();
				if(token.charAt(0) == '@')
				{
					token.trim();
					token = token.substring(1, token.length());
					System.out.println(token);
					tweet.setReplyToUser(token);
				}
			}
			tweet.setLatitude(request.getParameter("latitude"));
			tweet.setLongitude(request.getParameter("longitude"));
			tweet.setUser(sessionUser.getUserName());
			tweet.setTweetID(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("TweetID")));
			String unsafe = request.getParameter("Content");
			Whitelist custom = Whitelist.simpleText();
			custom.addEnforcedAttribute("a", "rel", "nofollow");
			custom.addProtocols("a", "href", "ftp", "http", "https");
			custom.addAttributes("a", "href");
			custom.addTags("a");
			String safe = Jsoup.clean(unsafe, custom);
			tweet.setContent(safe);
			//tweet.setReplyToUser(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("ReplyToUser")));
			try {
				TweetConnector connector = new TweetConnector();
				connector.addTweet(tweet);
			} catch (Exception e)
			{
				System.out.println("There was curious error in tweeting the tweet." + e);
			}
			
			response.sendRedirect("/Litter/");
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		UserStore sessionUser =(UserStore)session.getAttribute("User");
		if (sessionUser != null && sessionUser.isloggedIn() == true)
		{
			StringSplitter split = new StringSplitter();
			String args[]=split.SplitRequestPath(request);
			
			if (args.length == 3)
			{
				try {
					TweetConnector connector = new TweetConnector();
					connector.deleteTweet(args[2]);
				} catch (Exception e)
				{
					System.out.println("There was curious error in detweeting the tweet." + e);
				}
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
		UserConnector connector = new UserConnector();
		if (tweets == null || tweets.size() < 1) return;
		UserStore store = connector.getUserByUsername(tweets.get(0).getUser());
		store = connector.getUserByEmail(store.getEmail());
		String avatarurl = store.getAvatarUrl();
		
		for (TweetStore tweet: tweets)
		{
			tweet.setAvatarUrl(avatarurl);
		}
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
