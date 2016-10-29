
function loadPersonalData(){
	//var username = get_cookie('username');
	//var token = get_cookie('authenticationToken');
	var username = get_cookie("username");
	var token = get_cookie("authenticationToken");

	f1 = function(arr){
					
		var values = $('.personales');
		var a = arr["account"];

		values[0].innerHTML = a.firstName;
		values[1].innerHTML = a.lastName;

		var bdate = a.birthDate.split("-");
		values[2].innerHTML = bdate[2] + "/" + bdate[1] + "/"+ bdate[0];
		

		if(a.gender == "M")
			values[3].innerHTML = "Masculino";
		else
			values[3].innerHTML = "Femenino";
		
		values[4].innerHTML = "DNI";
		values[5].innerHTML = a.identityCard;

		var values2 = $('.cuenta');
		
		
		mod2 = $("<a href='changeEmail.html'>Modificar</a>");
		mod3 = $("<a href='changePass.html'>Modificar</a>");
		
		values2[0].innerHTML = a.username;
		global_email = a.email;
		values2[1].innerHTML = a.email + "&emsp;";
		$(values2[1]).append(mod2);
		
		values2[2].innerHTML = "*******" + "&emsp;";
		$(values2[2]).append(mod3);

	};


	url = "http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAccount&username="+username+"&authentication_token="+token;

	APIAction(url,f1);

}


function loadCreditCards(){
	//var username = get_cookie('username');
	//var token = get_cookie('authenticationToken');
	var username = get_cookie("username");
	var token = get_cookie("authenticationToken");		
	
	var url = 'http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAllCreditCards&username='+username+'&authentication_token='+token;
	// console.log(url);
	APIAction(url,getCards);
}

var cardsAPI = function(arr){
		var a = arr["creditCards"];
		var i;
		for(i=0; i<a.length; i++){

			var expireDate = a[i].expirationDate;
			expireDate = expireDate[0] + expireDate[1]+"/"+ expireDate[2] + expireDate[3];
			
			aux = $("<div class='row profile-card'>"+
									    "<div class='col-xs-12 col-md-9'>"+
									    	"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Número: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='tarjeta-"+i+"'>"+a[i].number+"</p>"+
													"</div>"+
									    	"</div>"+
									    	"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Código de Seguridad: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='tarjeta-"+i+"'>***</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Fecha de Vencimiento: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='tarjeta-"+i+"'>"+expireDate+"</p>"+
													"</div>"+
											"</div>"+
									    "</div>"+
									    "<div class='col-xs-12 col-md-3'>"+
									    	"<div class='row center-vert'>"+
													"<div class='col-xs-10 col-xs-offset-1'>"+
														"<button type='button' class='btn btn-sm btn-warning btn-block mod-tarjeta' id='mod-tarjeta-"+i+"'>Modificar</button>"+
													"</div>"+
											"</div>"+
									    	"<div class='row center-vert'>"+
												"<div class='col-xs-10 col-xs-offset-1'>"+
													"<button type='button' id='delete-"+a[i].id+"' class='btn btn-sm btn-block btn-danger delete-card'>Quitar</button>"+
												"</div>"+
											"</div>"+
									    "</div>"+
									"</div>"+
								    "<div class='row'>"+
								    	"<div class='col-xs-12'>"+
								    		"<hr class='hr_horizontal'>"+
								    	"</div>"+
								   "</div>");

			$("#cards-table").append(aux);

			$(".mod-tarjeta").on('click', function(){
					cardNumber = $(this).attr('id').split("-");					
					var cardValues = $('.tarjeta-'+cardNumber[2]); //OBTENER this.id y pedir tarjeta-id
					
					var cardPath = "tarjetas.html?";					
					cardPath += "number=" + cardValues[0].innerHTML;
					cardPath += "&code=" + cardValues[1].innerHTML;
					cardPath += "&expire=" + cardValues[2].innerHTML;					
					window.location = cardPath;
			});
			$(".delete-card").on('click', function() {
				
				var url = "http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=DeleteCreditCard&username="
				+ get_cookie("username")+"&authentication_token="+ get_cookie("authenticationToken")
				+"&id="+$(this).attr('id').split("-")[1]+"";

				var f3 = function(arr){
   				};
   		
				APIAction(url,f3);

				$(this).closest('div.profile-card').next().remove();
				$(this).closest('div.profile-card').remove();								
			});

		}
}

var getCards = function(arr){
	//var username = get_cookie('username');
	//var token = get_cookie('authenticationToken');
	var username = get_cookie("username");
	var token = get_cookie("authenticationToken");	
	var a = arr["total"];
	url2 ='http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAllCreditCards&username='+username+'&authentication_token='+token+"&page_size="+a;
	APIAction(url2,cardsAPI);
}

function loadAddresses(){
	//var username = get_cookie('username');
	//var token = get_cookie('authenticationToken');
	var username = get_cookie("username");
	var token = get_cookie("authenticationToken");	
	var url = 'http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAllAddresses&username='+username+'&authentication_token='+token;
	initializeStates();
	APIAction(url,getAddresses);
}

var addressAPI = function(arr){
		var a = arr["addresses"];
		var i;
		var floor, gate, province;

		for(i=0; i<a.length; i++){
			
			province = convertCodeToProv(a[i].province);
			floor = a[i].floor;
			gate = a[i].gate;
			if(floor == null)
				floor = "---"
			if(gate==null)
				gate= "---"

		

			aux = $("<div class='row profile-address'>"+
									    "<div class='col-xs-12 col-md-9'>"+
									    	"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Nombre: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+a[i].name+"</p>"+
													"</div>"+
									    	"</div>"+
									    	"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Provincia: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+province+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Ciudad: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+a[i].city+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Teléfono: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+a[i].phoneNumber+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Código Postal: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+a[i].zipCode+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Calle: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+a[i].street+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Altura: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+a[i].number+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Piso: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+floor+"</p>"+
													"</div>"+
											"</div>"+
											"<div class = 'row spaced'>"+
													"<div class = 'col-xs-3'>"+
															"<p> Departamento: </p>"+
													"</div>"+
													"<div class = 'col-xs-6' >"+
														"<p class='address-"+i+"'>"+gate+"</p>"+
													"</div>"+
											"</div>"+
									    "</div>"+
									    "<div class='col-xs-12 col-md-3'>"+
									    	"<div class='row center-vert'>"+
													"<div class='col-xs-10 col-xs-offset-1'>"+
														"<button type='button' class='btn btn-sm btn-warning btn-block mod-address' id='mod-address-"+i+"'>Modificar</button>"+
													"</div>"+
											"</div>"+
									    	"<div class='row center-vert'>"+
												"<div class='col-xs-10 col-xs-offset-1'>"+
													"<button type='button' id='delete-"+a[i].id+"-address' class='btn btn-sm btn-block btn-danger delete-address'>Quitar</button>"+
												"</div>"+
											"</div>"+
									    "</div>"+
									"</div>"+
								    "<div class='row'>"+
								    	"<div class='col-xs-12'>"+
								    		"<hr class='hr_horizontal'>"+
								    	"</div>"+
								   "</div>");

			$("#addresses-table").append(aux);

			$(".mod-address").on('click', function(){
					addressNumber = $(this).attr('id').split("-");					
					var addressesValues = $('.address-'+addressNumber[2]);
					
					var addressPath = "addresses.html?";					
					addressPath += "name=" + addressesValues[0].innerHTML;
					addressPath += "&province=" + addressesValues[1].innerHTML;
					addressPath += "&city=" + addressesValues[2].innerHTML;
					addressPath += "&phoneNumber=" + addressesValues[3].innerHTML;
					addressPath += "&zipCode=" + addressesValues[4].innerHTML;
					addressPath += "&street=" + addressesValues[5].innerHTML;
					addressPath += "&number=" + addressesValues[6].innerHTML;
					addressPath += "&floor=" + addressesValues[7].innerHTML;
					addressPath += "&gate=" + addressesValues[8].innerHTML;
					
					window.location = addressPath;
			});
			
			$(".delete-address").on('click', function() {
				var url = "http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=DeleteAddress&username="
				+ get_cookie('username')+"&authentication_token="+ get_cookie('authenticationToken')
				+"&id="+$(this).attr('id').split("-")[1]+"";
				// console.log(url);

				var f2 = function(arr){
   				};
   		
				APIAction(url,f2);

				$(this).closest('div.profile-address').next().remove();
				$(this).closest('div.profile-address').remove();									
			});
			
		}
}

var getAddresses = function(arr){
	//var username = get_cookie('username');
	//var token = get_cookie('authenticationToken');
	var username = get_cookie("username");
	var token = get_cookie("authenticationToken");	
	
	var a = arr["total"];
	url2 ='http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAllAddresses&username='+username+'&authentication_token='+token+"&page_size="+a;
	// console.log(url2);
	APIAction(url2,addressAPI);
}
/*
function w(orderID, url, callback){
	var auxVar = orderID;
	var auxUrl = url;
	APIAction(auxUrl, callback);
}*/


function loadOrders(){
	var username = get_cookie("username");
	var token = get_cookie("authenticationToken");

	//var username = get_cookie('username');
	//var token = get_cookie('authenticationToken');	

	var url = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetAllOrders&username='+username+'&authentication_token='+token;
	// console.log(url);

	APIAction(url,getOrders);
}

/*
function deleteOrder(orderId) {
	var token = get_cookie("authenticationToken");
	var username = get_cookie("username");

	var getOrderToDelete = function(arr) {
		
		var item;
		var url;
		var deleteItemFromOrder = function(arr) {

		}

		for (var i = 0 ; i < arr["items"].length ; i++) {
			item = arr["items"][i];
			url = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=RemoveItemFromOrder&username='+username+'&authentication_token='+token+"&id="+item.id;
			
			APIAction(url, deleteItemFromOrder);

		}

	}

	var url = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetAllOrders&username='+username+'&authentication_token='+token+"&id="+orderID;

	APIAction(url,getOrderToDelete);
}
*/

/* 

"address": {
"id": 7,
"name": "ITBA – Sede Central",
"street": "Av. Eduardo Madero",
"number": "399",
"floor": null,
"gate": null,
"province": "C",
"city": null,
"zipCode": "C1106ACD",
"phoneNumber": "6393-ITBA",
"enabled":"T
}


*/

function convertCodeToProv(code){
    var prov;
    for(var i=0; i<global_states_arr.length; i++){
            
            if(code.localeCompare(global_states_arr[i].stateId) == 0){
                
                prov = global_states_arr[i].name;
            }
        }
    return prov;
}

function eachOrder(arr, value, price) {
	var orderID = value.id;
	var b = arr["address"];
	var name = b.name;
	var province = convertCodeToProv(b.province);
	var city = b.city;
	var telephone = b.phoneNumber;
	var code = b.zipCode;
	var street = b.street;
	var number = b.number;
	var floor = b.floor;
	var gate = b.gate;
	if(floor == null){
		floor = "---"
	}
	if(gate == null){
		gate = "---";
	}

	var status;

	switch(value.status) {
		case "2":
			status = "Confirmada (sin despachar aún)";
			fechaCreacion = value.processedDate.split(" ")[0];
			fechaEntrega = "---";
			break;
		case "3":
			status = "Despachada";
			fechaCreacion = value.processedDate.split(" ")[0];
			fechaEntrega = "---";
			break;
		case "4":
			status = "Entregada";
			fechaCreacion = value.processedDate.split(" ")[0];
			fechaEntrega = value.processedDate.split(" ")[0];
			break;
		default:
			break;
	}

	var aux =  $("<div class='row'>"+
		    		"<div class='col-xs-12'>"+
		    			"<hr class='hr_horizontal'>"+
		    		"</div>"+
		    	"</div>"+
		    	"<div class='row row-eq-height profile-order'>"+
			    	"<div class='col-xs-2 verticalLine centered'>"+
		    			"<p>"+orderID+"</p>"+
		    		"</div>"+
		    		"<div class='col-xs-10'>"+
		    			"<div class='row'>"+
		    				"<div class='col-xs-6 centered'>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12'>"+
		    							"<p><strong>Estado: </strong>"+status+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12'>"+
		    							"<p><strong>Fecha de Creación: </strong>"+fechaCreacion+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12'>"+
		    							"<p><strong>Fecha de Entrega: </strong>"+fechaEntrega+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12'>"+
		    							"<p><strong>Precio Total: </strong>$"+price+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    				"</div>"+
		    				"<div class='col-xs-6 centered'>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12'>"+
		    							"<p><strong>Dirección de Entrega</strong></p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12'>"+
		    							"<p><strong>Nombre: </strong>"+name+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Provincia: </strong>"+province+" </p>"+
		    						"</div>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Ciudad: </strong>"+city+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Teléfono: </strong>"+telephone+" </p>"+
		    						"</div>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Código Postal: </strong>"+code+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Calle: </strong>"+street+" </p>"+
		    						"</div>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Altura: </strong>"+number+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    					"<div class='row'>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Piso: </strong>"+floor+" </p>"+
		    						"</div>"+
		    						"<div class='col-xs-12 col-md-6'>"+
		    							"<p><strong>Departamento: </strong>"+gate+" </p>"+
		    						"</div>"+
		    					"</div>"+
		    				"</div>"+
		    			"</div>"+
		    		"</div>"+
			    "</div>");
	$("#orders-table").append(aux);
}

var getOrders = function(arr){

	var a = arr["orders"];
	/* +++xdebug */
	var token = get_cookie("authenticationToken");
	var username = get_cookie("username");
	var orderID;
	var day;
	var month; //Split " " y despues otro split "-"
	var hour;
	var addressID;

	var status;

	var fechaCreacion;
	var fechaEntrega;

	/* for(var i=0; i<a.length; i++){ */ /* +++xdebug */
	$.each(a, function (index, value) {
		/* +++xdebug */
		/*if (a[i].status.localeCompare("1")==0) {
			deleteOrder(a[i].id);
			break;
		}*/
		
		if(value.status.localeCompare("1")!=0){

			//console.log(value);
			orderID = value.id;
			addressID = value.address.id;
			name = value.address.name;
			//console.log("status");
			//console.log(value.status);

			var receivedDate = value.receivedDate;

			var receivedDateArr = receivedDate.split(' ');

			hour = receivedDateArr[1];

			var receivedDateArr2 = receivedDateArr[0].split('-');
			
			day = receivedDateArr2[2];
			month = convertIntToMonth(parseInt(receivedDateArr2[1]));			

/*			var ordersAPI = function(arr) {
				address(arr, value, month, day, hour);
			};*/


			/* ordersAPI(orderID, address); */

		
			/* DELETE
			(function() {
				
				var aux2 = orderID;
				APIAction(url, ordersAPI);
				
			});
			})();*/

			//w(orderID, url, ordersAPI);

				

			// APIAction(url, ordersAPI);
		
			var url = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetOrderById&username='+username+'&authentication_token='+token+'&id='+value.id;

			var getOrderById = function(arr) {
				var tmpUser
				var price = 0;

				if (!("order" in arr)) {
					return;
				}
				if (!("items" in arr["order"])) {
					return;
				}
				for (var i = 0 ; i < arr["order"]["items"].length ; i++) {
					price += arr["order"]["items"][i].price;
				}

				//console.log(price);
				var url = 'http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=GetAddressById&username='+username+'&authentication_token='+token+'&id='+value.address.id;
				var ordersAPI = function(arr) {
					eachOrder(arr, value, price);
				};
				APIAction(url, ordersAPI);
			};
			APIAction(url, getOrderById);


		}
								
	});
	
}



function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
	    results = regex.exec(location.search);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}