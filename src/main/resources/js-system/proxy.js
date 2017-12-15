function(request) {
  var bodyAsString = request["input"].readAll();
  if (bodyAsString === "") {
    return {
      status: 200,
      headers: {"Content-Type":"application/json"},
      body: ['{"error":{"status":200, "message": ""}}']
    };
  }
  //var params = dc.util.queryParse(bodyAsString);
  var params = JSON.parse(bodyAsString);
  var service = params.service;
  if (typeof(service) === 'undefined') {
    return {
      status: 500,
      headers: {"Content-Type": "application/json"},
      body: ['{"error":{"status":500, "message": "service not defined"}}']
    };
  }

  var parts = service.split('/');
  var target = parts[0] + '/' + parts[1] + '/' + parts[2] + '/';


  var dcx = {sports: {HTTP: {}}};
  var __a = new Packages.io.personium.client.PersoniumContext(pjvm.getBaseUrl(), pjvm.getCellName(), pjvm.getBoxSchema(), pjvm.getBoxName()).withToken(null);
  dcx.sports.HTTP._ra = Packages.io.personium.client.http.RestAdapterFactory.create(__a);

  var formatRes = function(dcr) {
    var resp = {body: "" + dcr.bodyAsString(), status: dcr.getStatusCode(), headers: {}};
    return resp;
  }

  dcx.sports.HTTP.get = function(url, headers) {
    if (!headers) {
      headers = {"Accept": "text/plain"};
    }
    var dcr = dcx.sports.HTTP._ra.get(url, dc.util.obj2javaJson(headers), null);
    return formatRes(dcr);
  };

  dcx.sports.HTTP.post = function(url, body, contentType, headers) {
    if (!headers) {
      headers = {"Accept": "text/plain"};
    }
    var dcr = dcx.sports.HTTP._ra.post(url, dc.util.obj2javaJson(headers), body, contentType);
    return formatRes(dcr);
  };

  // get TransCellToken
  var token = _p.as('serviceSubject').cell(target).getToken();

  // exec script
  var urlE = service;
  var bodyE = bodyAsString;

  var contentTypeE = "application/json";
  var headersE = {
    "Authorization": "Bearer " + token.access_token,
  };

  apiRes = dcx.sports.HTTP.post(urlE, bodyE, contentTypeE, headersE);
  if (apiRes === null || apiRes.status !== 200) {
    return {
      status: apiRes.status,
      headers: {"Content-Type": "application/json"},
      body: ['{"error": {"status": ' + apiRes.status + ', "message": "API call failed."}}']
    };
  }

  return {
    status: 200,
    headers: {"Content-Type":"text/html"},
    body: [apiRes.body]
  };
}

