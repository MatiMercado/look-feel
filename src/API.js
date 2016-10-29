/*function APIAction(url, func, whenReady){
	var f;

	if (whenReady === undefined){
		f = function(){}
	}else{
		f = whenReady;
	}

	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
	    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
	        var myArr = JSON.parse(xmlhttp.responseText);
	        func(myArr);
	        f();
	    }
	}
	xmlhttp.open("GET", url, true);
	xmlhttp.timeout = 30000; //miliseconds
	xmlhttp.ontimeout = function () {
		$('#noconn').remove();
		$('noscript').after(
				'<div id="noconn" class="alert alert-danger" role="alert">' +
       			'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>' +
       			'<span class="sr-only">Error:</span>' + 
        			'No es posible establecer una conexión con el servidor. Por favor, inténtelo más tarde.' +
      			'</div>'
			);
		KillLoader();
	}
	xmlhttp.send();
	return xmlhttp;
}*/

/*function APIAction(url_g, func, whenReady){
	var f;

	if (whenReady === undefined){
		f = function(){}
	}else{
		f = whenReady;
	}

	$.ajax({
		url: url_g,
		dataType: "jsonp",
		timeout: 30000,
		global: true
	}).done(function (data) {
		func(data);
		f();
		//if (!callbacks) {
		//	callbacks = {};
		//}

		//if (data.error && callbacks.hasOwnProperty('error')) {
		//	callbacks.error(data);
		//	return;
		//}

		//if (callbacks.hasOwnProperty('success')) {
		//	callbacks.success(data);
		//}


	}).fail(function () {
		$('#noconn').remove();
		$('noscript').after(
				'<div id="noconn" class="alert alert-danger" role="alert">' +
       			'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>' +
       			'<span class="sr-only">Error:</span>' + 
        			'No es posible establecer una conexión con el servidor. Por favor, inténtelo más tarde.' +
      			'</div>'
			);
		KillLoader();
	})

}*/

/*		WORKS TOO!
function APIAction(url, func, whenReady){
	var f;

	if (whenReady === undefined){
		f = function(){}
	}else{
		f = whenReady;
	}

	$.getJSON(url, function(data){
		func(data);
		f();
	});
}*/
function APIAction(url, func, whenReady){

	var f;

	if (whenReady === undefined){
		f = function(){}
	}else{
		f = whenReady;
	}

	$.ajax({
	  dataType: "json",
	  url: url,
	  timeout: 30000,

	  success: function(data){

		if (data.hasOwnProperty('error')){
			Error('Ha ocurrido un error procesando la solicitud. Por favor, inténtelo más tarde.');
		} else { 	
		  	func(data);
		  	f();
		}
	  }

	}).fail(function () {
		$('#noconn').remove();
		$('noscript').after(
				'<div id="noconn" class="alert alert-danger" role="alert">' +
       			'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>' +
       			'<span class="sr-only">Error:</span>' + 
        			'No es posible establecer una conexión con el servidor. Por favor, inténtelo más tarde.' +
      			'</div>'
			);
		KillLoader();
	});
}
