package ru.job4j.io.csv;

import ru.job4j.io.args.ArgsName;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CSVReader {
	/**
	 * Работа метода:
	 * 1. Сопоставляются имена полей фильтра и заголовка csv файла. В результате запоминаются номера полей.
	 * 2. В случае, если имена полей сопоставлены успешно, то обрабатываются остальные строки файла.
	 * <p>
	 * Перенос строк в файле может быть любым.
	 *
	 * @param argsName Аргументы командной строки.
	 * @throws Exception
	 */
	public static void handle(ArgsName argsName) throws Exception {
		try (Scanner scanner = new Scanner(Path.of(argsName.get("path")), StandardCharsets.UTF_8).useDelimiter("\\R")) {
			String delimiter = argsName.get("delimiter");
			String[] csvLineValues = new String[0];
			String csvHeader = scanner.hasNext() ? scanner.next() : null;
			if (csvHeader != null) {
				csvLineValues = csvHeader.split(delimiter);
			}
			if (csvLineValues.length == 0) {
				return;
			}
			String[] neededHeader = argsName.get("filter").split(",");
			List<Integer> neededColumnNums = new LinkedList<>();
			StringBuilder csvOut = new StringBuilder();
			for (String s : neededHeader) {
				for (int j = 0; j < csvLineValues.length; j++) {
					if (csvLineValues[j].equals(s)) {
						neededColumnNums.add(j);
						csvOut.append(csvLineValues[j]).append(delimiter);
					}
				}
			}
			if (!neededColumnNums.isEmpty()) {
				String ls = System.lineSeparator();
				csvOut.deleteCharAt(csvOut.length() - 1).append(ls);
				while (scanner.hasNext()) {
					csvLineValues = scanner.next().split(delimiter);
					for (Integer i : neededColumnNums) {
						csvOut.append(csvLineValues[i]).append(delimiter);
					}
					csvOut.deleteCharAt(csvOut.length() - 1).append(ls);
				}
			}
			if ("stdout".equals(argsName.get("out"))) {
				System.out.println(csvOut);
			} else {
				Files.writeString(Path.of(argsName.get("out")), csvOut.toString(), StandardCharsets.UTF_8);
			}
		}
	}

	public static void main(String[] args) {
		ArgsName argsName = ArgsName.of(args);
		validateArgs(argsName);
		try {
			handle(argsName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void validateArgs(ArgsName args) {
		if (!Files.exists(Path.of(args.get("path")))) {
			throw new IllegalArgumentException("Файл '%s' не найден.".formatted(args.get("path")));
		}

		String out = args.get("out");
		if (!("stdout".equals(out) || Files.exists(Path.of(out)))) {
			throw new IllegalArgumentException("Параметр 'out' должен принимать одно из значений: 'stdout' или имя существующего файла.");
		}
	}
}
