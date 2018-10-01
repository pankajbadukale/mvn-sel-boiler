package com.mvnboiler.selenium;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.Console;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.text.NumberFormat;

import org.openqa.selenium.logging.LogEntry;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.logging.LogType;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.apache.commons.lang3.time.StopWatch;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.NoSuchElementException;

import net.lightbody.bmp.proxy.CaptureType;
import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pbadkule<pankaj.badkule@elliemae.com>
 * Date: 7/30/18
 * Wrapper Class which provide code bridge between selenium and code readability
 */
public class Frontend {
    //public Logger logger = Logger.getLogger(Frontend.class);  
    private String appName = "EncompassNG_";    
    //private TestBase testBase;
    //private SeleniumResultProcessor seleniumResultProcessor;
    private StopWatch pageLoad = new StopWatch();
    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final Frontend instance = new Frontend();
    HttpTracker httptrack;
    Constants constant;
    public String scenarioname;
    public String state;
    public enum eleToBe {
        VISIBLE,
        INVISIBLE,
        CLICKABLE
    };
    public enum searchBy {
        CLASS,
        ID,
        CSS,
        XPATH
    }
    public enum uiTag {
        div,
        ul,
        li,
        lable
    }

    // font colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Frontend() {
        /*String filePath = new File("").getAbsolutePath();
        NG_Constants.FILE_JsName = getSaltString()+".js";
        NG_Constants.LINK_JsName = NG_Constants.LINK_JsName+NG_Constants.FILE_JsName;
        fileWithDirectoryAssurance(filePath+File.separator+NG_Constants.assetTmpDIR, NG_Constants.FILE_JsName);
        fileWithDirectoryAssurance(filePath+File.separator+NG_Constants.assetTmpDIR, NG_Constants.FILE_JsName);
        NG_Constants.PATH_AssetJS = filePath+File.separator+NG_Constants.assetTmpDIR+File.separator+NG_Constants.FILE_JsName;
        testBase = TestBase.getInstance();
        seleniumResultProcessor = testBase.getSeleniumResultProcessor();
        driver = testBase.getDriver();
        wait = new WebDriverWait(driver, 45);*/
        WebDriverManager.chromedriver().setup();
        httptrack = HttpTracker.getInstance();
        DesiredCapabilities caps = httptrack.setUp();
        driver = new ChromeDriver(caps);
        wait = new WebDriverWait(driver, 45);
    }

    public Frontend printLog() {
        //tmp
        //httptrack.testGoogleSearch(driver);
        Map log = httptrack.printLog(LogType.PERFORMANCE, driver);
        deleteFile(HttpTracker.HttpResultDir, "demo.json");
        fileWithDirectoryAssurance(HttpTracker.HttpResultDir, "demo.json", log.get("xhrs").toString());
        
        deleteFile(HttpTracker.HttpResultDir, "rawdata.txt");
        fileWithDirectoryAssurance(HttpTracker.HttpResultDir, "rawdata.txt", log.get("raw").toString());

        deleteFile(HttpTracker.HttpResultDir, "network.txt");
        fileWithDirectoryAssurance(HttpTracker.HttpResultDir, "network.txt", log.get("network").toString());
        return this;
    }

    public Frontend open(String url) {
        driver.get(url);
        return this;
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

	private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(directory + File.separator + filename);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("function a() { alert('pankaj') }");
            writer.close();
        } catch( IOException ex) {
        }
        
        return file;
    }

	private static File fileWithDirectoryAssurance(String directory, String filename, String fileContent) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(directory + File.separator + filename);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(fileContent);
            writer.close();
        } catch( IOException ex) {
        }
        
        return file;
    }

    private boolean deleteFile(String directory, String filename) {
        boolean deleted = false;
        try {
            File file = new File(directory + File.separator + filename);
            deleted = Files.deleteIfExists(file.toPath());
        } catch(IOException e ) {

        }
        return deleted;
    }

    //close
    public Frontend close() {
        driver.close();
        driver.quit();
        return this;
    }

    //go to url with get request
    public Frontend gotoURL(String url, String lookFor) {
        //seleniumResultProcessor.executeGetCall(driver, url, getElementRef(lookFor));
        return this;
    }

    //manage window size
    public Frontend windowMax() {
        driver.manage().window().maximize();
        return this;
    }

    // process har file
    public Frontend processHarEntry(String message1, String message2) {
        /*if (testBase.withProxy) {
            testBase.processHarEntry(logMessage(message1), logMessage(message1), message2);
        }*/
        return this;
    }

    public Frontend gotoTab(String pageTitle) {
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        /*tabs.forEach(tab -> {
            driver.switchTo().window(tab);
            String currentPageTitle = driver.getTitle();
            if( currentPageTitle == pageTitle ) {
                return;
            }
        });*/
        return this;
    }

    public Frontend DragnDrop(String from, String to) {		
        //WebElement From=driver.findElement(By.xpath("//*[@id='credit2']/a"));	
        WebElement From = getElement(from);
        //Element on which need to drop.		
        //WebElement To=driver.findElement(By.xpath("//*[@id='bank']/li"));					
        WebElement To = getElement(to);
        //Using Action class for drag and drop.		
        Actions act = new Actions(driver);					
	    //Dragged and dropped.		
        act.dragAndDrop(From, To).build().perform();	
        return this;	
	}
    
    // execute js 
    public String exeJS(String content, boolean returnMe) {
        JavascriptExecutor js = (JavascriptExecutor)driver;         
        if(returnMe) {
            return (String) js.executeScript(content);
        }
        js.executeScript(content);
        return "";
    }

    // execute js 
    public Frontend exeJS(String content) {
        JavascriptExecutor js = (JavascriptExecutor)driver;         
        js.executeScript(content);
        return this;
    }

    //setter
    public Frontend setScenarioname(){
        return this;
    }

    //setter
    public Frontend setState() {
        return this;
    }

    // common web elements
    public WebElement getBody() {
        return getElement("body");
    }

    // hightlight ele
    public Frontend highlight(String selector) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", getElement(selector),
                    "color: navy; border: 8px solid navy;");
            holdOnFor(500);
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", getElement(selector), "");

        }
        return this;
    }

    public Frontend logTime() {
        if(pageLoad.isStopped())
        pageLoad.start();
        return this;
    }

    public Frontend endLog(String message) {
        //seleniumResultProcessor.stopTimer(logMessage(message));
        if(pageLoad.isStarted())
        pageLoad.stop();

        long responsetime = pageLoad.getTime();
        double time = ((double) responsetime / 1000);
        //captureUsableResponsetime(message, responsetime);
        pageLoad.reset();
        return this;
    }

    public String MemInfo() {
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        sb.append("\n");
        sb.append(ANSI_CYAN+"Free memory: "+ANSI_RESET);
        sb.append("\n");
        sb.append(ANSI_CYAN+"Allocated memory: "+ANSI_RESET);
        sb.append(format.format(allocatedMemory / 1024));
        sb.append("\n");
        sb.append(ANSI_CYAN+"Max memory: "+ANSI_RESET);
        sb.append(format.format(maxMemory / 1024));
        sb.append("\n");
        sb.append(ANSI_CYAN+"Total free memory: "+ANSI_RESET);
        sb.append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
        sb.append("\n");
        //logger.info(sb.toString()); 
        return (format.format(allocatedMemory / (1024*1024)));

    }

    // wait till ele available
    public Frontend waitFor(String selector) {
        d("Waiting For => "+selector);
        //seleniumResultProcessor.waitForElementtoVisible(driver, getElementRef(selector));
        d(selector+" Available now");
        return this;
    }

    // wait till ele available
    public Frontend waitFor(String selector, eleToBe state) {
        d("Waiting For => "+selector);
        if( state == eleToBe.INVISIBLE ) {
            wait.until(ExpectedConditions.invisibilityOf(getElement(selector)));
            d(selector+" invisible now");
        } else if( state == eleToBe.VISIBLE ) {
            wait.until(ExpectedConditions.visibilityOf(getElement(selector)));
            d(selector+" visible now");
        } else if( state == eleToBe.CLICKABLE ) {
            wait.until(ExpectedConditions.elementToBeClickable(getElementRef(selector)));
        }
        
        return this;
    }

    //wait till ele available and then stop
    public Frontend waitForAndStop(String selector, String message) {
        d("Waiting For => "+selector);
        //seleniumResultProcessor.waitForElementtobeInVisibleWithStop(driver, getElement(selector), logMessage(message));
        d(selector+" Available now");
        return this;
    }

    //upload file 
    public Frontend uploadFile(String selector, String filePath) {
        WebElement fileInput = getElement(selector);
        fileInput.sendKeys(filePath);
        return this;
    }

    public Frontend holdOnFor(int time) throws InterruptedException {
        Thread.sleep(time);
        return this;
    }

    // operform click on element
    public Frontend click(String selector) throws InterruptedException {
        waitFor(selector, eleToBe.VISIBLE);
        waitFor(selector, eleToBe.CLICKABLE);
        highlight(selector);
        //seleniumResultProcessor.executeClickOperation(driver, getElement(selector));
        c("CLICK EVENT ON =>"+selector);
        return this;
    }

    public Frontend click(WebElement ele) throws InterruptedException {
        //seleniumResultProcessor.executeClickOperation(driver, ele);
        c("CLICK EVENT ON =>"+ele.toString());
        return this;
    }

    // provide class instance
    public static Frontend getInstance() {
        return instance;
    }

    // set value for element
    public Frontend setValue(String selector, String value) {
        getElement(selector).sendKeys(value);
        return this;
    }

    // set value for element
    public Frontend setValue(String selector, String value, String tagName) {
        if( tagName == "SELECT" ) {
            Select dropdown = new Select(getElement(selector));
            dropdown.selectByValue("string:"+value);
        }
        return this;
    }

    public boolean pageHas(String text) {
        return driver.getPageSource().contains(text);
    }

    //closest  , uiTag tag
    public WebElement closest(searchBy searchby, String selector, WebElement child) {
        //String selectorText  = "/ancestor::div[contains(concat(' ', @class, ' '), ' "+selector+" ')][1]"; // default
        String selectorText  = "/ancestor-or-self::div[contains(@class,'"+selector+"')]"; // default
        if(searchby == searchBy.ID) {

        }
        return child.findElement(getElementRef(selectorText)); // parent
    }

    // search for element
    public WebElement findEle(String search) {
        return  driver.findElement(getElementRef(search));
    }

    //find in element
    public WebElement findEle(String search, WebElement container) {
        return  container.findElement(getElementRef(search));
    }

    //find in element
    public List<WebElement> findEles(String search) {
        return driver.findElements(getElementRef(search));
    }

    //find in element
    public List<WebElement> findEles(String search, WebElement container) {
        return container.findElements(getElementRef(search));
    }

    // get web element
    public WebElement getElement(String selector) {
        int selType = selectorType(selector);
        return getElementEle(selType, selector);
    }

    //get element as instance of BY
    public By getElementRef(String selector) {
        int selType = selectorType(selector);
        return getElementEleRef(selType, selector);
    }

    // clear element values
    public Frontend clear(String selectors[]) {
        for(int i =0; i < selectors.length; i++) {
            getElement(selectors[i]).clear();
        }
        return this;
    }

    public Frontend clear(String selectors) {
        getElement(selectors).clear();
        return this;
    }

    private WebElement getElementEle(int selType, String selector) {
        return driver.findElement(getElementEleRef(selType, selector));
    }

    private By getElementEleRef(int selType, String selector) {
        By ele;
        switch(selType) {
            case 1:
                selector = selector.replace("#", "");
                ele = By.id(selector);
                break;
            case 2:
                selector = selector.replace(".", "");
                ele = By.className(selector);
                break;
            case 3: 
                ele = By.tagName(selector);
                break;
            case 4: 
                ele = By.name(selector);
                break;
            case 5: 
                ele = By.xpath(selector);
                break;
            case 6:
            selector = selector.replace(">", "");
                ele = By.linkText(selector);
                break;
            
            default:
                ele = By.cssSelector(selector);
                break;

        }
        i("Element Accessed => "+ele.toString());
        return ele;
    }

    private int selectorType(String selector) {
        char zeroPos = selector.charAt(0);
        char matchToId = '#';
        char matchToClass = '.';
        char matchToXPath = '/';
        char matchToLinkText = '>';
        if(zeroPos == matchToId) return hasMultiple(matchToId, selector) ? 0 : 1;
        else if(zeroPos == matchToClass) return hasMultiple(matchToClass, selector) ? 0 : 2;
        else if(zeroPos == matchToXPath) return 5;
        else if(zeroPos == matchToLinkText) return 6;
        else return 0;
    }

    private boolean hasMultiple(char ch, String str) {
        String find = String.valueOf(ch);
        if (str.contains(find) && str.indexOf(ch) != str.lastIndexOf(ch)) {
            return true;
        }
        return false;
    }

    public String logMessage(String msg) {
        return msg;
    }

    public Frontend setLogger(Class className) {
        //logger = Logger.getLogger(className);
        return this;
    }

    public Frontend logIt(String text) {
        i(text);
        return this;
    }

    //info
    public Frontend i(String message) {
        //logger.info(ANSI_GREEN  + " : " + message + ANSI_RESET);
        return this;
    }

    //error
    public Frontend e(String message) {
        //logger.error(ANSI_RED  + " : " + message + ANSI_RESET);
        return this;
    }

    //debug
    public Frontend d(String message) {
        //logger.debug(ANSI_BLUE  + " : " + message + ANSI_RESET);
        return this;
    }

    //warning
    public Frontend w(String message) {
        //logger.warn(ANSI_YELLOW  + " : " + message + ANSI_RESET);
        return this;
    }

    //clickevent
    public Frontend c(String message) {
        //logger.warn(ANSI_PURPLE  + " : " + message + ANSI_RESET);
        return this;
    }
}
