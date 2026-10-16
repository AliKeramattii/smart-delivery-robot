import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputValidator {
    private static final char START = 'S';
    private static final char CUSTOMER = 'G';
    private static final char OBSTACLE = 'X';
    private static final char TELEPORT = 'T';
    private static final char EMPTY = '.';

    public static void validate(int n, int m, int k, char[][] grid, List<TeleportDefinition> teleports) {
        validateDimensions(n, m, k);
        validateGridShape(n, m, grid);
        validateGridSymbols(n, m, grid);
        validateStartCell(n, m, grid);
        validateTeleports(n, m, grid, teleports);
    }

    private static void validateDimensions(int n, int m, int k) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of rows n must be positive.");
        }
        if (m <= 0) {
            throw new IllegalArgumentException("Number of columns m must be positive.");
        }
        if (k < 0) {
            throw new IllegalArgumentException("Energy k cannot be negative.");
        }
    }

    private static void validateGridShape(int n, int m, char[][] grid) {
        if (grid == null || grid.length != n) {
            throw new IllegalArgumentException("Invalid grid size.");
        }

        for (int i = 0; i < n; i++) {
            if (grid[i] == null || grid[i].length != m) {
                throw new IllegalArgumentException("Invalid grid row size at row " + i + ".");
            }
        }
    }

    private static void validateGridSymbols(int n, int m, char[][] grid) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!isValidCell(grid[i][j])) {
                    throw new IllegalArgumentException(
                            "Invalid cell symbol '" + grid[i][j] + "' at (" + i + "," + j + ")."
                    );
                }
            }
        }
    }

    private static void validateStartCell(int n, int m, char[][] grid) {
        int startCount = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == START) {
                    startCount++;
                }
            }
        }

        if (startCount != 1) {
            throw new IllegalArgumentException("Grid must contain exactly one S, but found " + startCount + ".");
        }
    }

    private static void validateTeleports(int n, int m, char[][] grid, List<TeleportDefinition> teleports) {
        if (teleports == null) {
            throw new IllegalArgumentException("Teleport list cannot be null.");
        }

        int teleportCellCount = countTeleportCells(n, m, grid);

        if (teleports.size() != teleportCellCount) {
            throw new IllegalArgumentException(
                    "Number of teleport definitions must match number of T cells. " +
                            "T cells: " + teleportCellCount + ", definitions: " + teleports.size()
            );
        }

        Set<String> usedSources = new HashSet<>();

        for (TeleportDefinition teleport : teleports) {
            validateSingleTeleport(n, m, grid, teleport, usedSources);
            usedSources.add(teleport.sourceRow + "," + teleport.sourceCol);
        }

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < m; col++) {
                if (grid[row][col] == TELEPORT && !usedSources.contains(row + "," + col)) {
                    throw new IllegalArgumentException(
                            "Teleport cell at (" + row + "," + col + ") has no definition."
                    );
                }
            }
        }
    }

    private static void validateSingleTeleport(
            int n,
            int m,
            char[][] grid,
            TeleportDefinition teleport,
            Set<String> usedSources
    ) {
        if (teleport == null) {
            throw new IllegalArgumentException("Teleport definition cannot be null.");
        }

        if (!isInside(teleport.sourceRow, teleport.sourceCol, n, m)) {
            throw new IllegalArgumentException(
                    "Teleport source is outside grid: (" + teleport.sourceRow + "," + teleport.sourceCol + ")"
            );
        }

        if (!isInside(teleport.destRow, teleport.destCol, n, m)) {
            throw new IllegalArgumentException(
                    "Teleport destination is outside grid: (" + teleport.destRow + "," + teleport.destCol + ")"
            );
        }

        if (grid[teleport.sourceRow][teleport.sourceCol] != TELEPORT) {
            throw new IllegalArgumentException(
                    "Teleport source must be a T cell: (" + teleport.sourceRow + "," + teleport.sourceCol + ")"
            );
        }

        if (grid[teleport.destRow][teleport.destCol] == OBSTACLE) {
            throw new IllegalArgumentException(
                    "Teleport destination cannot be an obstacle X: (" + teleport.destRow + "," + teleport.destCol + ")"
            );
        }

        if (teleport.destRow < teleport.sourceRow || teleport.destCol < teleport.sourceCol) {
            throw new IllegalArgumentException(
                    "Teleport must move forward: (" + teleport.sourceRow + "," + teleport.sourceCol + ") -> (" +
                            teleport.destRow + "," + teleport.destCol + ")"
            );
        }

        String sourceKey = teleport.sourceRow + "," + teleport.sourceCol;

        if (usedSources.contains(sourceKey)) {
            throw new IllegalArgumentException(
                    "Duplicate teleport source: (" + teleport.sourceRow + "," + teleport.sourceCol + ")"
            );
        }
    }

    public static int countTeleportCells(int n, int m, char[][] grid) {
        int count = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == TELEPORT) {
                    count++;
                }
            }
        }

        return count;
    }

    public static boolean isValidCell(char cell) {
        return cell == START || cell == CUSTOMER || cell == OBSTACLE || cell == TELEPORT || cell == EMPTY;
    }

    private static boolean isInside(int row, int col, int n, int m) {
        return row >= 0 && row < n && col >= 0 && col < m;
    }
}
