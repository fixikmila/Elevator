import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.abs;

public class ElevatorManager {
    private ArrayList<Elevator> elevators = new ArrayList<>();
    private List<User> observerAssigners = new ArrayList<>();
    private Queue<UnprocessedElevatorCall> unprocessed_elevator_calls = new LinkedList<>();
    private int current_time = 0, maximum_floor = 100;

    public int getCurrentTime() {
        return current_time;
    }

    private void setMaximumFloor(int maximum_floor) {
        this.maximum_floor = maximum_floor;
    }

    private void addElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    ElevatorManager(int elevator_number) {
        ElevatorFactory elevatorFactory = new StandardElevatorFactory();
        for (int i = 0; i < elevator_number; i++) {
            Elevator elevator = elevatorFactory.createElevator(i);
            addElevator(elevator);
        }
    }

    public void pickUp(int floor, Direction direction) {
        pick_up_delayed(floor, direction, current_time);
    }

    private void update_correctness_check(Elevator e, int current_floor, int destination_floor) throws Exception {
        if (destination_floor > maximum_floor) {
            throw new Exception("Destination floor is greater than maximum floor");
        }
        if (destination_floor < 0) {
            throw new Exception("Error: destination floor is negative");
        }
        if (e.getFirstRequestFloor() != null && e.getFirstRequestFloor() != current_floor) {
            throw new Exception("Error: nobody is in this elevator");
        }
        if (current_floor != e.getCurrentFloor()) {
            throw new Exception("Error: the elevator is not at this floor");
        }
        if (current_floor == destination_floor) {
            throw new Exception("Error: the elevator is already on the destination floor");
        }
        if ((destination_floor > current_floor) != (e.getDirection() == Direction.UP)) {
            throw new Exception("Error: elevator was called in the other direction");
        }
    }

    private boolean insert_into_existing_record(int current_floor, int destination_floor, Elevator e) {
        for (ElevatorCall p : e.getElevatorCalls()) {
            if (p.current_floor != current_floor || p.destination_floor != null) {
                // this record is not suitable or is already used
                continue;
            }
            // this record is not used
            p.destination_floor = destination_floor;
            return true;
        }
        return false;
    }

    private Integer get_time_call_at_floor(Elevator e, int current_floor) {
        Integer time_call_at_floor = null;
        for (ElevatorCall p : e.getElevatorCalls()) {
            if (p.current_floor == current_floor) {
                time_call_at_floor = p.time_call_at_floor;  // the call button was pressed in this time on current_floor
            }
        }
        return time_call_at_floor;
    }

    private void insert_into_new_record(Elevator e, int current_floor, int destination_floor) {
        Integer time_call_at_floor = get_time_call_at_floor(e, current_floor);
        if (time_call_at_floor == null) {  // this person entered the elevator at the different floor
            time_call_at_floor = current_time;
        }
        e.addFloor(current_floor, destination_floor, time_call_at_floor);
    }

    public void update(int id, int current_floor, int destination_floor) throws Exception {
        Elevator e = elevators.get(id);
        try {
            update_correctness_check(e, current_floor, destination_floor);
        } catch (Exception ex) {
            throw ex;
        }
        // we either put this info into existing record or into new
        if (insert_into_existing_record(current_floor, destination_floor, e)) {
            // this information was inserted to elevatorCalls list
            return;
        }
        insert_into_new_record(e, current_floor, destination_floor);
    }

    public void doSteps(int number) {
        for (int i = 0; i < number; i++) {
            step();
        }
    }

    public ArrayList<Elevator> status() {
        return elevators;
    }

    private void do_elevators_steps() {
        for (Elevator e : elevators) {
            if (!e.isFree()) {
                e.step();
            }
        }
    }

    private void process_waiting_calls() {
        int size = unprocessed_elevator_calls.size(), cnt = 0;
        while (cnt++ != size) {
            UnprocessedElevatorCall unprocessed_call = unprocessed_elevator_calls.poll();
            pick_up_delayed(unprocessed_call.floor, unprocessed_call.direction, unprocessed_call.time_call_at_floor);
        }
    }

    private void step() {
        current_time++;
        process_waiting_calls();
        do_elevators_steps();
    }

    private boolean should_notify_observer(ObserverAssigner observerAssigner, int floor, Direction direction) {
        return observerAssigner.getFloorNumber() == floor && observerAssigner.getDirection() == direction;
    }

    private void observers_notify(Elevator e, int floor, Direction direction) {
        int cnt = 0;
        while (cnt < observerAssigners.size()) {
            User observerAssigner = observerAssigners.get(cnt);
            if (should_notify_observer(observerAssigner, floor, direction)) {
                observerAssigner.assignElevator(e);
                e.addObserver(observerAssigner);
                removeObserver(observerAssigner);
            } else {
                cnt++;
            }
        }
    }

    private void update_information_pick_up(Integer elevator_id, int floor, Direction direction, int call_time) {
        if (elevator_id == null) {
            // no elevator can pick up us at the moment, so it adds us to queue
            unprocessed_elevator_calls.add(new UnprocessedElevatorCall(floor, direction, call_time));
        } else {
            Elevator e = elevators.get(elevator_id);
            e.setDirection(direction);  // it is required only if the elevator was free before
            e.addFloor(floor, null, call_time);
            observers_notify(e, floor, direction);
        }
    }

    private void pick_up_delayed(int floor, Direction direction, int call_time) {
        int minimum = Integer.MAX_VALUE;
        Integer elevator_id = null;
        for (Elevator e : elevators) {
            if (!e.can_elevator_pick_up(floor, direction)) {
                continue;
            }
            if (floor == e.getCurrentFloor()) {  // this elevator is in our floor
                elevator_id = e.getId();
                break;
            }
            int time = e.get_time_to_reach(floor);  // time this elevator will take to ride to us
            if (time < minimum) {  // it is more optimal elevator
                minimum = time;
                elevator_id = e.getId();
            }
        }
        update_information_pick_up(elevator_id, floor, direction, call_time);
    }

    public void addObserver(User observerAssigner) {
        observerAssigners.add(observerAssigner);
    }

    public void removeObserver(ObserverAssigner observerAssigner) {
        observerAssigners.remove(observerAssigner);
    }
}
