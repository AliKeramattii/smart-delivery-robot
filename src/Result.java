import java.util.List;

public class Result {
    public final int maxCustomers;
    public final int energyUsed;
    public final List<String> path;

    public Result(int maxCustomers, int energyUsed, List<String> path) {
        this.maxCustomers = maxCustomers;
        this.energyUsed = energyUsed;
        this.path = path;
    }
}
