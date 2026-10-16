public class TeleportDefinition {
    public final int sourceRow;
    public final int sourceCol;
    public final int destRow;
    public final int destCol;

    public TeleportDefinition(int sourceRow, int sourceCol, int destRow, int destCol) {
        this.sourceRow = sourceRow;
        this.sourceCol = sourceCol;
        this.destRow = destRow;
        this.destCol = destCol;
    }
}
