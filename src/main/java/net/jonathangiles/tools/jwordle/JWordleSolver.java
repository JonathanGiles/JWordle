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
            String guess = gameState.generateGuess();
            processResponse(guess, gameState);
        }
    }

    private void processResponse(String guess, GameState gameState) {
        System.out.println("Suggested guess is " + guess.toUpperCase());

        // parse the string and feed it back into the game state.
        // The format is "G" for "green", "Y" for "yellow", and "W" for grey
        boolean isValidResponse = false;
        String response = "";
        start: while (!isValidResponse) {
            System.out.println("Please enter response from Wordle: ");
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
            switch (response.charAt(i)) {
                case 'G' -> gameState.setCorrectCharacter(i, guess.charAt(i));
                case 'Y' -> gameState.addMisplacedCharacter(i, guess.charAt(i));
                case 'W' -> gameState.addRejectedCharacter(guess.charAt(i));
            }
        }
    }
}
