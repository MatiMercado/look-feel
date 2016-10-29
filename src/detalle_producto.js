function getQueryVariable() {
		var request = {};
		var query = location.search.substring(1);
		var pairs = query.split('&');
		for (var i = 0; i < pairs.length; i++) {
			if(!pairs[i])
				continue;
			var pair = pairs[i].split('=');
			request[decodeURIComponent(pair[0])] = decodeURIComponent(pair[1]);
		}
		return request;
}

/* order_by example: http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsByCategoryId&id=1&sort_key=precio&sort_order=desc */
function getUrl(values) {
	var url = "";
	if ("subcategory" in values) {
		url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsBySubcategoryId&id="+values.subcategory;
	} else if ("category" in values) {
		url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsByCategoryId&id="+values.category;
	} else if ("name" in values) {
		url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllProducts";
	} else if ("search" in values) {
		url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllProducts"; /* +++xthink*/
	} else {
		/* +++xtodo: error */
		return;
	}

	url += "&page="+values.pageNumber;
	url += "&page_size="+values.page_size;
	if (values.sort_key !== undefined) {
		url += "&sort_key="+values.sort_key;
	}
	if (values.sort_order !== undefined) {
		url += "&sort_order="+values.sort_order;
	}
	if (values.completeFilters !== undefined) {
		url += "&filters="+JSON.stringify(values.completeFilters);
	}

	return url;
}

function getQueryValues() {
	var values = getQueryVariable();
	var filters = values["filters"];

	var searchMethod;

	if ("name" in values) {
		searchMethod = "menu";
		if (filters === undefined) {
			filters = [];
		} else {
			filters = JSON.parse(filters);
		}
		var nameFilters = getCatFilterById(values.name);
		nameFilters = JSON.parse(nameFilters);
		for (var i = 0 ; i < nameFilters.length ; i++) {
			filters.push(nameFilters[i]);
		}
		values.completeFilters = filters;
	} else {
		searchMethod = "searchBox"
	}
	
	PAGE_SIZE = 16;
	values.page_size = PAGE_SIZE;

	var pageNumber;
	if ("pageNumber" in values) {
		pageNumber = parseInt(values.pageNumber);
	} else {
		pageNumber = 1;
		values.pageNumber = pageNumber;
	}

	return values;
}

function loadSearchPage() {
	var values = getQueryValues();

	var searchMethod;

	if ("name" in values) {
		searchMethod = "menu";
	} else {
		searchMethod = "searchBox"
	}

	var lambda = function(arr) {
		loadPageBreadcrumbs(values);
	}

	var url = getUrl(values);
	if(url===undefined){
		return;
	}

	APIAction(url, lambda);
}

function loadPageBreadcrumbs(values) {

	var breadcrumbs_list = $(".breadcrumb");
	var breadcrumb_item = $("<li></li>");
	var breadcrumb_link = $("<a href='#'></a>");

	var title = "";
	if("search" in values) {

		var lambda = function(arr) {
			var a = arr["product"];
			var mainCategory = title;

			title += "Resultados de búsqueda";
			var subtitle = values.search; /* +++xtodo: validate */

			var breadcrumb_item = $("<li></li>");
			var breadcrumb_link = $("<a href='search_page.html?search="+values.search+"'></a>");
			breadcrumb_link.text(title+": \""+subtitle+"\"");
			breadcrumb_item.append(breadcrumb_link);
			breadcrumbs_list.append(breadcrumb_item);

			var breadcrumb_item = $("<li></li>");
			breadcrumb_item.text(a.name);
			breadcrumb_item.attr('class', 'active');
			breadcrumbs_list.append(breadcrumb_item);
		}

		var url="http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id="+values.productId; /* +++xtodo: validate not undefined value at category */
		APIAction(url, lambda);

	} else if ("name" in values) {
		switch(values.name) {
			case "men":
				title += "Hombre";
				break;
			case "women":
				title += "Mujer";
				break;
			case "boys":
				title += "Niño";
				break;
			case "girls":
				title += "Niña";
				break;
			case "babies":
				title += "Bebé";
				break;
			default:
				break; /* +++xtodo: validate */
		}
		if ("subcategory" in values) {

			var lambda = function(arr) {
				var a = arr["product"];
				var mainCategory = title;

				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"'></a>");
				breadcrumb_link.text(mainCategory);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"&category="+a.category.id+"'></a>");
				breadcrumb_link.text(a.category.name);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"&category="+a.category.id+"&subcategory="+a.subcategory.id+"'></a>");
				breadcrumb_link.text(a.subcategory.name);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				breadcrumb_item.text(a.name);
				breadcrumb_item.attr('class', 'active');
				breadcrumbs_list.append(breadcrumb_item);
			}

			var url="http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id="+values.productId; /* +++xtodo: validate not undefined value at category */
			APIAction(url, lambda);
		} else if ("category" in values) {
			var lambda = function(arr) {
				var a = arr["product"];
				var mainCategory = title;


				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"'></a>");
				breadcrumb_link.text(mainCategory);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"&category="+a.category.id+"'></a>");
				breadcrumb_link.text(a.category.name);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				breadcrumb_item.text(a.name);
				breadcrumb_item.attr('class', 'active');
				breadcrumbs_list.append(breadcrumb_item);
			}
			var url="http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id="+values.productId; /* +++xtodo: validate not undefined value at category */
			APIAction(url, lambda);
		} else {
			var lambda = function(arr) {
				var a = arr["product"];
				var mainCategory = title;

				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"'></a>");
				breadcrumb_link.text(mainCategory);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				breadcrumb_item.text(a.name);
				breadcrumb_item.attr('class', 'active');
				breadcrumbs_list.append(breadcrumb_item);
			}
			var url="http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id="+values.productId; /* +++xtodo: validate not undefined value at category */
			APIAction(url, lambda);
		}
	}
}



function getProductUrl(categoty, subCategory, prodID){
	$("#CATEGORY").text(categoty);
	$("#SUB-CATEGORY").text(subCategory);
	var url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id="+prodID;
	return url;
}

function f(arr) {
 
    var a = arr["states"];
    var out = "";
    var i;

    for(i = 0; i < a.length; i++) {
        out += a[i]["name"] + '<br>';
    }

    $('#list').append(out);

    return a;
}

function g(arr){
	var a = arr["product"];
	var i;

	$("#TITLE").text(a.name);
	$("#TITLE2").text(a.name);
	$("#TITLE3").text(a.name);
	$("#PRICE").text('$ '+a.price);
	$("#IMGX").attr("src",a.imageUrl[0]);
	for(i=0;i<a.imageUrl.length;i++){
		aux=$("<div class='col-xs-3 col-md-12 tipoZ'><div class='row row-eq-height hovereffect'><div class='col-xs-12'><img id='IMG"+i+"' src='"+a.imageUrl[i]+"'class='imagen2' alt='"+a.name+"'/></div><div class='overlay'><a class='info clickeable'></a></div></div></div>");

		(function() {
    		var gid = i;
    		aux.on('click',function(){
        		$("#IMGX").attr("src",$("#IMG"+gid).attr("src"));
    		});
		})();

		$("#FOTOS").append(aux);	 
	}
	var subCat = a["subcategory"];
	a = a["attributes"];
	var valid_color = false;
	var valid_size = false;
	var valid_brand = false;
	var valid_comp = false;

	for(i=0;i<a.length;i++){
		switch (a[i].id){
			case 4:
				for (var x = 0; x<a[i].values.length; x++) {
					$("#COLOR1").append("<li class='clickeable2'>"+a[i].values[x]+"</li>");
				};
				valid_color = true;
				break;
			case 7:
				for (var x = 0; x<a[i].values.length; x++) {
					$("#TALLE1").append("<li class='clickeable2'>"+a[i].values[x]+"</li>");
				};
				valid_size = true;
				break;
			case 8:
				$("#COMP").text("Composición: "); 
				for (var x = 0; x<a[i].values.length; x++) {
					$("#COMP").append(a[i].values[x]+" ");
				};
				valid_comp=true;
				break;
			case 9:
			valid_brand=true;
				$("#SUBTITLE1").text("Marca: "); 
				for (var x = 0; x<a[i].values.length; x++) {
					$("#SUBTITLE1").append(a[i].values[x]+" ");
				};
				$("#SUBTITLE2").text("Marca: "); 
				for (var x = 0; x<a[i].values.length; x++) {
					$("#SUBTITLE2").append(a[i].values[x]+" ");
				};


			
		}

	}

	a = arr["product"]["category"];

	$("#CATEGORY").text(a.name);

	a= arr["product"]["subcategory"];

	$("#SUB-CATEGORY").text(a.name);

	localStorage.setItem("SCID",a.id);


	if(!valid_color){
		$("#COLOR1").append("<li>No hay colores disponibles.</li>");
	}
	if(!valid_size){
		$("#TALLE1").append("<li>Talle único.</li>");
	}
	if(!valid_comp){
		$("#COMP").append("<li>No están los componentes disponibles.</li>");
	}
	if(!valid_brand){
		$("#SUBTITLE2").append("<li>No hay más información disponibles.</li>");
		$("#SUBTITLE2").append("<li>No hay más información disponibles.</li>");
	}

	var subCatID = subCat.id;
	var dir = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsBySubcategoryId&id="+subCatID
	APIAction(dir,h2);


}

function h2(arr){
	var countCarousel=0;
	var a = arr["products"];
	var w;
	for (w=0; w < a.length; w++) {
		if(w%4==0){
			countCarousel++;
			if(w==0){

				newCarousel = $("<div class='item active' id='CAROUSEL"+countCarousel+"'>"+
								"<div class='row'>"+
									"<div class='col-xs-8 col-xs-offset-2'>"+
									"</div>"+
								"</div>"+
							"</div>");

				$("#SUPER_CAROUSEL").append(newCarousel);
			}else{
				newCarousel = $("<div class='item' id='CAROUSEL"+countCarousel+"'>"+
								"<div class='row'>"+
									"<div class='col-xs-8 col-xs-offset-2'>"+
									"</div>"+
								"</div>"+
							"</div>");

				$("#SUPER_CAROUSEL").append(newCarousel);

			}
		}
		recomendado = $("<div class='col-xs-6 col-md-3'>"+
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
		$("#CAROUSEL"+countCarousel).children().children().append(recomendado);

	}

}

function addFav(id,size,quantity,color){
	var username = get_cookie("username");
	var aux = ":"+id+","+size+","+quantity+","+color;
	var toAdd = localStorage.getItem("addItems");
	if(toAdd==null){
		localStorage.setItem("addItems"+username,aux);
	}else{
		localStorage.setItem("addItems"+username,toAdd+aux);
	}
}
