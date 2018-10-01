/**
 * https://gist.githubusercontent.com/klepikov/5457750/raw/ecedc6dd4eed82f318db91adb923627716fb6b58/Test.java?_sm_au_=iNVPN112QWqSQnFF
 * https://www.quora.com/How-do-I-capture-network-traffic-using-Selenium-WebDriver
 * 
 */


package com.mvnboiler.selenium;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import org.json.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;

import org.openqa.selenium.chrome.ChromeDriver;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

public class HttpTracker {
  public static final String HttpResultDir = "httptrack";
  private static final HttpTracker instance = new HttpTracker();
  public BrowserMobProxy proxy;
  private WebDriver driver;

  public static HttpTracker getInstance() {
    return instance;
  }

  public void testGoogleSearch(ChromeDriver driver) {
    WebElement element = driver.findElement(By.name("q"));
    element.sendKeys("Selenium Conference 2013");
    element.submit();
  }

  public DesiredCapabilities proxySetup() {
	   // start the proxy
     proxy = new BrowserMobProxyServer();
     proxy.start(0);

     //get the Selenium proxy object - org.openqa.selenium.Proxy;
     Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

     // configure it as a desired capability
     DesiredCapabilities capabilities = new DesiredCapabilities();
     capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

     return capabilities;
  }

  public void proxyCaps() {
      // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
      proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

      // create a new HAR with the label "seleniumeasy.com"
      proxy.newHar("seleniumeasy");
  }

  public void writeHarLog() {
		// get the HAR data
		Har har = proxy.getHar();

		// Write HAR Data in a File
    File harFile = new File("harresult.json");
    try {
			har.writeTo(harFile);
		} catch (IOException ex) {
			 System.out.println (ex.toString());
		     System.out.println("Could not find file ");
		}
  }

  public DesiredCapabilities setUp() {
    DesiredCapabilities caps = DesiredCapabilities.chrome();
    
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.BROWSER, Level.ALL);
    logPrefs.enable(LogType.PERFORMANCE, Level.INFO);

    caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

    Map<String, Object> perfLogPrefs = new HashMap<String, Object>();
    //perfLogPrefs.put("traceCategories", "browser,devtools.timeline,devtools"); // comma-separated trace categories
    perfLogPrefs.put("enableNetwork", true);
    ChromeOptions options = new ChromeOptions();
    options.setExperimentalOption("perfLoggingPrefs", perfLogPrefs);
    caps.setCapability(ChromeOptions.CAPABILITY, options);
    
    return caps;
  }

  public Map printLog(String type, ChromeDriver driver) {
    List<LogEntry> entries = driver.manage().logs().get(type).getAll();
    System.out.println(entries.size() + " " + type + " log entries found");
    
    String result = "[";
    int size = entries.size();
    for (LogEntry entry : entries) {
      int i = entries.indexOf(entry);
      String comma = ((size - 1) == i) ? "" : ",";
      result = result + entry.getMessage() +  comma;
    }
    result = result +"]";
    JSONArray results = new JSONArray(result);
    JSONObject resultByRequestId = new JSONObject("{}");

    for (int i = 0; i < results.length(); i++) {
      JSONObject resultItem = results.getJSONObject(i);
      JSONObject message = resultItem.getJSONObject("message");
      if( message.getString("method").contains("Network") ) {
        JSONObject params = message.getJSONObject("params");
        String requestId = params.getString("requestId");
        
        if( resultByRequestId.has(requestId) ) {
          JSONArray httpStatues = resultByRequestId.getJSONArray(requestId);
          httpStatues.put(message);
          resultByRequestId.put(requestId, httpStatues);
        } else {
          JSONArray httpStatues = new JSONArray();
          httpStatues.put(message);
          resultByRequestId.put(requestId, httpStatues);
        }
      }      
    }
    JSONArray xhrRequestOnly = new JSONArray();
    for (int j =0; j < resultByRequestId.names().length(); j++) {
      String requestId = resultByRequestId.names().getString(j);
      JSONArray fullRequest = resultByRequestId.getJSONArray(requestId);

      for (int k = 0; k < fullRequest.length(); k++) {
        try {
          JSONObject oneRequest = fullRequest.getJSONObject(k);
          JSONObject params = oneRequest.getJSONObject("params");
          if( params.has("type") && params.getString("type").equals("XHR") ) {
            xhrRequestOnly.put(fullRequest);
          }
        } catch( JSONException e ) {
          // no need 
          System.out.println("<OK>");
        }
      }
    }
    
    Map<String, String> data = new HashMap<String, String>();
    data.put("xhrs", xhrRequestOnly.toString());
    data.put("raw", result.toString());
    data.put("network", resultByRequestId.toString());


    return data;
  }
}