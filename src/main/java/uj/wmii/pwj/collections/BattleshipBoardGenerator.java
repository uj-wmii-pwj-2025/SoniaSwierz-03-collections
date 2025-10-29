package uj.wmii.pwj.collections;

import java.util.Random;

public class BattleshipBoardGenerator implements BattleshipGenerator {

    private final int SIZE = 10;
    private final char WATER = '.';
    private final char BLOCKED = 'x';

    private final int[][] directions = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private final char[][] board = new char[SIZE][SIZE];
    private final Random random = new Random();

    public BattleshipBoardGenerator() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = WATER;
    }

    public String generateMap() {
        placeShips(4, 1);
        placeShips(3, 2);
        placeShips(2, 3);
        placeShips(1, 4);

        StringBuilder generatedMap = new StringBuilder(SIZE * SIZE);
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) {
                if(board[i][j] == BLOCKED)
                    generatedMap.append(WATER);
                else
                    generatedMap.append(board[i][j]);
            }
        return generatedMap.toString();
    }

    private void placeShips(int shipSize, int shipCount) {
        for (int i = 0; i < shipCount; i++) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(SIZE);
                int column = random.nextInt(SIZE);
                if (board[row][column] == WATER && canPlaceHere(row, column)) {
                    placed = tryBuildShip(row, column, shipSize);
                }
            }
        }
    }

    private boolean tryBuildShip(int startingRow, int startingColumn, int size) {
        char ship = '#';
        board[startingRow][startingColumn] = ship;

        int[] positions = new int[size];
        positions[0] = startingRow * SIZE + startingColumn;

        int currentRow = startingRow;
        int currentColumn = startingColumn;

        for (int i = 1; i < size; i++) {
            int direction = random.nextInt(4);
            switch (direction) {
                case 0 -> currentRow--;
                case 1 -> currentRow++;
                case 2 -> currentColumn--;
                case 3 -> currentColumn++;
            }

            if (!isInsideBoard(currentRow, currentColumn) || board[currentRow][currentColumn] != WATER || !canPlaceHere(currentRow, currentColumn)) {
                for (int j = 0; j < i; j++)
                    board[positions[j] / SIZE][positions[j] % SIZE] = WATER;
                return false;
            }

            board[currentRow][currentColumn] = ship;
            positions[i] = currentRow * SIZE + currentColumn;
        }

        for (int cell : positions)
            markAround(cell);

        return true;
    }

    private void markAround(int cell) {
        int row = cell / SIZE;
        int column = cell % SIZE;

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];
            if (isInsideBoard(newRow, newColumn) && board[newRow][newColumn] == WATER) {
                board[newRow][newColumn] = BLOCKED;
            }
        }
    }

    private boolean canPlaceHere(int row, int column) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];
            if (isInsideBoard(newRow, newColumn) && board[newRow][newColumn] == BLOCKED)
                return false;
        }
        return true;
    }

    private boolean isInsideBoard(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }
}