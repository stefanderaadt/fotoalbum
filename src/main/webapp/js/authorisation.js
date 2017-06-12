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
			getLoggedInUser();
			console.log("user");
			console.log(user);
			window.location.href = "#home";
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
		async: false,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			user = data;
			console.log("getloggedinuser: "+user);
			setHeader();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
}

function setHeader(){
	if(userloggedIn()){
		$("#navDropdown").show();
		$("#navUsername").text(user.email);
		console.log("User: "+user);
	}else{
		$("#navDropdown").hide();
	}
}

//Check if user is logged in
function userloggedIn(){
	if(window.sessionStorage.getItem("sessionToken") === null || typeof user === 'undefined') return false;
	
	return true;
}