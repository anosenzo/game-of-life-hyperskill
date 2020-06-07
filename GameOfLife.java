package life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOfLife extends JFrame implements GameOfLifeUiHandler {
    JLabel generationLabel;
    JLabel aliveLabel;

    JPanel infoPanel;
    JPanel cellsFieldPanel;
    JToggleButton pauseResumeToggleButton;

    static final int WINDOW_WIDTH = 300;
    static final int WINDOW_HEIGHT = 300;

    boolean shouldBeRunning = true;
    boolean isReseted = false;

    @Override
    public Object getPauseLock() {
        return pauseLock;
    }

    @Override
    public boolean isReseted() {
        return isReseted;
    }

    @Override
    public void resetDone() {
        isReseted = false;
    }

    public Object pauseLock = new Object();

    int mapSize;

    public GameOfLife() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Game of Life");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new BorderLayout());

        generationLabel = buildGenerationLabel("1");
        aliveLabel = buildAliveLabel("0");
        pauseResumeToggleButton = buildResumePauseButton("PlayToggleButton");
        JButton resetButton = buildResetButton("ResetButton");

        infoPanel = buildInfoPanel();
        infoPanel.add(pauseResumeToggleButton);
        infoPanel.add(resetButton);
        infoPanel.add(generationLabel);
        infoPanel.add(aliveLabel);
        add(infoPanel, BorderLayout.WEST);

        setVisible(true);
    }

    JButton buildResetButton(String name) {
        JButton resetButton = new JButton("Reset");
        resetButton.setName(name);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                isReseted = true;
                resumeExecution();
            }
        });
        return resetButton;
    }

    JToggleButton buildResumePauseButton(String name) {
        Object pauseLockLocal = pauseLock;
        JToggleButton pauseResumeToggleButton = new JToggleButton("Pause");
        pauseResumeToggleButton.setName(name);
        pauseResumeToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton toggleButton = ((JToggleButton) e.getSource());
                if (toggleButton.isSelected()) {
                    toggleButton.setText("Resume");
                    shouldBeRunning = false;
                } else {
                    toggleButton.setText("Pause");
                    resumeExecution();
                }
            }
        });
        return pauseResumeToggleButton;
    }

    void resumeExecution() {
        synchronized (pauseLock) {
            pauseResumeToggleButton.setText("Pause");
            pauseResumeToggleButton.setSelected(false);
            shouldBeRunning = true;
            pauseLock.notifyAll();
        }
    }

    JPanel buildInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(40,150,220,70);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        return infoPanel;
    }

    @Override
    public void showGenerationLabel(int generationNumber) {
        setGenerationLabel(Integer.toString(generationNumber));
    }

    @Override
    public void showCurrentGeneration(boolean[][] currentGeneration) {

//        if (cellsFieldPanel != null) {
//            remove(cellsFieldPanel);
//        }
//
//        JPanel cellsFieldPanel = new JPanel() {
//
//            @Override
//            public void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                repaint();
//                revalidate();
//                setSize(WINDOW_WIDTH -  70, WINDOW_HEIGHT - 70);
//                Graphics2D g2d = (Graphics2D) g;
//                drawCellsField(currentGeneration, getSize().getWidth(), getSize().getHeight(), g2d);
//            }
//        };

        JPanel cellsFieldPanel = new JPanel();
        cellsFieldPanel.setLayout(new GridLayout(currentGeneration.length, currentGeneration.length, 1, 1));
        drawCellsFieldWithPanels(currentGeneration, cellsFieldPanel);
        this.cellsFieldPanel = cellsFieldPanel;
        add(cellsFieldPanel, BorderLayout.CENTER);
    }

    void drawCellsFieldWithPanels(boolean[][] currentGeneration, JPanel cellsField) {
        for (int row = 0; row < currentGeneration.length; row++) {
            for (int column = 0; column < currentGeneration[row].length; column++) {
                JPanel cell = new JPanel();

                cell.setBackground(currentGeneration[row][column] ? Color.BLACK : Color.WHITE);
                cellsField.add(cell);
            }
        }
    }

    private class DrawRectangle extends JComponent {

        private boolean isBlack;

        public DrawRectangle(boolean isBlack) {
            this.isBlack = isBlack;
        }

        public void paint(Graphics graph) {
            Graphics2D graph2d = (Graphics2D) graph;

            graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape rectangle = new Rectangle(20, 20);

            graph2d.setColor(isBlack ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            graph2d.fill(rectangle);
            graph2d.draw(rectangle);
        }
    }

    void drawCellsField(boolean[][] currentGeneration, double PANEL_WIDTH, double PANEL_HEIGHT, Graphics2D g2d) {
        int cellNumberPerRow = currentGeneration.length;
        int cellWidth = (int) PANEL_WIDTH / cellNumberPerRow;
        int cellHeight = (int) PANEL_HEIGHT / cellNumberPerRow;
//        int cellSize = 300 / cellNumberPerRow;

        g2d.setColor(Color.BLACK);

        for (int row = 0; row < currentGeneration.length; row++) {
//            g2d.drawLine(0, cellWidth * row, (int) PANEL_WIDTH, cellWidth * row); // X Axis Lines / Horizontal Lines
//            g2d.drawLine(cellHeight * row, 0, cellHeight * row, (int) PANEL_HEIGHT); //

            for (int column = 0; column < currentGeneration[row].length; column++) {
                int x = cellWidth * column;
                int y = cellHeight * row;

                if (currentGeneration[row][column]) {
                    drawSquare(x, y, cellWidth, cellHeight, Color.BLACK, g2d);
                } else {
                    drawSquare(x, y, cellWidth, cellHeight, Color.LIGHT_GRAY, g2d);
                }
            }
        }

//        g2d.setColor(Color.BLACK);
//        g2d.drawLine(0, cellWidth * currentGeneration.length - 1, (int) PANEL_WIDTH, cellWidth * currentGeneration.length - 1); // X Axis Lines / Horizontal Lines
//        g2d.drawLine(cellHeight * currentGeneration.length - 1, 0, cellHeight * currentGeneration.length - 1, (int) PANEL_HEIGHT); //
    }

    void drawSquare(int x, int y, int cellWidth, int cellHeight, Color color, Graphics2D g2d) {
//        g2d.setColor(Color.BLACK);
//        g2d.drawRect(x, y, cellWidth - 1, cellHeight - 1 );
        g2d.setColor(color);
        g2d.fillRect(x, y, cellWidth - 1, cellHeight - 1);
    }

    @Override
    public void clear() {
    }

    @Override
    public void showAliveLabel(int aliveNumber) {
        setAliveLabel(Integer.toString(aliveNumber));
    }

    JLabel buildGenerationLabel(String generationNumber) {
        return buildLabel("Generation #" + generationNumber, "GenerationLabel");
    }

    JLabel buildAliveLabel(String aliveNumber) {
        return buildLabel("Alive : " + aliveNumber, "AliveLabel");
    }

    void setGenerationLabel(String generationNumber) {
        generationLabel.setText("Generation #" + generationNumber);
    }

    void setAliveLabel(String generationNumber) {
        aliveLabel.setText("Alive : " + generationNumber);
    }

    JLabel buildLabel(String text, String name) {
        JLabel label = new JLabel();
        label.setText(text);
        label.setName(name);
        label.setBounds(40, 20, 100, 30);
        return label;
    }

    JPanel buildEmptyLabelWithBlackColour() {
        return buildEmptyLabelWithBackgroundColour(Color.GRAY);
    }

    JPanel buildEmptyLabelWithWhiteColour() {
        return buildEmptyLabelWithBackgroundColour(Color.BLACK);
    }

    JPanel buildEmptyLabelWithBackgroundColour(Color color) {
        JPanel greenPanel = new JPanel();
        greenPanel.setLayout(new BorderLayout());
        greenPanel.setBackground(color);
        greenPanel.setBackground(color);
        return greenPanel;
    }
//    JLabel buildEmptyLabelWithBackgroundColour(Color color) {
//        JLabel label = new JLabel();
//        label.setText("asdas");
//        label.setBackground(color);
////        label.setBounds(40, 20, 100, 100);
//        return label;
//    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    @Override
    public boolean shouldBeRunning() {
        return shouldBeRunning;
    }


}