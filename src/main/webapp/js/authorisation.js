/*
 * #################### Login Form ####################
 */

//Login user
$("#loginForm").submit(function(e) {
	//Stop default code
	e.preventDefault();

	//Login request
	$.ajax({
		type : "POST",
		url : "rest/user/login",
		async : false,
		data : $("#loginForm").serialize(),
		success : function(data) {
			//Set sessiontoken
			window.sessionStorage.setItem("sessionToken", data);

			// Get logged in user
			getLoggedInUser();

			//Load new page
			if(window.location.hash === "#login" || window.location.hash === "#register" || window.location.hash === ""){
				window.location.hash = "#home";
			}else{
				changePage();
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Give error by code
			switch (xhr.status) {
				case 401:
					displayError("Email of wachtwoord is fout.");
					break;
				default: 
					displayError("Fout bij het inloggen.");
			}
		}
	});
});

/*
 * #################### Logout ####################
 */

//Logout user
$("#logout").click(function(){
	//Set sessionstorage to null
	window.sessionStorage.setItem("sessionToken", null);
	
	//Move to login page
	window.location.hash = "#login";
	
	//Remove userinfo from navigation bar
	$("#navDropdown").hide();
});

/*
 * #################### Get logged in userdata ####################
 */

//Get logged in user
function getLoggedInUser() {
	$.ajax({
		type : "GET",
		url : "rest/user",
		async : false,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			//Set user object from data
			user = data;
			
			//Update header
			setHeader();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Give error by code
			switch (xhr.status) {
				case 403:
					user = null;
					break;
				default: 
					displayError("Kan gebruikersdata niet ophalen.");
			}
		}
	});
}

//Set user info in navigation bar
function setHeader() {
	//Check if user is logged in
	if (userloggedIn()) {
		$("#navDropdown").show();
		$("#navUsername").text(user.firstName + " " + user.lastName);
	} else {
		$("#navDropdown").hide();
	}
}

// Check if user is logged in function
function userloggedIn() {
	if (window.sessionStorage.getItem("sessionToken") === null || user === null || typeof user === 'undefined')
		return false;

	return true;
}