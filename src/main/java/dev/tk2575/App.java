package dev.tk2575;

import lombok.*;
import lombok.extern.java.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Log
public class App {

	private static final String YAHOO_FANTASY_FOOTBALL_URL = "https://football.fantasysports.yahoo.com/f1";
	private static final String EDIT_SETTINGS_CONTEXT = "editleaguesettings";
	private static final Set<String> WAVIER_RULES = Set.of("all", "none", "suntue", "firsttue", "gametime", "continuous");

	public static void main(String[] args) throws Exception {
		if (args.length == 0 || args[0] == null) {
			throw illegalArgumentException();
		}

		String waiverRuleValue = args[0].toLowerCase();
		Configuration config = Configuration.getInstance();

		if (waiverRuleValue.equalsIgnoreCase("login")) {
			login(config);
		}
		else {
			if (!WAVIER_RULES.contains(waiverRuleValue)) {
				throw illegalArgumentException();
			}

			updateWaiverRuleWithRetries(config, waiverRuleValue.equals("none") ? "all" : waiverRuleValue);
		}
	}

	private static IllegalArgumentException illegalArgumentException() {
		return new IllegalArgumentException("expecting waiver rule value argument. Valid arguments include: " + String.join(", ", WAVIER_RULES));
	}

	private static void updateWaiverRuleWithRetries(Configuration config, String waiverRuleValue) throws Exception {
		Exception ex = null;
		for (int i = 0; i < 3; i++) {
			try {
				updateWaiverRule(config, waiverRuleValue);
				ex = null;
				break;
			}
			catch (Exception e) {
				ex = e;
				Thread.sleep(60L * 1000L); //pause for a minute
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	public static void login(@NonNull Configuration config) throws MalformedURLException, InterruptedException {
		log.info("Attempting login");
		log.info("username=" + config.getUsername());
		log.info("leagueId=" + config.getLeagueId());
		log.info("seleniumUrl=" + config.getSeleniumUrl());

		WebDriver driver = new RemoteWebDriver(new URL(config.getSeleniumUrl()), new FirefoxOptions());
		WebDriverWait wait = new WebDriverWait(driver, 10);
		String url = String.join("/", YAHOO_FANTASY_FOOTBALL_URL, config.getLeagueId());

		try {
			driver.get(url);

			wait.until(presenceOfElementLocated(By.id("login-username")));
			driver.findElement(By.id("login-username")).sendKeys(config.getUsername() + Keys.ENTER);

			wait.until(presenceOfElementLocated(By.cssSelector("#password-container")));
			driver.findElement(By.id("login-passwd")).sendKeys(config.getPassword() + Keys.ENTER);

			TimeUnit.SECONDS.sleep(10);
		}
		finally {
			driver.quit();
		}
	}

	public static void updateWaiverRule(@NonNull Configuration config, @NonNull String waiverRuleValue) throws MalformedURLException {
		log.info("Starting...");
		log.info("username=" + config.getUsername());
		log.info("leagueId=" + config.getLeagueId());
		log.info("seleniumUrl=" + config.getSeleniumUrl());
		log.info("waiverRuleValue=" + waiverRuleValue);

		WebDriver driver = new RemoteWebDriver(new URL(config.getSeleniumUrl()), new FirefoxOptions());
		WebDriverWait wait = new WebDriverWait(driver, 10);
		String url = String.join("/", YAHOO_FANTASY_FOOTBALL_URL, config.getLeagueId(), EDIT_SETTINGS_CONTEXT);
		try {
			driver.get(url);

			wait.until(presenceOfElementLocated(By.id("login-username")));
			driver.findElement(By.id("login-username")).sendKeys(config.getUsername() + Keys.ENTER);

			wait.until(presenceOfElementLocated(By.cssSelector("#password-container")));
			driver.findElement(By.id("login-passwd")).sendKeys(config.getPassword() + Keys.ENTER);

			wait.until(presenceOfElementLocated(By.id("ysf-commish-form-submit")));
			new Select(driver.findElement(By.id("waiver-rule"))).selectByValue(waiverRuleValue.toLowerCase());

			driver.findElement(By.id("ysf-commish-form-submit")).click();
		}
		finally {
			driver.quit();
		}
	}
}
