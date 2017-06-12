// #################### Navigation ####################

function pageChangedEvents(hash){
	switch(hash) {
		case "#home":
			getPublicAlbums();
			break;
			
		case "#albums":
			
			break;
	}
}

function changePage(){
	var hash = "#home";
	
	hash = window.location.hash;
	
	//Check if user is logged in and not on register page
	if(!userloggedIn() && hash != "#register")
		hash = "#login";
	
	$("#pages > section").hide();
	
	$(hash).show();
	
	//Set active button in navbar
	$("#navigation").find("li").removeClass("active");
	$(hash+"Nav").addClass("active");
	
	pageChangedEvents(hash);
}

$(window).on('hashchange', changePage);
