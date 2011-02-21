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
    	<% UserStore displayUser = (UserStore)request.getAttribute("ViewUser"); %>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Litter - <%= displayUser.getUserName() %></title>
        <script src="../js/jquery-1.4.4.min.js"></script>
        <script src="../js/jquery-ui-1.8.9.custom.min.js"></script>
        <script src="../js/JSON.js"></script>
        <script>
        function loadfeed() {
       		var url = '/Litter/Tweet/<%= displayUser.getUserName() %>/json';
       		$.getJSON(url, function(json) {
       			//$("#feed").fadeOut('fast');
       			$("#feed").html('');
       			$.each(json.Data, function(i, Data) {   
       				var url3 = '/Litter/Like/' + this.TweetID;
       				TheObject2 = {
       					check : function(callback) {
           			   		$.ajax({
           			   			type: "GET",
           			   			url: url3,
           			   			async: false,
           			   			success: function(msg) {$.ajax({
           			    			//aysnc: true,
           		    			//	type: "DELETE",
           		    			///	url: url,
           		    			//	dataType: "text",
           		    			//	success: function(msg){
           		    			//		location.reload(); 
           		    			//	}
           		    			});
         			   			callback.call(this, msg);
           			   		}
           			   	});
           		 	}
       			};
       				
       			
       				
     			var like = 'Like';
     			TheObject2.check(function(a) {
     				like = a;
     			});
       				
       			var isempty = this.ReplyToUser;
       			var bleh = '';
       			if (isempty)
       			{
       				bleh = ' to <a href="/Litter/User/' +
							this.ReplyToUser + '">' + this.ReplyToUser + '</a>';
       			}
       				
       			
       			var deletetext = '';
       			var displayuser = '';
       			var tweetuser = this.User;
       			<% if (loggedin) { %>
       				
       			displayuser = '<%= User.getUserName() %>';

       			<% } %>
       			
       			if (displayuser == tweetuser)
     			{
     				deletetext = '<a style="float: right" class="delete"' +
   					'id="' + this.TweetID + '"><img src="/Litter/img/delete.png" /></a>';
     			}

   				$("#feed").append('<div class="tweet">' +
   						'<img width = "33px" height = "33px" style="margin-top: 11px; margin-right: 15px"' +
   	  					'src="' + this.AvatarUrl + '" align="left" />' +
   	  					'<p>' + this.Content +
   	  					'<span style="float: right">Likes: ' + this.Likes +
   	  					<%
   	  					if (loggedin == true)
   	  						{
   	  						%>
   	  						
   	  						
   	  					' - <a class="like"' +
   	   					'id="' + this.TweetID + '">' + like + '</a>' +
   	   				<%
   	  						}%>
   	  					'</span></p>' +
   	   					'<p>' +
   	  					deletetext + 
   	  					'<a href="/Litter/User/' + this.User + '">' + this.User + '</a>' +
   	   					bleh + 
   	   					'</p>' +
   	   					'</div>');
      		});
       			//$("#feed").fadeIn('slow');
       	});	
       		
        }	
        
        function loadmentions() {
       		var url = '/Litter/Mentions/<%= displayUser.getUserName() %>/json';
       		$.getJSON(url, function(json) {
       			//$("#feed").fadeOut('fast');
       			$("#mentions").html('');
       			$.each(json.Data, function(i, Data) {   
       				var url3 = '/Litter/Like/' + this.TweetID;
       				TheObject2 = {
       					check : function(callback) {
           			   		$.ajax({
           			   			type: "GET",
           			   			url: url3,
           			   			async: false,
           			   			success: function(msg) {$.ajax({
           			    			//aysnc: true,
           		    			//	type: "DELETE",
           		    			///	url: url,
           		    			//	dataType: "text",
           		    			//	success: function(msg){
           		    			//		location.reload(); 
           		    			//	}
           		    			});
         			   			callback.call(this, msg);
           			   		}
           			   	});
           		 	}
       			};
       				
       			
       				
     			var like = 'Like';
     			TheObject2.check(function(a) {
     				like = a;
     			});
       				
       			var isempty = this.ReplyToUser;
       			var bleh = '';
       			if (isempty)
       			{
       				bleh = ' to <a href="/Litter/User/' +
							this.ReplyToUser + '">' + this.ReplyToUser + '</a>';
       			}
       			
       			var deletetext = '';
       			var displayuser = '';
       			var tweetuser = this.User;
       			<% if (loggedin) { %>
       				
       			displayuser = '<%= User.getUserName() %>';

       			<% } %>
       			
       			
       			
       			if (displayuser == tweetuser)
     			{
     				deletetext = '<a style="float: right" class="delete"' +
   					'id="' + this.TweetID + '"><img src="/Litter/img/delete.png" /></a>';
     			}
       				
       				

       			$("#mentions").append('<div class="tweet">' +
      					'<img width = "33px" height = "33px" style="margin-top: 11px; margin-right: 15px"' +
      					'src="' + this.AvatarUrl + '" align="left" />' +
      					'<p>' + this.Content +
      					'<span style="float: right">Likes: ' + this.Likes +
      					<%
    	  					if (loggedin == true)
    	  						{
    	  						%>
    	  						
    	  						
    	  					' - <a class="like"' +
    	   					'id="' + this.TweetID + '">' + like + '</a>' +
    	   				<%
    	  						}%>
    	  					'</span></p>' +
       					'<p>' +
      					deletetext + 
      					'<a href="/Litter/User/' + this.User + '">' + this.User + '</a>' +
       					bleh + 
       					'</p>' +
       					'</div>');
      		});
       			//$("#feed").fadeIn('slow');
       	});	
       		
        }	
        </script>
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
					}
					%>
					</td>
					</tr>
					</table>
					<div class = "line"></div>
					<h4><%=displayUser.getUserName() %> has <span id="followercount"></span> Followers</h4>
					<p>
					<div id="followers"></div>	
					</p>
					<h4><%=displayUser.getUserName() %> Follows <span id="followeecount"></span> Users</h4>
					<p>
					<div id="followees"></div>	
					</p>
					<div class="line"></div>  <!-- Dividing line --> 		
					 
					<table style="width: 100%">
					<tr>
					<td style="width: 50%; vertical-align: top">
					<h2><%= displayUser.getUserName() %>'s Timeline</h2>
					<div id="feed"></div>
				</td>
				<td style="width: 50%; vertical-align: top">
					<h2><%= displayUser.getUserName() %>'s Mentions</h2>
					<div id="mentions"></div>
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
        <% if (loggedin)
        {
        	
        	%>
        	
	        $(function() {
		   		var url = '/Litter/Follows/<%= displayUser.getUserName() %>/Check';
		   		$.ajax({
		   			type: "GET",
		   			url: url,
		   			success: function(msg) {
		   				$("#follow").text(msg);
		   			}
		   		});
	        });
	        
	        $("a.like").live('click', function () {
	        	var url = "/Litter/Like/" + (this.id);
	        	var text = this.text;
	        	var text2 = "Like";
	        	if (text == text2)
	       		{
		       		$.ajax({
		        			aysnc: true,
		   				type: "POST",
		   				url: url,
		   				dataType: "text",
		   				success: function(msg){
		   					loadfeed();
		   					loadmentions();
		   				}
		   	     	});
	       		}
	        	else {
	        		$.ajax({
	         			aysnc: true,
	    				type: "DELETE",
	    				url: url,
	    				dataType: "text",
	    				success: function(msg){
	    					loadfeed();
	    					loadmentions();
	    				}
	    	     	});
	        	}
	     		
		     	
	     	
			}); 
	        
	        $("a.delete").live('click', function () {
	        	var url = "/Litter/Tweet/" + (this.id);
        		$.ajax({
         			aysnc: true,
    				type: "DELETE",
    				url: url,
    				dataType: "text",
    				success: function(msg){
    					loadfeed();
    					loadmentions();
    				}
	        	});
	        });
	        
        <%
        } %>
        $(function() {
        	loadfeed();
        	loadmentions();
        });
        $("#follow").click(function () {
        	var url = '/Litter/Follows/<%= displayUser.getUserName() %>/Check';
	   		$.ajax({
	   			type: "GET",
	   			url: url,
	   			success: function(msg) {
	   				
	   				if (msg=='Follow')
   					{
	   					
	   					$.ajax({
	   		        		aysnc: true,
	   		   				type: "POST",
	   		   				url: "/Litter/Follow/<%= displayUser.getUserName() %>",
	   		   				dataType: "text",
	   		  				success: function(msg){
	   		  					location.reload(); 
	   		   				}
	   		        	});
   					}
	   				else
	   				{
	   					$.ajax({
	   		        		aysnc: true,
	   		   				type: "DELETE",
	   		   				url: "/Litter/Follow/<%= displayUser.getUserName() %>",
	   		   				dataType: "text",
	   		  				success: function(msg){
	   		  					location.reload(); 
	   		   				}
	   		        	});
	   				}
	   			}
	   		});
 		});
        
        
        
        
		$(function() {
			$( "button", ".demo" ).button();
			$( "a", ".demo" ).click(function() { return false; });
		});
       	$(function() {
       		var count = 0;
       		var url = '/Litter/Follows/<%= displayUser.getUserName() %>/json';
       		$.getJSON(url, function(json) {
       			$.each(json.Data, function(i, Data) {
       				$("#followees").append('<a href="/Litter/User/' 
       						+ this.Username 
       						+ '" class="info"><img width = "25px" height = "25px" src="' 
       						+ this.AvatarUrl + '" /><span>' 
       						+ this.Username+ '</span></a> ');
					count += 1;
       				
       			});
       			$("#followeecount").append(count);
       		});		
       	
       		var url = '/Litter/Follow/<%= displayUser.getUserName() %>/json';
       		$.getJSON(url, function(json) {
       			var count = 0;
       			$.each(json.Data, function(i, Data) {
       				$("#followers").append('<a href="/Litter/User/' 
       						+ this.Username 
       						+ '" class="info"><img width = "25px" height = "25px" src="' 
       						+ this.AvatarUrl + '" /><span>' 
       						+ this.Username+ '</span></a> ');
       				count += 1;
       				
       			});
       			$("#followercount").append(count);
       		});	
       	});
       </script>

    </body>
</html>