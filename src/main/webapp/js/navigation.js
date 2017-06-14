// #################### Navigation ####################

function pageChangedEvent(hash){
	switch(hash) {
		case "#home":
			getPublicAlbums();
			break;
			
		case "#albums":
			getUserAlbums();
			break;
			
		case "#album":
			getAlbum();
			break;
			
		case "#picture":
			getPicture();
			break;
	}
}

function changePage(){
	var hash = "#home";
	
	hash = window.location.hash;
	
	getLoggedInUser();
	
	//Check if user is logged in and not on register page
	if(!userloggedIn() && hash != "#register")
		hash = "#login";
	
	$("#pages > section").hide();
	
	$(hash).show();
	
	//Set active button in navbar
	$("#navigation").find("li").removeClass("active");
	$(hash+"Nav").addClass("active");
	
	pageChangedEvent(hash);
}

$(document).on('click', '.change-page', function(){
	var path = $(this).attr("path");
	var code = $(this).attr("code");
	
	if(code !== null && typeof code !== 'undefined'){
		localStorage.setItem("code",code);
	}
	
	window.location.hash = path;
});

$(window).on('hashchange', changePage);
