package ru.job4j.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Balancer {
	public static void split(List<ArrayList<Integer>> nodes, Iterator<Integer> source) {
		int nodesSize = nodes.size();
		int nodesCounter = nodesSize;
		while (source.hasNext()) {
			nodes.get(nodesSize - nodesCounter--).add(source.next());
			if (nodesCounter == 0) {
				nodesCounter = nodesSize;
			}
		}
	}
}
