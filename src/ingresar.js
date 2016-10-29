function loadEnterButton(){
	$('#AD1').on('click', function(){
		var user = $('#username').val();
		var pass = $('#pass1').val();

		//Validar 
		var url = "http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=SignIn&username=" + user + "&password=" + pass;

		APIAction(url, function(data){
			if (data.hasOwnProperty('error')){
				$('#collapse_loginattemp').collapse('show');
			} else {
				//Login success
				set_cookie("username", data.account.username, 7200);
				set_cookie("authenticationToken", data.authenticationToken, 7200);
				location.href = 'index.html';
			}
		});
		
	});


	$("#pass1").focusout(function(){
		 pass1 = document.getElementById("pass1").value;
		if(validate_Password(pass1)){
			//valid_pass1=true;
			$("#collapse_pass1").collapse('hide');
		}else{
			$("#collapse_pass1").collapse('show');
		}
	});
	$("#username").focusout(function(){
		 username = document.getElementById("username").value;
		if(validate_Username(username)){
			//valid_username=true;
			$("#collapse_username").collapse('hide');
		}else{
			$("#collapse_username").collapse('show');
		}
	});




}
