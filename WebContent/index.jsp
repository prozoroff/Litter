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
        <script src="http://maps.google.com/maps?file=api&amp;v=2.133d&amp;&kry=ABQIAAAApihxGewKXLQTYcrRwFn4ERT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSFC-RZh21eklbK4vFYR6Te1DHqEA"></script> 
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
		
		 
		function displayPosition(position) {
			$("#latitude").val(position.coords.latitude).trigger('change');
			$("#longitude").val(position.coords.longitude).trigger('change');
		}
		 
		function displayError(positionError) {
		  alert("error");
		}
		 
		try {
		  if(typeof(navigator.geolocation) == 'undefined'){
		    gl = google.gears.factory.create('beta.geolocation');
		  } else {
		    gl = navigator.geolocation;
		  }
		}catch(e){}
		 
		if (gl) {
		  gl.getCurrentPosition(displayPosition, displayError);
		} else {  
		  alert("I'm sorry, but geolocation services are not supported by your browser.");  
		}
		
		
		
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
		
		function loadfeed() {
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
       			
       			
       			var location = '';
       			var locationtext = '';
       			if (this.Latitude)
    			{
       				location = '<img class = "tweetimage" src="http://maps.google.com/maps/api/staticmap?center=' + this.Latitude + ',' + this.Longitude + '&zoom=14&size=400x100&&markers=color:red%7Clabel:!%7C' + this.Latitude + ',' + this.Longitude + '&sensor=true" />';

       				
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
       					'</p>' +
       					location +
       					locationtext+
       					'</div>');
      		});
       			//$("#feed").fadeIn('slow');
       	});	
       		
        }	
		
		function loadlikes() {
       		var count = 0;
       		var url = '/Litter/Feed/Like/json';
       		$.getJSON(url, function(json) {
       			//$("#feed").fadeOut('fast');
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

       			var location = '';
       			if (this.Latitude)
       				{
       					location = '<img class = "tweetimage" src="http://maps.google.com/maps/api/staticmap?center=' + this.Latitude + ',' + this.Longitude + '&zoom=14&size=400x100&&markers=color:red%7Clabel:!%7C' + this.Latitude + ',' + this.Longitude + '&sensor=true" />';
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
       					location
       							+
       					'</div>');
      		});
       			//$("#feed").fadeIn('slow');
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
							<input style="display: none" type="hidden" id = "latitude" name="latitude">
							<input style="display: none" type="hidden" id = "longitude" name="longitude">
							<input style="width: 90%; background-color: white;" type="submit"  value="Tweet">
							<p>Add your location?  <input value = "location" type="checkbox" name="location"  style="width: auto; background: none; display: inline; margin: 0; padding: 0; min-height: 0"/></p>
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
        	loadfeed();
        	loadlikes();
        });
        
        $("a.delete").live('click', function () {
			showpopup(this);
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
	   					loadlikes();
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
    					loadlikes();
    				}
    	     	});
        	}
     		
	     	
     	
		}); 
        
        $("#totweet").submit(function() {
        	var form = $(this).serialize();
        	$.ajax({
       			url: "/Litter/Tweet?" + form,
       		  	type: "POST",
       		  	complete: function() {
       		  		$("#comment").val('');
       		  		limitChars('comment', 140, 'charlimitinfo');
       		  		loadfeed();
       		  		loadlikes();
       		  	}		
        	});		
        	return false;
        });
        
        
        window.setInterval(loadfeed, 10000);
       	window.setInterval(loadlikes, 10000);
       	
       	function showpopup(a) {
			$( "#dialog:ui-dialog" ).dialog( "destroy" );
        	
    		$( "#dialog-confirm" ).dialog({
    			resizable: false,
    			height:150,
    			modal: true,
    			buttons: {
    				"Delete tweet": function() {
    					var url = "/Litter/Tweet/" + (a.id);
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
    					$( this ).dialog( "close" );
    				},
    				Cancel: function() {
    					$( this ).dialog( "close" );
    				}
    			}
    		});
       	}
        <% } %>
        

    	</script>
    </body>
</html>