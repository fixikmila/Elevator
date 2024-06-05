import java.util.ArrayList;
import java.util.List;

public class Elevator {
    private List<TupleFloor> floors = new ArrayList<>();
    private int current_floor = 0, id, first_floor_pickup = -1;
    private boolean direction;

    public Elevator(int id) {
        this.id = id;
    }

    public int getFirst_floor_pickup() {
        return first_floor_pickup;
    }

    public boolean getDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    int getCurrentFloor() {
        return current_floor;
    }

    List<TupleFloor> getFloors() {
        return floors;
    }

    int getFirstTime() {
        int minimum = Integer.MAX_VALUE;
        for (TupleFloor floor : floors) {
            if (floor.time_call < minimum) {
                minimum = floor.time_call;
            }
        }
        return minimum;
    }

    boolean isFree() {
        int size = 0;
        for (TupleFloor floor : floors) {
            size += (floor.destination_floor != -1 ? 1 : 0);
            if (floor.destination_floor == -1 && floor.current_floor == first_floor_pickup) {
                size++;
            }
        }
        if (size == 0) {
            floors.clear();
            first_floor_pickup = -1;
        }
        return size == 0;
    }

    void addFloor(int current_floor, int destination_floor, int time_call) {
        if (isFree()) {
            first_floor_pickup = current_floor;
        }
        floors.add(new TupleFloor(current_floor, destination_floor, time_call));
    }

    void step() {
        if (isFree()) {
            return;
        }
        if (first_floor_pickup != -1) {
            if (first_floor_pickup == current_floor) {
                first_floor_pickup = -1;
            } else if (first_floor_pickup > current_floor) {
                current_floor++;
                return;
            } else {
                current_floor--;
                return;
            }
        }
        if (isFree()) {
            return;
        }
        current_floor += direction ? 1 : -1;
        for (int i = 0; i < floors.size(); i++) {
            TupleFloor floor = floors.get(i);
            if (floor.destination_floor == current_floor) {
                floors.remove(floor);
                i--;
            }
        }
        isFree();
    }

}
