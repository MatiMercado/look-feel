function set_cookie (cookie_name, cookie_value, lifespan, valid_domain)
{
  var domain_string = valid_domain ? ("; domain=" + valid_domain) : '' ;
  document.cookie = cookie_name + "=" + encodeURIComponent(cookie_value) +
      "; max-age=" + lifespan +
      "; path=/" + domain_string;
}

function get_cookie(name)
  {
    var re = new RegExp(name + "=([^;]+)");
    var value = re.exec(document.cookie);
    return (value != null) ? unescape(value[1]) : null;
  }

function delete_cookie(name) {
    /*document.cookie = name + '=;max-age=0;expires=Thu, 01 Jan 1970 00:00:01 GMT;';*/
    set_cookie(name, "", -1);
};