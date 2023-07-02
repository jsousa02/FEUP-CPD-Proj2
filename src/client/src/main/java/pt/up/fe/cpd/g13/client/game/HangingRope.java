package pt.up.fe.cpd.g13.client.game;

public class HangingRope {

    public static String getHangingRopeForNumberOfFailedTries(int num) {
        var indentedRope = switch (num) {
            case 0 -> """
                         --------
                         |      |
                         |
                         |
                         |
                         |
                      ___|_________
                      """;

            case 1 -> """
                         --------
                         |      |
                         |      O
                         |
                         |
                         |
                      ___|_________
                      """;

            case 2 -> """
                         --------
                         |      |
                         |      O
                         |      |
                         |
                         |
                      ___|_________
                      """;

            case 3 -> """
                         --------
                         |      |
                         |      O
                         |      |\\
                         |
                         |
                      ___|_________
                      """;

            case 4 -> """
                         --------
                         |      |
                         |      O
                         |     /|\\
                         |
                         |
                      ___|_________
                      """;

            case 5 -> """
                         --------
                         |      |
                         |      O
                         |     /|\\
                         |       \\
                         |
                      ___|_________
                      """;

            case 6 -> """
                         --------
                         |      |
                         |      💀
                         |     /|\\
                         |     / \\
                         |
                      ___|_________
                      """;

            default -> throw new NullPointerException("Unknown hanging rope state");
        };

        return indentedRope.stripIndent();
    }
}
