package net.jonathangiles.tools.jwordle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class JWordleSolver implements Runnable {
    private List<String> words;

    private final Scanner keyboard;

    public static void main(String[] args) {
        new JWordleSolver().run();
    }

    public JWordleSolver() {
        // for reading the input from the user (or the test app)
        this.keyboard = new Scanner(System.in);

        // load words into memory
        words = loadWords();

        System.out.println("Loaded " + words.size() + " words");
    }

    public static List<String> loadWords() {
        try {
            return Files.lines(Paths.get(JWordleSolver.class.getResource("words.txt").toURI()))
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Failed to read words.txt file");
            e.printStackTrace();
            System.exit(-1);
        }
        return Collections.emptyList();
    }

    public void run() {
        GameState gameState = new GameState(words);
        for (int i = 0; i < 6; i++) {
            String guess = gameState.generateGuess().toUpperCase();
            processResponse(guess, gameState);
        }
    }

    private void processResponse(String guess, GameState gameState) {
        System.out.println("Suggested guess is " + guess);

        // parse the string and feed it back into the game state.
        // The format is "G" for "green", "Y" for "yellow", and "W" for grey
        boolean isValidResponse = false;
        String response = "";
        start: while (!isValidResponse) {
            System.out.print("Please enter response from Wordle: ");
            response = keyboard.next().toUpperCase();

            if (response.length() != 5) {
                break;
            }

            for (int i = 0; i < 5; i++) {
                switch (response.charAt(i)) {
                    case 'G', 'Y', 'W' -> { }
                    default -> { break start; }
                }
            }

            isValidResponse = true;
        }

        for (int i = 0; i < 5; i++) {
            char responseChar = response.charAt(i);
            char guessChar = guess.charAt(i);
            switch (responseChar) {
                case 'G' -> gameState.setCorrectCharacter(i, guessChar);
                case 'Y' -> gameState.addMisplacedCharacter(i, guessChar);
                case 'W' -> {
                    // we do this to better optimise situations where the same letter appears multiple times
                    int count = getCharCount(guess, guessChar);
                    gameState.addRejectedCharacter(count == 1 ? -1 : i, guessChar);
                }
            }
        }
    }

    // how many times does c appear in word
    private int getCharCount(String word, char c) {
        return (int) word.chars().filter(ch -> ch == c).count();
    }
}
