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

  // change Type
  //   External: true -> prepend relay.external.
  //   External: false -> prepend relay.
  var type = params.Type;
  if (type.indexOf("relay.") !== 0) {
    var external = params.External;
    if (external) {
      type = "relay.ext." + type;
    } else {
      type = "relay." + type;
    }
    params.Type = type;
  }

  // delete unused key. TargetUrl, External, Schema, Subject
  delete params.TargetUrl;
  delete params.External;
  delete params.Schema;
  delete params.Subject;
  
  // relay event
  var apiRes = _p.as('client').cell(service).event.post(params);
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

