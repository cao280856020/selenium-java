package selenium.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumUtil {
	private static WebDriver webDriver;
	private static WebElement webElement;
	private static Robot robot;
	private static By byelement;
	
	static {
		try {
			robot=new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	// 在驱动里执行javascript的脚本
	public void Script(String script, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;

		if (element == null) {
			js.executeScript(script);
		} else {
			js.executeScript(script, element);
		}
	}

	// 通过执行javascript脚本后 返回数据
	public String ScriptString(String script) {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;

		String s = (String) js.executeScript(script);
		return s;
	}

	/**
	 * 初始化
	 * 
	 * @param select
	 *            1:Firefox 2:IE 3:Chrome 4:后台执行
	 * @param browserpath
	 *            选择本地浏览路径
	 */
	public void Init(int switchbrowser, String browserpath) {
		switch (switchbrowser) {
		case 1:
			System.setProperty("webdriver.firefox.bin", browserpath);
			webDriver = new FirefoxDriver();
			webDriver.manage().window().maximize();
			break;
		case 2:
			System.setProperty("webdriver.ie.driver", browserpath);
			webDriver = new InternetExplorerDriver();
			webDriver.manage().window().maximize();
			break;
		case 3:
			System.setProperty("webdriver.chrome.driver", browserpath);
			webDriver = new ChromeDriver();
//			webDriver.manage().window().maximize();
			break;
		case 4:
			webDriver = new HtmlUnitDriver();
			break;
		}
	}

	// 复制
	public static void CopyKeys() {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_C);
	}
	
	// 全选
	public static void AllTextKeys() {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_A);
	}

	// 粘贴
	public static void PasteKeys() {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_V);
	}

	/**
	 * 拖拽
	 * 
	 * @param by
	 *            要多拽的元素
	 * @param target
	 *            目的地
	 */
	protected void DragAndDrop(By by, By target) {
		(new Actions(webDriver)).dragAndDrop(webDriver.findElement(by), webDriver.findElement(target)).perform();
	}

	// 传入值
	public static void SendKeys(By by, String value) {
		webDriver.findElement(by).sendKeys(value);
	}

	/**
	 * 截图
	 * 
	 * @param outpath
	 *            导出截图后的路径 文件名是以当前时间
	 */
	public void GetScreen(String outpath) {
		File screenShotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenShotFile, new File(outpath + "/" + getSysTime() + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取元素
	 * 
	 * @param by
	 * @param second
	 *            查找需要时间
	 */
	public static WebElement Wait(final By by, int second) {
		WebDriverWait wait = new WebDriverWait(webDriver, second);
		return wait.until(new ExpectedCondition<WebElement>() {

			public WebElement apply(WebDriver d) {
				return d.findElement(by);
			}
		});
	}

	// 打开浏览器
	public void Start(String url) {
		webDriver.get(url);
	}

	// 结束
	public void Stop() {
		webDriver.quit();
	}

	// 获取当前时间
	public static String getSysTime() {
		SimpleDateFormat filename = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return filename.format(new Date());
	}

	// 睡眠
	public void Sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static WebDriver getWebDriver() {
		return webDriver;
	}

	public static void setWebDriver(WebDriver webDriver) {
		SeleniumUtil.webDriver = webDriver;
	}

	public static WebElement getWebElement() {
		return webElement;
	}

	public static void setWebElement(WebElement webElement) {
		SeleniumUtil.webElement = webElement;
	}

	public static By getByelement() {
		return byelement;
	}

	public static void setByelement(By byelement) {
		SeleniumUtil.byelement = byelement;
	}
}
