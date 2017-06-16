/*
 * #################### Login Form ####################
 */

$("#loginForm").submit(function(e) {
	e.preventDefault();

	$.ajax({
		type : "POST",
		url : "rest/user/login",
		async : false,
		data : $("#loginForm").serialize(),
		success : function(data) {
			window.sessionStorage.setItem("sessionToken", data);

			// Get logged in user
			getLoggedInUser();

			// Change hash to #home
			if(window.location.hash === "#home"){
				changePage();
			}else{
				window.location.hash = "#home";
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			switch (xhr.status) {
				case 401:
					displayError("Gebruikersnaam of wachtwoord is fout.");
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

$("#logout").click(function(){
	window.sessionStorage.setItem("sessionToken", null);
	window.location.hash = "#login";
	$("#navDropdown").hide();
});

/*
 * #################### Get logged in userdata ####################
 */
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
			user = data;
			setHeader();
		},
		error : function(xhr, ajaxOptions, thrownError) {
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

function setHeader() {
	if (userloggedIn()) {
		$("#navDropdown").show();
		$("#navUsername").text(user.firstName + " " + user.lastName);
	} else {
		$("#navDropdown").hide();
	}
}

// Check if user is logged in
function userloggedIn() {
	if (window.sessionStorage.getItem("sessionToken") === null || user === null || typeof user === 'undefined')
		return false;

	return true;
}