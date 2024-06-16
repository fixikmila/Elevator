public class UnprocessedElevatorCall {
    int floor;
    Direction direction;
    int time_call_at_floor = 0;

    public UnprocessedElevatorCall(int floor, Direction direction, int time_call_at_floor) {
        this.floor = floor;
        this.direction = direction;
        this.time_call_at_floor = time_call_at_floor;
    }
}