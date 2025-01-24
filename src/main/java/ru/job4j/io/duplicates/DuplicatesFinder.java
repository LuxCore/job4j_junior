package ru.job4j.io.duplicates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DuplicatesFinder {
	public Map<FileProperty, List<Path>> getDuplicateFiles(Path start) {
		DuplicatesVisitor dv = new DuplicatesVisitor();
		try {
			Files.walkFileTree(start, dv);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dv.getPaths();
	}
}
