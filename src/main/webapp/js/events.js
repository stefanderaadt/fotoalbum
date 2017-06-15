//Shared users events
$(document).on("click", ".input-share-type", function(){
    if (this.value == 'U') {
    	$(this).parent().parent().find(".select-users-group").show();
        //$(".select-users-group").show();
    }else{
    	$(this).parent().parent().find(".select-users-group").hide();
    	//$(".select-users-group").hide();
    }
});

$(document).on("click", ".add-shared-user-btn", function(){
	var testEmail = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
	
	var table = $(this).parent().find(".add-shared-users-table");
	var input = $(this).parent().find(".input-user");
	
	var email = input.val();
	input.val("");
	
	if(email != "" && testEmail.test(email)){
		table.find('tbody').append("<tr><td>"+email+"</td><td><div class='btn btn-danger delete-shared-users-btn'>Verwijderen</div></td></tr>");
	}
})

$(document).on("click", ".delete-shared-users-btn", function() {
	$(this).parent().parent().remove();
});

// Edit album events
$(document).on("click", ".edit-image-btn", function() {
	$(this).parent().find(".picture-options").toggle();
});

$(document).on("click", "#edit-title-btn", function(){
	$(this).parent().parent().find(".edit-title").toggle();
});

$(document).on("click", "#edit-desc-btn", function(){
	$(this).parent().parent().find(".edit-desc").toggle();
});

$(document).on("change keyup paste click", "#edit-title-input", function(){
	$(this).parent().parent().parent().find("#edit-title-view").text($(this).val());
});

$(document).on("change keyup paste click", "#edit-desc-input", function(){
	$(this).parent().parent().parent().find("#edit-desc-view").text($(this).val());
});