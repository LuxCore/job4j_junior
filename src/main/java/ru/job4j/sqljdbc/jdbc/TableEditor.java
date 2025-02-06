package ru.job4j.sqljdbc.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringJoiner;

public class TableEditor implements AutoCloseable {
	private Connection connection;

	private Properties properties;

	public TableEditor(Properties properties) {
		this.properties = properties;
		initConnection();
	}

	public static void main(String[] args) {
		Properties properties = new Properties();
		String propFile = "app.properties";
		try (InputStream propInput = TableEditor.class.getClassLoader().getResourceAsStream(propFile)) {
			properties.load(propInput);
		} catch (IOException e) {
			throw new RuntimeException("Файл '%s' не найден.".formatted(propFile));
		}
		try (TableEditor tableEditor = new TableEditor(properties)) {
			tableEditor.createTable("categories");
			System.out.println(tableEditor.getTableDefinition("categories"));

			tableEditor.addColumn("categories", "id", "INTEGER");
			System.out.println(tableEditor.getTableDefinition("categories"));

			tableEditor.addColumn("categories", "maincat", "TEXT");
			tableEditor.addColumn("categories", "subcat", "TEXT");
			System.out.println(tableEditor.getTableDefinition("categories"));

			tableEditor.dropColumn("categories", "maincat");
			tableEditor.dropColumn("categories", "subcat");
			tableEditor.dropColumn("categories", "sub_cat");
			tableEditor.addColumn("categories", "main_cat", "TEXT");
			tableEditor.addColumn("categories", "sub_category", "TEXT");
			System.out.println(tableEditor.getTableDefinition("categories"));

			tableEditor.renameColumn("categories", "sub_category", "sub_cat");
			System.out.println(tableEditor.getTableDefinition("categories"));

			tableEditor.dropTable("categories");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void initConnection() {
		try {
			Class.forName(properties.getProperty("db.driver"));
			String dbUrl = properties.getProperty("db.url");
			String dbLogin = properties.getProperty("db.login");
			String dbPassword = properties.getProperty("db.password");
			connection = DriverManager.getConnection(dbUrl, dbLogin, dbPassword);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Не удалось найти класс подключения к базе данных.");
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось установить подключение к базе данных.");
		}
	}

	public void createTable(String tableName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS %s();".formatted(tableName));
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось создать таблицу.");
		}
	}

	public void dropTable(String tableName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("DROP TABLE IF EXISTS %s;".formatted(tableName));
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось удалить таблицу.");
		}
	}

	public void addColumn(String tableName, String columnName, String columnType) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("ALTER TABLE IF EXISTS %s ADD COLUMN IF NOT EXISTS %s %s;"
					.formatted(tableName, columnName, columnType));
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось добавить поле в таблицу.");
		}
	}

	public void dropColumn(String tableName, String columnName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("ALTER TABLE IF EXISTS %s DROP COLUMN IF EXISTS %s;"
					.formatted(tableName, columnName));
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось удалить поле из таблицы.");
		}
	}

	public void renameColumn(String tableName, String columnName, String newColumnName) {
		StringJoiner query = new StringJoiner(System.lineSeparator());
		query.add("DO $$")
				.add("BEGIN")
				.add("IF EXISTS(SELECT * FROM information_schema.columns")
				.add("WHERE table_name='%s' and column_name='%s')")
				.add("THEN")
				.add("ALTER TABLE %1$s RENAME COLUMN %2$s TO %3$s;")
				.add("END IF;\nEND $$;");
//		System.out.printf((query.toString()) + "%n", tableName, columnName, newColumnName);
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(
//					"ALTER TABLE IF EXISTS %s RENAME COLUMN %s TO %s;"
					query.toString()
					.formatted(tableName, columnName, newColumnName));
		} catch (SQLException e) {
			throw new RuntimeException("Не удалось переименовать поле в таблице.");
		}
	}

	public String getTableDefinition(String tableName) throws Exception {
		var headerColumnSeparator = "-".repeat(15);
		var header = String.format("%-15s | %-15s%n%-15s-+-%-15s", "NAME", "TYPE",
				headerColumnSeparator, headerColumnSeparator);
		var buffer = new StringJoiner(System.lineSeparator(), "", System.lineSeparator());
		buffer.add(header);
		try (var statement = connection.createStatement()) {
			var selection = statement.executeQuery(String.format(
					"SELECT * FROM %s LIMIT 1", tableName
			));
			var metaData = selection.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				buffer.add(String.format("%-15s | %-15s",
						metaData.getColumnName(i), metaData.getColumnTypeName(i))
				);
			}
		}
		return buffer.toString();
	}

	@Override
	public void close() throws Exception {
		if (connection != null) {
			connection.close();
		}
	}
}
