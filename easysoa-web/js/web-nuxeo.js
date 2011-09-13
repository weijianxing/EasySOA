/* 
 * Provides functions to communicate with the Nuxeo server.
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('base64');

eval(fs.readFileSync('./settings.js', 'ASCII'));

var nuxeoReady = false;
var nuxeoNotification = url.parse(settings.nuxeoNotification);
var nuxeoAutomation = url.parse(settings.nuxeoAutomation);

// INTERNAL FUNCTIONS

computeAuthorization = function(username, password) {
    if (username != null && password != null) {
        return "Basic " + base64.encode(username + ':' + password);
    }
    else {
        return null;
    }
}

sendNotification = function(nuxeoUploadOptions, body, callback) {

    console.log(nuxeoUploadOptions);

      restRequest = http.request(nuxeoUploadOptions, function(restResponse) {
	
          // Nuxeo response handling
          var data = "";
          restResponse.on('data', function(chunk) {
            data += chunk.toString("ascii");
          });
          restResponse.on('end', function() {
            try {
                callback(JSON.parse(data));
            }
            catch (error) {
                callback({result: error});
            }
          });
          
      });
      
      restRequest.addListener('error', function(error) {
        console.log("[WARN] Failure while sending REST request to Nuxeo "+error);
        callback({result: error});
      });
      restRequest.write(body);
      restRequest.end();
    
};

// EXPORTS

/*
 *
 */
exports.isNuxeoReady = function() {
    return nuxeoReady;
}

/*
 *
 */
exports.checkNuxeo = function(username, password, callback) {


  var requestOptions = {
	  port : nuxeoAutomation.port,
	  method : 'GET',
	  host : nuxeoAutomation.hostname,
	  path : nuxeoAutomation.href,
	  headers : {
	    'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': computeAuthorization(username, password)
	  }
  };
  
  // Test request

  var nxRequest = http.request(requestOptions, function(response) {
        var responseData = "";
        response.on('data', function(data) {
              responseData += data;
          });
		response.on('end', function() {
            if (!nuxeoReady) {
                nuxeoReady = true;
            }
			callback(responseData);
		});
  });
  
  nxRequest.on('error', function(data) {
    callback('Nuxeo doesn\'t answer');
  });
	
  nxRequest.end();
	
  if (!nuxeoReady) {
	  console.log("[INFO] Nuxeo is not ready yet...");
      setTimeout(function() { 
              exports.checkNuxeo(username, password, callback) 
          }, 3000);
  }
  
};


/*
 *
 */
exports.registerWsdl = function(data, callback) {

    try {
    
      // Service notification
      
      console.log("[INFO] Registering: "+data.url);
        
      var body = 'url='+data.url+
          '&fileUrl='+data.url+
          '&title='+data.servicename+
          '&discoveryTypeBrowsing=Discovered by browsing';
      var nuxeoUploadOptions = {
	          port : nuxeoNotification.port,
	          method : 'POST',
	          host : nuxeoNotification.hostname,
	          path : nuxeoNotification.href+"service",
	          headers : {
	            'Content-Type': 'application/x-www-form-urlencoded',
	            'Content-Length': body.length,
	            'Authorization': computeAuthorization(data.session.username, data.session.password)
	          }
          };
          
      sendNotification(nuxeoUploadOptions, body, callback);
        
    }
    catch (error) {
      console.error("[ERROR] Client message badly formatted. "+error);
    }
        
}