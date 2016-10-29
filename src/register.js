function putFecha(){
	var i;
	var days;
	var years;
	for(var i = 1; i < 32; i++){
		aux = $("<li id='day" + i + "'><a>" + i + "</a></li>");
		$("#DAYS").append(aux);
	}
	for(var x = 2015; x > 1900; x--){
		aux2=$("<li><a>" + x + "</a></li>");
		$("#YEARS").append(aux2);
	}
}

function RegisterLoadButtons(){
	$(".month3").on('click', function(){
		$("#day29").remove();
		$("#day30").remove();
		$("#day31").remove();
		var x = $("#DAYS").parents(".dropdown").find("button").text();
		if(x>28){
			$("#DAYS").parents(".dropdown").find("button").text("Día");
		}
		flagF=1;
	});

	$(".month2").on('click', function(){
		if(flagF==1){
			aux3=$("<li id='day29'><a>29</a></li>");
			$("#DAYS").append(aux3);
			aux4=$("<li id='day30'><a>30</a></li>");
			$("#DAYS").append(aux4);
		}
		$("#day31").remove();
		var x = $("#DAYS").parents(".dropdown").find("button").text();
		if(x>30){
			$("#DAYS").parents().text("Día");
		}
		flagF=0;
		flagO=1;
	});

	$(".month1").on('click', function(){
		if(flagF==1){
			aux3=$("<li id='day29'><a>29</a></li>");
			$("#DAYS").append(aux3);
			aux4=$("<li id='day30'><a>30</a></li>");
			$("#DAYS").append(aux4);
			aux5=$("<li id='day31'><a>31</a></li>");
			$("#DAYS").append(aux5);
		}
		if(flagO==1){
			aux5=$("<li id='day31'><a>31</a></li>");
			$("#DAYS").append(aux5);
		}

		flagF=0;
		flagO=0;
	});
}

function convertMonth(month){
	switch(month){
		case "Enero":
			return 0;
		case "Febrero":
			return 1;
		case "Marzo":
			return 2;
		case "Abril":
			return 3;
		case "Mayo":
			return 4;
		case "Junio":
			return 5;
		case "Julio":
			return 6;
		case "Agosto":
			return 7;
		case "Septiembre":
			return 8;
		case "Octubre":
			return 9;
		case "Noviembre":
			return 10;
		case "Diciembre":
			return 11;			
	}
}

function afterReglogin(username, pass){
	var url = "http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=SignIn&username=" + username + "&password=" + pass;

	APIAction(url, function(data){
		set_cookie("username", data.account.username, 7200);
		set_cookie("authenticationToken", data.authenticationToken, 7200);

		CreateOrder(function(data){location.href = 'index.html?msgmsg=login_success';});
	});
}