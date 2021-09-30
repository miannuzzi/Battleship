package battleship;

import java.util.Scanner;

class Coordinates {
    private char x;
    private int y;

    public Coordinates(char x, int y) {
        this.x = x;
        this.y = y;
    }

    public char getX() {
        return x;
    }

    public int getXOrdinal() {
        return x - Battlefield.LOWER_ROW_BOUNDARY;
    }

    public int getY() {
        return y;
    }

    public void setX(char x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}

enum Ship {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    private int size;
    private String name;

    Ship(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public boolean isValid(Coordinates begin, Coordinates end) {
        return (end.getX() == begin.getX() && (end.getY() - begin.getY()) == getSize())
                || (end.getY() == begin.getY() && (end.getX() - begin.getX() == getSize()));
    }
}

class OnBoardShip {
    private Ship ship;
    private CellValue[] cells;
    private int life;
    private boolean isHorizontal;
    private Coordinates begin;
    private Coordinates end;


    public OnBoardShip(Ship ship, Coordinates[] coordinates) {
        this.ship = ship;
        this.cells = new CellValue[ship.getSize()];
        this.life = ship.getSize();

        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = CellValue.FOW;
        }

        this.begin = coordinates[0];
        this.end = coordinates[1];

        this.isHorizontal = begin.getX() == end.getX();

    }

    public boolean isHit(Coordinates coordinates) {
        boolean result = false;
        int beginIndex = begin.getY();
        int endIndex = end.getY();
        boolean isFixed = begin.getXOrdinal() == coordinates.getXOrdinal();
        int shot = coordinates.getY();

        if (!isHorizontal) {
            beginIndex = begin.getXOrdinal();
            endIndex = end.getXOrdinal();

            isFixed = begin.getY() == coordinates.getY();
            shot = coordinates.getXOrdinal();
        }

        if (isFixed && beginIndex <= shot && shot <= endIndex) {
            if (this.cells[shot - beginIndex] != CellValue.HIT) {
                this.life--;//FIXME: the D8 value in the test 2 is hit twice. Then life is -1 and later the areAllSank is not true
                this.cells[shot - beginIndex] = CellValue.HIT;
            }
            result = true;
        }

        return result;
    }

    public boolean isSank() {
        return this.life == 0;
    }

}

enum CellValue {
    FOW('~'),
    OCCUPIED('o'),
    HIT('x'),
    MISS('M');

    private char value;
    String message;

    CellValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return this.value;
    }

}

enum InputState {
    AIRCRAFT_CARRIER {
        @Override
        public InputState nextState() {
            return BATTLESHIP;
        }

        @Override
        public Ship getShip() {
            return Ship.AIRCRAFT_CARRIER;
        }
    },

    BATTLESHIP {
        @Override
        public InputState nextState() {
            return SUBMARINE;
        }

        @Override
        public Ship getShip() {
            return Ship.BATTLESHIP;
        }
    },

    SUBMARINE {
        @Override
        public InputState nextState() {
            return CRUISER;
        }

        @Override
        public Ship getShip() {
            return Ship.SUBMARINE;
        }
    },

    CRUISER {
        @Override
        public InputState nextState() {
            return DESTROYER;
        }

        @Override
        public Ship getShip() {
            return Ship.CRUISER;
        }

    },

    DESTROYER {
        @Override
        public InputState nextState() {
            return EXIT;
        }

        @Override
        public Ship getShip() {
            return Ship.DESTROYER;
        }
    },
    EXIT {
        @Override
        public InputState nextState() {
            return null;
        }

        @Override
        public Ship getShip() {
            return null;
        }
    };

    public abstract InputState nextState();


    public abstract Ship getShip();


}

class Battlefield {
    private static final int ROWS = 10;
    private static final int COLUMNS = 10;
    public static final int LOWER_ROW_BOUNDARY = 65;
    public static final int UPPER_ROW_BOUNDARY = LOWER_ROW_BOUNDARY + ROWS;

    private CellValue[][] grid = new CellValue[ROWS][COLUMNS];


    public void loadBattlefield(Coordinates coordinates, CellValue value) {
        grid[coordinates.getXOrdinal()][coordinates.getY()] = value;
    }

    public CellValue getCellValue(Coordinates coordinates) {
        return grid[coordinates.getXOrdinal()][coordinates.getY()];
    }

    public void printBattlefield() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");

        for (int i = 0; i < ROWS; i++) {
            System.out.print((char) (i + LOWER_ROW_BOUNDARY));
            for (int j = 0; j < COLUMNS; j++) {
                System.out.printf(" %s", grid[i][j].getValue());
            }
            System.out.println();
        }
        //System.out.println();
    }

    public void printFOWBattlefield() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");

        for (int i = 0; i < ROWS; i++) {
            System.out.print((char) (i + LOWER_ROW_BOUNDARY));
            for (int j = 0; j < COLUMNS; j++) {
                if (grid[i][j] == CellValue.MISS || grid[i][j] == CellValue.HIT) {
                    System.out.printf(" %s", grid[i][j].getValue());
                } else {
                    System.out.printf(" %s", CellValue.FOW.getValue());
                }

            }
            System.out.println();
        }
        //System.out.println();
    }

    public void initializeBattlefield() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                grid[i][j] = CellValue.FOW;
            }
        }
    }

    public boolean isValidCoordinate(Coordinates begin, Coordinates end) {
        return //check begin coordinate between boundaries
                begin.getX() >= LOWER_ROW_BOUNDARY && begin.getX() < UPPER_ROW_BOUNDARY
                        && begin.getY() >= 0 && begin.getY() < COLUMNS

                        //check begin coordinate between boundaries
                        && end.getX() >= LOWER_ROW_BOUNDARY && end.getX() < UPPER_ROW_BOUNDARY
                        && end.getY() >= 0 && end.getY() < COLUMNS

                        //Check they are aligned
                        && (begin.getX() == end.getX() || begin.getY() == end.getY());
    }

    public boolean isValidShot(Coordinates coordinates) {
        return //check begin coordinate between boundaries
                coordinates.getX() >= LOWER_ROW_BOUNDARY && coordinates.getX() < UPPER_ROW_BOUNDARY
                        && coordinates.getY() >= 0 && coordinates.getY() < COLUMNS;
    }


    public boolean isOverlapped(Ship ship, Coordinates begin, Coordinates end) {
        boolean isHorizontal = begin.getX() == end.getX();
        int variable = begin.getY();

        int fixed = begin.getXOrdinal();
        boolean isOccupied = false;

        if (!isHorizontal) {
            variable = begin.getXOrdinal();
            fixed = begin.getY();
        }

        int variableBoundary = (variable + ship.getSize()) >= COLUMNS ? COLUMNS : variable + ship.getSize() + 1;

        if (variable > 0) {
            variable--;
        }

        for (int i = variable; i < variableBoundary; i++) {
            if (isHorizontal) {
                if (fixed - 1 >= 0) {
                    isOccupied = isOccupied || grid[fixed - 1][i] == CellValue.OCCUPIED;
                }
                isOccupied = isOccupied || grid[fixed][i] == CellValue.OCCUPIED;

                if (fixed + 1 < COLUMNS) {
                    isOccupied = isOccupied || grid[fixed + 1][i] == CellValue.OCCUPIED;
                }
            } else {
                if (fixed - 1 >= 0) {
                    isOccupied = isOccupied || grid[i][fixed - 1] == CellValue.OCCUPIED;
                }
                isOccupied = isOccupied || grid[i][fixed] == CellValue.OCCUPIED;

                if (fixed + 1 < COLUMNS) {
                    isOccupied = isOccupied || grid[i][fixed + 1] == CellValue.OCCUPIED;
                }
            }
        }

        return isOccupied;
    }

    public boolean isValidLength(Coordinates begin, Coordinates end, int length) {
        return (begin.getXOrdinal() - end.getXOrdinal() == 0 && end.getY() - begin.getY() + 1 == length)
                || (begin.getY() - end.getY() == 0 && end.getXOrdinal() - begin.getXOrdinal() + 1 == length);
    }

    public void setShip(Ship ship, Coordinates begin, Coordinates end) {
        boolean isHorizontal = begin.getX() == end.getX();
        int variable = begin.getY();
        int fixed = begin.getXOrdinal();

        if (!isHorizontal) {
            variable = begin.getXOrdinal();
            fixed = begin.getY();
        }

        for (int i = variable; i < (variable + ship.getSize()); i++) {
            if (isHorizontal) {
                grid[fixed][i] = CellValue.OCCUPIED;
            } else {
                grid[i][fixed] = CellValue.OCCUPIED;
            }
        }
    }
}


class Player {

    private final static String HIT_MESSAGE = "You hit a ship! Try again:";
    private final static String SANK_MESSAGE = "You sank a ship! Specify a new target:";
    private final static String CONGRATULATIONS_MESSAGE = "You sank the last ship. You won. Congratulations!";

    private final static int SHIPS_AMOUNT = 5;

    private OnBoardShip[] onBoardShips = new OnBoardShip[SHIPS_AMOUNT];
    private Battlefield battlefield;

    private String name;

    public Player(String pName) {
        this.name = pName;
        this.battlefield = new Battlefield();
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(Battlefield battlefield) {
        this.battlefield = battlefield;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hit(Coordinates coordinates) {
        boolean areAllSank = true;
        boolean isSank = false;

        for (OnBoardShip ship : onBoardShips) {
            if (ship.isHit(coordinates)) {
                if (ship.isSank()) {
                    isSank = true;
                } else {
                    System.out.printf("%n%s%n", HIT_MESSAGE);
                }
            }

            areAllSank = areAllSank && ship.isSank();
        }

        if (areAllSank) {
            System.out.printf("%n%s%n", CONGRATULATIONS_MESSAGE);
        } else if (isSank) {
            System.out.printf("%n%s%n", SANK_MESSAGE);
        }

        return areAllSank;
    }

    public static Coordinates[] getCoordinates() {
        Scanner scanner = new Scanner(System.in);
        String[] input = scanner.nextLine().split("\\s");

        Coordinates[] coordinates = new Coordinates[2];
        coordinates[0] = new Coordinates(input[0].charAt(0), Integer.valueOf(input[0].substring(1)) - 1);
        coordinates[1] = new Coordinates(input[1].charAt(0), Integer.valueOf(input[1].substring(1)) - 1);

        if (coordinates[1].getXOrdinal() - coordinates[0].getXOrdinal() < 0
                || coordinates[1].getY() - coordinates[0].getY() < 0) {
            Coordinates aux = coordinates[0];
            coordinates[0] = coordinates[1];
            coordinates[1] = aux;
        }

        return coordinates;
    }

    public Coordinates[] inputCoordinates(Battlefield battlefield, Ship ship) {
        boolean result = false;
        Coordinates[] coordinates = new Coordinates[2];

        while (!result) {
            System.out.println(getInputMessage(ship));
            System.out.println();
            coordinates = getCoordinates();

            result = true;

            if (!battlefield.isValidCoordinate(coordinates[0], coordinates[1])) {
                System.out.println();
                System.out.println(getLocationErrorMessage());
                System.out.println();
                result = false;
            } else if (!battlefield.isValidLength(coordinates[0], coordinates[1], ship.getSize())) {
                System.out.println();
                System.out.println(getLengthErrorMessage(ship));
                System.out.println();
                result = false;
            } else if (battlefield.isOverlapped(ship, coordinates[0], coordinates[1])) {
                System.out.println();
                System.out.println(getOverlappedErrorMessage());
                System.out.println();
                result = false;
            }
        }

        return coordinates;
    }

    public void placeShips() {
        InputState state = InputState.AIRCRAFT_CARRIER;
        int i = 0;

        battlefield.initializeBattlefield();
        battlefield.printBattlefield();

        while (state != InputState.EXIT) {

            Coordinates[] coordinates = inputCoordinates(this.getBattlefield(), state.getShip());
            this.getBattlefield().setShip(state.getShip(), coordinates[0], coordinates[1]);
            this.getBattlefield().printBattlefield();
            onBoardShips[i] = new OnBoardShip(state.getShip(), coordinates);
            state = state.nextState();
            i++;
        }
    }



    public CellValue shot(Coordinates coordinates) {
        CellValue actual = this.getBattlefield().getCellValue(coordinates);
        CellValue result = CellValue.MISS;

        if (actual == CellValue.OCCUPIED || actual == CellValue.HIT) {
            result = CellValue.HIT;
        }

        this.getBattlefield().loadBattlefield(coordinates, result);

        return result;
    }

    public static String getInputMessage(Ship ship) {
        return String.format("Enter the coordinates of the %s (%d cells):", ship.getName(), ship.getSize());
    }

    public static String getLengthErrorMessage(Ship ship) {
        return String.format("Error! Wrong length of the %s! Try again:", ship.getName());
    }

    public static String getLocationErrorMessage() {
        return "Error! Wrong ship location! Try again:";
    }

    public static String getOverlappedErrorMessage() {
        return "Error! You placed it too close to another one. Try again:";
    }
}

public class Main {


    private final static String START_MESSAGE = "The game starts!";
    private final static String SHOT_MESSAGE = "Take a shot!";
    private final static String MISSED_MESSAGE = "You missed. Try again:";
    private final static String INVALID_SHOT_MESSAGE = "Error! You entered the wrong coordinates! Try again:";
    private final static String SET_SHIPS_MESSAGE = ", place your ships on the game field";
    private final static String TURN_CHANGE_MESSAGE = "Press Enter and pass the move to another player";
    private final static String TURN_START_MESSAGE = ", it's your turn:";



    public static void main(String[] args) {
        // Write your code here

        Main main = new Main();
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        System.out.printf("%n%s%s%n%n", player1.getName(), SET_SHIPS_MESSAGE);
        player1.placeShips();

        //System.out.printf("%n%s%n%n", START_MESSAGE);
        main.changeTurn();

       // player1Battlefield.printFOWBattlefield();

        System.out.printf("%n%s%s%n%n", "Player 2", SET_SHIPS_MESSAGE);

        player2.placeShips();
        main.changeTurn();
        //player1Battlefield.printFOWBattlefield();

        CellValue shotState = CellValue.MISS;

        // while (shotState == CellValue.MISS) {
        System.out.printf("%n%s%n", SHOT_MESSAGE);

        boolean areAllSank = false;

        Player currentPlayer = player1;
        Player waitingPlayer = player2;

        while (!areAllSank) {

            main.displayBoard(currentPlayer,waitingPlayer);

            Coordinates coordinates = main.getShotCoordinate();
            boolean isValidShot = currentPlayer.getBattlefield().isValidShot(coordinates);

            while (!isValidShot) {

                System.out.printf("%n%s%n%n", INVALID_SHOT_MESSAGE);

                coordinates = main.getShotCoordinate();
                isValidShot = currentPlayer.getBattlefield().isValidShot(coordinates);
            }


            //shotState = currentPlayer.shot(coordinates);
            shotState = waitingPlayer.shot(coordinates);

           // currentBattlefield.printFOWBattlefield();

            switch (shotState) {
                case MISS:
                    System.out.printf("%n%s%n%n", MISSED_MESSAGE);
                    break;

                case HIT:
                    areAllSank = waitingPlayer.hit(coordinates);
                    break;
            }

            Player swap = currentPlayer;
            currentPlayer = waitingPlayer;
            waitingPlayer = swap;

            main.changeTurn();

        }

    }

    private Coordinates getShotCoordinate() {
        Scanner scanner = new Scanner((System.in));
        String raw = scanner.nextLine();
        Coordinates coordinates = new Coordinates(raw.charAt(0), Integer.valueOf(raw.substring(1)) - 1);

        return coordinates;
    }


    private void changeTurn() {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%n%s%n%n", TURN_CHANGE_MESSAGE);
        String input = scanner.nextLine();
    }

    private void changeTurn(Player currentPlayer, Player waitingPlayer) {
        Player swap = currentPlayer;
        currentPlayer = waitingPlayer;
        waitingPlayer = swap;

        Scanner scanner = new Scanner(System.in);
        System.out.printf("%n%s%n%n", TURN_CHANGE_MESSAGE);
        String input = scanner.nextLine();
    }

    private void displayBoard(Player currentPlayer, Player waitingPlayer) {
        waitingPlayer.getBattlefield().printFOWBattlefield();
        System.out.println("---------------------");
        currentPlayer.getBattlefield().printBattlefield();

        System.out.printf("%n%s%s%n%n", currentPlayer.getName(), TURN_START_MESSAGE);
    }


}

