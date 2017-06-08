// #################### Default functions ####################

$( document ).ready(function() {
    changePage();
});

// #################### Navigation ####################

function pageChangedEvents(hash){
	switch(hash) {
		case "#home":
			getPublicAlbums();
			break;
			
		case "#albums":
			
			break;
			
		default:
			getPublicAlbums();
	}
}

function changePage(){
	var hash = window.location.hash;
	
	if (hash == "") hash = "#home";
	
	$("#pages > section").hide();
	
	$(hash).show();
	
	pageChangedEvents(hash);
}

$(window).on('hashchange', changePage);

// #################### Form Requests ####################
$("#albumForm").submit(function(e) {
	e.preventDefault();
	
	var pictures = $("#inputPictures").prop("files");
	var data = $("#albumForm").serialize();
	var sharedUsers = [];
	
	$("#usersSharedTable tr").each(function() {
		var columns = $(this).find('td');
		sharedUsers.push($(columns[0]).text())
	});
	
	data += "&sharedusers="+JSON.stringify(sharedUsers);

	$.ajax({
		type : "POST",
		url : "rest/album",
		data : data,
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

//#################### Get Requests ####################

function getPublicAlbums(){
	$.ajax({
		type : "GET",
		url : "rest/album/public",
		/*beforeSend : function(xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader('Authorization', 'Bearer ' + token);
		},*/
		success : function(data) {
			console.log(data);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
}

function getUserAlbums(){
	
}

//Shared users events
$('input[type=radio][name=shareType]').change(function() {
    if (this.value == 'U') {
        $("#selectUsersGroup").css("display", "block");
    }else{
    	$("#selectUsersGroup").css("display", "none");
    }
});

$("#addSharedUserBtn").click(function(){
	var email = $("#inputUser").val();
	$("#inputUser").val("");
	
	if(email != ""){
		$("#usersSharedTable").find('tbody').append("<tr><td>"+email+"</td><td><div class='btn btn-danger deleteSharedUserBtn'>delete</div></td></tr>");
	}
})

$(document).on("click", ".deleteSharedUserBtn", function() {
	$(this).parent().parent().remove();
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

