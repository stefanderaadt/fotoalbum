/*
#################### Form Requests ####################
 */

$("#albumForm").submit(function(e) {
	e.preventDefault();

	var pictures = $("#inputPictures").prop("files");
	var data = $("#albumForm").serialize();
	var sharedUsers = [];

	$(this).find(".add-shared-users-table").find("tr").each(function() {
		var columns = $(this).find('td');
		sharedUsers.push($(columns[0]).text())
	});
	
	console.log(sharedUsers);

	data += "&sharedusers=" + JSON.stringify(sharedUsers);

	$.ajax({
		type : "POST",
		url : "rest/album",
		data : data,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			console.log(data);
			uploadPictures(pictures, data.code);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});

});

// Upload pictures
function uploadPictures(pictures, albumCode) {
	var formData = new FormData();

	for (var i = 0; i < pictures.length; i++) {
		var file = pictures[i];

		// Add the file to the request.
		formData.append(file.name, file);
	}

	// Set up the request.
	var xhr = new XMLHttpRequest();

	// Open the connection.
	xhr.open('POST', 'rest/picture/' + albumCode, true);
	
	//Authorize user
	var token = window.sessionStorage.getItem("sessionToken");
	xhr.setRequestHeader('Authorization', 'Bearer ' + token);

	xhr.onload = function() {
		if (xhr.status === 200) {
			// File(s) uploaded.
			uploadButton.innerHTML = 'Upload';
		} else {
			alert('An error occurred!');
		}
	};

	xhr.send(formData);
}

/*
#################### Update Requests ####################
 */

$(document).on("click", "#album-save-btn", function(){
	var data = "";
	var code = $(this).attr("code");
	var sharedUsers = [];
	
	var parent = $(this).parent().parent();
	
	data += "title="+parent.find("#edit-title-input").val();
	
	data += "&description="+parent.find("#edit-desc-input").val();
	
	data += "&shareType="+parent.find("input[name=share-type]:checked").val();
	
	parent.find(".add-shared-users-table").find("tr").each(function() {
		var columns = $(this).find('td');
		sharedUsers.push($(columns[0]).text())
	});

	data += "&sharedusers=" + JSON.stringify(sharedUsers);
	
	$.ajax({
		type : "PUT",
		url : "rest/album/"+code,
		data : data,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			getAlbum();
			//uploadPictures(pictures, data.code);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
});

/*
#################### Delete Requests ####################
 */



$(document).on("click", "#album-delete-btn", function(){
	var code = $(this).attr("code");
	
	$.ajax({
		type : "DELETE",
		url : "rest/album/"+code,
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			window.location.hash = "#home";
			changePage();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
});

/*
 * #################### Get Requests ####################
 */

function getPublicAlbums() {
	$.ajax({
		type : "GET",
		url : "rest/album/public",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			console.log(data);
			var source = $("#publicAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#publicAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
}

function getUserAlbums() {
	$.ajax({
		type : "GET",
		url : "rest/album/user",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			console.log(data);
			var source = $("#userAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#userAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
}

function getSharedAlbums() {
	$.ajax({
		type : "GET",
		url : "rest/album/shared",
		beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},
		success : function(data) {
			console.log(data);
			var source = $("#sharedAlbumsTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#sharedAlbumsTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
}

function getAlbum() {
	var code = localStorage.getItem("code");

	// Check if parameter exists
	if (code === null || typeof code === 'undefined') {
		window.location.hash = "#404";
		changePage();
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
			console.log(data);
			var source = $("#albumTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(data);
			$("#albumTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
}

function getPicture() {
	var code = localStorage.getItem("code");

	// Check if parameter exists
	if (code === null || typeof code === 'undefined') {
		window.location.hash = "#404";
		changePage();
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
			console.log(data);
			var source = $("#pictureTemplate").html();
			var template = Handlebars.compile(source);
			var html = template(JSON.parse(data));
			$("#pictureTemplateResult").html(html);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
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
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
});
