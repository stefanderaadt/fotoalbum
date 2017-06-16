/*
#################### Form Requests ####################
 */

$("#albumForm")
		.submit(
				function(e) {
					e.preventDefault();

					var pictures = $("#inputPictures").prop("files");
					var data = $("#albumForm").serialize();
					var sharedUsers = [];

					$(this).find(".add-shared-users-table").find("tr").each(
							function() {
								var columns = $(this).find('td');
								sharedUsers.push($(columns[0]).text())
							});

					data += "&sharedusers=" + JSON.stringify(sharedUsers);

					$
							.ajax({
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
									uploadPictures(pictures, data.code);
								},
								error : function(xhr, ajaxOptions, thrownError) {
									displayAlbumError("Er is iets fout gegaan met het opslaan van dit album.");
								}
							});

				});

// Upload pictures
function uploadPictures(pictures, albumCode) {
	var formData = new FormData();
	var percentComplete = 0;
	printProgressBar("#uploadAlbumProgressBarResult", percentComplete);

	for (var i = 0; i < pictures.length; i++) {
		var file = pictures[i];

		// Add the file to the request.
		formData.append(file.name, file);
	}

	// Set up the request.
	var xhr = new XMLHttpRequest();

	// Open the connection.
	xhr.open('POST', 'rest/picture/' + albumCode, true);

	// Authorize user
	var token = window.sessionStorage.getItem("sessionToken");
	xhr.setRequestHeader('Authorization', 'Bearer ' + token);

	// Get upload bar
	xhr.upload.addEventListener("progress", function(evt) {
		if (evt.lengthComputable) {
			percentComplete = evt.loaded / evt.total;
			percentComplete = parseInt(percentComplete * 50);
			printProgressBar("#uploadAlbumProgressBarResult", percentComplete);
		}
	}, false);

	xhr.onload = function() {
		if (xhr.status === 200) {
			// File(s) uploaded.
			precentComplete = 100;
			printProgressBar("#uploadAlbumProgressBarResult", percentComplete);

			// Set code
			localStorage.setItem("code", albumCode);

			// Go to album page
			window.location.hash = "#album";
			
			$("#newAlbumModal").modal("hide");

			// Display success
			displaySuccess("Nieuw album opgeslagen!");
		} else {
			displayAlbumError("Er is iets fout gegaan met het opslaan van dit album.");
		}
	};

	xhr.send(formData);
}

/*
 * #################### Update Requests ####################
 */

$(document)
		.on(
				"click",
				"#album-save-btn",
				function() {
					var data = "";
					var code = $(this).attr("code");
					var sharedUsers = [];

					var parent = $(this).parent().parent();

					data += "title=" + parent.find("#edit-title-input").val();

					data += "&description="
							+ parent.find("#edit-desc-input").val();

					data += "&shareType="
							+ parent.find("input[name=share-type]:checked")
									.val();

					parent.find(".add-shared-users-table").find("tr").each(
							function() {
								var columns = $(this).find('td');
								sharedUsers.push($(columns[0]).text())
							});

					data += "&sharedusers=" + JSON.stringify(sharedUsers);

					$
							.ajax({
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
									displaySuccess("Album succesvol aangepast.");
									changePage();
								},
								error : function(xhr, ajaxOptions, thrownError) {
									displayError("Er is iets fout gegaan met het aanpassen van dit album.");
								}
							});
				});

/*
 * #################### Delete Requests ####################
 */

$(document)
		.on(
				"click",
				"#album-delete-btn",
				function() {
					var code = $(this).attr("code");

					$
							.ajax({
								type : "DELETE",
								url : "rest/album/" + code,
								beforeSend : function(xhr) {
									var token = window.sessionStorage
											.getItem("sessionToken");
									xhr.setRequestHeader('Authorization',
											'Bearer ' + token);
								},
								success : function(data) {
									window.location.hash = "#home";
									displaySuccess("Album verwijderd aangepast.");
								},
								error : function(xhr, ajaxOptions, thrownError) {
									displayError("Er is iets fout gegaan met het verwijderen van dit album.");
								}
							});
				});

/*
 * #################### Get Requests ####################
 */

function getPublicAlbums() {
	$("#publicAlbumsTemplateResult").html(getSpinner());
	$.ajax({
		type : "GET",
		url : "rest/album/public",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			var source = $("#publicAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#publicAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Albums kunnen niet worden opgehaald.");
		}
	});
}

function getUserAlbums() {
	$("#userAlbumsTemplateResult").html(getSpinner());

	$.ajax({
		type : "GET",
		url : "rest/album/user",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			var source = $("#userAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#userAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Albums kunnen niet worden opgehaald.");
		}
	});
}

function getSharedAlbums() {
	$("#sharedAlbumsTemplateResult").html(getSpinner());

	$.ajax({
		type : "GET",
		url : "rest/album/shared",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			var source = $("#sharedAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#sharedAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Albums kunnen niet worden opgehaald.");
		}
	});
}

function getAlbum() {
	$("#albumTemplateResult").html(getSpinner());

	var code = localStorage.getItem("code");

	// Check if parameter exists
	if (code === null || typeof code === 'undefined') {
		window.location.hash = "#404";
		return;
	}

	$.ajax({
		type : "GET",
		url : "rest/album/" + code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			var source = $("#albumTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#albumTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Fout bij het ophalen van dit album.");
		}
	});
}

function getPicture() {
	$("#pictureTemplateResult").html(getSpinner());

	var code = localStorage.getItem("code");

	// Check if parameter exists
	if (code === null || typeof code === 'undefined') {
		window.location.hash = "#404";
		return;
	}

	$.ajax({
		type : "GET",
		url : "rest/picture/" + code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			var source = $("#pictureTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(JSON.parse(data));
			$("#pictureTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Fout bij het ophalen van deze afbeelding.");
		}
	});
}

/*
 * #################### Register Form ####################
 */

$("#registerForm").submit(function(e) {
	e.preventDefault();

	$.ajax({
		type : "POST",
		url : "rest/user/register",
		data : $("#registerForm").serialize(),
		success : function(data) {
			console.log(data);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			displayError("Fout bij het registreren.");
		}
	});
});
