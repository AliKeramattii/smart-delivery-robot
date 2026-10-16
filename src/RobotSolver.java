import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RobotSolver {
    private static final int UNREACHABLE = -1;

    private static final char START = 'S';
    private static final char CUSTOMER = 'G';
    private static final char OBSTACLE = 'X';
    private static final char TELEPORT = 'T';

    private static final int[][] DIRECTIONS = {
            {0, 1}, // right
            {1, 0}  // down
    };

    private final int n;
    private final int m;
    private final int k;
    private final char[][] grid;
    private final TeleportManager teleportManager;

    private int[][][] dp;
    private Parent[][][] parent;

    private int startRow;
    private int startCol;

    private int bestRow;
    private int bestCol;
    private int bestEnergy;
    private int bestCustomers;

    public RobotSolver(int n, int m, int k, char[][] grid, TeleportManager teleportManager) {
        this.n = n;
        this.m = m;
        this.k = k;
        this.grid = grid;
        this.teleportManager = teleportManager;

        findStart();
        initializeDP();
    }

    private void findStart() {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < m; col++) {
                if (grid[row][col] == START) {
                    startRow = row;
                    startCol = col;
                    return;
                }
            }
        }

        throw new IllegalArgumentException("Start cell S not found.");
    }

    private void initializeDP() {
        dp = new int[n][m][k + 1];
        parent = new Parent[n][m][k + 1];

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < m; col++) {
                Arrays.fill(dp[row][col], UNREACHABLE);
            }
        }

        dp[startRow][startCol][0] = 0;

        bestRow = startRow;
        bestCol = startCol;
        bestEnergy = 0;
        bestCustomers = 0;
    }

    public void solve() {
        for (int energy = 0; energy <= k; energy++) {
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < m; col++) {
                    if (dp[row][col][energy] == UNREACHABLE) {
                        continue;
                    }

                    updateBest(row, col, energy);

                    for (int[] direction : DIRECTIONS) {
                        int nextRow = row + direction[0];
                        int nextCol = col + direction[1];
                        processMove(row, col, energy, nextRow, nextCol);
                    }
                }
            }
        }
    }

    private void processMove(int row, int col, int energy, int nextRow, int nextCol) {
        if (!isValidNextCell(nextRow, nextCol)) {
            return;
        }

        int newEnergy = energy + 1;

        if (newEnergy > k) {
            return;
        }

        int finalRow = nextRow;
        int finalCol = nextCol;
        boolean usedTeleport = false;
        int teleportSourceRow = -1;
        int teleportSourceCol = -1;

        if (grid[nextRow][nextCol] == TELEPORT) {
            if (!teleportManager.hasTeleport(nextRow, nextCol)) {
                throw new IllegalStateException(
                        "Missing teleport definition for T cell at (" + nextRow + "," + nextCol + ")."
                );
            }

            Cell destination = teleportManager.getDestination(nextRow, nextCol);
            validateTeleportDestination(destination);

            usedTeleport = true;
            teleportSourceRow = nextRow;
            teleportSourceCol = nextCol;
            finalRow = destination.row;
            finalCol = destination.col;
        }

        int newCustomers = dp[row][col][energy] + customerValue(finalRow, finalCol);

        if (newCustomers > dp[finalRow][finalCol][newEnergy]) {
            dp[finalRow][finalCol][newEnergy] = newCustomers;
            parent[finalRow][finalCol][newEnergy] = usedTeleport
                    ? new Parent(row, col, energy, teleportSourceRow, teleportSourceCol)
                    : new Parent(row, col, energy);
        }
    }

    private boolean isValidNextCell(int row, int col) {
        return isInside(row, col) && grid[row][col] != OBSTACLE;
    }

    private void validateTeleportDestination(Cell destination) {
        if (destination == null) {
            throw new IllegalStateException("Teleport destination cannot be null.");
        }

        if (!isInside(destination.row, destination.col)) {
            throw new IllegalStateException("Teleport destination is outside the grid: " + destination);
        }

        if (grid[destination.row][destination.col] == OBSTACLE) {
            throw new IllegalStateException("Teleport destination cannot be an obstacle: " + destination);
        }
    }

    private int customerValue(int row, int col) {
        return grid[row][col] == CUSTOMER ? 1 : 0;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < n && col >= 0 && col < m;
    }

    private void updateBest(int row, int col, int energy) {
        int customers = dp[row][col][energy];

        if (customers > bestCustomers || (customers == bestCustomers && energy < bestEnergy)) {
            bestCustomers = customers;
            bestEnergy = energy;
            bestRow = row;
            bestCol = col;
        }
    }

    public Result getResult() {
        return new Result(bestCustomers, bestEnergy, reconstructPath());
    }

    private List<String> reconstructPath() {
        List<String> path = new ArrayList<>();

        int row = bestRow;
        int col = bestCol;
        int energy = bestEnergy;

        path.add(new Cell(row, col).toString());

        while (!(row == startRow && col == startCol && energy == 0)) {
            Parent p = parent[row][col][energy];

            if (p == null) {
                throw new IllegalStateException("Path reconstruction failed at state (" + row + "," + col + "," + energy + ").");
            }

            if (p.usedTeleport) {
                path.add("T");
                path.add(new Cell(p.teleportSourceRow, p.teleportSourceCol).toString());
            }

            row = p.prevRow;
            col = p.prevCol;
            energy = p.prevEnergy;
            path.add(new Cell(row, col).toString());
        }

        Collections.reverse(path);
        return path;
    }
}
