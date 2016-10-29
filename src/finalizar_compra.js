//finaleizar_compra.js





	/* Funcion para loguearse (Despues la reemplazamos por el SignIn)*/
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
	function convertMonthToString(month){
		switch(month){
			case "Enero":
				return "01";
			case "Febrero":
				return "02";
			case "Marzo":
				return "03";
			case "Abril":
				return "04";
			case "Mayo":
				return "05";
			case "Junio":
				return "06";
			case "Julio":
				return "07";
			case "Agosto":
				return "08";
			case "Septiembre":
				return "09";
			case "Octubre":
				return "10";
			case "Noviembre":
				return "11";
			case "Diciembre":
				return "12";			
		}
	}

	var h = function(arr){
		var a = arr["creditCards"];
		var i;
		for(i=0;i<a.length;i++){

			aux = $("<div class='radio col-xs-12'>"+
			            "<label id='label_radio_"+i+"'>"+
			              "<input type='radio' name='radios_cart' class='track-order-change' id='RadioCart"+i+"'>"+
			              "Número: "+a[i].number+"</label></div>");
			(function() {
	        		var gid = i-1;
	        		$("input[name='radios_cart']").change( function() {
	        	
		        		if($('#RadioCart'+gid).is(":checked")){
		        			var username = get_cookie("username");
		        			//var username = localStorage.getItem("username");
		        			cart_number=a[gid].number;
		        			cart_date=a[gid].expirationDate;
		        			cart_cod=a[gid].securityCode;
		        			//cart_id=a[gid].id;
		        			localStorage.setItem("cart_id"+username,'{"id":'+a[gid].id+'}');
		        			
		        		}

		        	});
       
        		})();

			$("#CARDS").append(aux);
		}
		for(var x=2015;x<2030;x++){
			aux2=$("<li><a>"+x+"</a></li>");
			$("#YEARS").append(aux2);
		} 
	}
	
	var getPersonalInfo = function(arr){
		var a = arr["account"];
		$("#fname_id").text($("#fname_id").text()+a.firstName);
		$("#lname_id").text($("#lname_id").text()+a.lastName);
		$("#DNI_id").text($("#DNI_id").text()+a.identityCard);
	}

	var getCards = function(arr){
		var a=arr["total"];
		//var username = localStorage.getItem("username");
		var username = get_cookie('username');
		url2="http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAllCreditCards&username="+username+"&authentication_token="+token+"&page_size="+a;
		APIAction(url2,h);
	}

	var gatAddr = function(arr){
		var a = arr["addresses"];
		var i;
		for(i=0;i<a.length;i++){
			aux = $("<div class='radio col-xs-12'><label id='label_addr_"+i+"' class='width'>"+
			              "<input type='radio' name='radios_addr' class='track-order-change' id='RadioAddr"+i+"'>"+
			              "<ul class='datos_de_cuenta2'>"+
			              		"<li id='name"+i+"'class='textList2'>Provincia:<span class='rojito'>*</span>"+parNull(a[i].name)+"</li>"+
			              		"<li id='prov"+i+"'class='textList2'>Provincia:<span class='rojito'>*</span>"+parNull(a[i].province)+"</li>"+
								"<li id='city"+i+"class='textList2'>Ciudad:<span class='rojito'>*</span>"+parNull(a[i].city)+"</li>"+
								"<li id='phone"+i+"class='textList2'>Téfono:<span class='rojito'>*</span>"+parNull(a[i].phoneNumber)+"</li>"+
								"<li id='zipCode"+i+"class='textList2'>Código Postal:<span class='rojito'>*</span>"+parNull(a[i].zipCode)+"</li>"+
								"<li id='street"+i+"class='textList2'>Calle:<span class='rojito'>*</span>"+parNull(a[i].street)+"</li>"+
								"<li id='number"+i+"class='textList2'>Altura:<span class='rojito'>*</span>"+parNull(a[i].number)+"</li>"+
								"<li class='textList2'>"+
									"<div class='row'>"+
										"<div class='col-xs-4'>"+
											"<p id='floor"+i+"'>Piso:"+parNull(a[i].floor)+"</p>"+
										"</div>"+
										"<div class='col-xs-4'>"+
											"<p id='gate"+i+"'>Departamento:"+parNull(a[i].gate)+"</p>"+
										"</div>"+	
									"</div>"+
								"</li>"+
							"</ul>"+
			              "</label>"+
			              	"<div name= completarCollapse' class='col-xs-12 panel-collapse collapse' id='CD"+i+"'>"+
					            "<div class='col-xs-6 col-xs-offset-3 listElem'>"+
					        		"<button type='button' class='btn btn-lg btn-block btn-warning extra'>Completar Datos</button>"+
					            "</div>"+
					        "</div>"+
			              "</div>");
			(function() {
	        		var gid = i-1;
	        		$("input[name='radios_addr']").change( function() {
	        				//var username = localStorage.getItem("username");
	        				var username = get_cookie('username');
		        		if($('#RadioAddr'+gid).is(":checked")){
		        			if(parNull(a[gid].province).localeCompare("----")==0||parNull(a[gid].phoneNumber).localeCompare("----")==0||parNull(a[gid].zipCode).localeCompare("----")==0||parNull(a[gid].street).localeCompare("----")==0||parNull(a[gid].number).localeCompare("----")==0||parNull(a[gid].phoneNumber).localeCompare("----")==0){

		        				for(var x = 0; x<i; x++){
		        					$("#CD"+x).collapse('hide');

		        				}
		        				//addr_id=gid;
		        				$("#CD"+gid).collapse('show');

		        			}else{
	
		        				addr_gate= a[gid].gate;
								addr_floor= a[gid].floor;
								addr_heigth= a[gid].number;
								addr_street= a[gid].street;
								addr_phone= a[gid].phoneNumber;
								addr_city= a[gid].city;
								addr_prov= a[gid].province;
								addr_zipCode= a[gid].zipCode;
								addr_name= a[gid].name;
		        				localStorage.setItem("addr_id"+username,'{"id":'+a[gid].id+'}');
		        			}

		        		}		
		        		

		        	});
       
        		})();
    		
			$("#ADDRESS").append(aux);
		} 
	
	}

	function parNull(dato){
		if(dato==null){
			return "----"
		}else{
			return dato;
		}
	}
	

	function qwe(){
		if (localStorage.getItem('addr_id' + get_cookie('username')) == null){
			window.setTimeout(qwe, 100);
		}else{
		}
	}


	$("#cart_number").focusout(function(){
		 cart_number = document.getElementById("cart_number").value;
		if(validate_number(cart_number)){
			valid_cart_number=true;
			$("#collapse_cart_number").collapse('hide');
		}else{
			$("#collapse_cart_number").collapse('show');
		}
	});
	$("#cart_cod").focusout(function(){
		 cart_cod = document.getElementById("cart_cod").value;
		if(validate_cod(cart_cod)){
			valid_cart_cod=true;
			$("#collapse_cart_cod").collapse('hide');
		}else{
			$("#collapse_cart_cod").collapse('show');
		}
	});

	$("#ADD_CART").on('click',function(){
		var year;
		var month;
		month = $("#MONTH").parents(".dropdown").find("button").text();
	  	year = $("#YEARS").parents(".dropdown").find("button").text();
	  	var d = new Date();
	  	if(month.localeCompare("Mes")==0 || year.localeCompare("Año")==0){
	  		$("#collapse_cart_date").collapse('show');
	  		valid_cart_date = false;
	  	}else{
	  		valid_cart_date=true;
	  		if(d.getFullYear()==year){
	  			if(d.getMonth()<=convertMonth(month)){
	  				valid_cart_date=false;
	  			}
	  		}
	  		else if(d.getFullYear()<year){
	  			valid_date=false;
	  		}
	  	}
	  	if(!valid_cart_date){
	  		$("#collapse_cart_date").collapse('show');
	  	}else{
	  		if(valid_cart_date && valid_cart_cod && valid_cart_number){
	  			$("#collapse_add_cart").collapse('hide');
	  			cart_date=convertMonthToString(month)+year[2]+year[3];
	  			valid_cart=true;
	  			//var username = localStorage.getItem("username");
	  			var username = get_cookie('username');
	  			url='http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=CreateCreditCard&username='+username+'&authentication_token='+token+'&credit_card={"number":"'+cart_number+'","expirationDate":"'+cart_date+'","securityCode":"'+cart_cod+'"}';
	  			APIAction(url,helppp2);

	  		}else{
	  		$("#collapse_add_cart").collapse('show');
	  		}
	  	}
	});
	
	var states = function(arr) {
		    var a = arr["states"];
		    var i;
		    for(i = 0; i < a.length; i++) {

		        if(addr_prov.localeCompare(a[i].name)==0){
		        	$("#collapse_addr_prov").collapse('hide');
		        	addr_prov=a[i].stateId;        	
		        	valid_addr_prov=true;
		        	return true;
		        }
		    }
		    $("#collapse_addr_prov").collapse('show');
		    return false;
    }

    function validate_prov(){
    	url="http://eiffel.itba.edu.ar/hci/service3/Common.groovy?method=GetAllStates";
    	APIAction(url,states);
    	
    }
    function validate_number(num){
    	if(!num){
    		return false;
    	}
    	var ini = num[0]+num[1];
    	switch (ini){
    		case "34":
    		case "37":
    			if(num.length==15){
    				return /^([0-9]{15})$/g.test(num);
    			}
    			return false;
    		case "36":
    			if(num.length==16){
    				return /^([0-9]{16})$/g.test(num);
    			}
    			return false;
    		case "51":
    		case "52":	
    		case "53":
    			if(num.length==16){
    				return /^([0-9]{16})$/g.test(num);
    			}
    			return false;
    		default:
    			if(num[0]=="4"){
    				if(num.length==16){
    					return /^[0-9]+$/g.test(num);
    				}
    			}
    			return false;
    	}

    	return false;
    }
    function helppp(arr){
    	localStorage.setItem("addr_id"+get_cookie('username'),'{"id":'+arr["address"].id+'}');
    	//addr_id = arr["address"].id;
    }
    function helppp2(arr){
    	localStorage.setItem("cart_id"+ get_cookie('username'),'{"id":' + arr["creditCard"].id + '}');
    	//cart_id = arr["creditCard"].id;
    }
    function validate_heigth(num){
    	if(!num || num.length>6){
    		return false;
    	}
    	return /^([0-9]{6})$/g.test(num);
    }
    function validate_floor(num){
    	if(!num){
    		return true;
    	}
    	if(num.length>2){
    		return false;
    	}
    	return /^[0-9]+$/g.test(num);
    }
    function validate_gate(num){
    	if(!num){
    		return true;
    	}
    	if(num.length>2){
    		return false;
    	}
    	return /^[a-zA-Z0-9]+$/g.test(num);
    }

    function validate_cod(num){
    	if(!num){
    		return false;
    	}
    	var init = cart_number[0]+cart_number[1];
    	if(init.localeCompare("34")==0||init.localeCompare("37")==0){
    		if(num.length!=4){
    			return false;
    		}
    		return /^([0-9]{4})$/g.test(num);
    	}
    	if(num.length!=3){
    		return false;
    	}
    	return /^([0-9]{3})$/g.test(num);
    }
    function validate_zipCod(num){
    	if(!num || num.length>10){
    		return false;
    	}
    	return /^[0-9]+$/g.test(num);
    }
    function validate_Names(name) {
	    if (!name || name.length > 80) {
	        return false;
	    }

	    return /[a-zA-Z0-9]/i.test(name);
	}
	function validate_phone(name) {
	    if (!name || name.length > 25) {
	        return false;
	    }

	    return /[a-zA-Z0-9]/i.test(name);
	}

	$("#addr_name").focusout(function(){
		 addr_name = document.getElementById("addr_name").value;
		if(validate_Names(addr_name)){
			valid_addr_name=true;
			$("#collapse_addr_name").collapse('hide');
		}else{
			$("#collapse_addr_name").collapse('show');
		}
	});

    $("#addr_prov").focusout(function(){
		addr_prov = document.getElementById("addr_prov").value;
		validate_prov();
		
	});

	$("#addr_city").focusout(function(){
		 addr_city = document.getElementById("addr_city").value;
		if(validate_Names(addr_city)){
			valid_addr_city=true;
			$("#collapse_addr_city").collapse('hide');
		}else{
			$("#collapse_addr_city").collapse('show');
		}
	});
	$("#addr_street").focusout(function(){
		 addr_street = document.getElementById("addr_street").value;
		if(validate_Names(addr_street)){
			valid_addr_street=true;
			$("#collapse_addr_street").collapse('hide');
		}else{
			$("#collapse_addr_street").collapse('show');
		}
	});
	$("#addr_phone").focusout(function(){
		 addr_phone = document.getElementById("addr_phone").value;
		if(validate_phone(addr_phone)){
			valid_addr_phone=true;
			$("#collapse_addr_phone").collapse('hide');
		}else{
			$("#collapse_addr_phone").collapse('show');
		}
	});
	$("#addr_heigth").focusout(function(){
		 addr_heigth = document.getElementById("addr_heigth").value;
		if(validate_phone(addr_heigth)){
			valid_addr_heigth=true;
			$("#collapse_addr_heigth").collapse('hide');
		}else{
			$("#collapse_addr_heigth").collapse('show');
		}
	});
	$("#addr_zipCode").focusout(function(){
		 addr_zipCode = document.getElementById("addr_zipCode").value;
		if(validate_zipCod(addr_zipCode)){
			valid_addr_zipCode=true;
			$("#collapse_addr_zipCode").collapse('hide');
		}else{
			$("#collapse_addr_zipCode").collapse('show');
		}
	});
	$("#addr_floor").focusout(function(){
		 addr_floor = document.getElementById("addr_floor").value;
		if(validate_floor(addr_floor)){
			valid_addr_floor=true;
			$("#collapse_addr_floor").collapse('hide');
		}else{
			$("#collapse_addr_floor").collapse('show');
		}
	});

	$("#addr_gate").focusout(function(){
		 addr_gate = document.getElementById("addr_gate").value;
		if(validate_gate(addr_gate)){
			valid_addr_gate=true;
			$("#collapse_addr_gate").collapse('hide');
		}else{
			$("#collapse_addr_gate").collapse('show');
		}
	});

	$("#ADD_ADDR").on('click',function(){
		if(valid_addr_name && valid_addr_phone && valid_addr_heigth && valid_addr_city && valid_addr_street && valid_addr_prov && valid_addr_gate && valid_addr_floor && valid_addr_zipCode){
			$("#collapse_add_addr").collapse('hide');
			valid_addr=true;
			url='http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=CreateAddress&username='+username+'&authentication_token='+token+'&address={"name":"'+addr_name+'","street":"'+addr_street+'","number":"'+addr_heigth+'","floor":'+addr_floor+',"gate":'+addr_gate+',"province":"'+addr_prov+'","city":"'+addr_city+'","zipCode":"'+addr_zipCode+'","phoneNumber":"'+addr_phone+'"}';
				APIAction(url,helppp);
				


	  		}else{
	  			$("#collapse_add_addr").collapse('show');
	  		}


	});

	$("#AD1").on('click',function(){
		if(!(Point1 && Point2 && Point3)){
			$("#collapse_comprar").collapse('show');
			return;
		}
		if ($('#firstRadio').is(":checked")){

			address='{"name":"'+addr_name+'","street":"'+addr_street+'","number":"'+addr_heigth+'","province":"'+addr_prov+'","zipCode":"'+addr_zipCode+'","phoneNumber":"'+addr_phone+'", "floor":"'+addr_floor+'","gate":"'+addr_gate+'"}';
			
		}else{
			if(valid_addr_name && valid_addr_phone && valid_addr_heigth && valid_addr_city && valid_addr_street && valid_addr_prov&& valid_addr_zipCode){
				if(valid_addr){
					address='{"name":"'+addr_name+'","street":"'+addr_street+'","number":"'+addr_heigth+'","province":"'+addr_prov+'","zipCode":"'+addr_zipCode+'","phoneNumber":"'+addr_phone+'","floor":"'+addr_floor+'","gate":"'+addr_gate+'"}';
					
				}else{
					$("#collapse_add_addr").find('p').text("Es necesario agregar la dirección para seguir.");
					$("#collapse_add_addr").collapse('show');
					return;	
				}
			}else{

				$("#collapse_comprar").collapse('show');
				return;
			}
		}
		if ($('#thirdRadio').is(":checked")){
			creditCards=null;
			cart_id=null;

		
		}else{
			if ($('#fifthRadio').is(":checked")){
				creditCards='{"number":"'+cart_number+'","expirationDate":"'+cart_date+'","securityCode":"'+cart_cod+'"}';
			
			}else{
				var year;
				var month;
				month = $("#MONTH").parents(".dropdown").find("button").text();
			  	year = $("#YEARS").parents(".dropdown").find("button").text();
			  	var d = new Date();
			  	if(month.localeCompare("Mes")==0 || year.localeCompare("Año")==0){
			  		$("#collapse_cart_date").collapse('show');
			  		valid_cart_date = false;
			  	}else{
			  		valid_cart_date=true;
			  		if(d.getFullYear()==year){
			  			if(d.getMonth()<=convertMonth(month)){
			  				valid_cart_date=false;
			  			}
			  		}
			  		else if(d.getFullYear()<year){
			  			valid_date=false;
			  		}
			  	}
			  	if(valid_cart_date && valid_cart_cod && valid_cart_number){
			  		if(valid_cart){
		  				$("#collapse_add_cart").collapse('hide');
		  				cart_date=convertMonthToString(month)+year[2]+year[3];
		  				creditCards='{"number":"'+cart_number+'","expirationDate":"'+cart_date+'","securityCode":"'+cart_cod+'"}';
		  			}else{
		  				$("#collapse_add_cart").find('p').text("Es necesario agregar la tarjeta para seguir");
		  				$("#collapse_add_cart").collapse('show');
		  			}	
	  			}else{

	  				$("#collapse_comprar").collapse('show');
					return;
	  			}
					

			}
		}
		if(!($("#drop-day").parents('.dropdown').hasClass('uncomplete'))){
			select_day = true;
		}
		if(!($("#drop-month").parents('.dropdown').hasClass('uncomplete'))){
			select_month = true;
		}
		if(!($("#drop-hour").parents('.dropdown').hasClass('uncomplete'))){
			select_hour = true;
		}
		if(!(select_hour && select_month && select_day)){

			$("#collapse_comprar").collapse('show');
			return;
		}
		var buy_date= $("#but-month").text()+"-"+$("#but-day").text()+"-"+$("#but-hour").text();
		localStorage.setItem("date"+username,buy_date);
		localStorage.setItem("address"+username,address);
		localStorage.setItem("creditCard"+username,creditCards);
		//localStorage.setItem("addr_id"+username,'{"id":'+addr_id+'}');
		//localStorage.setItem("cart_id"+username,'{"id":'+cart_id+'}');

			
		window.location='pagina_confirmacion.html';

		
		

	});

	$("#C1").on('click',function(){
		window.location="cart.html";
	});
	
	

	$('input[name="radios"]').change( function() {
		Point1=true;
		$('#firstAccordion, #secondAccordion').collapse('hide');
		

	    if ($('#firstRadio').is(":checked")){

	        $('#firstAccordion').collapse('show');

	    } else {

	        $('#firstAccordion').collapse('hide');
	    }

	    if ($('#secondRadio').is(":checked")){

	        $('#secondAccordion').collapse('show');

	    } else {

	        $('secondAccordion').collapse('hide');
	    }
	});

	$('input[name="radios2"]').change( function() {
		Point2=true;
		$('#thirdAccordion, #fouthAccordion').collapse('hide');

	    if ($('#thirdRadio').is(":checked")){

	        $('#thirdAccordion').collapse('show');

	    } else {

	        $('#thirdAccordion').collapse('hide');
	    }
	    if ($('#fourthRadio').is(":checked")){


	        $('#fourthAccordion').collapse('show');

	    } else {


	        $('#fourthAccordion').collapse('hide');
	    }

	});
	$('input[name="radios3"]').change( function() {
		Point3=true;
		$('#fifthAccordion, #sixthAccordion').collapse('hide');
	    if ($('#fifthRadio').is(":checked")){

	        $('#fifthAccordion').collapse('show');

	    } else {

	        $('#fifthAccordion').collapse('hide');
	    }
	    if ($('#sixthRadio').is(":checked")){

	        $('#sixthAccordion').collapse('show');

	    } else {

	        $('#sixthAccordion').collapse('hide');
	    }

	   
	});



	$(".dropdown-menu").on('click','li',function(){
		var selText = $(this).text();
	  	$(this).parents('.dropdown').find('.dropdown-toggle').text(selText);
	  	$(this).parents('.dropdown').removeClass('uncomplete');
		
	});