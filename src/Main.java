import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printWelcomeMessage();

        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runManualInputMode();
                    break;
                case "2":
                    runPasteInputMode();
                    break;
                case "3":
                    runBuiltInSample();
                    break;
                case "4":
                    printInputGuide();
                    break;
                case "0":
                    System.out.println("Program finished.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose 0, 1, 2, 3, or 4.");
            }

            if (running) {
                System.out.println();
            }
        }

        scanner.close();
    }

    private static void runManualInputMode() {
        try {
            System.out.println();
            System.out.println("Manual Input Mode");
            System.out.println("-----------------");

            int n = readPositiveInt("Enter number of rows n: ");
            int m = readPositiveInt("Enter number of columns m: ");
            int k = readNonNegativeInt("Enter initial energy k: ");

            char[][] grid = new char[n][m];

            System.out.println();
            System.out.println("Enter the grid row by row.");
            System.out.println("Allowed symbols: S, G, X, T, .");
            System.out.println("Example row for 5 columns: S G . X .");

            for (int row = 0; row < n; row++) {
                grid[row] = readGridRow(row, m);
            }

            int teleportCount = InputValidator.countTeleportCells(n, m, grid);
            List<TeleportDefinition> teleports = new ArrayList<>();

            if (teleportCount > 0) {
                System.out.println();
                System.out.println("This grid contains " + teleportCount + " teleport cell(s).");
                System.out.println("Enter exactly " + teleportCount + " teleport definition(s).");
            }

            for (int i = 1; i <= teleportCount; i++) {
                System.out.println();
                System.out.println("Teleport " + i + ":");

                int sourceRow = readIntInRange("Source row: ", 0, n - 1);
                int sourceCol = readIntInRange("Source column: ", 0, m - 1);
                int destRow = readIntInRange("Destination row: ", 0, n - 1);
                int destCol = readIntInRange("Destination column: ", 0, m - 1);

                teleports.add(new TeleportDefinition(sourceRow, sourceCol, destRow, destCol));
            }

            InputValidator.validate(n, m, k, grid, teleports);
            solveAndPrint(n, m, k, grid, new TeleportManager(teleports));
        } catch (Exception e) {
            printError(e);
        }
    }

    private static void runPasteInputMode() {
        try {
            System.out.println();
            System.out.println("Paste Input Mode");
            System.out.println("----------------");
            System.out.println("Paste the full input. When finished, type END on a new line.");
            System.out.println();

            List<String> lines = new ArrayList<>();

            while (true) {
                String line = scanner.nextLine();

                if (line.trim().equalsIgnoreCase("END")) {
                    break;
                }

                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }

            ParsedInput input = parseFullInput(lines);
            solveAndPrint(input.n, input.m, input.k, input.grid, input.teleportManager);
        } catch (Exception e) {
            printError(e);
        }
    }

    private static void runBuiltInSample() {
        try {
            System.out.println();
            System.out.println("Running official 6x6 sample test...");
            System.out.println();

            List<String> lines = new ArrayList<>();
            lines.add("6 6 11");
            lines.add("S . . X . G");
            lines.add(". X . X . .");
            lines.add(". . T . X .");
            lines.add("X . . . . .");
            lines.add(". X . X . G");
            lines.add(". . . . . G");
            lines.add("Teleport: (2,2) -> (4,4)");

            System.out.println("Input:");
            for (String line : lines) {
                System.out.println(line);
            }

            ParsedInput input = parseFullInput(lines);
            solveAndPrint(input.n, input.m, input.k, input.grid, input.teleportManager);
        } catch (Exception e) {
            printError(e);
        }
    }

    private static ParsedInput parseFullInput(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Input is empty.");
        }

        String[] firstLine = lines.get(0).trim().split("\\s+");

        if (firstLine.length != 3) {
            throw new IllegalArgumentException("First line must contain exactly three integers: n m k");
        }

        int n = parseInteger(firstLine[0], "n");
        int m = parseInteger(firstLine[1], "m");
        int k = parseInteger(firstLine[2], "k");

        if (lines.size() < 1 + n) {
            throw new IllegalArgumentException("Not enough grid rows. Expected " + n + " grid row(s).");
        }

        char[][] grid = new char[n][m];

        for (int row = 0; row < n; row++) {
            grid[row] = parseGridRow(lines.get(row + 1), row, m);
        }

        List<TeleportDefinition> teleports = new ArrayList<>();

        for (int i = 1 + n; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (!line.startsWith("Teleport")) {
                throw new IllegalArgumentException(
                        "Invalid extra line after grid. Expected teleport format, got: " + line
                );
            }

            int[] numbers = extractNumbers(line);

            if (numbers.length != 4) {
                throw new IllegalArgumentException("Invalid teleport format: " + line);
            }

            teleports.add(new TeleportDefinition(numbers[0], numbers[1], numbers[2], numbers[3]));
        }

        InputValidator.validate(n, m, k, grid, teleports);
        return new ParsedInput(n, m, k, grid, new TeleportManager(teleports));
    }

    private static char[] parseGridRow(String line, int rowIndex, int m) {
        String[] parts = line.trim().split("\\s+");

        if (parts.length != m) {
            throw new IllegalArgumentException("Grid row " + rowIndex + " must contain exactly " + m + " symbols.");
        }

        char[] row = new char[m];

        for (int col = 0; col < m; col++) {
            if (parts[col].length() != 1) {
                throw new IllegalArgumentException("Invalid cell '" + parts[col] + "' at (" + rowIndex + "," + col + ").");
            }

            char cell = parts[col].charAt(0);

            if (!InputValidator.isValidCell(cell)) {
                throw new IllegalArgumentException("Invalid grid symbol '" + cell + "' at (" + rowIndex + "," + col + ").");
            }

            row[col] = cell;
        }

        return row;
    }

    private static char[] readGridRow(int rowIndex, int m) {
        while (true) {
            System.out.print("Row " + rowIndex + ": ");
            String line = scanner.nextLine().trim();

            try {
                return parseGridRow(line, rowIndex, m);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void solveAndPrint(int n, int m, int k, char[][] grid, TeleportManager teleportManager) {
        RobotSolver solver = new RobotSolver(n, m, k, grid, teleportManager);
        solver.solve();
        Result result = solver.getResult();

        System.out.println();
        System.out.println("Result");
        System.out.println("------");
        System.out.println("Max customers served: " + result.maxCustomers);
        System.out.println("Energy used: " + result.energyUsed);
        System.out.println("Path:");
        System.out.println(String.join(" -> ", result.path));
    }

    private static int readPositiveInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Error: value must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid integer.");
            }
        }
    }

    private static int readNonNegativeInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
                System.out.println("Error: value cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid integer.");
            }
        }
    }

    private static int readIntInRange(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Error: value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid integer.");
            }
        }
    }

    private static int parseInteger(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a valid integer.");
        }
    }

    private static int[] extractNumbers(String line) {
        List<Integer> numbers = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (Character.isDigit(ch)) {
                current.append(ch);
            } else if (current.length() > 0) {
                numbers.add(Integer.parseInt(current.toString()));
                current.setLength(0);
            }
        }

        if (current.length() > 0) {
            numbers.add(Integer.parseInt(current.toString()));
        }

        int[] result = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            result[i] = numbers.get(i);
        }
        return result;
    }

    private static void printWelcomeMessage() {
        System.out.println("==============================================");
        System.out.println(" Intelligent Delivery Robot - DP Solver");
        System.out.println(" Algorithm Design Course Project");
        System.out.println("==============================================");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Choose an option:");
        System.out.println("1. Enter input manually");
        System.out.println("2. Paste full input");
        System.out.println("3. Run official built-in sample test");
        System.out.println("4. Show input guide");
        System.out.println("0. Exit");
        System.out.print("Your choice: ");
    }

    private static void printInputGuide() {
        System.out.println();
        System.out.println("Input Guide");
        System.out.println("-----------");
        System.out.println("First line: n m k");
        System.out.println("n = number of rows, m = number of columns, k = initial energy");
        System.out.println();
        System.out.println("Allowed grid symbols:");
        System.out.println("S = start, G = customer, X = obstacle, T = teleport, . = empty cell");
        System.out.println();
        System.out.println("Rules:");
        System.out.println("1. The grid must contain exactly one S.");
        System.out.println("2. Every T cell must have exactly one teleport definition.");
        System.out.println("3. A teleport source must be a T cell.");
        System.out.println("4. A teleport destination cannot be X.");
        System.out.println("5. Teleports must move forward: destinationRow >= sourceRow and destinationCol >= sourceCol.");
        System.out.println();
        System.out.println("Teleport format:");
        System.out.println("Teleport: (sourceRow,sourceCol) -> (destRow,destCol)");
        System.out.println();
        System.out.println("Official sample:");
        System.out.println("6 6 11");
        System.out.println("S . . X . G");
        System.out.println(". X . X . .");
        System.out.println(". . T . X .");
        System.out.println("X . . . . .");
        System.out.println(". X . X . G");
        System.out.println(". . . . . G");
        System.out.println("Teleport: (2,2) -> (4,4)");
    }

    private static void printError(Exception e) {
        System.out.println();
        System.out.println("An error occurred.");
        System.out.println("Reason: " + e.getMessage());
        System.out.println("Please check your input and try again.");
    }

    private static class ParsedInput {
        final int n;
        final int m;
        final int k;
        final char[][] grid;
        final TeleportManager teleportManager;

        ParsedInput(int n, int m, int k, char[][] grid, TeleportManager teleportManager) {
            this.n = n;
            this.m = m;
            this.k = k;
            this.grid = grid;
            this.teleportManager = teleportManager;
        }
    }
}
