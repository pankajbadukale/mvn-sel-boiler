// all possible http request methods
["Page.frameResized", "Page.frameAttached", "Page.frameStartedLoading", "Page.frameNavigated", "Page.frameStoppedLoading", "Network.requestWillBeSent", "Page.frameDetached", "Network.responseReceived", "Network.dataReceived", "Network.loadingFinished", "Network.resourceChangedPriority", "Page.domContentEventFired", "Network.requestServedFromCache", "Page.loadEventFired", "Page.frameScheduledNavigation", "Page.frameClearedScheduledNavigation", "Network.loadingFailed"]


// logic
var resultByRequestId = {};
httptracktext.forEach(t => {
  if(t.message.method.indexOf('Network') > -1) {
    if(resultByRequestId[t.message.params.requestId]== undefined) resultByRequestId[t.message.params.requestId] = [t.message];
    else resultByRequestId[t.message.params.requestId].push(t.message)
  }
})

var xhrRequestOnly = [];
Object.keys(resultByRequestId).forEach(k => {
  resultByRequestId[k].map(t => t.params.type == 'XHR' ? xhrRequestOnly.push(resultByRequestId[k]) : null);
})

      for (int k = 0; k < fullRequest.length(); k++) {
        try {
          JSONObject oneRequest = fullRequest.getJSONObject(k);
          JSONObject params = oneRequest.getJSONObject("params");
          if( params.getString("type").equals("XHR") ) {
            xhrRequestOnly.put(fullRequest);
          }
        } catch( JSONException e ) {
          // no need 
          System.out.println("OK===");
        }
      }