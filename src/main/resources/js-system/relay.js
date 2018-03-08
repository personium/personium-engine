function(request) {
  var bodyAsString = request["input"].readAll();
  if (bodyAsString === "") {
    return {
      status: 200,
      headers: {"Content-Type":"application/json"},
      body: ['{"error":{"status":200, "message": ""}}']
    };
  }
  var params = JSON.parse(bodyAsString);
  var service = params.TargetUrl;
  if (typeof(service) === 'undefined') {
    return {
      status: 500,
      headers: {"Content-Type": "application/json"},
      body: ['{"error":{"status":500, "message": "TargetUrl is not defined."}}']
    };
  }

  var parts = service.split('/');
  var len = parts.length;
  if (len < 7) {
    return {
      status: 500,
      headers: {"Content-Type": "application/json"},
      body: ['{"error":{"status":500, "message": "TargetUrl is invalid."}}']
    };
  }
  var svc = parts[len-1];
  var col = parts[len-2];
  var box = parts[len-3];
  var target = parts[0];
  for (var i = 1; i < len-3; i++ ) {
    target += '/' + parts[i];
  }

  // exec script
  var bodyE = bodyAsString;
  var contentTypeE = "application/json";

  var apiRes = _p.as('client').cell(target).box(box).service(col).call(svc, bodyE, contentTypeE);
  if (apiRes === null || apiRes.getStatusCode() !== 200) {
    return {
      status: apiRes.getStatusCode(),
      headers: {"Content-Type": "application/json"},
      body: ['{"error": {"status": ' + apiRes.getStatusCode() + ', "message": "API call failed."}}']
    };
  }

  return {
    status: 200,
    headers: {"Content-Type":"text/html"},
    body: [apiRes.bodyAsString()]
  };
}

