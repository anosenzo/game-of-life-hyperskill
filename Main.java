package life;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        int mapSize = scanner.nextInt();

        int numberOfGenerations = 20;
        scanner.close();

        GenerationAlgorithm neighboursGeneration = new NeighboursGenerationAlgorithm();
        GameOfLifeUiHandler swingHandler = new GameOfLife();
//        GameOfLifeUiHandler consoleUiHandler = new ConsoleGameOfLifeUiHandler();

//        Universe gameUniverse = new Universe(10L, mapSize, neighboursGeneration, swingHandler, numberOfGenerations);
        Universe gameUniverse = new Universe(mapSize, neighboursGeneration, swingHandler, numberOfGenerations);
        Thread gameUniverseThread = new Thread(gameUniverse);
        gameUniverseThread.setName("Game Universe Thread");
        gameUniverseThread.start();
        gameUniverseThread.join();
    }
}

class Universe implements Runnable {

    boolean[][] initialGeneration;
    boolean[][] currentGeneration;
    int numberOfGenerations;

    Random random;

    GenerationAlgorithm generationAlgorithm;

    GameOfLifeUiHandler gameOfLifeUiHandler;

    Universe(int mapSize, GenerationAlgorithm generationAlgorithm, GameOfLifeUiHandler gameOfLifeFrame, int numberOfGenerations) {
        this.random = new Random();
        this.initialGeneration = getInitialGenerationFromInput(mapSize);
        this.currentGeneration = initialGeneration;
        this.generationAlgorithm = generationAlgorithm;
        this.gameOfLifeUiHandler = gameOfLifeFrame;
        this.numberOfGenerations = numberOfGenerations;
    }

    Universe(Long randomSeed, int mapSize, GenerationAlgorithm generationAlgorithm, GameOfLifeUiHandler gameOfLifeFrame,  int numberOfGenerations) {
        this.random = new Random(randomSeed);
        this.initialGeneration = getInitialGenerationFromInput(mapSize);
        this.currentGeneration = initialGeneration;
        this.generationAlgorithm = generationAlgorithm;
        this.gameOfLifeUiHandler = gameOfLifeFrame;
        this.numberOfGenerations = numberOfGenerations;
    }

    private boolean[][] getInitialGenerationFromInput(int mapSize) {
        boolean[][] generation = new boolean[mapSize][mapSize];

        for (int row = 0; row < mapSize; row++) {
            for (int column = 0; column < mapSize; column++) {
                generation[row][column] = random.nextBoolean();
            }
        }
        return generation;
    }

    void setGenerationAfter(int numberOfGenerations) {
        for(int i = 0; i < numberOfGenerations; i++) {
            currentGeneration = generationAlgorithm.getNextGeneration(currentGeneration);
        }
    }

    void setGenerationAndPrintAfter(int numberOfGenerations) {
        for(int i = 1; i <= numberOfGenerations; i++) {

            synchronized(gameOfLifeUiHandler.getPauseLock()) {
                while (! gameOfLifeUiHandler.shouldBeRunning()) {
                    try {
                        System.out.println("Sleep");
                        gameOfLifeUiHandler.getPauseLock().wait();
                        System.out.println("Awake");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (gameOfLifeUiHandler.isReseted()) {
                reset();
                i = 1;
                gameOfLifeUiHandler.resetDone();
            }

            System.out.println("Running generation " + i);

            currentGeneration = generationAlgorithm.getNextGeneration(currentGeneration);
            gameOfLifeUiHandler.showGenerationLabel(i);
            gameOfLifeUiHandler.showAliveLabel(getAliveCells());
            gameOfLifeUiHandler.showCurrentGeneration(currentGeneration);
            sleep(500);
            gameOfLifeUiHandler.clear();
        }
    }

    void reset() {
        System.out.println("The reset button was pressed, going to reset.");
        currentGeneration = initialGeneration;
    }

    int getAliveCells() {
        int aliveCellsCounter = 0;
        for (int i = 0; i < currentGeneration.length; i++) {
            for (int j = 0; j < currentGeneration.length; j++) {
                if (currentGeneration[i][j]) {
                    aliveCellsCounter++;
                }
            }
        }
        return aliveCellsCounter;
    }

    void sleep(long sleepTimeMillis) {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        setGenerationAndPrintAfter(numberOfGenerations);
    }

    public Object lockObject = "";

    public void pause() throws InterruptedException {
        synchronized (lockObject) {
            System.out.println("Current Thread Name: " + Thread.currentThread().getName());
            lockObject.wait();
        }
    }

    public void resume() throws InterruptedException {
        synchronized (lockObject) {
            lockObject.notify();
        }
    }
}

interface GenerationAlgorithm {

    boolean[][] getNextGeneration(boolean[][] currentGeneration);

}

class NeighboursGenerationAlgorithm implements GenerationAlgorithm {

    @Override
    public boolean[][] getNextGeneration(boolean[][] currentGeneration) {
        boolean[][] nextGeneration = new boolean[currentGeneration.length][currentGeneration[0].length];

        for (int i = 0; i < currentGeneration.length; i++) {
            for (int j = 0; j < currentGeneration[i].length; j++) {
                boolean cellIsAlive = currentGeneration[i][j];
                int neighboursSum = getSumOfNeighbours(i, j, currentGeneration);
                if (cellIsAlive) {
                    cellIsAlive = (neighboursSum >= 2 && neighboursSum < 4);
                } else {
                    cellIsAlive = (neighboursSum == 3);
                }
                nextGeneration[i][j] = cellIsAlive;
            }
        }

        return nextGeneration;
    }

    private int getSumOfNeighbours(int row, int column, boolean[][] matrix) {
        int result = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                result += (getNeighbourValue(row + i, column + j, matrix)) ? 1 : 0;
            }
        }

        return result;
    }

    private boolean getNeighbourValue(int row, int column, boolean[][] matrix) {
        int neighbourRow;
        int neighbourColumn;

        int numberOfRows = matrix.length;
        int numberOfColumns = matrix.length;

        neighbourRow = getNeighbourPosition(row, numberOfRows);
        neighbourColumn = getNeighbourPosition(column, numberOfColumns);

        return matrix[neighbourRow][neighbourColumn];
    }

    private int getNeighbourPosition(int position, int maxPosition) {
        int neighbourPosition = 0;
        if (position >= 0 && position < maxPosition) {
            neighbourPosition = position;
        } else if (position < 0) {
            neighbourPosition = maxPosition - 1;
        } else if (position >= maxPosition) {
            neighbourPosition = 0;
        }
        return neighbourPosition;
    }
}