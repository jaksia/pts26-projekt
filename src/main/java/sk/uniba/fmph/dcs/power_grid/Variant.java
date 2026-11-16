package sk.uniba.fmph.dcs.power_grid;

/**
 * A result that is either a success value of type {@code S} or an error of
 * type {@code E}.
 *
 * <p>This interface intentionally contains no implementations. Concrete
 * variants are left to the implementing classes.</p>
 *
 * @param <S> type of the success value
 * @param <E> type of the error value
 */
public interface Variant<S, E> {
}
