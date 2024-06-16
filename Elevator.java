import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;

public class Elevator {
    private List<ElevatorCall> elevatorCalls = new ArrayList<>();
    private List<ObserverUpdater> observerUpdaters = new ArrayList<>();
    /*
    we can have many records with the same first value, but different second ones,
    because the elevator can be called by many people from the same floor and
    at the same direction, but their destination floors can be different
     */

    private Integer current_floor = 0, first_request_floor = null;
    private int id;
    private Direction direction;

    public Elevator(int id) {
        this.id = id;
    }

    public Integer getFirstRequestFloor() {
        return first_request_floor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    int getCurrentFloor() {
        return current_floor;
    }

    List<ElevatorCall> getElevatorCalls() {
        return elevatorCalls;
    }

    void clearAllElevatorCallsInformation() {
        elevatorCalls.clear();
        first_request_floor = null;
    }

    boolean isFree() {
        for (ElevatorCall floor : elevatorCalls) {
            if (floor.destination_floor != null) {
                return false;
            }
            if (Objects.equals(floor.current_floor, first_request_floor)) {
                return false;
            }
        }
        return true;
    }

    void addFloor(Integer current_floor, Integer destination_floor, int time_call) {
        if (isFree()) {
            first_request_floor = current_floor;
        }
        elevatorCalls.add(new ElevatorCall(current_floor, destination_floor, time_call));
    }

    private void move_to_first_caller() {
        if (first_request_floor == null) {
            return;
        }
        if (first_request_floor.equals(current_floor)) {
            first_request_floor = null;
        } else if (first_request_floor > current_floor) {
            current_floor++;
        } else {
            current_floor--;
        }
        if (Objects.equals(first_request_floor, current_floor)) {
            observers_notify();
        }
    }

    private void observers_notify() {
        for (ObserverUpdater observer : observerUpdaters) {
            if (current_floor == observer.getCallFloorNumber() && !observer.IsInElevator()) {
                observer.elevatorPickUp(this);
            }
            if (observer.IsInElevator()) {
                observer.elevatorRide(this);
            }
        }
    }

    private void move_one_step_in_direction() {
        current_floor += direction == Direction.UP ? 1 : -1;
        observers_notify();
        int cnt = 0;
        while (cnt < elevatorCalls.size()) {
            ElevatorCall floor = elevatorCalls.get(cnt);
            if (Objects.equals(floor.destination_floor, current_floor)) {
                elevatorCalls.remove(cnt);
            } else {
                cnt++;
            }
        }
    }

    public void step() {
        if (isFree()) {
            return;
        }
        move_to_first_caller();
        if (first_request_floor != null || isFree()) {
            return;
        }
        move_one_step_in_direction();
        if (isFree()) {
            clearAllElevatorCallsInformation();
        }
    }

    public boolean can_elevator_pick_up(int floor, Direction direction) {
        if (!isFree() && getDirection() != direction) {  // this elevator rides to the different direction
            return false;
        }
        if (isFree()) {
            return true;
        }
        if (getFirstRequestFloor() == null) {  // the elevator picked up his first caller
            if ((direction == Direction.UP) != (floor > getCurrentFloor())) {
                // our floor is not on the way of this elevator
                return false;
            }
        } else if ((floor < getFirstRequestFloor()) == (direction == Direction.UP)) {
            // the elevator will pick up the first caller and only then it can ride to us
            // we are not on the way of the elevator after he will pick up its first caller
            return false;
        }
        return true;
    }

    public int get_time_to_reach(int floor) {
        int time;  // time this elevator will take to ride to us
        if (!isFree() && getFirstRequestFloor() != null && (floor < getFirstRequestFloor()) != (direction == Direction.UP)) {
            // we are on the way of the elevator after he will pick up its first caller
            time = abs(getCurrentFloor() - getFirstRequestFloor()) + abs(floor - getFirstRequestFloor());
            // the time to ride to the first caller + time to ride to us from here
        } else {
            time = abs(floor - getCurrentFloor());
        }
        return time;
    }

    public void addObserver(ObserverUpdater observerUpdater) {
        observerUpdaters.add(observerUpdater);
    }

    public void removeObserver(ObserverUpdater observerUpdater) {
        observerUpdaters.remove(observerUpdater);
    }
}
