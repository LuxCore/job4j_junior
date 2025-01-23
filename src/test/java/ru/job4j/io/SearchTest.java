package ru.job4j.io;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SearchTest {

	@Test
	void testSearchForTxt() {
		List<Path> expected = List.of(
				Path.of("./src/main/java/ru/job4j/io/search_resources/one.txt"),
				Path.of("./src/main/java/ru/job4j/io/search_resources/three.txt")
		);
		Path start = Path.of(".");
		List<Path> actual = Search.search(start, path -> path.toFile().getName().endsWith(".txt"));
		assertThat(actual).isEqualTo(expected);
	}
}