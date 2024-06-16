public class User implements ObserverAssigner, ObserverUpdater {
    private int floor_call_number, current_floor;
    private Integer elevator_id = null;
    private Direction direction;
    private boolean is_in_elevator = false, assigned = false;
    private ElevatorManager elevator_manager;

    public User(ElevatorManager elevatorManager, int floor_call_number, Direction direction) {
        this.floor_call_number = floor_call_number;
        this.current_floor = floor_call_number;
        this.direction = direction;
        elevator_manager = elevatorManager;
        elevator_manager.addObserver(this);
        elevatorManager.pickUp(floor_call_number, direction);
    }

    public Integer getElevatorId() {
        return elevator_id;
    }

    @Override
    public boolean IsInElevator() {
        return is_in_elevator;
    }

    @Override
    public int getCallFloorNumber() {
        return floor_call_number;
    }

    @Override
    public void assignElevator(Elevator elevator) {
        assigned = true;
        elevator_id = elevator.getId();
        System.out.println("Floor " + floor_call_number + ", direction " + direction + " notified. Assigned elevator "
                + elevator.getId() + " is now at floor " + elevator.getCurrentFloor());
    }

    @Override
    public void elevatorPickUp(Elevator elevator) {
        is_in_elevator = true;
        System.out.println("Assigned elevator " + elevator.getId() + " for floor " + floor_call_number +
                " for direction " + direction + " came");
    }

    @Override
    public void elevatorRide(Elevator elevator) {
        current_floor = elevator.getCurrentFloor();
        System.out.println("Assigned elevator " + elevator.getId() + " is now at floor " + current_floor);
    }

    public void exitElevator(Elevator elevator) {
        elevator.removeObserver(this);
    }

    @Override
    public int getFloorNumber() {
        return current_floor;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    void pressButtonInElevator(ElevatorManager elevatorManager, int destination_floor) throws Exception {
        if (!is_in_elevator) {
            throw new Exception("Not in elevator yet");
        }
        elevatorManager.update(elevator_id, current_floor, destination_floor);
    }
}
