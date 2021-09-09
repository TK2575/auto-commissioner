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

	public static Configuration getInstance() throws Exception {
		if (instance == null) {
			instance = readFromFile();
		}
		return instance;
	}

	private static Configuration readFromFile() throws Exception {
		Properties properties = new Properties();
		File configFile = new File(System.getProperty("user.dir"), "/conf/config.properties");
		try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
			properties.load(reader);
		}

		String secret = properties.getProperty("secret");
		if (secret == null || secret.isBlank()) {
			throw new IllegalArgumentException("could not find secret in config.properties");
		}

		return Configuration.builder()
				.username(properties.getProperty("username"))
				.password(PasswordProtector.decrypt(properties.getProperty("password"), secret))
				.leagueId(properties.getProperty("leagueId"))
				.seleniumUrl(properties.getProperty("seleniumUrl"))
				.build();
	}
}