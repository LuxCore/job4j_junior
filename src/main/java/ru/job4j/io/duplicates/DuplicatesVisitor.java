package ru.job4j.io.duplicates;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DuplicatesVisitor extends SimpleFileVisitor<Path> {
	private Path startDir;
	private final Map<FileProperty, List<Path>> paths = new TreeMap<>();

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (startDir == null && dir != null) {
			startDir = dir;
		}
		return super.preVisitDirectory(dir, attrs);
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		paths.compute(new FileProperty(file.getFileName().toString(), attrs.size()), (k, v) -> {
			if (v == null) {
				v = new ArrayList<>();
			}
			v.add(file);
			return v;
		});
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (dir != null && dir.equals(startDir)) {
			paths.entrySet().removeIf(entry -> entry.getValue().size() == 1);
		}
		return super.postVisitDirectory(dir, exc);
	}

	public Map<FileProperty, List<Path>> getPaths() {
		return paths;
	}
}
