package pt.up.fe.cpd.g13.client.io;

import pt.up.fe.cpd.g13.common.utils.Pair;

import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TerminalIO {

    private final Scanner scanner;

    public TerminalIO(Scanner scanner) {
        this.scanner = scanner;
    }

    public final String askText(String prompt) {
        System.out.print(prompt);

        try {
            return scanner.nextLine();
        } catch (Exception e) {
            System.exit(1);
            return null;
        }
    }

    public final void waitForEnter() {
        askText("Press ENTER to continue...");
    }

    public final int askInt(String prompt) {
        var textAnswer = askText(prompt);

        try {
            return Integer.parseInt(textAnswer);
        } catch (NumberFormatException e) {
            return askInt(prompt);
        }
    }

    public final int askInt(String prompt, IntPredicate isValid) {
        var answer = askInt(prompt);
        if (isValid.test(answer))
            return answer;

        return askInt(prompt, isValid);
    }

    public final char askChar(String prompt) {
        var textAnswer = askText(prompt);
        return textAnswer.length() == 1 ? textAnswer.charAt(0) : askChar(prompt);
    }

    public final char askChar(String prompt, Predicate<Character> isValid) {
        var answer = askChar(prompt);
        if (isValid.test(answer))
            return answer;

        return askChar(prompt, isValid);
    }

    @SafeVarargs
    public final <T> T askOption(String prompt, Pair<String, Supplier<T>>... options) {
        if (options.length < 1) {
            throw new IllegalArgumentException("No options were provided");
        }

        System.out.println(prompt);

        for (int i = 0; i < options.length; i++) {
            var pair = options[i];
            System.out.printf("[%d.] %s%n", i + 1, pair.first());
        }

        System.out.println();

        var selectedOptionNumber = askInt("Your option: ", i -> i >= 1 && i <= options.length);
        var selectedOption = options[selectedOptionNumber - 1];
        return selectedOption.second().get();
    }
}
