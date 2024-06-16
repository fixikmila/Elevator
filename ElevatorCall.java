public class ElevatorCall {
    Integer current_floor, destination_floor;
    int time_call_at_floor;

    public ElevatorCall(Integer current_floor, Integer destination_floor, int time_call) {
        this.current_floor = current_floor;
        this.destination_floor = destination_floor;
        this.time_call_at_floor = time_call;
    }
}
