$("#albumForm").submit(function(e) {
	e.preventDefault();
	
	var pictures = $("#inputPictures").prop("files");

	$.ajax({
		type : "POST",
		url : "rest/album",
		data : $("#albumForm").serialize(),
		/*beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},*/
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

//Upload pictures
function uploadPictures(pictures, albumCode){
	var formData = new FormData();

	console.log($("#inputPictures"));
	console.log(pictures);

	for (var i = 0; i < pictures.length; i++) {
		var file = pictures[i];

		// Add the file to the request.
		formData.append(file.name, file);
	}

	console.log(formData);

	// Set up the request.
	var xhr = new XMLHttpRequest();

	// Open the connection.
	xhr.open('POST', 'rest/picture/'+albumCode, true);

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