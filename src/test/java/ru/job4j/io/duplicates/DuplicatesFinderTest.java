package ru.job4j.io.duplicates;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatesFinderTest {

	@Test
	void testSearchDuplicateFiles() {
		Path start = Path.of("./src/main/java/ru/job4j/io/search_resources");
		DuplicatesFinder df = new DuplicatesFinder();
		Map<FileProperty, List<Path>> actual = df.getDuplicateFiles(start);
		Map<FileProperty, List<Path>> expected = Map.of(
				new FileProperty("one.txt", 32), List.of(
						Path.of("./src/main/java/ru/job4j/io/search_resources/one.txt"),
						Path.of("./src/main/java/ru/job4j/io/search_resources/a/one.txt")
				),
				new FileProperty("three.txt", 0), List.of(
						Path.of("./src/main/java/ru/job4j/io/search_resources/three.txt"),
						Path.of("./src/main/java/ru/job4j/io/search_resources/b/three.txt")
				)
		);

		assertThat(actual).hasSameSizeAs(expected);
		assertThat(actual.keySet()).isEqualTo(expected.keySet());
		actual.forEach((key, value) ->
				assertThat(value).containsExactlyInAnyOrderElementsOf(expected.get(key))
		);
	}
}