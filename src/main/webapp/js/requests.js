/*
#################### Form Requests ####################
 */

$("#albumForm").submit(function(e) {
	e.preventDefault();

	var pictures = $("#inputPictures").prop("files");
	var data = $("#albumForm").serialize();
	var sharedUsers = [];

	$("#usersSharedTable tr").each(function() {
		var columns = $(this).find('td');
		sharedUsers.push($(columns[0]).text())
	});

	data += "&sharedusers=" + JSON.stringify(sharedUsers);

	$.ajax({
		type : "POST",
		url : "rest/album",
		data : data,
		/*
		 * beforeSend : function(xhr) { var token =
		 * window.sessionStorage.getItem("sessionToken");
		 * xhr.setRequestHeader('Authorization', 'Bearer ' + token); },
		 */
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
