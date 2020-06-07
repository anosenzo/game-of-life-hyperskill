package life;

public interface GameOfLifeUiHandler {

    void showGenerationLabel(int generationNumber);

    void showAliveLabel(int aliveNumber);

    void showCurrentGeneration(boolean[][] currentGeneration);

    default boolean shouldBeRunning() {
        throw new RuntimeException("Not implemented");
    }

    default Object getPauseLock() {
        throw new RuntimeException("Not implemented");
    }

    default boolean isReseted() {
        throw new RuntimeException("Not implemented");
    }

    default void resetDone() {
        throw new RuntimeException("Not implemented");
    }

    void clear();
}
