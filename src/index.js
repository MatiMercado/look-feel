function isLogged(){
	  return (get_cookie("username") == '' || get_cookie("username") == null)? false : true;
}

function loadLoginLogoutInterface() {

  if (isLogged()){
	var login = $('#login');
	var register = $('#register');

	var myprofile = $('<a id="myprofile" href="profile.html">Mi Perfil</a>');
	var logout = $('<li class="verticalLine"><a id="logout" href="index.html">Salir</a></li>');
	var nameli = $('<li><a id="userlabel" class="greeting">¡Hola, ' + get_cookie('username') + '!</a></li>');

	var aux;
	aux = login.parent();

	aux.after(logout);
	register.remove();

	aux.append(myprofile);
	aux.removeClass('verticalLine');
	login.remove();

	aux.before(nameli);


	$('#logout').on('click', function(){
	  delete_cookie('username');
	  delete_cookie('authenticationToken');
	});
  } 

}

/* Map functions */
function setMenuUrl(id){

  $('#' + id).on('click', function(){
	window.location = "search_page.html?name=" + id;
  });

}

function getCatFilterById(id){
  switch (id){
	case 'men':
	  return '[{"id":1,"value":"Masculino"},{"id":2,"value":"Adulto"}]'
	case 'women':
	  return '[{"id":1,"value":"Femenino"},{"id":2,"value":"Adulto"}]'
	case 'boys':
	  return '[{"id":1,"value":"Masculino"},{"id":2,"value":"Infantil"}]'
	case 'girls':
	  return '[{"id":1,"value":"Femenino"},{"id":2,"value":"Infantil"}]'
	case 'babies':
	  return '[{"id":2,"value":"Infantil"}]'
	default:
	  return undefined
  }
}

/* End Map functions*/

function loadCategory(id) {

	var categories_url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllCategories";
	var obj = $('#' + id);
	var obj_link = $('#' + id + '-link');

	obj.addClass('dropdown');
	obj_link.attr('data-toggle', 'dropdown');
	obj_link.addClass('dropdown-toggle');

	setMenuUrl(id)

	//Set Categories
	APIAction(categories_url + '&filters=' + getCatFilterById(id), function(data){
		var cat = data.categories;
		var st = "";

		for (var index = 0; index < cat.length; index++){
		  st = st + '<li data-subid="' + cat[index].id + '"><a href="search_page.html?name=' + id + '&category='+ cat[index].id +'">' + cat[index].name + '<span class="glyphicon glyphicon-menu-right cb pull-right" aria-hidden="true"></span><i class="icon-arrow-right"></i></a></li>';
		}
		st = '<ul class="dropdown-menu">' + st + '</ul>';

		obj.append(st);

		function getSubcatUrlByID(id, buttonid){
		 	var x ='http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllSubcategories&id=' + id
		  + '&filters=' + getCatFilterById(buttonid);
		  	return x;
		}

		$.each($('#' + id + ' [data-subid]'), function(index, value){

		  var url = getSubcatUrlByID($(value).data("subid"), id);

		  APIAction(url, function(arr){
			var subarr = arr['subcategories'];
			var subst = '<ul class="dropdown-menu sub-menu scrollable-menu">';
			
			for (i = 0; i < subarr.length; i++){
			  subst = subst + '<li><a href="search_page.html?name=' + id + '&category=' + $(value).data('subid') + '&subcategory=' + subarr[i].id + '">' + subarr[i].name + '</a></li>';
			}
			
			subst = subst + "</ul>";
			$(value).append(subst);
		  });

		});

	});
}

function loadAllCategories(arr){
	$.each(arr, function(k, v){
    	loadCategory(v);
    });
}


function CheckSuccess(){

    /////////////////MENSAJE

    var dict = {'login_success': 'La cuenta ha sido creada satisfactoriamente.',
                'order_confirmed': 'La compra ha sido realizada existosamente. La misma puede visualizarse en <em>Mis Compras</em>.'};

    var uio = location.search.indexOf('msgmsg');

    if (uio >= 0){
      var key = location.search.substr(uio);

      try{
        key = key.split('=')[1];
        var msg = dict[key];

        if (!(msg === undefined)){
          Success(msg);
        }

      } catch(e){

      }
    }
}

function Success(msg){
	$('#success').remove();
	$('noscript').after(
			'<div id="success" class="alert alert-success" role="alert">' +
       		'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>' +
       		'<span class="sr-only">Éxito:</span>' +
        		msg +
      		'</div>'
		);
}

function Error(msg){
	$('#error').remove();
	$('noscript').after(
			'<div id="error" class="alert alert-danger" role="alert">' +
       		'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>' +
       		'<span class="sr-only">Error:</span>' +
        		msg +
      		'</div>'
		);
}

function InjectLoader(){
	$('body').prepend(
		'<div id="loading">' +
			'<img id="loading-image" src="http://i.stack.imgur.com/MnyxU.gif" alt="Loading..." />' +
		'</div>'
		);
}

function KillLoader(){
		$('#loading').remove();
}

function h(arr){
	var countCarousel=0;
	var a = arr["products"];
	var w;
	for (w=0; w < a.length; w++) {
		if(w%4==0){
			countCarousel++;
			if(w==0){
				newCarousel = $("<div class='item active' id='1CAROUSEL"+countCarousel+"'>"+
								"<div class='row'>"+
									"<div class='col-xs-8 col-xs-offset-2'>"+
										"<div class='row'>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>");

				$("#SUPER_CAROUSEL1").append(newCarousel);
			}else{
				newCarousel = $("<div class='item' id='1CAROUSEL"+countCarousel+"'>"+
								"<div class='row'>"+
									"<div class='col-xs-8 col-xs-offset-2'>"+
										"<div class='row'>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>");

				$("#SUPER_CAROUSEL1").append(newCarousel);

			}
		}
		recomendado = $("<div class='col-xs-12 col-sm-6 col-md-3'>"+
			        		"<div class='row margin-top'>"+
			        			"<div class='col-xs-12'>"+
					        		"<div class='row hovereffect'>"+
										"<div class='col-xs-6 col-xs-offset-3'>"+
					        				"<div class='row centered'>"+
						          				"<img class='starred-prod' src="+a[w].imageUrl[0]+" alt='"+a[w].name+"'/>"+
						          			"</div>"+
						          			"<div class='row centered'>"+
					          					"<h6>"+a[w].name+"</h6>"+
					          					"<p class='textoChico'>$"+a[w].price+"</p>"+
						          			"</div>"+
						          		"</div>"+
						          		"<div class='overlay'>"+
											"<a class='info' href='detalle_producto.html?productId="+a[w].id+"'></a>"+
										"</div>"+
						          	"</div>"+
						        "</div>"+
						    "</div>"+
				        "</div>");
		$("#1CAROUSEL"+countCarousel).children().children().children().append(recomendado);

	}

}


function q(arr){
	var countCarousel=0;
	var a = arr["products"];
	var w;
	for (w=0; w < a.length; w++) {
		if(w%4==0){
			countCarousel++;
			if(w==0){
				newCarousel = $("<div class='item active' id='2CAROUSEL"+countCarousel+"'>"+
								"<div class='row'>"+
									"<div class='col-xs-8 col-xs-offset-2'>"+
										"<div class='row'>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>");

				$("#SUPER_CAROUSEL2").append(newCarousel);
			}else{
				newCarousel = $("<div class='item' id='2CAROUSEL"+countCarousel+"'>"+
								"<div class='row'>"+
									"<div class='col-xs-8 col-xs-offset-2'>"+
										"<div class='row'>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>");

				$("#SUPER_CAROUSEL2").append(newCarousel);

			}
		}
		recomendado = $("<div class='col-xs-12 col-sm-6 col-md-3'>"+
			        		"<div class='row margin-top'>"+
			        			"<div class='col-xs-12'>"+
					        		"<div class='row hovereffect'>"+
										"<div class='col-xs-6 col-xs-offset-3'>"+
					        				"<div class='row centered'>"+
						          				"<img class='starred-prod' src="+a[w].imageUrl[0]+" alt='"+a[w].name+"'/>"+
						          			"</div>"+
						          			"<div class='row centered'>"+
					          					"<h6>"+a[w].name+"</h6>"+
					          					"<p class='textoChico'>$"+a[w].price+"</p>"+
						          			"</div>"+
						          		"</div>"+
						          		"<div class='overlay'>"+
											"<a class='info' href='detalle_producto.html?productId="+a[w].id+"'></a>"+
										"</div>"+
						          	"</div>"+
						        "</div>"+
						    "</div>"+
				        "</div>");
		$("#2CAROUSEL"+countCarousel).children().children().children().append(recomendado);

	}

}

function CarrouselLoad(){
	window.setTimeout(function(){
		
		var dir = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsByCategoryId&id=1";
		APIAction(dir,h);
	},2000);

	window.setTimeout(function(){
		var dir = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsByCategoryId&id=2";
		APIAction(dir,q);
	},2000);
}

function floads(){
	LoaderEvents();
	FavFix();
	CheckSuccess();
    SearchKeyUpSet();

    CarrouselLoad();

    $('#howtobuy').on('click', function() {
      window.location = 'comocomprar.html';
    });

    $('#search').on('click', function() {
      window.location = 'search_page.html?search=' + $('#search_key').val();
    });

    $('#fav-link').on('click', function(){
        Error('Esta herramienta aún no ha sido implementada. Disculpe las molestias.');
    });

    $('#cart-link').on('click', function(){
      if (!isLogged()){
        Error('Es necesario estar autenticado para acceder al Carrito.');
      } else {
        location.href = 'cart.html';
      }
    });

    loadLoginLogoutInterface();

    loadAllCategories(['women', 'men', 'girls', 'boys', 'babies']);
}

function FavFix(){
	if (isLogged()){
      var fav_q = localStorage.getItem('countItem');
      if (fav_q == null || fav_q == "null")
        localStorage.setItem('countItem', 0);
	}
}

function LoaderEvents(){
	InjectLoader();
	$( document ).ajaxStart(function() {
		KillLoader();
		InjectLoader();
	});

	$( document ).ajaxStop(function() {
		KillLoader();
	});
}

function SearchKeyUpSet(){;
	$( "#search_key" ).keydown(function(event) {
  		if (event.which == 13){
  			event.preventDefault();
  			$('#search').click();
  		}
	});
}

