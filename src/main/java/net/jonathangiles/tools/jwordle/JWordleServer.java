package net.jonathangiles.tools.jwordle;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class JWordleServer implements Runnable {
    private final List<String> words;
    private Scanner keyboard;

    public static void main(String[] args) {
        JWordleServer server = new JWordleServer();

        while (true) {
            server.run();
        }
    }

    public JWordleServer() {
        this.words = JWordleSolver.loadWords();

        // for reading the input from the user
        this.keyboard = new Scanner(System.in);
    }

    public void run() {
        // pick a random word
        String wordToGuess = words.get(new Random().nextInt(words.size()));
        System.out.println("Word to guess is "+ wordToGuess);

        for (int i = 0; i < 5; i++) {
            System.out.print("Please enter your guess: ");
            String guess = keyboard.next().toUpperCase();

            if (guess.length() != 5) {
                continue;
            }

            StringBuilder response = new StringBuilder();
            for (int j = 0; j < 5; j++) {
                if (guess.charAt(j) == wordToGuess.charAt(j)) {
                    response.append("G");
                } else if (wordContainsChar(wordToGuess, guess.charAt(j))) {
                    response.append("Y");
                } else {
                    response.append("W");
                }
            }

            System.out.println("Response: " + response);

            if ("GGGGG".equals(response.toString())) {
                System.out.println("Congratulations - you did it! Starting new game!");
                System.out.println("-------------------");
                break;
            }
        }
    }

    private static boolean wordContainsChar(String wordToGuess, char guessedChar) {
        for (int j = 0; j < 5; j++) {
            if (wordToGuess.charAt(j) == guessedChar) {
                return true;
            }
        }

        return false;
    }
}
