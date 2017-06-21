// #################### Navigation ####################

//Page events
function pageChangedEvent(hash){
	switch(hash) {
		case "#home":
			getPublicAlbums();
			break;
			
		case "#albums":
			getUserAlbums();
			break;
			
		case "#shared":
			getSharedAlbums();
			break;
			
		case "#album":
			//Check parameter in URL
			checkAlbumCode();
			getAlbum();
			break;
			
		case "#picture":
			getPicture();
			break;
	}
}

//Switch page function
function changePage(){
	//Set default page
	var hash = "#home";
	
	//Empty messages
	$("#successTemplateResult").empty();
	$("#errorTemplateResult").empty();
	
	//Close navigation bar on mobile
	$("#navbar-collapse").removeClass("in");
	
	//Get hash from url
	hash = window.location.hash;
	
	//Get user
	getLoggedInUser();
	
	//Check if user isn't logged in and not on register page else set hash to #login
	if(!userloggedIn() && hash != "#register")
		hash = "#login";
	
	//Hide other pages
	$("#pages > section").hide();
	
	//Open current page
	$(hash).show();
	
	//Set active button in navbar
	$("#navigation").find("li").removeClass("active");
	$(hash+"Nav").addClass("active");
	
	//Run page changed events
	pageChangedEvent(hash);
}

//Switch page button
$(document).on('click', '.change-page', function(){
	//Get path and code from button
	var path = $(this).attr("path");
	var code = $(this).attr("code");
	
	//Set code in localstorage if exists
	if(code !== null && typeof code !== 'undefined'){
		localStorage.setItem("code",code);
	}
	
	//Go to page
	window.location.hash = path;
});

//Run changePage function if hash changed in URL bar
$(window).on('hashchange', changePage);
