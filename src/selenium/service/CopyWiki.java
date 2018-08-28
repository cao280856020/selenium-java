package selenium.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import selenium.util.SeleniumUtil;

public class CopyWiki {
	
	private static SeleniumUtil util=new SeleniumUtil();
	
	private static WebDriver driver;
	
	//showdoc用户名密码
	private final static String SHOW_USERNAME="xxx";
	private final static String SHOW_PASSWORD="yyy";
	//gitlab用户名密码
	private final static String GIT_USERNAME="xxx";
	private final static String GIT_PASSWORD="yyy";
	
	private final static String TITLE_PRE="接口文档/1.0/";
	
	//读取需要操作的目标文章
	private List<String> readTargetUrl() throws IOException{
		return FileUtils.readLines(new File("C:/Users/win10/Desktop/linux/selenium.txt"));
	}
	
	//打开showdoc页面 并并填写用户名密码登录
	private void loginShowDoc(){
		//登录目标网站
		util.Start("https://www.showdoc.cc/home/user/login");
		
		//填写登录名
		util.SendKeys(By.xpath("//*[@id=\"app\"]/div/section/div/div/form/div[1]/div/div/input"), SHOW_USERNAME);
		//填写密码
		util.SendKeys(By.xpath("//*[@id=\"app\"]/div/section/div/div/form/div[2]/div/div/input"), SHOW_PASSWORD);
		//点击登录按钮
		driver.findElement(By.xpath("//*[@id=\"app\"]/div/section/div/div/form/div[3]/div/button")).click();
		
		//等待加载页面，因为有时会没等到登录成功返回的报文
		util.Sleep(1000);
		
	}
	
	/**
	 * 获取showdoc的数据
	 * @param url:获取数据目标url
	 * 
	 * @return dataMap:@param
	 * 						title:标题
	 * 						group:分组名
	 */
	private Map<String,String> getShowDocData(String targetUrl){
		
		//打开要编辑的页面
		util.Start(targetUrl);
		
		//等待加载出来数据后操作，如果文章数据多就设定长点时间
		util.Sleep(2500);
		
		//获取标题   分组名
		String title=driver.findElement(By.xpath("//*[@id=\"app\"]/div/section/div[1]/form/div[1]/div/div/input")).getAttribute("value");
		String group=driver.findElement(By.xpath("//*[@id=\"app\"]/div/section/div[1]/form/div[3]/div/div/div[1]/input")).getAttribute("value");
		
		//获取到文本框后  由于showdoc有jquery支持所以我用jquery脚本,但是如果没有jquery支持的网页需要用原生js来执行脚本
		util.ScriptString("$(\".CodeMirror-code\").focus()");
		
		//全选编辑框
		util.AllTextKeys();
		//复制markdown文章
		util.CopyKeys();
		
		//设定返回值
		Map<String,String> dataMap=new HashMap<>();
		dataMap.put("title", title);
		dataMap.put("group", group);
		return dataMap;
	}
	
	
	//登录gitlab 并授权登录
	private void loginGitLab(){
		//打开gitlab登录页面
		util.Start("https://gitlab.yiqishanyuan.cn/users/sign_in");
		
		//登录操作 填充用户名，密码
		util.SendKeys(By.xpath("//*[@id=\"user_login\"]"),GIT_USERNAME);
		util.SendKeys(By.xpath("//*[@id=\"user_password\"]"),GIT_PASSWORD);
		driver.findElement(By.xpath("//*[@id=\"new_user\"]/div[4]/input")).click();
		
		//等待加载页面
		util.Sleep(500);
	}
	
	/**
	 * 写新的文章
	 * @param title:标题
	 * @param group:分组
	 */
	private void writGitLab(String title,String group){
		//打开gitlab新建页面
		util.Start("https://gitlab.yiqishanyuan.cn/v3/welfare-center/wikis/home");

		//新建页面
		driver.findElement(By.xpath("//*[@id=\"content-body\"]/div[1]/div[2]/a[1]")).click();
		
		//设定新文章标题
		String newTitle=TITLE_PRE+group+"/"+title;
		util.SendKeys(By.xpath("//*[@id=\"new_wiki_path\"]"),newTitle);
		
		//创建页面
		driver.findElement(By.xpath("//*[@id=\"modal-new-wiki\"]/div/div/div[2]/form/div[2]/button")).click();
		
		//获取到文本框后
		util.ScriptString("$(\"#wiki_content\").focus()");
		//全选编辑框
		util.AllTextKeys();
		//粘贴文字
		util.PasteKeys();
		
		//点击保存
		util.ScriptString("$(\"input[name='commit']\").click()");
		
		//等待保存页面
		util.Sleep(1000);
	}
	
	public static void main(String[] args) throws IOException {
		
		CopyWiki copyWiki=new CopyWiki();
		
		//1.读取之前需要拷贝的url地址
		List<String> targetUrls=copyWiki.readTargetUrl();
		if(targetUrls!=null && targetUrls.size()>0){
			//初始化chrome驱动插件执行具体的自动化操作
			util.Init(3, "C:/Program Files (x86)/Google/Chrome/Application/chromedriver.exe");
			//获取驱动
			driver=util.getWebDriver();
			
			//登录showdoc
			copyWiki.loginShowDoc();
			
			//设定gitlab登录状态
			copyWiki.loginGitLab();
			
			//循环遍历
			for(String targetUrl:targetUrls){
				//获取目标数据
				Map<String,String> dataMap=copyWiki.getShowDocData(targetUrl);
				
				if(dataMap!=null && dataMap.size()>0){
					//新建文章，写入拷贝的数据
					copyWiki.writGitLab(dataMap.get("title"), dataMap.get("group"));
					
				}
			}
			//读取后结束
			util.Stop();
		}
	}
}
