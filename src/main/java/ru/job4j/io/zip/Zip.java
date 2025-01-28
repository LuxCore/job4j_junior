package ru.job4j.io.zip;

import ru.job4j.io.Search;
import ru.job4j.io.args.ArgsName;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
	public void packFiles(List<Path> sources, File target) {
		try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
			for (Path source : sources) {
				zip.putNextEntry(new ZipEntry(source.toFile().getPath()));
				try (BufferedInputStream output = new BufferedInputStream(new FileInputStream(source.toFile()))) {
					zip.write(output.readAllBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void packSingleFile(File source, File target) {
		try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
			zip.putNextEntry(new ZipEntry(source.getPath()));
			try (BufferedInputStream output = new BufferedInputStream(new FileInputStream(source))) {
				zip.write(output.readAllBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws NotDirectoryException, FileNotFoundException {
		ArgsName argsName = ArgsName.of(args);
		validateArgs(argsName);
		Zip zip = new Zip();
		zip.packSingleFile(
				new File("./pom.xml"),
				new File("./pom.zip")
		);
		List<Path> filesToPack = Search.search(Path.of(argsName.get("d")), path -> !path.endsWith(args[1]) && path.toFile().isFile());
		zip.packFiles(filesToPack, new File(argsName.get("o")));
	}

	private static void validateArgs(ArgsName argsName) throws FileNotFoundException, NotDirectoryException {
		Path path = Path.of(argsName.get("d"));
		if (!Files.exists(path)) {
			throw new FileNotFoundException("Directory '" + argsName.get("d") + "' not found.");
		}

		if (!Files.isDirectory(path)) {
			throw new NotDirectoryException('\'' + argsName.get("d") + "' is not a directory.");
		}

		if (!argsName.get("e").startsWith(".")) {
			throw new IllegalArgumentException("Extension '" + argsName.get("e") + "' must start with '.'.");
		}

		if (!argsName.get("o").endsWith(".zip")) {
			throw new IllegalArgumentException("Extension of output file must end with '.zip'");
		}
	}
}
