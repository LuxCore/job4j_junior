package ru.job4j.io.duplicates;

import java.util.Comparator;
import java.util.Objects;

public class FileProperty implements Comparable<FileProperty> {
	private String name;
	private long size;

	public FileProperty(String name, long size) {
		this.name = name;
		this.size = size;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	@Override
	public String toString() {
		return name + " - " + size + " bytes";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FileProperty that = (FileProperty) o;
		return size == that.size && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, size);
	}

	@Override
	public int compareTo(FileProperty o) {
		return Comparator.comparing(FileProperty::getName)
				.thenComparing(FileProperty::getSize).compare(this, o);
	}
}
