function validate_Names(name) {
    if (!name || name.length > 80) {
        return false;
    }

    return /[a-zA-Z0-9]/i.test(name);
}

function validate_DNI(dni) {

    if (!dni) {
        return false;
    }

    //return /^([0-9]{0,3}).([0-9]{3}).([0-9]{3})$/g.test(dni);
    return /^([0-9]{4,8})$/g.test(dni);


}

function newAddress(arr){
    window.location = "profile.html";
    //window.location = "profile.html";
}


function validate_Email(email) {
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    return re.test(email);
}

function validate_Password(pass) {

    if (!pass || pass.length < 8 || pass.length > 15) {
        return false;
    }

    return /^[a-zA-Z0-9]{8,15}$/i.test(pass);

}

function validate_Username(pass) {

    if (!pass || pass.length < 6 || pass.length > 15) {
        return false;
    }

    return /^[a-zA-Z0-9]{6,15}$/i.test(pass);

}

function maxLengthValidatorConstructor(length, testEmpty) {

    return function (s) {
        if ((testEmpty && !s) || s.length > length) {
            return false;
        }

        return true;
    };

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

 function validate_cod(num){
    if(!num){
        return false;
    }
    var init = cart_number[0]+cart_number[1];
    init = init.toString();
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

function convertIntToMonth(month){
        switch(month){
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
            default:
                return "Error";              
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


global_pending_state = 1;
global_states_arr = null;

function initializeStates(){
   
    url="http://eiffel.itba.edu.ar/hci/service3/Common.groovy?method=GetAllStates";
    APIAction(url, createStatesArr);
    checkFlag();
}

function checkFlag() {
    if(global_pending_state != 0) {
       
        window.setTimeout(checkFlag, 100);
    }
    else{
    }
}

function createStatesArr(arr){
    global_states_arr = arr["states"];
    global_pending_state = 0;   
}

function convertCodeToProv(code){
    var prov;
    for(var i=0; i<global_states_arr.length; i++){
            
            if(code.localeCompare(global_states_arr[i].stateId) == 0){
                
                prov = global_states_arr[i].name;
            }
        }
    return prov;
}

function ProvToCode(prov){
    var code;
    for(var i=0; i<global_states_arr.length ; i++){
        if(prov.localeCompare(global_states_arr[i].name)){
           
            code = global_states_arr[i].stateId;
        }
    }
    return code;
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
function validate_zipCod(num){
    if(!num || num.length>10){
        return false;
    }
    return /^[0-9]+$/g.test(num);
}

function validate_phone(name) {
    if (!name || name.length > 25) {
        return false;
    }

    return /[a-zA-Z0-9]/i.test(name);
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

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}