import java.util.ArrayList;

public interface ElevatorManagerInterface {
    void pickup(int floor, boolean direction); // direction true if up and down otherwise

    void update(int id, int current_floor, int destination_floor) throws Exception;

    void do_steps(int number);

    ArrayList<Elevator> status();
}
