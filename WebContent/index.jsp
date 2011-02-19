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
	Boolean loggedin = false;
	if (User != null)
	{
		loggedin = User.isloggedIn();
	}
%>
<!DOCTYPE html> <!-- The new doctype -->
<html>
    <head>
    
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Litter</title>
        <link rel="stylesheet" type="text/css" href="styles.css" />
        <link href='http://fonts.googleapis.com/css?family=Chewy' rel='stylesheet' type='text/css'>
        <script src="js/jquery-1.4.4.min.js"></script>
        <script src="js/jquery-ui-1.8.9.custom.min.js"></script>
         <script language="javascript" src="Jquery.js"></script>
		<script language="javascript">
		function limitChars(textid, limit, infodiv)
		{
			var text = $('#'+textid).val(); 
 			var textlength = text.length;
			if(textlength > limit)
			{
				$('#' + infodiv).html('You cannot write more then '+limit+' characters!');
				$('#'+textid).val(text.substr(0,limit));
				return false;
			}
			else
			{
				$('#' + infodiv).html('You have '+ (limit - textlength) +' characters left.');
				return true;
			}
		}
 		</script>
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
                    	<%
                    		if (!loggedin)
                    		{
                    			%>
                    			<li><a href="Login/Google">Login With Google</a></li>
                    			<li><a href="Login/Yahoo">Login With Yahoo</a></li>
                    		<%
                    		} else {
                    			%>
                    			<li><a href="User/<%=User.getUserName() %>">Your Profile</a></li>
                    			<li><a href="Logout">Logout</a></li>
                    			<%
                    		}
                    	%>
                    </ul>
                </nav>
            </header>
            <section id="articles"> <!-- A new section with the articles -->
                <div class="line"></div>  <!-- Dividing line -->   
                <% if (!loggedin) { %>
                <article id="Welcome">
                    <h2>Welcome!</h2>
					<div class="line"></div>  <!-- Dividing line -->
					<img align="right" src="/Litter/img/IMG.png" />
					<p>Welcome to Litter. To begin, click Login with Google or Yahoo at the top left of the page. </p>
					<p style="margin-bottom: 200px;">Currently in Alpha development. Please be patient!</p>
                </article>
                <% } else { %>
                <article id="Welcome">
                    <h2>Welcome back <%=User.getUserName() %></h2>
					<div class="line"></div>  <!-- Dividing line -->
					<form style="width: 80%" action='/Litter/Tweet' method="POST">
						<table style="width: 100%">
						<tr>
						<td>
						<textarea id = "comment" style="background: none; width: 90%; min-height: 42px; font-size: 1em; background-color: white;" name="Content" required placeholder="Write your post here"></textarea> 
						<p id="charlimitinfo">
						<script>
						$(function(){
							$('#comment').keyup(function(){
								limitChars('comment', 140, 'charlimitinfo');
							});
							
							limitChars('comment', 140, 'charlimitinfo');			
						
						});
						
						</script>
						</td>
						<td style="vertical-align: top;">
							<input style="width: 90%; background-color: white;" type="submit"  value="Tweet">
						</td>
						</tr>
						</table>
					</form>
					<%
						TweetConnector connect = new TweetConnector();
						List<TweetStore> tweets= connect.GetFeed(User.getUserName());
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
                </article>
                <% } %>
            </section>
        <%@ include file="footer.jsp" %>                 
		</section> <!-- Closing the #page section -->
        
        <!-- JavaScript Includes -->
        <script src="jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
        <script src="script.js"></script>
        
        <script>
        
        $(".Unlike").click(function () {
    		var url = "/Litter/Like/" + (this.id);
    		$.ajax({
    			aysnc: true,
    				type: "DELETE",
    				url: url,
    				dataType: "text",
    				success: function(msg){
    					location.reload(); 
    				}
    		});
    		}); 
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
    </body>
</html>
