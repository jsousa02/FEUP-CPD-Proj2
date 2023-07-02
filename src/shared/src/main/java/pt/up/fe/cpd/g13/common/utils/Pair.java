package pt.up.fe.cpd.g13.common.utils;

public record Pair<First, Second>(First first, Second second) {

    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<>(first, second);
    }
}
