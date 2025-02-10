package ru.job4j.sqljdbc.jdbc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ImportDB {
	private Properties config;
	private String dump;

	public ImportDB(Properties config, String dump) {
		this.config = config;
		this.dump = dump;
	}

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		try (InputStream input = ImportDB.class.getClassLoader().getResourceAsStream("app.properties")) {
			config.load(input);
		}
		ImportDB dataBase = new ImportDB(config, "data/spammer/dump.txt");
		System.out.println(dataBase.load());
		dataBase.save();
	}

	private List<User> load() {
		List<User> users = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(dump))) {
			reader.lines().forEach(line -> {
				String[] user = line.split(";");
				if (user.length < 2 || user[0].isEmpty() || user[1].isEmpty()) {
					throw new IllegalArgumentException("Имя и/или email не должны быть пустыми.");
				}
				users.add(new User(user[0], user[1]));
			});
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Файл '%s' не найден.".formatted(dump));
		} catch (IOException e) {
			throw new RuntimeException("Проблемы с чтением файла '%s'.".formatted(dump));
		}
		return users;
	}

	public void save() {
		try (Connection connection = getConnection()) {
			List<User> users = load();
			for (User user : users) {
				String sql = "INSERT INTO spammer.users (name, email) VALUES (?, ?)"
						.concat("ON CONFLICT (name) DO UPDATE SET email = EXCLUDED.email");
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
					preparedStatement.setString(1, user.name);
					preparedStatement.setString(2, user.email);
					preparedStatement.execute();
				} catch (SQLException e) {
					throw new RuntimeException("Произошла ошибка при выполнении запроса.");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Произошла ошибка с соединением с базой данных.");
		}
	}

	private Connection getConnection() {
		Connection connection;
		try {
			Class.forName(config.getProperty("db.driver"));
			connection = DriverManager.getConnection(
					config.getProperty("db.url"),
					config.getProperty("db.login"),
					config.getProperty("db.password"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Класс драйвера базы данных не удалось найти.");
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось установить соединение с базой данных.");
		}
		return connection;
	}

	private static class User {
		String name;
		String email;

		public User(String name, String email) {
			this.name = name;
			this.email = email;
		}

		@Override
		public String toString() {
			return "User{name='%s', email='%s'}".formatted(name, email);
		}
	}
}
