package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
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
 * Servlet implementation class Mentions
 */
@WebServlet("/Mentions")
public class Mentions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> FormatsMap = new HashMap<String, Integer>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mentions() {
        super();
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
							case 3:GetFeed(request, response,3,sessionUser.getUserName()); //Only JSON implemented for now
							break;
							default: System.out.println("Incorrect Format");
							break;
						}
						
					}
				}
				
				break;
			case 4:
				if (FormatsMap.containsKey(args[3])) {
					Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
							case 3:GetFeed(request, response,3,args[2]); //Only JSON implemented for now
							break;
							default: System.out.println("incorrect format");
							break;
						}
						
				}
				break;
				
			default: System.out.println("Wrong number of arguements in doGet Author "+request.getRequestURI()+" : "+args.length);
			break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	public void GetFeed(HttpServletRequest request, HttpServletResponse response,int Format, String username) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		TweetConnector connect = new TweetConnector();
		
		List<TweetStore> feed = connect.getAtReplies(username);
		switch(Format){
			case 3: request.setAttribute("Data", feed);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			case 0: response.sendRedirect("/Litter/User/" + username);
			default: System.out.println("Invalid Format in ReturnAllAuthors ");
		}
	}

}
