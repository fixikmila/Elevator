public interface ObserverUpdater {
    void elevatorPickUp(Elevator elevator);

    void elevatorRide(Elevator elevator);

    int getCallFloorNumber();

    boolean IsInElevator();
}
