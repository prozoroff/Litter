$(document).ready(function(){
	/* This code is executed after the DOM has been completely loaded */
	
	$('footer a.up').click(function(e){
										  
		// If a link has been clicked, scroll the page to the link's hash target:
		
		$.scrollTo( this.hash || 0, 1500);
		e.preventDefault();
	});

});

/*
 * Limits the characters in the tweet input box, and displays helpful text
 */
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

/*
 * Adds geolocation-ness to the tweet
 */
function displayPosition(position) {
	$("#latitude").val(position.coords.latitude).trigger('change');
	$("#longitude").val(position.coords.longitude).trigger('change');
	var latlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);	
	if (latlng) {	
		var geocoder = new GClientGeocoder();
		geocoder.getLocations(latlng, function(addresses) {
		if(addresses.Status.code != 200) {
			newlocationtext = "No address found";
		}
		else {
			address = addresses.Placemark[0];
			newlocationtext = address.address;
			$("#locationname").val(address.address).trigger('change');
		}
	});
}
}
 
/*
 * Displays error if geolocation goes wrong
 */
function displayError(positionError) {
  alert("error");
}

/*
 * Initialises geolocationythingy
 */
function initgeo()
{
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
}

/*
 * delete button handler
 */
$("a.delete").live('click', function () {
	showpopup(this);
});

/*
 * like button handler
 */
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
					loadfeeds();
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
				loadfeeds();
			}
     	});
	}
		
 	
	
}); 

/*
 * Pop up to confirm deleting
 */
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

$("#totweet").submit(function() {
	var form = $(this).serialize();
	$.ajax({
			url: "/Litter/Tweet?" + form,
		  	type: "POST",
		  	complete: function() {
		  		$("#comment").val('');
		  		limitChars('comment', 140, 'charlimitinfo');
		  		loadfeeds();
		  	}		
	});		
	return false;
});