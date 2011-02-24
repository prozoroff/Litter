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
        <script src="http://maps.google.com/maps?file=api&amp;v=2.133d&amp;"></script> 
        <link type="text/css" href="css/vader/jquery-ui-1.8.9.custom.css" rel="Stylesheet" />
        <script src="js/jquery-1.4.4.min.js"></script>
        <script src="js/jquery-ui-1.8.9.custom.min.js"></script>
        <script src="js/gears_init.js"></script>
		<script language="javascript">
		<%
		if (loggedin)
		{
			%>
			    
		var gl = null;
		$(function() {
			initgeo();
		});
		
		function loadfeed() {
			$("#feed").fadeTo('slow', 0.5);
       		var count = 0;
       		var url = '/Litter/Feed/json';
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
           			   			success: function(msg) {
           			   			
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
       		
       			displayuser = '<%= User.getUserName() %>';

       		
       			
       			if (displayuser == tweetuser)
     			{
     				deletetext = '<a style="float: right" class="delete"' +
   					'id="' + this.TweetID + '"><img src="/Litter/img/delete.png" /></a>';
     			}
       			
       			var locationtext = '';
       			if (this.LocationName)
       				{
       					var locationtext = '<p style="height: 100px">' + this.LocationName + '<p>';
       				}
       			
       			
       			var location = '';
       			if (this.Latitude)
    			{
       				location = '<img class = "tweetimage" src="http://maps.google.com/maps/api/staticmap?center=' + this.Latitude + ',' + this.Longitude + '&zoom=14&size=300x100&&markers=color:red%7Clabel:!%7C' + this.Latitude + ',' + this.Longitude + '&sensor=true" />';

       				
    			}
   				$("#feed").append('<div class="tweet">' +
   						'<img width = "33px" height = "33px" style="margin-top: 11px; margin-right: 15px"' +
      					'src="' + this.AvatarUrl + '" align="left" />' +
      					'<p>' + this.Content +
      					'<span style="float: right">Likes: ' + this.Likes +
    	  					' - <a class="like"' +
    	   					'id="' + this.TweetID + '">' + like + '</a>' +
    	   			
    	  					'</span></p>' +
       					'<p>' +
      					deletetext + 
      					'<a href="/Litter/User/' + this.User + '">' + this.User + '</a>' +
       					bleh + 
       					'<p>' +
       					location +
       					locationtext +
       					'</div>');
      		});
       			$("#feed").fadeTo('slow', 1);
       	});	     		
        }	
		
		function loadlikes() {
       		var count = 0;
       		var url = '/Litter/Feed/Like/json';
       		$.getJSON(url, function(json) {
       			$("#likefeed").fadeTo('slow', 0.5);
       			$("#likefeed").html('');
       			$.each(json.Data, function(i, Data) {   
       				var url3 = '/Litter/Like/' + this.TweetID;
       				TheObject2 = {
       					check : function(callback) {
           			   		$.ajax({
           			   			type: "GET",
           			   			url: url3,
           			   			async: false,
           			   			success: function(msg) {
           			   			
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
       		
       				
       			displayuser = '<%= User.getUserName() %>';
       			
       			var locationtext = '';
       			if (this.LocationName)
       				{
       					var locationtext = '<p style="height: 100px">' + this.LocationName + '<p>';
       				}

       			var location = '';
       			if (this.Latitude)
       				{
       					location = '<img class = "tweetimage" src="http://maps.google.com/maps/api/staticmap?center=' + this.Latitude + ',' + this.Longitude + '&zoom=14&size=300x100&&markers=color:red%7Clabel:!%7C' + this.Latitude + ',' + this.Longitude + '&sensor=true" />';
					}
       			
       			if (displayuser == tweetuser)
     			{
     				deletetext = '<a style="float: right" class="delete"' +
   					'id="' + this.TweetID + '"><img src="/Litter/img/delete.png" /></a>';
     			}

   				$("#likefeed").append('<div class="tweet">' +
   						'<img width = "33px" height = "33px" style="margin-top: 11px; margin-right: 15px"' +
      					'src="' + this.AvatarUrl + '" align="left" />' +
      					'<p>' + this.Content +
      					'<span style="float: right">Likes: ' + this.Likes +
      
    	  						
    	  					' - <a class="like"' +
    	   					'id="' + this.TweetID + '">' + like + '</a>' +
    	   	
    	  					'</span></p>' +
       					'<p>' +
      					deletetext + 
      					'<a href="/Litter/User/' + this.User + '">' + this.User + '</a>' +
       					bleh + 
       					'</p>' +
       					location + 
       					locationtext + 
       					'</div>');
      		});
       			$("#likefeed").fadeTo('slow', 1);
       	});	
       		
        }	
		
		<%
		}
			%>
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
                    <h1><a href="/Litter/" style="color: white">Litter</a></h1>
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
					<form id = "totweet" style="width: 80%">
						<table style="width: 100%">
						<tr>
						<td>
						<ul class="menu">
						<li class="expand"> 
							<a href="#">Tweet</a> 
							<div class="acitem panel"> 
								<textarea id = "comment" style="background: none; width: 90%; min-height: 42px; font-size: 1em; background-color: white;" name="Content" required placeholder="Write your post here"></textarea> 
								<p id="charlimitinfo"></p>
								<script>
								$(function(){
									$('#comment').keyup(function(){
										limitChars('comment', 140, 'charlimitinfo');
									});
									limitChars('comment', 140, 'charlimitinfo');			
								});
								
								</script>
							</div> 
						</li> 
						<li> 
							<a href="#">Location</a> 
							<div class="acitem panel"> 
								<input style="width: 90%; background: none; background-color: white " id = "locationname" name="locationname">
								<p>Add your location?  <input value = "location" type="checkbox" name="location"  style="width: auto; background: none; display: inline; margin: 0; padding: 0; min-height: 0"/></p>
							</div> 
						</li> 
						</ul>
						</td>
						<td style="vertical-align: top;">
							<input style="display: none" type="hidden" id = "latitude" name="latitude">
							<input style="display: none" type="hidden" id = "longitude" name="longitude">
							<input style="width: 90%; background-color: white; margin-top: 22px" type="submit"  value="Tweet">
						</td>
						</tr>
						</table>
					</form>
					<table style="width: 100%">
						<tr>
							<td style="width: 50%; vertical-align: top">
							<h2>Your Feed</h2>
							<div id="feed"></div>
							</td>
							<td style="width: 50%; vertical-align: top">
							<h2>Most Popular Tweets</h2>
							<div id="likefeed"></div>
						</tr>
					</table>
                </article>
                <div id="dialog-confirm" style="display: none" title="Really delete? Really?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Will be deleted forever!</p>
</div>
                <% } %>
            </section>
        <%@ include file="footer.jsp" %>                 
		</section> <!-- Closing the #page section -->
        <!-- JavaScript Includes -->
        <script src="jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
        <script src="script.js"></script>
        <script>
        
        <% if (loggedin) { %>
        
        $(function() {
        	loadfeeds();
        	$('.menu').initMenu();
        });
        
        function loadfeeds()
        {
			loadfeed();
			loadlikes();
        }
       	window.setInterval(loadfeeds, 20000);
        <% } %>
    	</script>
    </body>
</html>