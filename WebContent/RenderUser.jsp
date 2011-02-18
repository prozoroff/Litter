<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="uk.co.ross_warren.litter.stores.*" %>
<%@ page import="uk.co.ross_warren.litter.connectors.*" %>
<%@ page import="java.util.*" %>
    <jsp:useBean id="User"
class="uk.co.ross_warren.litter.stores.UserStore"
scope="session"
></jsp:useBean>
<%
	String follow = "Follow";
	Boolean loggedin = false;
	if (User != null)
	{
		loggedin = User.isloggedIn();
	}
%>
<!DOCTYPE html> <!-- The new doctype -->
<html>
    <head>
    	<% UserStore displayUser = (UserStore)request.getAttribute("ViewUser"); %>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Litter - <%= displayUser.getUserName() %></title>
        <script src="../js/jquery-1.4.4.min.js"></script>
        <script src="../js/jquery-ui-1.8.9.custom.min.js"></script>
        <script src="../js/JSON.js"></script>
        <link rel="stylesheet" type="text/css" href="../styles.css" />
        <link href='http://fonts.googleapis.com/css?family=Chewy' rel='stylesheet' type='text/css'> 
        <link type="text/css" href="../css/ui-lightness/jquery-ui-1.8.9.custom.css" rel="Stylesheet" />
        <!-- Internet Explorer HTML5 enabling code: -->       
        <!--[if IE]>
        <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>       
        <style type="text/css">
        .clear {
          zoom: 1;
          display: block;
        }
        </style>       
        <![endif]-->   
    </head> 
    <body>	
    	<section id="page"> <!-- Defining the #page section with the section tag -->
            <header> <!-- Defining the header section of the page with the appropriate tag -->
                <a href="/Litter/"><img style="padding-top: 15px; padding-right: 15px;" align="left" src="/Litter/img/IMG-small.png" /></a>
                <hgroup>
                    <h1><a href="/Litter/" style="color: white;">Litter</a></h1>
                    <h3>Like Twitter but with liking!</h3>
                </hgroup>            
                <nav class="clear"> <!-- The nav link semantically marks your main site navigation -->
                    <ul>
                    	<li><a href="../">Home</a>
                    	<%
                    		if (!loggedin)
                    		{
                    			%>
                    			<li><a href="../Login/Google">Login With Google</a></li>
                    			<li><a href="../Login/Yahoo">Login With Yahoo</a></li>
                    		<%
                    		} else {
                    			%>
                    			<li><a href="../User/<%=User.getUserName() %>">Your Profile</a></li>
                    			<li><a href="../Logout">Logout</a></li>
                    			<%
                    		}
                    	%>
                    </ul>
                </nav>
            </header>
            <section id="articles"> <!-- A new section with the articles -->
                <div class="line"></div>  <!-- Dividing line -->   
                <article id="Welcome">
                   <% 
					System.out.println("In RenderAuthor.jsp");
					
					if (displayUser==null || displayUser.getEmail() == ""){
					 %>
						<h2>User was not found.</h2>
						<% 
					}else{
					%>
					<table>
					<tr><td><img title="<%=displayUser.getUserName() %>" style="padding-right: 10px;" src="<%=displayUser.getAvatarUrl() %>" /></td>
					<td><h2 style="top: 0%"><%=displayUser.getUserName() %> (<%=displayUser.getName() %>)</h2>
					<p><%=displayUser.getBio() %></p>
					<% 
					if (User != null && User.isloggedIn() == true)
					{
						if (displayUser.getUserName().equals(User.getUserName()) == false)
						{
						
						%>
						
						<div class="demo">
						<button id = "follow" >Follow</button>
						</div>
						<%} else {
							%>
							<a href="/Litter/User/Update">Update Your details</a>
							<%
						}
					} 
					
					List<FollowereeStore> Followers = (List<FollowereeStore>)session.getAttribute("followers");
					%>
					</td>
					</tr>
					</table>
					<div class = "line"></div>
					<h4><%=displayUser.getUserName() %> has <% if (Followers != null) { %><%= Followers.size()%><% } else { %>0<%} %> Followers</h4>
					<p>
					<%
						
						if (Followers != null)
						{
							for (FollowereeStore store : Followers)
							{
								if (store.getUsername().equals(User.getUserName()))
								{
									follow = "Unfollow";
								}
								%>
								<a href="/Litter/User/<%= store.getUsername() %>" class="info"><img width = "25px" height = "25px" src="<%= store.getAvatarUrl() %>" /><span><%= store.getUsername() %></span></a>
								<%
							}
						}
					List<FollowereeStore> Followees = (List<FollowereeStore>)session.getAttribute("followees");
					
					%>
					</p>
					
					<div class="line"></div>  <!-- Dividing line -->  
					<h4><%=displayUser.getUserName() %> Follows <% if (Followees != null) { %><%= Followees.size()%><% } else { %>0<%} %> Users</h4>
					<p>
					<%
						if (Followees != null)
						{
							for (FollowereeStore store : Followees)
							{
								%>
								<a href="/Litter/User/<%= store.getUsername() %>" class="info"><img width = "25px" height = "25px" src="<%= store.getAvatarUrl() %>" /><span><%= store.getUsername() %></span></a>
								<%
							}
							
						}
					%>
					</p>
					<div class="line"></div>  <!-- Dividing line -->  
					<table style="width: 100%">
					<tr>
					<td style="width: 50%; vertical-align: top">
					<h2><%= displayUser.getUserName() %>'s Timeline</h2>
					<% 
					List<TweetStore> tweets = (List<TweetStore>)request.getAttribute("Tweets");
					if (tweets != null && tweets.size() > 0)
					{
						for (TweetStore tweet: tweets)
						{
							%>
							<div class="tweet">
								<img width = "33px" height = "33px" style="margin-top: 11px; margin-right: 15px" class="<%= tweet.getUser() %>" src="" align="left"/>
								<% 
							TweetConnector connector = new TweetConnector();
							String like = "Like";
							if (connector.checkLike(User.getUserName(), tweet.getTweetID()) == true) like = "Unlike";
							String username = tweet.getUser();
							%>
							
							<p>
							<%= tweet.getContent() %>
							<span style="float: right">Likes: <%= tweet.getLikes() %></span></p>
							<p><a href="/Litter/User/<%= username %>"><%= username %></a>
								<% if (tweet.getReplyToUser() != null && !tweet.getReplyToUser().equals("")) {
								%>
								 to 
								<a href="/Litter/User/<%= tweet.getReplyToUser() %>"><%= tweet.getReplyToUser() %></a>
								<% } %>
								
							<a style="float: right" class="<%= like %>" id="<%=tweet.getTweetID() %>"><%= like %></a></p>
							</div>
							<script>
					        	$(function() {
					        		$.ajax({
					        			url: '/Litter/User/<%= tweet.getUser() %>/json',
					        			success: function(data) {
					        				var obj = jQuery.parseJSON(data);
					        				var avatarurl = data.AvatarURL;	
					        				$('.<%= tweet.getUser() %>').attr('src', obj.AvatarUrl);
					        			}
					        		});					        		
					        	});
					        </script>
							<%
						}	
					}
				%>
				</td>
				<td style="width: 50%; vertical-align: top">
					<h2><%= displayUser.getUserName() %>'s Mentions</h2>
					<% 
					List<TweetStore> atReplies = (List<TweetStore>)request.getAttribute("AtReplies");
					if (atReplies != null && atReplies.size() > 0)
					{
						for (TweetStore tweet: atReplies)
						{
							%>
							<div class="tweet">
								<img width = "33px" height = "33px" style="margin-top: 11px; margin-right: 15px" class="<%= tweet.getUser() %>" src="" align="left"/>
								<% 
							TweetConnector connector = new TweetConnector();
							String like = "Like";
							if (connector.checkLike(User.getUserName(), tweet.getTweetID()) == true) like = "Unlike";
							String username = tweet.getUser();
							%>
							
							<p>
							<%= tweet.getContent() %>
							<span style="float: right">Likes: <%= tweet.getLikes() %></span></p>
							<p><a href="/Litter/User/<%= username %>"><%= username %></a>
								<% if (tweet.getReplyToUser() != null && !tweet.getReplyToUser().equals("")) {
								%>
								 to 
								<a href="/Litter/User/<%= tweet.getReplyToUser() %>"><%= tweet.getReplyToUser() %></a>
								<% } %>
								
							<a style="float: right" class="<%= like %>" id="<%=tweet.getTweetID() %>"><%= like %></a></p>
							</div>
							<script>
					        	$(function() {
					        		$.ajax({
					        			url: '/Litter/User/<%= tweet.getUser() %>/json',
					        			success: function(data) {
					        				var obj = jQuery.parseJSON(data);
					        				var avatarurl = data.AvatarURL;	
					        				$('.<%= tweet.getUser() %>').attr('src', obj.AvatarUrl);
					        			}
					        		});					        		
					        	});
					        </script>
							<%
						}	
					}
					}
				%>
				</tr>
				</table>
				</article>
            </section>
       <%@ include file="footer.jsp" %>  
		</section> <!-- Closing the #page section -->
        
        <!-- JavaScript Includes -->
        
		
        <script src="../jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
        <script src="../script.js"></script>
        
        
        
        <script>
        $(window).load(function() {  
        	$("#follow").text("<%= follow %>");
       	});  
        
        </script>
        <script>
        
        $("#follow").click(function () {
        	$.ajax({
        		aysnc: true,
   				type: "POST",
   				url: "/Litter/Follow/<%= displayUser.getUserName() %>",
   				dataType: "text",
  				success: function(msg){
  					location.reload(); 
   				}
        	});
 		});
        </script>
        <script>
        
	        $(".Like").click(function () {
		var url = "/Litter/Like/" + (this.id);
		$.ajax({
			aysnc: true,
				type: "POST",
				url: url,
				dataType: "text",
				success: function(msg){
					location.reload(); 
				}
		});
		}); 
        </script>   
        
        </script>
        <script>
	$(function() {
		$( "button", ".demo" ).button();
		$( "a", ".demo" ).click(function() { return false; });
	});
	</script>
         
    </body>
</html>