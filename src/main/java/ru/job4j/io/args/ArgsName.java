package ru.job4j.io.args;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArgsName {
	private final Map<String, String> values = new HashMap<>();

	public String get(String key) {
		if (!values.containsKey(key)) {
			throw new IllegalArgumentException("This key: '" + key + "' is missing");
		}
		return values.get(key);
	}

	private void parse(String[] args) {
		for (String arg : args) {
			if (!arg.startsWith("-")) {
				throw new IllegalArgumentException("Error: This argument '" + arg + "' does not start with a '-' character");
			}
			if (!arg.contains("=")) {
				throw new IllegalArgumentException("Error: This argument '" + arg + "' does not contain an equal sign");
			}
			String[] res = arg.substring(1).split("=", 2);
			if (Objects.equals(res[0], "")) {
				throw new IllegalArgumentException("Error: This argument '" + arg + "' does not contain a key");
			}
			if (Objects.equals(res[1], "")) {
				throw new IllegalArgumentException("Error: This argument '" + arg + "' does not contain a value");
			}
			values.put(res[0], res[1]);
		}
	}

	public static ArgsName of(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("Arguments not passed to program");
		}
		ArgsName names = new ArgsName();
		names.parse(args);
		return names;
	}
}
