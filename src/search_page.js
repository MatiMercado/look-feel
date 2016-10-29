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
		loadPageTitle(values);
		loadFilters(arr, searchMethod, values);
		reloadPage();
	}

	var url = getUrl(values);

	APIAction(url, lambda);
}

function adaptSearchMethod(searchMethod, arr, values) {
	if (searchMethod == "menu") {
		return;
	}
	var search = getKeyItems(arr, values.search);
	/* always returning valid elements */
	arr.products = search.products;
	arr.total = search.total;
	arr.price = search.price;
}

function getKeyItems(obj, key){
	var lkey = key.toLowerCase();

	var products = obj["products"];
	var new_products = [];

	var max = 0;
	var min = 99999;

	for (var i = 0; i < products.length; i++){
		var p = products[i];
		if (p.name.toLowerCase().indexOf(lkey) >= 0){
			new_products.push(p);

			if (p.price > max)
				max = p.price;

			if (p.price < min)
				min = p.price;
		}
	}

	return {"products": new_products, "total": new_products.length, "price": {"min": min, "max": max}};
}

function loadPageTitle(values) {
	var pageTitle = $(".page-title");

	var breadcrumbs_list = $(".breadcrumb");
	var breadcrumb_item = $("<li></li>");
	var breadcrumb_link = $("<a href='#'></a>");

	var title = "";
	if("search" in values) {
		title += "Resultados de búsqueda";
		pageTitle.append("<strong>"+title+"</strong>");

		var subtitle = values.search; /* +++xtodo: validate */
		var subtitle_searchText = $("<p>\""+subtitle+"\"</p>");
		subtitle_searchText.attr('class', 'page-subtitle');
		subtitle_searchText.css('color', '#808080');
		pageTitle.after(subtitle_searchText);

		breadcrumb_item.text(title+": \""+subtitle+"\"");
		breadcrumb_item.attr('class', 'active');
		breadcrumbs_list.append(breadcrumb_item);

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
				var a = arr["subcategory"];
				var mainCategory = title;

				title += " - " + a.category.name; /* +++xtodo: validate not undefined value at a.name */
				title += " - " + a.name; /* +++xtodo: validate not undefined value at a.name */
				pageTitle.append("<strong>"+title+"</strong>");

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
				var breadcrumb_link = $("<a href='#'></a>");
				breadcrumb_item.text(a.name);
				breadcrumb_item.attr('class', 'active');
				breadcrumbs_list.append(breadcrumb_item);
			}

			var url="http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetSubcategoryById&id="+values.subcategory; /* +++xtodo: validate not undefined value at category */
			APIAction(url, lambda);
		} else if ("category" in values) {
			var lambda = function(arr) {
				var a = arr["category"];
				var mainCategory = title;

				title += " - " + a.name; /* +++xtodo: validate not undefined value at a.name */
				pageTitle.append("<strong>"+title+"</strong>");


				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='search_page.html?name="+values.name+"'></a>");
				breadcrumb_link.text(mainCategory);
				breadcrumb_item.append(breadcrumb_link);
				breadcrumbs_list.append(breadcrumb_item);

				var breadcrumb_item = $("<li></li>");
				var breadcrumb_link = $("<a href='#'></a>");
				breadcrumb_item.text(a.name);
				breadcrumb_item.attr('class', 'active');
				breadcrumbs_list.append(breadcrumb_item);
			}
			var url="http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetCategoryById&id="+values.category; /* +++xtodo: validate not undefined value at category */
			APIAction(url, lambda);
		} else {
			pageTitle.append("<strong>"+title+"</strong>");

			var breadcrumb_item = $("<li></li>");
			var breadcrumb_link = $("<a href='#'></a>");
			breadcrumb_item.text(title);
			breadcrumb_item.attr('class', 'active');
			breadcrumbs_list.append(breadcrumb_item);
		}
	}
}

/* Main Functions */

function loadPagination(arr, values) {
	var pageNumber = values.pageNumber;

	var totalArticles = arr["total"]; /* +++xchange */

	if (totalArticles == 0) {
		/* +++xtodo: no hay productos para mostrar */
		return;
	}

	var totalPages = Math.ceil(totalArticles/PAGE_SIZE);
	var firstPageArticle = (pageNumber-1)*PAGE_SIZE+1;
	var lastPageArticle = pageNumber*PAGE_SIZE > totalArticles ? totalArticles : pageNumber*PAGE_SIZE;


	/* Pagination text */
	var pagination_list = $("ul[name=pagination_list]");
	pagination_list.append	(
								"<li>Página "+pageNumber+" de "+totalPages+"</li>"+
								"<li>Mostrando artículos "+firstPageArticle+"-"+lastPageArticle+"</li>"+
								"<li>Total artículos: "+totalArticles+"</li>"
							);
	/*******************/

	/* Pagination menu */

	var pagination_menu_list = $("<ul class='pagination pagination-sm pagination_own pull-right'></ul>");
	
	li = $("<li></li>");
	a = $("<a></<a>");

	a.append("<span aria-hidden='true'>&laquo;</span>");
	a.attr("href", "#");
	a.attr("aria-label","Previous");
	a.attr("name", "pagination_prev");

	li.append(a);

	$("[id=left_arrow]").append(	"<a href='#' id='left_arrow_img' class='arrow'>" + 
										"<img src='./img/larrow.png' alt='Previous Search Page' class='btn btn-lg img-responsive'/>" +
									"</a>");
	if (pageNumber == 1) { /* +++xmagicnumber */
		li.css('visibility', 'hidden');
		$("[id=left_arrow]").css('visibility', 'hidden');
	} else {
		li.css('visibility', 'visible');
		$("[id=left_arrow]").css('visibility', 'visible');
	}
	pagination_menu_list.append(li);

	var li;
	var a;

	var iPrintable = -1;
	for (var i = 0 ; i < totalPages ; i++) {
		iPrintable = i+1;

		li = $("<li></li>");
		a = $("<a></<a>");

		a.text(iPrintable);
		a.attr("name", "pagination_page_"+iPrintable);

		if (iPrintable == pageNumber) {
			li.attr("class", "active");
		} else {
			a.attr("href", "#");
			loadPaginationRedirection(a, parseInt(iPrintable), values);
		}

		li.append(a);
		pagination_menu_list.append(li);
	}

	li = $("<li></li>");
	a = $("<a></<a>");

	a.append("<span aria-hidden='true'>&raquo;</span>");
	a.attr("href", "#");
	a.attr("aria-label","Next");
	a.attr("name", "pagination_next");

	li.append(a);

	$("[id=right_arrow]").append(	"<a href='#' id='right_arrow_img' class='arrow'>" + 
										"<img src='./img/rarrow.png' alt='Next Search Page' class='btn btn-lg img-responsive'/>" +
									"</a>");
	if (pageNumber == totalPages) {
		li.css('visibility', 'hidden');
		$("[id=right_arrow]").css('visibility', 'hidden');
	} else {
		li.css('visibility', 'visible');
		$("[id=right_arrow]").css('visibility', 'visible');
	}
	pagination_menu_list.append(li);

	var pagination_menu = $("nav[name=pagination_menu]");
	pagination_menu.append(pagination_menu_list);	
	/*******************/
	loadPaginationRedirection($("[name=pagination_prev]"), parseInt(values.pageNumber)-1, values);
	loadPaginationRedirection($("[id=left_arrow_img]"), parseInt(values.pageNumber)-1, values);
	loadPaginationRedirection($("[name=pagination_next]"), parseInt(values.pageNumber)+1, values);
	loadPaginationRedirection($("[id=right_arrow_img]"), parseInt(values.pageNumber)+1, values);
}

/* +++ximprove: hay que hacerlo como lo de la aplicación de los filtros, para que solo recarge paginacion y productos */
function loadPaginationRedirection(element, pageNumber, values) {
	element.on("click", function() {
		reloadPage(pageNumber);
		/*var path = "search_page.html?";
		if ("name" in values) {
			path += "name="+values.name;
			if ("category" in values) {
				path += "&category="+values.category;
			}
			if ("subcategory" in values) {
				path += "&subcategory="+values.subcategory;
			}
		} else {
			path += "search="+values.search;
		}

		if (values.filters !== undefined) {
			path += "&filters="+values.filters;
		}
		path += "&pageNumber="+pageNumber;
		path += "&page_size="+values.page_size;

		window.location = path; */
	});
}

function loadFilters(arr, searchMethod, values) {
	var a = arr["filters"];
	/* +++xTOADD */
	if (a == undefined) {
		return;
	}
	/*************/
	var filter_list = $("<ul class='no-bullets'></ul>");

	/* +++xdebug */

	var filter;

	var id;
	var name;
	var values;

	/* +++xtochange */
	/* Ordenar por */
	id = "Ordenar";
	name = "Ordenar Por";
	filter = 	$(
					"<button class='btn btn-primary' type='button' id='collapse_"+id+"'>"+
						name+
					"</button>"
 				);
	/* ************* */

	$('.order_by').append(filter);

	values = [	"Nombre (de la 'A' a la 'Z')", 
				"Nombre (de la 'Z' a la 'A')", 
				"Marca (de la 'A' a la 'Z')", 
				"Marca (de la 'Z' a la 'A')", 
				"Menor Precio", 
				"Mayor Precio",
				"Sin Orden"];

/* +++xtochange */
	loadFilterValues(id, name, values);
/******************/

	var loadFullFilters = "subcategory" in values ? true : false;

	/* list of mandatory filter's ids */
	var mandatoryFilters = [1, 2, 3, 4, 5, 6, 9];
	/* Genero, Edad, Ocasion, Color, Oferta, Nuevo, Marca, respectivamente */

	for (var i = 0 ; i < a.length ; i++) {

		id = a[i].id;
		name = a[i].name;
		values = a[i].values;

		if (!loadFullFilters && mandatoryFilters.indexOf(id) < 0) {
			continue;
		}

		if ((id == 1 || id == 2) && searchMethod == "menu") { /* Si genero o edad (respectivamente) y busqueda por menu, no poner esos filtros*/
			continue;
		}

		filter = $(
						"<li class='col-xs-4 col-md-2 centered'>"+
							"<button class='btn btn-primary' type='button' id='collapse_"+id+"'>"+
								name+
							"</button>"+
						"</li>"
					);
		filter_list.append(filter);
		

		if (values.length > 0) {
			loadFilterValues(id, name, values);
		} 
	}

	var price = arr["price"]; /* +++xtodo: considerar que pasa si no devuelve precio */
	
	name = "Precio";

	filter = 	$(
					"<li class='col-xs-4 col-md-2 centered'>"+
						"<button class='btn btn-primary' type='button' id='collapse_"+name+"'>"+
							name+
						"</button>"+
					"</li>"
				);
	filter_list.append(filter);

	/* Precio */
	values = [];
	var space = (price.max-price.min+1)/10; /* +++xmagicnumber */
	var min;
	var max;

	for (var i = price.min ; i < price.max ; i+=space) {
		min = Math.ceil(i);
		max = Math.floor(i+space); /* +++xtodo: fix borders */

		values.push("$" + min.toString() + " - $" + max.toString());
	}


	if (values.length > 0) {
		loadFilterValues(name, name, values);
	} 


	$("#filter_list").append(filter_list);

	loadBehaviour(values);
}

function loadProducts(arr, pageNumber, priceParams, values) {

	var a = arr["products"];

	var out = $("<div class='row row_container_div_product_search'></div>");

/* +++XTOADD */
	if (a === undefined) {
		var row = $(
					"<div class='row row_container_div_product_search'>" +
						"<div class='col-xs-12'>" +
							"<h2 class='page-subtitle'>" +
								"<em>No hay productos que cumplan con los criterios de búsqueda.</em>" +
							"</h2>" +
						"</div>" +
					"</div>"
				);

		out.append(a);
		$("#products_grid").append(out);
		return 0;
	}
/**************/

	var product;
	var id;
	var name;
	var price;
	var img;

	var totalArticles = 0;
	var insertedArticles = 0;
	var filterByPrice = typeof priceParams !== 'undefined' && priceParams.length > 0;
	var productPassedPriceFilter;

	var row = null;

	for (var i = 0 ; i < a.length ; i++) {
		price = a[i].price;
		/* price filter */
		if (filterByPrice) {
			productPassedPriceFilter = false;
			for (var j = 0 ; j < priceParams.length ; j++) {
				if (price >= priceParams[j].min && price <= priceParams[j].max) {
					productPassedPriceFilter = true;
					continue;
				}
			}
			if (!productPassedPriceFilter) {
				continue;
			} else {
				totalArticles++;
			}
		} else {
			totalArticles++;
		}
		/* end of price filter */
	}


	var firstPageArticle = (pageNumber-1)*PAGE_SIZE +1;
	var lastPageArticle = pageNumber*PAGE_SIZE > arr["total"] ? arr["total"] : pageNumber*PAGE_SIZE;

	/* var lastPageLastArticle = firstPageArticle == 0 ? 0 : firstPageArticle -1; */

	var i = 0;
	for (i = 0 ; i < arr["total"] && insertedArticles < firstPageArticle -1 ; i++) { /* el ultimo elemento de la pagina anterior es el inmediato al primero de esta */ 

		price = a[i].price;
		if (filterByPrice) {
			productPassedPriceFilter = false;
			for (var j = 0 ; j < priceParams.length ; j++) {
				if (price >= priceParams[j].min && price <= priceParams[j].max) {
					productPassedPriceFilter = true;
					continue;
				}
			}
			if (!productPassedPriceFilter) {
				continue;
			} else {
				insertedArticles++;
			}
		} else {
			insertedArticles++;
		}

	}

	var insertedOnCurrentPage = 0;
	/* +++xtochange */
	var marca;

	for ( ; i < parseInt(arr["total"]) && insertedOnCurrentPage < PAGE_SIZE; i++) {
		name = a[i].name;
		price = a[i].price;
		marca = "Sin Marca";
		for (var j = 0 ; j < a[i].attributes.length ; j++) {	
			if (a[i].attributes[j].id == 9) /* id de marca */ {
				if (a[i].attributes[j].values === undefined || a[i].attributes[j].values.length == 0) {
					break;
				}
				marca = a[i].attributes[j].values[0];
				if (marca === undefined) {
					marca = "Sin Marca";
				}

				break;
			}
		}
		/*******************/


		// if (insertedArticles >= PAGE_SIZE) {
		// 	if (filterByPrice) {
		// 		continue; /* have to test the other products price */
		// 	} else {
		// 		break;
		// 	}
		// }
/*
		if (insertedArticles % 4 == 0) {
			if (row != null) {
				out.append(row);
			}
			row = $("<div class='row row_container_div_product_search'></div>");
		}*/

/*		if (i % 4 == 0) {
			if (row != null) {
				out.append(row);
			}
			row = $("<div class='row row_container_div_product_search'></div>");
		}*/

		// insertedArticles++;

		if (insertedOnCurrentPage % 4 == 0) {
			if (row != null) {
				out.append(row);
			}
			row = $("<div class='row row_container_div_product_search'></div>");
		}

		if (filterByPrice) {
			productPassedPriceFilter = false;
			for (var j = 0 ; j < priceParams.length ; j++) {
				if (price >= priceParams[j].min && price <= priceParams[j].max) {
					productPassedPriceFilter = true;
					continue;
				}
			}
			if (!productPassedPriceFilter) {
				continue;
			} else {
				insertedOnCurrentPage++;
				insertedArticles++;
			}
		} else {
			insertedOnCurrentPage++;
			insertedArticles++;
		}

		if (a[i].imageUrl == null) {
			img = './img/img_not_available.png';
		} else {
			img = a[i].imageUrl[0];
		}

		product = $(
						"<div class='col-xs-12 col-sm-6 col-md-3'>" +
							"<div class='row hovereffect product_item'>" +
								"<div class='col-xs-6 img_div_product_search'>"+
									"<img class='img_product_search img-responsive' src='" + img +"' alt='"+name+"'/>"+
								"</div>"+
								"<div class='col-xs-6 verticalLine'>"+
									"<div class='row row_container_div_product_search'>"+
										"<h3 class='col-xs-12 product_title_product_search'>"+
											"<strong>"+ name + "</strong>"+
										"</h3>"+
										/* +++xtochange */
										"<label class='col-xs-12 product_description'>"+
											"Marca: "+ marca + ""+
										"</label>"+
										/**********/
										"<label class='col-xs-12 product_description_product_search product_price'>"+
										/* +++xtochange */
											"$"+ price + ""+
										/**********/
										"</label>"+
									"</div>"+
								"</div>"+			
								"<div class='overlay'>"+
									"<a class='info clickeable'></a>"+
								"</div>"+
							"</div>"+
						"</div>"
					);
		row.append(product);


		(function() {
			var prod = a[i];
			/* +++xtodo */
			product.on("click",function(){
				var category = prod.category === undefined ? undefined : prod.category.id;
				var subcategory = prod.subcategory === undefined ? undefined : prod.subcategory.id;
				if("search" in values) {
					window.location = "detalle_producto.html?productId="+prod.id+"&search="+values.search;
				} else if ("name" in values) {
					if ("subcategory" in values) {
						window.location = "detalle_producto.html?productId="+prod.id+"&name="+values.name+"&category="+values.category+"&subcategory="+values.subcategory;
					} else if ("category" in values) {
						window.location = "detalle_producto.html?productId="+prod.id+"&name="+values.name+"&category="+values.category;
					} else {
						window.location = "detalle_producto.html?productId="+prod.id+"&name="+values.name;
					}
				}
			});
		})();

	}
	if (totalArticles == 0) {
		row = $(
					"<div class='row row_container_div_product_search'>" +
						"<div class='col-xs-12'>" +
							"<h2 class='page-subtitle'>" +
								"<em>No hay productos que cumplan con los criterios de búsqueda.</em>" +
							"</h2>" +
						"</div>" +
					"</div>"
				);
	}

	if (row != null) {
		out.append(row);
	}

	$("#products_grid").append(out);

	return totalArticles;
}


function loadFilterValues(id, name, values) {

	var filterValues="";
	var type;
	
	if (id == "Ordenar") {
		type = "radio";
	} else {
		type = 'checkbox';
	}

	for (var i = 0 ; i < values.length ; i++) {
		filterValues +=	"<li class='col-xs-6 col-md-4 column_filter'>"+
							"<label class='filter_label'><input type='"+type+"' name='"+name+"' data-id='"+id+"' data-name='"+name+"' value=''>"+values[i]+"</label>"+
						"</li>";
	}

	var collapse = $(
						"<div class='col-xs-2'>"+
						"</div>"+
						"<div class='col-xs-8'>"+
							"<div class='collapse' id='accordion_collapse_"+id+"'>"+
								"<div class='well'>"+
									"<div class='row'>"+
										"<div class='col-xs-2'>"+
										"</div>"+
										"<div class='col-xs-8 center'>"+
											"<div class='row'>"+
												"<div class='col-xs-12'>"+
													"<p class='page-subtitle centered'>"+name+"</p>"+
												"</div>"+
												"<div class='col-xs-3'></div>"+
												"<div class='col-xs-6 centered'>"+
													"<button type='button' class='btn btn-danger btn-md centered cancel_filter'>"+
														"Cerrar"+
													"</button>"+
												"</div>"+
												"<div class='col-xs-3'></div>"+
											"</div>"+
											"<div class='row'>"+
												"<div class='col-xs-1'>"+
												"</div>"+
												"<div class='col-xs-10'>"+
													"<div class='input-group filter_search_box'>"+
														"<input id='filterBox_"+id+"' type='text' class='form-control' placeholder='Buscar filtro'>"+
													"</div>"+
												"</div>"+
												"<div class='col-xs-1'>"+
												"</div>"+
											"</div>"+
										"</div>"+
										"<div class='col-xs-2'>"+
										"</div>"+
										"<div class='col-xs-12'>"+
											"<div class='row'>"+
												"<ul id='filterList_"+id+"' class='no-bullets column_filter'>"+
													filterValues+
												"</ul>"+
											"</div>"+
										"</div>"+
										"<div class='col-xs-2'>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>"+
						"</div>"+
						"<div class='col-xs-2'>"+
						"</div>"
					);
	
	/* +++XTOCHANGE */
	if (id == "Ordenar") {
		$('#order_by_collapsed').append(collapse);
	} else {
		$('#filter_collapsed').append(collapse);
	}
	/* ******************** */

	$("#filterBox_"+id).keyup(function () {
	    var valThis = this.value.toLowerCase();

		$("#filterList_"+id+">li").each(function () {
			/* +++xdebug */
			var text  = $(this).text();
			var textL = text.toLowerCase();
			(textL.indexOf(valThis) >= 0) ? $(this).show() : $(this).hide();
		});
	});
}

function loadBehaviour(values) {
	loadCollapseBehaviour();
	loadCancelFilterBehaviour();
	loadFiltersBehaviour();
}

/* +++xdebug */
function loadError() {
}

function loadCollapseBehaviour() {
	var IDs = $("[id^='collapse_']").map(function() { return this.id; }).get();

	var s = "";
	var s2 = "";

	for(var i = 0 ; i < IDs.length ; i++){
	   s='#' + IDs[i];
	   
	   $(s).on("click",function() {
	        var s = $(this).attr('id');
	        var s2 = "";

	        for (var i = 0 ; i < IDs.length ; i++) {
	        	s2='#accordion_' + IDs[i];
	  			$(s2).collapse('hide');
	        }
	        s2 = '#accordion_' + s;
	        $(s2).collapse('show');
	   });
	}
}

function loadCancelFilterBehaviour() {
	var IDs = $("[id^='collapse_']").map(function() { 
		return this.id; 
	}).get();
	$(".cancel_filter").on("click",function() {
	        var s2 = "";
	        for (var i = 0 ; i < IDs.length ; i++) {
	        	s2='#accordion_' + IDs[i];
	  			$(s2).collapse('hide');
	        }
	});
}
/*
	(function() {
		var localName = name;
		applyFilter = function() {
			var filterList = $("[id='filterList_"+localName+"']");			
			var labels = $("label");
			filterLabels = filterList.find(labels);
			$.each(filterLabels, function() {
				if ($(this).children().prop('checked')) {
					console.log($(this).text());
					console.log(localName);
				}
			});
		}
	})();
*/

/*
	On change:
		Pasar por todos los filtros --> armar la estructura de filtros
		Pasar por la estructura de "Ordenar" y con eso armar el filtro "sort"
		Pasar por la estructura "Precio" y con eso armar el filtro del precio

		Con esas tres estructuras armadas, concatenarlas con la url del sitio,
		y ademas concatenarle name, category, subcategory, search, según corresponda.
		Luego hacer una request con esa url, y un lambda que adentro tenga las siguientes funciones:
			eliminar pagination
			eliminar products
			recargar products --> acá voy a ir aplicando el filtro del precio que me hayan seteado en la vez anterior.
			recargar pagination --> en base al filtrado de precio anterior, volver a setear la cantidad de productos totales
				(que serán aquellos que cumplan con el filtrado), y guardar esa variable en "values".
 */
// console.log($(this).children().attr('data-id')); /* filter id */
// console.log($(this).children().attr('data-name'));  filter name 
// console.log($(this).text());	/* filter value */
// console.log($(this).children().is(':checked')); /* filter status */
// 
function loadFiltersBehaviour() {
	var filterList = $("[id^='filterList_']");
	var labels = $("label");
	filterLabels = filterList.find(labels);

	$.each(filterLabels, function() {
		$(this).on('change', reloadPage);
	});
}

function reloadPage(pageNumber) {


	var orderParams = loadOrderParams(); /* have to check if undefined */
	var filterParams = loadFilterParams(); /* have to check if empty */
	var priceParams = loadPriceParams(); /* have to check if empty */

	var values = getQueryValues();
	values.pageNumber = 1;
	if (orderParams !== undefined) {
		values.sort_key = orderParams.sort_key;
		values.sort_order = orderParams.sort_order;
	}

	if (values.completeFilters === undefined) {
		values.completeFilters = [];
	}
	if (filterParams.completeFilters.length > 0) {
		for (var i = 0 ; i < filterParams.completeFilters.length ; i++) {
			values.completeFilters.push(filterParams.completeFilters[i]);					
		}
	}
	deleteAppliedFilters();
	addOrderFilter(filterParams.tagFilters, orderParams);
	addPriceFilters(filterParams.tagFilters, priceParams.tagFilters);
	loadAppliedFilters(filterParams.tagFilters); /* +++xtodo: deleteAppliedFilters */

	var searchMethod;
	if ("name" in values) {
		searchMethod = "menu";
	} else {
		searchMethod = "searchBox"
	}


	var lambda = function(arr) {

		var lambda = function(arr) {
			deleteProducts();
			deletePagination();
			pageNumber = $.isNumeric(pageNumber) ? pageNumber : 1;
			adaptSearchMethod(searchMethod, arr, values);

			var totalProducts = loadProducts(arr, pageNumber, priceParams.filters, values); 
			arr.total = totalProducts;
			values.pageNumber = pageNumber;
			loadPagination(arr, values);
		}
		
		values.page_size = arr["total"];

		var url = getUrl(values);

		APIAction(url, lambda);
	}

	var url = getUrl(values);

	APIAction(url, lambda);
}

function addOrderFilter(tagFilters, orderFilter) {
	if (orderFilter === undefined) {
		return;
	}

	var order = {
		"id": orderFilter.id,
		"name": orderFilter.name,
		"value": orderFilter.value
	};

	tagFilters.push(order);
}

function addPriceFilters(tagFilters, priceFilters) {
	if (priceFilters.length == 0) {
		return;
	}

	for (var i = 0 ; i < priceFilters.length ; i++) {
		tagFilters.push(priceFilters[i]);
	}
}

/*

<div class="row row_container_div_product_search">
	<div class="col-md-2">
		<div class="row row_container_div_product_search">
			<div class="col-xs-12">
				<p><strong class="filters_applied filters_applied_title">Filtros Aplicados: </strong></p>			
			</div>
			<div class="col-xs-6 col-md-12">
				<p class="filter_applied filters_applied">
					Eliminar filtros
					<a id='filters_applied' class='btn'>
						<img src="./img/cross.png" alt="Remove Filter" class="filter_applied_remove_icon pull-right clickeable"/>
					</a>
				</p>
			</div>
		</div>
	</div>
	
	<div class="col-md-10">
		<div class="row row_container_div_product_search">
			<ul id="filters_applied" class="filters_applied">
				<li class="col-xs-4 col-md-2 filter_applied">
					Color - Gris
					<a href="search_page_1.html">
						<img src="./img/cross.png" alt="Remove Filter" class="filter_applied_remove_icon pull-right"/>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>

 */

function loadAppliedFilters(filterTags) {
	if (filterTags.length == 0) {
		return;
	}
	
	var deleteFilters = $(
							"<div class='col-md-2'>" + 
								"<div class='row row_container_div_product_search'>" +
									"<div class='col-xs-12'>" +
										"<p><strong class='filters_applied filters_applied_title'>Filtros Aplicados: </strong></p>" +			
									"</div>" +
									"<div class='col-xs-6 col-md-12'>" +
										"<p class='filter_applied filters_applied'>" +
											"Eliminar filtros" +
											"<a id='filters_applied' class='clickeable'>" +
												"<img src='./img/cross.png' alt='Eliminar Filtros' class='filter_applied_remove_icon pull-right'/>" +
											"</a>" +
										"</p>" +
									"</div>" +
								"</div>" +
							"</div>"
						);
	

	var filters_applied = $(deleteFilters.find("[id=filters_applied]").get());
	filters_applied.on('click', function() {
		var filterList = $("[id^='filterList_']");
		var labels = $("label");
		filterLabels = filterList.find(labels);

		for(var j = 0 ; j < filterLabels.length ; j++) {
			filterItem = $(filterLabels[j]);
			filterItem.children().prop('checked',false);
		}

		reloadPage();
	});



	/*******************/
	var filtersApplied = 	$(
								"<ul id='filters_applied' class='filters_applied'>" +
								"</ul>"
							);


	for (var i = 0 ; i < filterTags.length ; i++) {
		var removeImg = 	$(
								"<a>" +
									"<img src='./img/cross.png' alt='Eliminar Filtro' class='filter_applied_remove_icon pull-right clickeable'/>" +
								"</a>"
							);
		var filterTag = filterTags[i];

		(function() {
			var localfilter = filterTag;
			removeImg.on('click', function() {
				var filterBox = $("[id=filterList_"+ localfilter.id + "]");
				var filterItems = filterBox.find("label").get();
				for(var j = 0 ; j < filterItems.length ; j++) {
					filterItem = $(filterItems[j]);
					if (filterItem.text() == localfilter.value) {
						filterItem.children().prop('checked',false);
						reloadPage();
					}
				}
			});
		})();
		var filterItem = 	$(
									"<li class='col-xs-4 col-md-2 filter_applied'>" +
										filterTag.name + " - " + filterTag.value +
									"</li>"
							);
		filterItem.append(removeImg);
		filtersApplied.append(filterItem);	
	}

	var filterList = 	$(
							"<div class='row row_container_div_product_search'>" +  
							"</div>"
						);
	filterList.append(filtersApplied);

	var filterListColumn = $(
						"<div class='col-md-10'>" + 
						"</div>"
					);

	filterListColumn.append(filterList);

	var containerRow = $(
							"<div class='row row_container_div_product_search'>" +
							"</div>"
						);

	containerRow.append(deleteFilters);
	containerRow.append(filterListColumn);

	$("[id=filters_applied_container]").append(containerRow);
}

function deleteAppliedFilters() {
	$("[id=filters_applied_container]").children().remove();
}

function deleteProducts() {
	$("#products_grid").children().remove();
}

function deletePagination() {
	$("ul[name=pagination_list]").children().remove();
	$("nav[name=pagination_menu]").children().remove();
	$("[id=right_arrow]").children().remove();
	$("[id=left_arrow]").children().remove();
}

function loadOrderParams() {
	var dictionary = {
		"Nombre (de la 'A' a la 'Z')": {
			"sort_key": "nombre",
			"sort_order": "asc"
		},
		"Nombre (de la 'Z' a la 'A')": {
			"sort_key": "nombre",
			"sort_order": "desc"
		},
		"Marca (de la 'A' a la 'Z')": {
			"sort_key": "marca",
			"sort_order": "asc"			
		},
		"Marca (de la 'Z' a la 'A')": {
			"sort_key": "marca",
			"sort_order": "desc"			
		},
		"Menor Precio": {
			"sort_key": "precio",
			"sort_order": "asc"
		},
		"Mayor Precio": {
			"sort_key": "precio",
			"sort_order": "desc"
		}
	};

	var filterList = $("[id=filterList_Ordenar]");
	var orderParams;
	var labels = $("label");
	filterLabels = filterList.find(labels);
	$.each(filterLabels, function() {
		var sort_key;
		var sort_order;

		if ($(this).children().is(':checked')) {
			if ($(this).text() in dictionary) {
				orderParams = {
					"id": "Ordenar",
					/* +++xtochange */
					"name": "Ordenar Por",
					/***********************/
					"value": $(this).text(),
					"sort_key": dictionary[$(this).text()]["sort_key"],
					"sort_order": dictionary[$(this).text()]["sort_order"]
				}
			}
		}
	});

	return orderParams;
}

function loadFilterParams() {
	var filterList = $("[id^='filterList_']");
	var labels = $("label");
	filterLabels = filterList.find(labels);
	var filterParams = {
		"completeFilters" : [],
		"tagFilters" : []
	};

	$.each(filterLabels, function() {
		var filter_id;
		var filter_name;
		var filter_value;
		var completeFilter = {};
		var tagFilter = {};

		if (	$(this).children().is(':checked') 
				&& $(this).children().attr('data-id') != "Ordenar"
				&& $(this).children().attr('data-id') != "Precio") {
			filter_id = $(this).children().attr('data-id');
			filter_name = $(this).children().attr('data-name');
			filter_value = $(this).text();

			completeFilter.id = parseInt(filter_id);
			completeFilter.value = filter_value;

			tagFilter.id = filter_id;
			tagFilter.name = filter_name;
			tagFilter.value = filter_value;

			filterParams.completeFilters.push(completeFilter);
			filterParams.tagFilters.push(tagFilter);
		}
	});

	return filterParams;
}

function loadPriceParams() {
	var filterList = $("[id=filterList_Precio]");
	var labels = $("label");
	filterLabels = filterList.find(labels);
	var priceParams = {
		"filters": [],
		"tagFilters": []
	};
	$.each(filterLabels, function() {
		var price_min;
		var price_max;
		var filter_value;
		var tagFilter = {};
		var price = {};

		if ($(this).children().is(':checked')) {
			filter_value = $(this).text();
			prices = filter_value.split("-");
			price_min = parseFloat(prices[0].trim().replace("$", ""));
			price_max = parseFloat(prices[1].trim().replace("$", ""));

			price.min = price_min;
			price.max = price_max;
			
			priceParams.filters.push(price);

			tagFilter.id = "Precio";
			tagFilter.name = "Precio";
			tagFilter.value = $(this).text();
			priceParams.tagFilters.push(tagFilter);
		}
	});

	return priceParams;
}

function SearchKeyPersistency(){
	var search_presence = location.search.indexOf('search=');
	
	if (search_presence >= 0){
		var st = location.search.substr(search_presence);
		st = st.split('=');
		st = st[1];

		$('#search_key').val(decodeURIComponent(st));
	}
}
