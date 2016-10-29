function compare(a, b){
	if (a.id > b.id)
		return 1;
	if (a.id < b.id)
		return -1;
	return 0;
}



function getLastOrder(lambda, ready){
	if (isLogged()){

		var allordersurl = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetAllOrders&username=' + get_cookie("username") + '&authentication_token=' + get_cookie("authenticationToken");

		APIAction(allordersurl, function(data){
			lambda(data.orders.sort(compare)[data.orders.length - 1]);
		}, ready);
	}
}

function CreateOrder(lambda, ready){
	if (lambda === undefined)
		lambda = function(data){}

	if (isLogged()){
		var courl = "http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=CreateOrder"
		courl += '&username=' + get_cookie("username");
		courl += '&authentication_token=' + get_cookie("authenticationToken");
		APIAction(courl, lambda, ready);

	} else {
		Error('Usuario no conectado.');
	}
}

function addItemToCart(itemid, quantity, ready){
	if (isLogged()){

		getLastOrder(function(last_order){
			var last_order_id = last_order.id;

			var addurl = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=AddItemToOrder';
			addurl = addurl + '&username=' + get_cookie("username");
			addurl = addurl + '&authentication_token=' + get_cookie("authenticationToken");
			addurl = addurl + '&order_item={"order":{"id":'+ last_order_id +'},"product":{"id":' + itemid + '},"quantity":' + quantity + '}';

			APIAction(addurl, function(data){}, ready);

		});
	}
}

function removeItemFromCart(additionid, ready){
	if (isLogged()){
		getLastOrder(function(last_order){
			var removeurl = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=RemoveItemFromOrder';
			removeurl += '&username=' + get_cookie("username");
			removeurl += '&authentication_token=' + get_cookie("authenticationToken");
			removeurl += '&id=' + additionid;

			APIAction(removeurl, function(data){}, ready);
		});
	}
}

function getLastOrderId(lambda){
	getLastOrder(function(last_order){
		lambda(last_order.id);
	});
}

function getLastOrderItems(lambda, ready){
	if (isLogged()){	
		getLastOrderId(function(last_order_id){
			var gburl = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetOrderById';
			gburl += '&username=' + get_cookie("username");
			gburl += '&authentication_token=' + get_cookie("authenticationToken");
			gburl += '&id=' + last_order_id;
			APIAction(gburl, function(data){
				var items = data.order.items;
				lambda(items);
			}, ready);
		});
	}
}

function CartShowProducts(ready){
	getLastOrderItems(function(items){
		if (isLogged()){
			if (items.length > 0){

				var locator = $('#locator');
				
				for (var i = 0; i < items.length; i++){
					var item = getCartObjHTMLContainer(i + 1, items[i]);
					locator.append(item);
					var my_flag = false;
					var prod;
					url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id="+items[i].product.id;
					APIAction(url,function(arr){
					prod = arr["product"];
						var a = prod["attributes"];
						var valid_color = false;
						
						var valid_brand = false;
						

						for(i=0;i<a.length;i++){
							switch (a[i].id){
								case 4:
									$("#COLOR"+prod.id).text(a[i].values[0]);
									valid_color = true;
									break;
								case 9:
									valid_brand=true;
									$("#BRANDED"+prod.id).text(a[i].values[0]); 
							
							}
						}
						if(!valid_color){
							$("#COLOR"+prod.id).text("----");
						}
						if(!valid_brand){
							$("#BRANDED"+prod.id).text("----"); 
						}
						
					});				
					CartLoadQuitButtons();
					CartCumSum();
					ready();
				}	
			} else {
				//alert("No hay items en el carrito.");
				aux=$('<h4>No hay productos agregados al carrito.</h4>');
				$("#locator").append(aux);
				$("#FC1").remove();
				ready();
			}
		}else{
			Error("Usuario no conectado.");
			ready();
		}
	});
}

function getCartLength(lambda, ready){
	getLastOrderItems(function(items){
		lambda(items.length);
	}, ready);
}

function CartLoadQuitButtons(){
	$('[data-elemid]').on('click', function(){
		var elemid = $(this).data('elemid');
		var additionid = $(this).data('additionid');
		$('#cart_listing_' + elemid).remove();
		removeItemFromCart(additionid);
		CartCumSum();
	});
}

function getCartObjHTMLContainer(index, obj)
{


	var color = "-----";
	var valid_color = false;
	try {
		img = obj.product.imageUrl;
	} catch(err){
		try {
			img = obj.product.imageUrl[0];
		} catch(err2){
			img = "img/img_not_available.png";
		}
	}

	var elArrrrrte = $('<!-- Cart Element -->'+
					'<div id="cart_listing_' + index + '">' +
					'<div class="row row-eq-height">'+
						'<!-- Number -->'+
						'<div class="col-xs-2 verticalLine centered">'+
							'<h4>' + index + '</h4>'+
						'</div>'+
						'<!-- End Number -->'+
						'<!-- Product Details -->'+
						'<div class="col-xs-7 verticalLine">'+
							'<div class="row">'+
								'<div class="col-xs-3">'+
									 '<a href="detalle_producto.html?productId='+obj.product.id+'"><img src="' + img + '" alt="'+obj.product.name+'" class="cartImg"></a>'+
								'</div>'+
								'<div class="col-xs-4 centered">'+
									'<ul class="noBullets">'+
										'<li class="page-subtitle"><a href="detalle_producto.html?productId='+obj.product.id+'"><strong>' + obj.product.name + '</strong></a></li>'+
									'</ul>'+
								'</div>'+
								'<div class="col-xs-5">'+
									'<div class="row spaced">'+
										'<div id="TITLE_BRANDED" class="col-xs-6 padding-left-aux">'+
											'<p>Marca:</p>'+
										'</div>'+
										'<div class="col-xs-12 col-md-6 centered">'+
												'<p id="BRANDED'+obj.product.id+'" ></p>'+
											'</div>'+	
									'</div>'+							
									'<div class="row spaced">'+
										'<div id="TITLE_COLOR" class="col-xs-6">'+
											'<p>Color:</p>'+
										'</div>	'+
										'<div id="PARENT_DROP_COLOR'+obj.product.id+'" class="col-xs-12 col-md-6 centered">'+
											'<p id="COLOR'+obj.product.id+'"></p>'+
										'</div>'+
									'</div>'+
									'<div class="row spaced">'+
										'<div class="col-xs-6">	'+
											'<p>Cantidad:</p>'+
										'</div>	'+
										'<div class="col-xs-12 col-md-6 centered">'+
											'<p>'+obj.quantity+'</p>'+
										'</div>'+
									'</div>'+
								'</div>'+
							'</div>'+
						'</div>'+
						'<!-- End Product Details -->'+
						'<!-- Price -->'+
						'<div class="col-xs-3 verticalLine centered">'+
							'<ul class="noBullets">'+
								'<li>Unitario: $' + obj.price + '</li>'+
								'<li data-uprice="' + obj.price + '" data-price="' + obj.price * obj.quantity + '" class="page-subtitle"> <strong>Subtotal: $' + obj.price * obj.quantity+ '</strong> </li>'+
							'</ul>'+
							'<div class="row">'+
								'<div class="col-xs-12 col-md-6 col-md-offset-3">'+
										'<button type="button" class="btn btn-sm btn-block btn-danger extra" data-elemid="' + index + '" data-additionid="' + obj.id + '">Quitar</button>'+
								'</div>'+
							'</div>'+
						'</div>'+
						'<!-- End Price -->'+
					'</div>'+
					'<hr class="hr_horizontal">'+
					'</div>' +
					'<!-- End Cart Element -->'+
					'<!-- Cart Element -->');
			

	return elArrrrrte;
}

function CartCumSum(){
	var x = $('#PRECIO1').find('strong');
	var amount = $('#NUMBER1');
	var arr = $('[data-price]');

	var quantsum = 0;
	var cumsum = 0.0;

	for (var i = 0; i < arr.length; i++){
		cumsum += parseFloat($(arr[i]).data('price'));
		quantsum += parseInt($(arr[i]).data('price')/$(arr[i]).data('uprice'));

	}

	amount.empty();
	amount.append(quantsum);
	localStorage.setItem('cart_itemamount', quantsum);
	x.empty();
	x.append('Total: $' + cumsum.toFixed(2));
	localStorage.setItem('cart_total', cumsum.toFixed(2));
	if(quantsum==0){
		$("#FC1").remove();
	}
}


function CartCopyLastOrder(ready){
	
	if (isLogged()){
		getLastOrder(function(last_order){
			getLastOrderItems(function(items){
				CreateOrder(function(order){
					/* ESTO QUEDA EN CONMEMORACION DE LOS STRINGS SIN UN + EN EL MEDIO (Gracias Harry)
					var delurl3 =￼'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=DeleteOrder';
					delurl3 += '&username=' + get_cookie('username');
					delurl3 += '&authentication_token=' get_cookie('authenticationToken');
					delurl3 += '&id=' + last_order.id;
					*/

					var pepe = 'http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=DeleteOrder';
					pepe += '&username=' + get_cookie("username");
					pepe += '&authentication_token=' + get_cookie("authenticationToken");
					pepe += '&id=' + last_order.id;


					global_pending_requests = items.length;
					
					for(var i = 0; i < items.length; i++){
						addItemToCart(items[i].product.id, items[i].quantity, function(){global_pending_requests -= 1;});
					}

					function checkFlag() {
					    if(global_pending_requests != 0) {
					    	window.setTimeout(checkFlag, 100);
					    } else {
					    	ready();
					    }
					}

					checkFlag();
				});
			});
		});
	}
}














