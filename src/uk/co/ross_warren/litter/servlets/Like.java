package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.TweetConnector;
import uk.co.ross_warren.litter.stores.UserStore;

/**
 * Servlet implementation class Like
 */
@WebServlet("/Like")
public class Like extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Like() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
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
			System.out.println("Tweet ID to be liked is " + args[2]);
			String tweetID = args[2];
			HttpSession session=request.getSession();
			UserStore lc =(UserStore)session.getAttribute("User");
			if (lc == null || lc.isloggedIn() == false)
			{
				return;
			}
			TweetConnector connect = new TweetConnector();
			try
			{
				connect.like(lc.getUserName(), tweetID);
			}
				catch (Exception e)
			{
				System.out.println("Errors " + e);
			}
			
		}
	}

}
