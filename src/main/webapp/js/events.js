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

$(document).on("click", ".edit-image-btn", function() {
	$(this).parent().find(".picture-options").toggle();
});
