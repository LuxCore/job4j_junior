package ru.job4j.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SearchFiles extends SimpleFileVisitor<Path> {
	private final List<Path> paths = new ArrayList<>();

	private final Predicate<Path> condition;

	public SearchFiles(Predicate<Path> condition) {
		this.condition = condition;
	}

	public List<Path> getPaths() {
		return paths;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (condition.test(file)) {
			paths.add(file);
		}
		return FileVisitResult.CONTINUE;
	}
}
