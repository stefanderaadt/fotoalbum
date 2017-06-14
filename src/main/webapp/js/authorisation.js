/*
 * #################### Login Form ####################
 */

$("#loginForm").submit(function(e) {
	e.preventDefault();

	$.ajax({
		type : "POST",
		url : "rest/user/login",
		data : $("#loginForm").serialize(),
		success : function(data) {
			window.sessionStorage.setItem("sessionToken", data);

			// Get logged in user
			getLoggedInUser();

			// Change hash to #home
			window.location.hash = "#home";

			// Go to home page
			changePage();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
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
					console.log(xhr.responseText);
					console.log(thrownError);
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