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

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Log
public class App {

	private static final String YAHOO_FANTASY_FOOTBALL_URL = "https://football.fantasysports.yahoo.com/f1";
	private static final String EDIT_SETTINGS_CONTEXT = "editleaguesettings";

	public static void main(String[] args) throws Exception {
		String waiverRuleValue = args[0];
		if (waiverRuleValue == null || !Set.of("all","continuous").contains(waiverRuleValue)) {
			throw new IllegalArgumentException("expecting waiver rule value argument of either \"all\" or \"continuous\"");
		}
		updateWaiverRule(Configuration.getInstance(), waiverRuleValue);
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
