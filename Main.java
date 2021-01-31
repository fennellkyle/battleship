import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static boolean allShipsSunk = false;

    public static void main(String[] args) {

        // Let's setup the boards
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Player[] playerList = { player1, player2 };

        // Set up turns
        Scanner nextTurn = new Scanner(System.in);

        // Place pieces
        for (int i = 0; i < 2; i++) {
            System.out.println(playerList[i].name + ", place your ships on the game field");
            for (Ship shipName : playerList[i].shipList) {
                playerList[i].printBoard("visible");
                placeShip(playerList[i].visibleBoard, shipName);
            }
            playerList[i].printBoard("visible");
            System.out.println("Press Enter and pass the move to another player");
            nextTurn.nextLine();
        }

        // Play the game
        for (int changeTurns = 0; !allShipsSunk; changeTurns++) {
            Player currentPlayer = changeTurns % 2 == 0 ? player1 : player2;
            Player otherPlayer = changeTurns % 2 == 0 ? player2 : player1;

            otherPlayer.printBoard("hidden");
            System.out.println("---------------------");
            currentPlayer.printBoard("visible");
            System.out.printf("%s, it's your turn:%n", currentPlayer.name);
            takeAShot(otherPlayer);
            if (!allShipsSunk) {
                System.out.println("Press Enter and pass the move to another player");
                nextTurn.nextLine();
            }
        }
        nextTurn.close();

    }

    static class Ship {
        private final String name;
        int cells;
        ArrayList<String> coordinates = new ArrayList<String>();
        boolean isSunk = false;

        public Ship(String name, int cells) {
            this.name = name;
            this.cells = cells;
        }

        public String getName() {
            return name;
        }

        public int getLength() {
            return cells;
        }
    }

    static class Player {
        private final String name;
        Board visibleBoard;
        Board hiddenBoard;
        String whichBoard;
        Ship aircraftCarrier;
        Ship battleship;
        Ship submarine;
        Ship cruiser;
        Ship destroyer;
        Ship[] shipList;

        public Player(String name) {
            this.name = name;
            this.visibleBoard = new Board();
            this.hiddenBoard = new Board();
            this.aircraftCarrier = new Ship("Aircraft Carrier", 5);
            this.battleship = new Ship("Battleship", 4);
            this.submarine = new Ship("Submarine", 3);
            this.cruiser = new Ship("Cruiser", 3);
            this.destroyer = new Ship("Destroyer", 2);
            this.shipList = new Ship[] { aircraftCarrier, battleship, submarine, cruiser, destroyer };
        }

        public void printBoard(String whichBoard) {
            for (int row = 0; row < 11; row++) {
                for (int col = 0; col < 11; col++) {
                    if ("visible".equals(whichBoard)) {
                        System.out.print(visibleBoard.board[row][col] + " ");
                    } else if ("hidden".equals(whichBoard)) {
                        System.out.print(hiddenBoard.board[row][col] + " ");
                    }
                }
                System.out.println();
            }
        }
    }

    static class Board {
        String[][] board = new String[11][11];
        String[] xCord = { " ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        String[] yCord = { " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };

        public Board() {
            for (int row = 0; row < 11; row++) {
                for (int col = 0; col < 11; col++) {
                    if (row == 0) {
                        board[row][col] = xCord[col];
                    } else if (col == 0) {
                        board[row][col] = yCord[row];
                    } else {
                        board[row][col] = "~";
                    }
                }
            }
        }
    }

    static void placeShip(Board visibleBoard, Ship ship) {
        Scanner scanner = new Scanner(System.in);
        String[][] board = visibleBoard.board;
        System.out.printf("Enter the coordinates of the %s (%d cells):%n", ship.getName(), ship.getLength());
        boolean notPlaced = true;
        while (notPlaced) {
            String[] shipCoordinates = scanner.nextLine().split(" ");
            String firstPoint = shipCoordinates[0];
            String secondPoint = shipCoordinates[1];

            // Horizontal
            if (firstPoint.charAt(0) == secondPoint.charAt(0)) {
                char rowLetter = firstPoint.charAt(0);
                for (int row = 1; row < 11; row++) {
                    if (board[row][0].charAt(0) == rowLetter) {
                        int startCol = Integer.parseInt(firstPoint.substring(1));
                        int endCol = Integer.parseInt(secondPoint.substring(1));

                        // verify positive order
                        if (endCol < startCol) {
                            int temp = startCol;
                            startCol = endCol;
                            endCol = temp;
                        }

                        // Verify length
                        if (endCol - startCol + 1 != ship.getLength()) {
                            System.out.printf("Error! Wrong length of the %s! Try again:\n", ship.getName());
                            break;
                        }

                        // Verify it's not in proximity of others
                        boolean proximityAlert = false;
                        if ("O".equals(board[row][startCol - 1])) {
                            proximityAlert = true;
                        }
                        if (endCol != 10 && "O".equals(board[row][endCol + 1])) {
                            proximityAlert = true;
                        }
                        for (int col = startCol; col <= endCol; col++) {
                            if ("O".equals(board[row][col]) || "O".equals(board[row - 1][col])) {
                                proximityAlert = true;
                                break;
                            }
                            if (row != 10 && "O".equals(board[row + 1][col])) {
                                proximityAlert = true;
                                break;
                            }
                        }

                        // Place the ship
                        if (!proximityAlert) {
                            for (int col = startCol; col <= endCol; col++) {
                                board[row][col] = "O";
                                ship.coordinates.add(String.format("%s%d", firstPoint.substring(0, 1), col));
                            }
                            notPlaced = false;
                        } else {
                            System.out.println("Error! You placed it too close to another one. Try again:");
                        }
                    }
                }

                // Vertical
            } else if (firstPoint.substring(1).equals(secondPoint.substring(1))) {
                int col = Integer.parseInt(firstPoint.substring(1));
                char startRow = firstPoint.charAt(0);
                char endRow = secondPoint.charAt(0);
                boolean placementStarted = false;
                boolean lengthVerified = false;

                // verify positive order
                if (endRow < startRow) {
                    char temp = startRow;
                    startRow = endRow;
                    endRow = temp;
                }

                // Verify length
                if (endRow - startRow + 1 == ship.getLength()) {
                    lengthVerified = true;
                }
                // Verify proximity to others
                boolean proximityAlert = false;
                for (int row = 1; row < 11; row++) {
                    if (board[row][0].charAt(0) >= startRow && board[row][0].charAt(0) <= endRow) {
                        if ("O".equals(board[row][col]) || "O".equals(board[row][col - 1])) {
                            proximityAlert = true;
                        }
                        if (col != 10 && "O".equals(board[row][col + 1])) {
                            proximityAlert = true;
                        }
                    }
                    if (board[row][0].charAt(0) == startRow) {
                        if ("O".equals(board[row - 1][col])) {
                            proximityAlert = true;
                        }
                    } else if (board[row][0].charAt(0) == endRow) {
                        if (row != 10) {
                            if ("O".equals(board[row + 1][col])) {
                                proximityAlert = true;
                            }
                        }
                    }
                }

                // Place ship
                if (!proximityAlert && lengthVerified) {
                    for (int row = 1; row < 11; row++) {
                        if (board[row][0].charAt(0) == startRow) {
                            board[row][col] = "O";
                            ship.coordinates.add(String.format("%s%d", board[row][0], col));
                            placementStarted = true;
                        } else if (board[row][0].charAt(0) == endRow) {
                            board[row][col] = "O";
                            ship.coordinates.add(String.format("%s%d", board[row][0], col));
                            break;
                        } else if (placementStarted) {
                            board[row][col] = "O";
                            ship.coordinates.add(String.format("%s%d", board[row][0], col));
                        } else {
                            continue;
                        }
                    }
                    notPlaced = false;
                } else if (!lengthVerified) {
                    System.out.printf("Error! Wrong length of the %s! Try again:%n", ship.getName());
                } else if (proximityAlert) {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                }
            } else {
                System.out.println("Error! Wrong ship location! Try again:");
            }
        }
    }

    static void takeAShot(Player otherPlayer) {
        Scanner scanner = new Scanner(System.in);
        String[][] board = otherPlayer.visibleBoard.board;
        String[][] hiddenView = otherPlayer.hiddenBoard.board;
        boolean completed = false;
        while (!completed) {
            String shot = scanner.nextLine();
            if (!shot.substring(0, 1).matches("[ABCDEFGHIJ]")) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    if (board[i][0].charAt(0) == shot.charAt(0)) {
                        if (j == Integer.parseInt(shot.substring(1))) {
                            if ("O".equals(board[i][j]) || "X".equals(board[i][j])) {
                                board[i][j] = "X";
                                hiddenView[i][j] = "X";
                                boolean sunkShip = checkSunkShip(shot, otherPlayer.shipList);
                                boolean allShipsSunk = checkAllShipsSunk(otherPlayer.shipList);
                                if (allShipsSunk) {
                                    System.out.println("You sank the last ship. You won. Congratulations!");
                                } else if (sunkShip) {
                                    System.out.println("You sank a ship!");
                                } else {
                                    System.out.println("You hit a ship!");
                                }
                                completed = true;
                            } else {
                                board[i][j] = "M";
                                hiddenView[i][j] = "M";
                                System.out.println("You missed!");
                                completed = true;
                            }
                        }
                    }
                }
            }
        }
    }

    static boolean checkSunkShip(String shot, Ship[] shipList) {
        for (Ship shipName : shipList) {
            if (!shipName.isSunk) {
                for (int i = 0; i < shipName.coordinates.size(); i++) {
                    if (shot.equals(shipName.coordinates.get(i))) {
                        shipName.coordinates.remove(i);
                    }
                }
                if (shipName.coordinates.size() == 0) {
                    shipName.isSunk = true;
                    return true;
                }
            }
        }
        return false;
    }

    static boolean checkAllShipsSunk(Ship[] shipList) {
        for (Ship shipName : shipList) {
            if (!shipName.isSunk) {
                return false;
            }
        }
        allShipsSunk = true;
        return true;
    }

}
