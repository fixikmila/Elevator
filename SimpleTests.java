import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTests {
    int getCurrentFloor(ElevatorManagerAbstract elevatorManager, int elevator_number) {
        return elevatorManager.status().get(elevator_number).getCurrentFloor();
    }

    @Test
    void one_person_down_test() {
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 0);
            elevatorManager.pickup(4, false);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 1);
            elevatorManager.do_steps(3);
            assertEquals(getCurrentFloor(elevatorManager, 0), 4);
            elevatorManager.update(0, 4, 0);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 3);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 2);
            elevatorManager.do_steps(2);
            assertEquals(getCurrentFloor(elevatorManager, 0), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void one_person_up_test() {
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);

            elevatorManager.pickup(4, true);
            elevatorManager.do_steps(4);
            assertEquals(getCurrentFloor(elevatorManager, 0), 4);
            elevatorManager.update(0, 4, 7);
            elevatorManager.do_steps(3);
            assertEquals(getCurrentFloor(elevatorManager, 0), 7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void update_for_not_existing_floor_test() {
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.setMaximum_floor(5);
            elevatorManager.pickup(0, false);
            elevatorManager.update(0, 0, 6);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void update_for_negative_floor_test() {
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(0, false);
            elevatorManager.update(0, 0, -1);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void update_not_called_elevator() {
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.update(0, 0, 2);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void update_for_empty_elevator() { // the button clicked not on the floor where elevator is now
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(1, true);
            elevatorManager.update(0, 0, 2);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void update_in_wrong_floor() {
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(1, true);
            elevatorManager.do_steps(1);
            elevatorManager.update(0, 2, 3);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void update_for_same_floor() {
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(1, true);
            elevatorManager.do_steps(1);
            elevatorManager.update(0, 1, 1);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void update_wrong_direction_floor() {
        boolean thrown = false;
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(1, true);
            elevatorManager.do_steps(1);
            elevatorManager.update(0, 1, 0);
        } catch (Exception e) {
            thrown = true;
        }
        assertEquals(thrown, true);
    }

    @Test
    void more_elevators_test() {
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(2);
            elevatorManager.pickup(4, true);
            elevatorManager.pickup(2, false);
            elevatorManager.do_steps(2);
            assertEquals(getCurrentFloor(elevatorManager, 0), 2);
            assertEquals(getCurrentFloor(elevatorManager, 1), 2);
            elevatorManager.update(1, 2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    void second_person_already_after_first_test() {
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(1, false);
            elevatorManager.do_steps(1);
            elevatorManager.update(0, 1, 0);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 0);
            elevatorManager.pickup(0, true);
            elevatorManager.update(0, 0, 1);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void few_person_in_one_elevator() { // 2 people are on the 0 floor, and 1 person is in 2nd floor
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(0, true);
            elevatorManager.update(0, 0, 1);
            elevatorManager.update(0, 0, 4);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 1);
            elevatorManager.pickup(2, true);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 2);
            elevatorManager.update(0, 2, 5);
            elevatorManager.do_steps(2);
            assertEquals(getCurrentFloor(elevatorManager, 0), 4);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 5);
            assertEquals(elevatorManager.elevators.get(0).isFree(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void waiting_list_test() {
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(1);
            elevatorManager.pickup(3, false);
            elevatorManager.pickup(5, false);
            elevatorManager.pickup(7, false);
            elevatorManager.do_steps(3);
            elevatorManager.update(0, 3, 0);
            elevatorManager.do_steps(3);
            assertEquals(getCurrentFloor(elevatorManager, 0), 0);
            elevatorManager.do_steps(5);
            assertEquals(getCurrentFloor(elevatorManager, 0), 5);
            elevatorManager.update(0, 5, 0);
            elevatorManager.do_steps(5);
            assertEquals(getCurrentFloor(elevatorManager, 0), 0);
            elevatorManager.do_steps(7);
            assertEquals(getCurrentFloor(elevatorManager, 0), 7);
            elevatorManager.update(0, 7, 0);
            elevatorManager.do_steps(7);
            assertEquals(getCurrentFloor(elevatorManager, 0), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void time_free_elevator_test() {  // the test where it is more optimal to send another elevator for second person
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(2);
            elevatorManager.pickup(3, false);
            elevatorManager.pickup(1, false);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 1), 1);
            elevatorManager.update(1, 1, 0);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 1), 0);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 0), 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void time_not_free_elevator_test() {  // the test where it is more optimal to send another elevator for second person
        try {
            ElevatorManagerAbstract elevatorManager = new ElevatorManager(2);
            elevatorManager.pickup(3, false);
            elevatorManager.do_steps(3);
            elevatorManager.update(0, 3, 0);
            elevatorManager.pickup(2, false);
            elevatorManager.do_steps(1);
            assertEquals(getCurrentFloor(elevatorManager, 1), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
