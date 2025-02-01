package ru.job4j.io.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ConsoleChat {
	private static final String OUT = "завершить";
	private static final String STOP = "стоп";
	private static final String CONTINUE = "продолжить";
	private static final String IAM = "Я";
	private static final String BOT = "Бот";
	private final String path;
	private final String botAnswers;

	public ConsoleChat(String path, String botAnswers) {
		this.path = path;
		this.botAnswers = botAnswers;
	}

	public void run() {
		Path outFile = Path.of(this.path);
		createFile(outFile);
		List<String> phrases = readPhrases();
		List<String> chat = new LinkedList<>();
		Scanner scanner = new Scanner(System.in);
		Random random = new Random();
		String botAnswer, myAnswer, answer;
		boolean exitApp = false, isBotStop = false;
		while (!exitApp) {
			if (!isBotStop) {
				botAnswer = phrases.get(random.nextInt(phrases.size()));
				answer = String.join(": ", BOT, botAnswer);
				System.out.println(answer);
				chat.add(answer);
			}
			System.out.print(String.join("", IAM, ": "));
			myAnswer = scanner.nextLine();
			answer = String.join(": ", IAM, myAnswer);
			exitApp = OUT.equals(myAnswer);
			chat.add(answer);
			if (STOP.equals(myAnswer)) {
				isBotStop = true;
			} else if (CONTINUE.equals(myAnswer)) {
				isBotStop = false;
			}
		}
		scanner.close();
		saveLog(chat);
	}

	private static void createFile(Path outFile) {
		try {
			if (!Files.exists(outFile)) {
				if (!Files.exists(outFile.getParent())) {
					Files.createDirectories(outFile.getParent());
				}
				Files.createFile(outFile);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private List<String> readPhrases() {
		List<String> phrases = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(Path.of(this.botAnswers))) {
			String line;
			while ((line = reader.readLine()) != null) {
				phrases.add(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return phrases;
	}

	private void saveLog(List<String> log) {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(this.path), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
			for (String line : log) {
				writer.write(line + System.lineSeparator());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		ConsoleChat consoleChat = new ConsoleChat("./data/chat/me-bot.log", "./data/chat/bot-answers.txt");
		consoleChat.run();
	}
}
