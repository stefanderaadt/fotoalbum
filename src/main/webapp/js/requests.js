/*
#################### Form Requests ####################
 */

$("#albumForm").submit(function(e) {
	//Stop default code
	e.preventDefault();

	//Get pictures from file selector
	var pictures = $("#inputPictures").prop("files");
	
	//Get form input values
	var data = $("#albumForm").serialize();
	var sharedUsers = [];
	
	//Check if user selected any images
	if(pictures.length === 0) {
		displayError("Er zijn geen afbeeldingen geselecteerd!", "#modalErrorTemplateResult");
		return;
	}

	//Get emails from shared users
	$(this).find(".add-shared-users-table").find("tr").each(
			function() {
				var columns = $(this).find('td');
				sharedUsers.push($(columns[0]).text())
			});

	data += "&sharedusers=" + JSON.stringify(sharedUsers);

	//Post album data to the back-end
	$.ajax({
		type : "POST",
		url : "rest/album",
		data : data,
		beforeSend : function(xhr) {
			var token = window.sessionStorage
					.getItem("sessionToken");
			xhr.setRequestHeader('Authorization',
					'Bearer ' + token);
		},
		success : function(data) {
			//On success upload pictures to the new album
			uploadPictures(pictures, data.code);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Send error message to user
			displayError("Er is iets fout gegaan met het opslaan van dit album.", "#modalErrorTemplateResult");
		}
	});

});

// Upload pictures
function uploadPictures(pictures, albumCode, progressBarId = "#uploadAlbumProgressBarResult") {	
	var length = pictures.length;
	
	//Check if user selected any files
	if(length === 0){
		if (progressBarId === "#uploadAlbumProgressBarResult"){
			displayError("Er zijn geen afbeeldingen geselecteerd!", "#modalErrorTemplateResult");
			return;
		}else{
			displayError("Er zijn geen afbeeldingen geselecteerd!");
			return;
		}
	}
	
	var formData = new FormData();

	//Add pictures to formdata and check filetype
	for (var i = 0; i < length; i++) {
		var file = pictures[i];
		
		var ext = file.name.split('.').pop().toLowerCase();
		if($.inArray(ext, ['gif','png','jpg','jpeg']) == -1) {
			if (progressBarId === "#uploadAlbumProgressBarResult"){
				displayError("Alleen gif, png, jpg en jpeg bestanden zijn toegestaan.", "#modalErrorTemplateResult");
				return;
			}else{
				displayError("Alleen gif, png, jpg en jpeg bestanden zijn toegestaan.");
				return;
			}
		}

		// Add the file to the request.
		formData.append(file.name, file);
	}
	
	//Set up progress bar
	var percentComplete = 0;
	printProgressBar(progressBarId, percentComplete);
	
	//Update progress bar timeout
	var timeout;

	// Set up the request.
	var xhr = new XMLHttpRequest();

	// Open the connection.
	xhr.open('POST', 'rest/picture/' + albumCode, true);

	// Authorize user
	var token = window.sessionStorage.getItem("sessionToken");
	xhr.setRequestHeader('Authorization', 'Bearer ' + token);
	
	//var uploadStatusInterval = setInterval(getUploadStatus, 2000);

	// Get upload bar
	xhr.upload.addEventListener("progress", function(evt) {
		if (evt.lengthComputable) {
			percentComplete = evt.loaded / evt.total;		
			percentComplete = parseInt(percentComplete * 50);
			printProgressBar(progressBarId, percentComplete);
			
			if(percentComplete === 50){
				//Change progressbar
				var add = Math.round(40/length);
				var counter = 0;
				timeout = setInterval(function(){
					counter++;
					percentComplete += add;
					printProgressBar(progressBarId, percentComplete);

					if(counter === (length)){
						if(percentComplete>100){
							percentComplete=100;
						}
						clearTimeout(timeout);
					}
				}, 3000);
			}
		}
	}, false);

	xhr.onload = function() {
		if (xhr.status === 200) {
			// File(s) uploaded.
			precentComplete = 100;
			printProgressBar(progressBarId, percentComplete);

			// Set code
			localStorage.setItem("code", albumCode);

			// Go to album page
			if(window.location.hash === "#album"){
				changePage();
			}else{
				window.location.hash = "#album";
			}
			
			$("#newAlbumModal").modal("hide");

			// Display success
			if (progressBarId === "#uploadAlbumProgressBarResult"){
				displaySuccess("Nieuw album opgeslagen!");
				clearTimeout(timeout);
			}else{
				displaySuccess("Afbeeldingen geüpload!");
				clearTimeout(timeout);
			}
			
		} else {
			if (progressBarId === "#uploadAlbumProgressBarResult"){
				displayError("Er is iets fout gegaan met het opslaan van dit album.", "#modalErrorTemplateResult");
			}else{
				displayError("Er is iets fout gegaan met het uploaden van de afbeeldingen.");
			}
		}
	}
	
	xhr.send(formData);
}

/*
 * #################### Update Requests ####################
 */

//Update album
$(document).on("click", "#album-save-btn", function() {
	//Get default variables
	var data = "";
	var code = $(this).attr("code");
	var sharedUsers = [];
	var parent = $(this).parent().parent();

	//Get title from input
	data += "title=" + parent.find("#edit-title-input").val();

	//Get description from input
	data += "&description="
			+ parent.find("#edit-desc-input").val();

	//Get sharetype from radio buttons
	data += "&shareType="
			+ parent.find("input[name=share-type]:checked")
					.val();

	//Get sharedusers
	parent.find(".add-shared-users-table").find("tr").each(
			function() {
				var columns = $(this).find('td');
				sharedUsers.push($(columns[0]).text())
			});

	//Stringify sharedusers
	data += "&sharedusers=" + JSON.stringify(sharedUsers);

	//Update request to back-end
	$.ajax({
		type : "PUT",
		url : "rest/album/" + code,
		data : data,
		beforeSend : function(xhr) {
			var token = window.sessionStorage
					.getItem("sessionToken");
			xhr.setRequestHeader('Authorization',
					'Bearer ' + token);
		},
		success : function(data) {
			changePage();
			displaySuccess("Album succesvol aangepast.");
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Er is iets fout gegaan met het aanpassen van dit album.");
		}
	});
});

//Upload new pictures
$(document).on("click", "#album-picture-save-btn", function(){
	//Get album code
	var code = $(this).attr("code");
	
	//Get pictures
	var pictures = $(this).parent().parent().find("#addPictures").prop("files");

	//Upload pictures
	uploadPictures(pictures, code, "#uploadPicturesProgressBarResult");
});

/*
 * #################### Delete Requests ####################
 */

//Delete album
$(document).on("click", "#album-delete-btn", function() {
	//Ask user if he wants to delete the album
	if(!confirm("Weet je zeker dat je dit album wilt verwijderen?")) return;
	
	//Get album code
	var code = $(this).attr("code");

	//Delete request to back-end
	$.ajax({
		type : "DELETE",
		url : "rest/album/" + code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage
					.getItem("sessionToken");
			xhr.setRequestHeader('Authorization',
					'Bearer ' + token);
		},
		success : function(data) {
			if(data.response === "deleted"){
				window.location.hash = "#home";
				displaySuccess("Album verwijderd.");
			}else{
				displayError("Er is iets fout gegaan met het verwijderen van dit album.");
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Er is iets fout gegaan met het verwijderen van dit album.");
		}
	});
});

//Delete image
$(document).on("click", ".picture-delete-btn", function() {
	//Ask user if he wants to delete the picture
	if(!confirm("Weet je zeker dat je deze afbeelding wilt verwijderen?")) return;
	
	//Get picture code
	var code = $(this).attr("code");

	//Delete request to back-end
	$.ajax({
		type : "DELETE",
		url : "rest/picture/" + code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage
					.getItem("sessionToken");
			xhr.setRequestHeader('Authorization',
					'Bearer ' + token);
		},
		success : function(data) {
			if(data.response === "deleted"){
				if(window.location.hash === "#album"){
					changePage();
				}else{
					window.location.hash = "#home";
				}
				
				displaySuccess("Afbeelding verwijderd.");
			}else{
				displayError("Er is iets fout gegaan met het verwijderen van deze afbeelding.");
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Er is iets fout gegaan met het verwijderen van deze afbeelding.");
		}
	});
});

/*
 * #################### Get Requests ####################
 */

//Get public albums
function getPublicAlbums() {
	//Add loading icon to screen
	$("#publicAlbumsTemplateResult").html(getSpinner());
	
	//Get public albums request
	$.ajax({
		type : "GET",
		url : "rest/album/public",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			//Check if albums exist
			if(data.length === 0) {
				$("#publicAlbumsTemplateResult").html("<h3>Nog geen albums gevonden probeer er een toe te voegen met de <b>Nieuw album</b> knop.</h3>");
				return;
			}
			
			//Print data to screen
			var source = $("#publicAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#publicAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Remove loading icon and give error
			$("#publicAlbumsTemplateResult").html("");
			displayError("Albums kunnen niet worden opgehaald.");
		}
	});
}

//Get albums from user
function getUserAlbums() {
	//Add loading icon to screen
	$("#userAlbumsTemplateResult").html(getSpinner());

	//Get user albums request
	$.ajax({
		type : "GET",
		url : "rest/album/user",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			//Check if albums exist
			if(data.length === 0) {
				$("#userAlbumsTemplateResult").html("<h3>Nog geen albums gevonden probeer er een toe te voegen met de <b>Nieuw album</b> knop.</h3>");
				return;
			}
			
			//Print data to screen
			var source = $("#userAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#userAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Remove loading icon and give error
			$("#userAlbumsTemplateResult").html("");
			displayError("Albums kunnen niet worden opgehaald.");
		}
	});
}

//Get shared albums with user
function getSharedAlbums() {
	//Add loading icon to screen
	$("#sharedAlbumsTemplateResult").html(getSpinner());

	$.ajax({
		type : "GET",
		url : "rest/album/shared",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			//Check if albums exist
			if(data.length === 0) {
				$("#sharedAlbumsTemplateResult").html("<h3>Er zijn nog geen albums met je gedeeld!</h3>");
				return;
			}
			
			//Print data to screen
			var source = $("#sharedAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#sharedAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Remove loading icon and give error
			$("#sharedAlbumsTemplateResult").html("");
			displayError("Albums kunnen niet worden opgehaald.");
		}
	});
}

//Get album
function getAlbum() {
	//Add loading icon to screen
	$("#albumTemplateResult").html(getSpinner());

	//Get album code from localstorage
	var code = localStorage.getItem("code");

	// Check if parameter exists
	if (code === null || typeof code === 'undefined') {
		//Go to 404 page if parameter doesn't exist
		window.location.hash = "#404";
		return;
	}

	//Get album request
	$.ajax({
		type : "GET",
		url : "rest/album/" + code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			//Print data to screen
			var source = $("#albumTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#albumTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Remove loading icon and give error
			$("#albumTemplateResult").html("");
			displayError("Fout bij het ophalen van dit album.");
		}
	});
}

//Get picture
function getPicture() {
	//Add loading icon to screen
	$("#pictureTemplateResult").html(getSpinner());

	//Get picture code from localstorage
	var code = localStorage.getItem("code");

	// Check if parameter exists
	if (code === null || typeof code === 'undefined') {
		//Go to 404 page if parameter doesn't exist
		window.location.hash = "#404";
		return;
	}

	//Get picture request
	$.ajax({
		type : "GET",
		url : "rest/picture/" + code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			//Print data to screen
			var source = $("#pictureTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(JSON.parse(data));
			$("#pictureTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Remove loading icon and give error
			$("#pictureTemplateResult").html("");
			displayError("Fout bij het ophalen van deze afbeelding.");
		}
	});
}

/*
 * #################### Register Form ####################
 */

//Register new user
$("#registerForm").submit(function(e) {
	//Stop default form
	e.preventDefault();

	//Register user request
	$.ajax({
		type : "POST",
		url : "rest/user/register",
		data : $("#registerForm").serialize(),
		success : function(data) {
			if(data.response === "success"){
				//Go to login page
				window.location.hash = "#login";
				displaySuccess("Registreren gelukt! Je kan nu inloggen.");
			}else{
				//Give error to user
				displayError("Er is iets fout gegaan bij het registeren.");
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//Give error to user
			displayError("Er is iets fout gegaan bij het registeren.");
		}
	});
});
