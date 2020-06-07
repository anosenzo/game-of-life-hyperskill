package life;

public class ConsoleGameOfLifeUiHandler implements GameOfLifeUiHandler {

    Universe gameThread;

    @Override
    public void showGenerationLabel(int generationNumber) {
        System.out.println("Generation #" + Integer.toString(generationNumber));
    }

    @Override
    public void showAliveLabel(int aliveNumber) {
        System.out.println("Alive " + aliveNumber);
    }

    @Override
    public void showCurrentGeneration(boolean[][] currentGeneration) {
        for (int row = 0; row < currentGeneration.length; row++) {
            for (int column = 0; column < currentGeneration[row].length; column++) {
                System.out.print((currentGeneration[row][column]) ? "O" : " ");
            }
            System.out.println();
        }
    }

    @Override
    public void clear() {
        //        try {
//            if (System.getProperty("os.name").contains("Windows"))
//                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
//            else
//                Runtime.getRuntime().exec("clear");
//        }
//        catch (IOException | InterruptedException e) {}

            System.out.print("\033[H\033[2J");
            System.out.flush();
    }

}
