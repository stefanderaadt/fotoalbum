// #################### Global variables ####################

var user;

//#################### Alerts functions ####################

//Display succes message on screen
function displaySuccess(message, location = "#successTemplateResult"){
	var data = {};
	data.message = message;
	
	var source = $("#successTemplate").html();
	var template = Handlebars.compile(source);
	var html = template(data);
	$(location).html(html);
}

//Display error message on screen
function displayError(message, location = "#errorTemplateResult"){
	var data = {};
	data.message = message;
	
	var source = $("#errorTemplate").html();
	var template = Handlebars.compile(source);
	var html = template(data);
	$(location).html(html);
}

//#################### Handlebars helper functions ####################

//Handlebars helpers functions (Templating engine)
function setHandlebarsHelpers() {
	// Check radio buttons when set value is the same as the radio button value
	Handlebars.registerHelper("setChecked", function(value, currentValue) {
		if (value == currentValue) {
			return "checked"
		} else {
			return "";
		}
	});

	//Display shared users if value is U
	Handlebars.registerHelper("displaySharedUsersGroup", function(value) {
		if (value === 'U') {
			return 'style="display: block"';
		} else {
			return 'style="display: none"';
		}
	});
	
	//Return share url to share album with other users
	Handlebars.registerHelper("displaySharePath", function(code) {
		var url = window.location.href;
		url = url.split('#');
		
		return url[0]+'?c='+code+'#album';
	});

	//Format date to time ago
	Handlebars.registerHelper("formatDate", function(currentDate) {
		var date = new Date(currentDate);
		
		var seconds = Math.floor((new Date() - date) / 1000);

		var interval = Math.floor(seconds / 31536000);

		if (interval > 0) {
			if(interval === 1) return interval + " jaar geleden";
			
			return interval + " jaren geleden";
		}
		interval = Math.floor(seconds / 2592000);
		if (interval > 0) {
			if(interval === 1) return interval + " maand geleden";
			
			return interval + " maanden geleden";
		}
		interval = Math.floor(seconds / 86400);
		if (interval > 0) {
			if(interval === 1) return interval + " dag geleden";
			
			return interval + " dagen geleden";
		}
		interval = Math.floor(seconds / 3600);
		if (interval > 0) {	
			return interval + " uur geleden";
		}
		interval = Math.floor(seconds / 60);
		if (interval > 0) {
			if(interval === 1) return interval + " minuut geleden";
			
			return interval + " minuten geleden";
		}
		if(seconds === 1) return interval + " seconde geleden";
		
		return Math.floor(seconds) + " seconden geleden";
	});
}

//#################### Default functions ####################

//Document ready function
$(document).ready(function() {
	changePage();
	setHandlebarsHelpers();
});

//Get album code if its in the url
function checkAlbumCode(){
	//Get parameter from url
	var code = getUrlParameter("c");
	
	//Check if parameter has a value
	if(code === null || typeof code === 'undefined') return;
	
	//Set code in localstorage
	localStorage.setItem("code", code);
	
	//Remove parameter from url
	window.location.href = removeUrlParameter(window.location.href);
}

//Get default spinner/loading icon
function getSpinner(){
	return '<div style="text-align: center"><i class="fa fa-spinner fa-spin" style="font-size:40px"></i></div>';
}

//Print progressbar on location
function printProgressBar(templateResult, value){
	var data = {};
	
	data.value = value;
	
	//Display progressbar on screen
	var source = $("#defaultProgressBarTemplate").html();
	var template = Handlebars.compile(source);
	var html = template(data);
	$(templateResult).html(html);
}

// Get url parameter function
function getUrlParameter(sParam) {
	var sPageURL = decodeURIComponent(window.location.search.substring(1)), sURLVariables = sPageURL
			.split('&'), sParameterName, i;

	for (i = 0; i < sURLVariables.length; i++) {
		sParameterName = sURLVariables[i].split('=');

		if (sParameterName[0] === sParam) {
			return sParameterName[1] === undefined ? true : sParameterName[1];
		}
	}
};

//Remove url parameter function
function removeUrlParameter(url) {
	var urlParts = url.split('?');
	var urlParts2 = urlParts[1].split('#');
	  
	var url = urlParts[0]+"#"+urlParts2[1];
	  
	return url;
}