package net.jonathangiles.tools.jwordle;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private static final String INITIAL_GUESS = "ADIEU";
    private static final char NOT_SET_CHAR = '!';

    private boolean isNewGame = true;

    // all words that meet the criteria - trimmed on each step
    private List<String> possibleWords;

    // any accepted letters that are recorded in their correct location
    private final char[] correctLetters = new char[5];

    // all rejected letters that are not in the word at all
    private final Map<Character, LetterLocation> rejectedLetters = new HashMap<>();

    // all letters that are in the word, but in the incorrect location
    private final Map<Character, LetterLocation> misplacedLetters = new HashMap<>();

    public GameState(List<String> possibleWords) {
        this.possibleWords = new ArrayList<>(possibleWords);
        for (int i = 0; i < 5; i++) {
            correctLetters[i] = NOT_SET_CHAR;
        }
    }

    public void setCorrectCharacter(int correctLocation, char inputChar) {
        this.correctLetters[correctLocation] = inputChar;
    }

    public void addMisplacedCharacter(int wrongLocation, char inputChar) {
        misplacedLetters.computeIfAbsent(inputChar, _char -> new LetterLocation()).addKnownWrongLocation(wrongLocation);
    }

    public void addRejectedCharacter(int wrongLocation, char inputChar) {
        this.rejectedLetters.computeIfAbsent(inputChar, _char -> new LetterLocation()).addKnownWrongLocation(wrongLocation);
    }

    public String generateGuess() {
        if (isNewGame) {
            this.isNewGame = false;
            return INITIAL_GUESS;
        }

        // before we generate, we apply the feedback from the previous iteration to prune our wordlist
        possibleWords = possibleWords.parallelStream()
                .filter(this::keepWord)
                .collect(Collectors.toList());

        System.out.print("Words list reduced to " + possibleWords.size());
        if (possibleWords.size() <= 10) {
            System.out.println(" " + possibleWords);
        } else {
            System.out.println("");
        }

        return possibleWords.get(0);
    }

    private boolean keepWord(String word) {
        int countOfMisplacedLettersFound = 0;

        for (int i = 0; i < 5; i++) {
            Character letter = word.charAt(i);

            // firstly check the correct characters array for matches
            if (correctLetters[i] != NOT_SET_CHAR && letter != correctLetters[i]) {
                return false;
            }

            // work through all rejected letters. We have to be careful because many words have duplicate letters.
            if (rejectedLetters.containsKey(letter)) {
                if (!rejectedLetters.get(letter).isLetterAllowedAtLocation(i)) {
                    return false;
                }
            }

            // work through misplaced letters
            // These misplaced letters have to be somewhere in the word, but not where they were misplaced
            if (misplacedLetters.containsKey(letter)) {
                if (!misplacedLetters.get(letter).isLetterAllowedAtLocation(i)) {
                    return false;
                }
                countOfMisplacedLettersFound++;
            }
        }

        // we need the number of misplaced letters to match the misplaced letters found in the word
        if (countOfMisplacedLettersFound != misplacedLetters.size()) {
            return false;
        }

        return true;
    }

    private static class LetterLocation {
        private final BitSet knownWrongLocations = new BitSet();
        private boolean letterFullyRejected = false;

        public void addKnownWrongLocation(int wrongLocation) {
            if (wrongLocation == -1) {
                letterFullyRejected = true;
            } else {
                this.knownWrongLocations.set(wrongLocation);
            }
        }

        public boolean isLetterAllowedAtLocation(int i) {
            if (letterFullyRejected) {
                return false;
            }
            return !knownWrongLocations.get(i);
        }
    }
}