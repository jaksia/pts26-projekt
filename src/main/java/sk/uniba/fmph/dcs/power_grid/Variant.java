package sk.uniba.fmph.dcs.power_grid;

/**
 * A result that is either a success value of type {@code S} or an error of
 * type {@code E}.
 *
 * @param <S> type of the success value
 * @param <E> type of the error value
 */
public sealed interface Variant<S, E> permits Variant.Success, Variant.Failure {

    /**
     * A successful result carrying a value.
     *
     * @param <S>   type of the success value
     * @param <E>   type of the error value
     * @param value the success value
     */
    record Success<S, E>(S value) implements Variant<S, E> {
    }

    /**
     * A failed result carrying an error.
     *
     * @param <S>   type of the success value
     * @param <E>   type of the error value
     * @param error the error value
     */
    record Failure<S, E>(E error) implements Variant<S, E> {
    }

    /**
     * Creates a successful variant.
     *
     * @param <S>   type of the success value
     * @param <E>   type of the error value
     * @param value the success value
     * @return a variant carrying {@code value}
     */
    static <S, E> Variant<S, E> success(final S value) {
        return new Success<>(value);
    }

    /**
     * Creates a failed variant.
     *
     * @param <S>   type of the success value
     * @param <E>   type of the error value
     * @param error the error value
     * @return a variant carrying {@code error}
     */
    static <S, E> Variant<S, E> failure(final E error) {
        return new Failure<>(error);
    }
}
