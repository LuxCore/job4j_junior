package ru.job4j.sqljdbc.jdbc;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDemo {
	private static final String APP_PROPERTIES = "src/main/resources/app.properties";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Properties properties = new Properties();
		try (FileReader reader = new FileReader(APP_PROPERTIES)) {
			properties.load(reader);
		} catch (IOException e) {
			throw new RuntimeException("Файл '%s' не найден.".formatted(APP_PROPERTIES));
		}
		Class.forName(properties.getProperty("db.driver"));
		String dbUrl = properties.getProperty("db.url");
		String dbLogin = properties.getProperty("db.login");
		String dbPassword = properties.getProperty("db.password");
		try (Connection connection = DriverManager.getConnection(dbUrl, dbLogin, dbPassword)) {
			DatabaseMetaData dbMetaData = connection.getMetaData();
			System.out.println(dbMetaData.getUserName());
			System.out.println(dbMetaData.getURL());
		}
	}
}
