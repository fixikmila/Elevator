import java.util.ArrayList;

public abstract class ElevatorManagerAbstract implements ElevatorManagerInterface {
    protected ArrayList<Elevator> elevators = new ArrayList<Elevator>();
    protected ArrayList<TupleWaiter> waiting_list = new ArrayList<>();
    protected int current_step = 0, maximum_floor = 100;

    protected void setMaximum_floor(int maximum_floor) {
        this.maximum_floor = maximum_floor;
    }

    protected void addElevator(Elevator elevator) {
        elevators.add(elevator);
    }
}
