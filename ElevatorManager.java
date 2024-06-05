import java.util.ArrayList;

import static java.lang.Math.abs;

public class ElevatorManager extends ElevatorManagerAbstract {
    ElevatorManager(int elevator_number) {
        for (int i = 0; i < elevator_number; i++) {
            Elevator elevator = new Elevator(i);
            addElevator(elevator);
        }
        waiting_list = new ArrayList<>();
    }

    public void pickup(int floor, boolean direction) {
        pick_up(floor, direction, 0);
    }

    public void update(int id, int current_floor, int destination_floor) throws Exception {
        Elevator e = elevators.get(id);
        if (destination_floor > maximum_floor) {
            throw new Exception("Destination floor is greater than maximum floor");
        }
        if (destination_floor < 0) {
            throw new Exception("Error: destination floor is negative");
        }
        if (e.getFirst_floor_pickup() != -1 && e.getFirst_floor_pickup() != current_floor) {
            throw new Exception("Error: nobody is in this elevator");
        }
        if (current_floor != e.getCurrentFloor()) {
            throw new Exception("Error: the elevator is not at this floor");
        }
        if (current_floor == destination_floor) {
            throw new Exception("Error: the elevator is the destination floor");
        }
        if ((destination_floor > current_floor) != e.getDirection()) {
            throw new Exception("Error: elevator was called in the other direction");
        }

        boolean inserted = false;
        int time_call = -1;
        for (TupleFloor p : e.getFloors()) {
            if (p.current_floor == current_floor) {
                time_call = p.time_call;
            }
            if (p.current_floor != current_floor || p.destination_floor != -1) {
                continue;
            }
            p.destination_floor = destination_floor;
            inserted = true;
            break;
        }
        if (!inserted) {
            if (time_call == -1) {
                time_call = current_step;
            }
            e.addFloor(current_floor, destination_floor, time_call);
        }
    }

    public void do_steps(int number) {
        for (int i = 0; i < number; i++) {
            step();
        }
    }

    public ArrayList<Elevator> status() {
        return elevators;
    }

    private void step() {
        current_step++;
        ArrayList<Elevator> empty_elevators = new ArrayList<>();
        for (Elevator e : elevators) {
            if (e.isFree()) {
                empty_elevators.add(e);
            }
        }
        if (!empty_elevators.isEmpty() && !waiting_list.isEmpty()) {
            int size = waiting_list.size();
            for (int cnt = 0; cnt < size; cnt++) {
                TupleWaiter waiting_person = waiting_list.get(0);
                waiting_list.remove(0);
                pick_up(waiting_person.floor, waiting_person.direction, current_step - waiting_person.time);
            }
        }
        for (Elevator e : elevators) {
            if (!e.isFree()) {
                e.step();
            }
        }
    }

    private void pick_up(int floor, boolean direction, int waiting_time) {
        int minimum = Integer.MAX_VALUE, elevator_id = -1;
        for (Elevator e : elevators) {
            if (!e.isFree() && e.getDirection() != direction) {
                continue;
            }
            if (floor == e.getCurrentFloor()) {
                elevator_id = e.getId();
                break;
            }
            int time = abs(floor - e.getCurrentFloor());
            if (!e.isFree()) {
                if (e.getFirst_floor_pickup() == -1) {
                    if (direction != (floor > e.getCurrentFloor())) {
                        continue;
                    }
                } else {
                    if ((floor < e.getFirst_floor_pickup()) != direction) {
                        time = abs(e.getCurrentFloor() - e.getFirst_floor_pickup()) + abs(floor - e.getFirst_floor_pickup());
                    } else {
                        continue;
                    }
                }
            }
            if (time < minimum) {
                minimum = time;
                elevator_id = e.getId();
            }
        }
        if (elevator_id == -1) {
            waiting_list.add(new TupleWaiter(floor, direction, current_step - waiting_time));
        } else {
            Elevator e = elevators.get(elevator_id);
            e.setDirection(direction);
            e.addFloor(floor, -1, current_step - waiting_time);
        }
    }
}
