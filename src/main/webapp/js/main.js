$("#albumForm").submit(function(e) {
	e.preventDefault();
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
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.responseText);
			console.log(thrownError);
		}
	});
});