public class StandardElevatorFactory extends ElevatorFactory {
    @Override
    public Elevator createElevator(int id) {
        return new Elevator(id);
    }
}