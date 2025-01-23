package ru.job4j.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public class Search {
	public static List<Path> search(Path root, Predicate<Path> condition) {
		SearchFiles searcher = new SearchFiles(condition);
		try {
			Files.walkFileTree(root, searcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searcher.getPaths();
	}
}
