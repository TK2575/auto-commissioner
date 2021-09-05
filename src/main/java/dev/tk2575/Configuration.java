package dev.tk2575;

import lombok.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Configuration {

	private static Configuration instance;

	private String username;
	private String password;
	private String leagueId;
	private String seleniumUrl;

	public static Configuration getInstance() throws IOException {
		if (instance == null) {
			instance = readFromFile();
		}
		return instance;
	}

	private static Configuration readFromFile() throws IOException {
		Properties properties = new Properties();
		File configFile = new File(System.getProperty("user.dir"), "/conf/config.properties");
		try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
			properties.load(reader);
		}

		return Configuration.builder()
				.username(properties.getProperty("username"))
				.password(properties.getProperty("password"))
				.leagueId(properties.getProperty("leagueId"))
				.seleniumUrl(properties.getProperty("seleniumUrl"))
				.build();
	}
}