public class Parent {
    public final int prevRow;
    public final int prevCol;
    public final int prevEnergy;

    public final boolean usedTeleport;
    public final int teleportSourceRow;
    public final int teleportSourceCol;

    public Parent(int prevRow, int prevCol, int prevEnergy) {
        this.prevRow = prevRow;
        this.prevCol = prevCol;
        this.prevEnergy = prevEnergy;
        this.usedTeleport = false;
        this.teleportSourceRow = -1;
        this.teleportSourceCol = -1;
    }

    public Parent(int prevRow, int prevCol, int prevEnergy, int teleportSourceRow, int teleportSourceCol) {
        this.prevRow = prevRow;
        this.prevCol = prevCol;
        this.prevEnergy = prevEnergy;
        this.usedTeleport = true;
        this.teleportSourceRow = teleportSourceRow;
        this.teleportSourceCol = teleportSourceCol;
    }
}
