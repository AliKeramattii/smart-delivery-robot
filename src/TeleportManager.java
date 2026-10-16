import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleportManager {
    private final Map<String, Cell> teleports = new HashMap<>();

    public TeleportManager() {
    }

    public TeleportManager(List<TeleportDefinition> definitions) {
        for (TeleportDefinition teleport : definitions) {
            addTeleport(teleport.sourceRow, teleport.sourceCol, teleport.destRow, teleport.destCol);
        }
    }

    public void addTeleport(int sourceRow, int sourceCol, int destRow, int destCol) {
        Cell source = new Cell(sourceRow, sourceCol);
        Cell destination = new Cell(destRow, destCol);
        teleports.put(source.key(), destination);
    }

    public boolean hasTeleport(int row, int col) {
        return teleports.containsKey(row + "," + col);
    }

    public Cell getDestination(int row, int col) {
        Cell destination = teleports.get(row + "," + col);

        if (destination == null) {
            throw new IllegalStateException(
                    "Teleport cell at (" + row + "," + col + ") has no destination definition."
            );
        }

        return destination;
    }
}
