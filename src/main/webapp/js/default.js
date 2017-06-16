// #################### Global variables ####################

var user;

//#################### Alerts functions ####################

function displaySuccess(message){
	var data = {};
	data.message = message;
	
	var source = $("#successTemplate").html();
	var template = Handlebars.compile(source);
	var html = template(data);
	$("#successTemplateResult").html(html);
}

function displayError(message){
	var data = {};
	data.message = message;
	
	var source = $("#errorTemplate").html();
	var template = Handlebars.compile(source);
	var html = template(data);
	$("#errorTemplateResult").html(html);
}

function displayAlbumError(message){
	var data = {};
	data.message = message;
	
	var source = $("#modalErrorTemplate").html();
	var template = Handlebars.compile(source);
	var html = template(data);
	$("#modalErrorTemplateResult").html(html);
}

//#################### Handlebars helper functions ####################

function setHandlebarsHelpers() {
	// Check radio buttons when set value is the same as the radio button value
	Handlebars.registerHelper("setChecked", function(value, currentValue) {
		if (value == currentValue) {
			return "checked"
		} else {
			return "";
		}
	});

	Handlebars.registerHelper("displaySharedUsersGroup", function(value) {
		if (value === 'U') {
			return 'style="display: block"';
		} else {
			return '';
		}
	});

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

$(document).ready(function() {
	changePage();
	setHandlebarsHelpers();
});

function getSpinner(){
	return '<div style="text-align: center"><i class="fa fa-spinner fa-spin" style="font-size:40px"></i></div>';
}

function printProgressBar(templateResult, value){
	var data = {};
	
	data.value = value;
	
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