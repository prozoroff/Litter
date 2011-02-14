package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.ross_warren.litter.stores.TweetStore;

/**
 * Servlet implementation class Tweet
 */
@WebServlet("/Tweet")
public class Tweet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tweet() {
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
		TweetStore tweet = new TweetStore();
		tweet.setUser(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("Username")));
		tweet.setTweetID(org.apache.commons.lang.StringEscapeUtils.escapeHtml(request.getParameter("TweetID")));
	}

}
