import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTests {
    int getCurrentFloor(ElevatorManager elevatorManager, int elevator_number) {
        return elevatorManager.status().get(elevator_number).getCurrentFloor();
    }

    public abstract class Query {
        static ElevatorManager elevatorManager;
        protected Direction direction;
        protected int time, current_floor, destination_floor;
        protected Integer elevator_id;
        protected boolean is_exception_expected = false, is_free = false, is_in_elevator = false;
        protected User user;

        abstract void doQuery() throws Exception;
    }

    class UserPressButtonQuery extends Query {
        @Override
        void doQuery() throws Exception {
            user.pressButtonInElevator(elevatorManager, destination_floor);
        }

        UserPressButtonQuery(int time, int destination_floor, User user) {
            this.time = time;
            this.destination_floor = destination_floor;
            this.user = user;
        }
    }

    class UserAssignedElevatorQuery extends Query {
        @Override
        void doQuery() throws Exception {
            assertEquals(user.getElevatorId(), elevator_id);
        }

        UserAssignedElevatorQuery(int time, Integer elevator_id, User user) {
            this.time = time;
            this.elevator_id = elevator_id;
            this.user = user;
        }
    }

    class UserElevatorArrivedQuery extends Query {
        @Override
        void doQuery() throws Exception {
            assertEquals(is_in_elevator, user.IsInElevator());
        }

        UserElevatorArrivedQuery(int time, boolean is_in_elevator, User user) {
            this.time = time;
            this.is_in_elevator = is_in_elevator;
            this.user = user;
        }
    }

    class UserElevatorMoveQuery extends Query {
        @Override
        void doQuery() throws Exception {
            assertEquals(current_floor, user.getFloorNumber());
        }

        UserElevatorMoveQuery(int time, int current_floor, User user) {
            this.time = time;
            this.current_floor = current_floor;
            this.user = user;
        }
    }

    class IsFreeQuery extends Query {
        @Override
        void doQuery() {
            assertEquals(elevatorManager.status().get(elevator_id).isFree(), is_free);
        }

        IsFreeQuery(int time, boolean is_free, int elevator_id) {
            this.time = time;
            this.is_free = is_free;
            this.elevator_id = elevator_id;
        }
    }

    class OutsideClickQuery extends Query {
        @Override
        void doQuery() {
            elevatorManager.pickUp(current_floor, direction);
        }

        OutsideClickQuery(int time, int current_floor, Direction direction) {
            this.direction = direction;
            this.time = time;
            this.current_floor = current_floor;
        }
    }

    class InsideClickQuery extends Query {
        @Override
        void doQuery() throws Exception {
            elevatorManager.update(elevator_id, current_floor, destination_floor);
        }

        InsideClickQuery(int time, int current_floor, int destination_floor, int elevator_id, boolean is_exception_expected) {
            this.elevator_id = elevator_id;
            this.time = time;
            this.current_floor = current_floor;
            this.destination_floor = destination_floor;
            this.is_exception_expected = is_exception_expected;
        }
    }

    class FloorCheckQuery extends Query {
        @Override
        void doQuery() {
            assertEquals(getCurrentFloor(elevatorManager, elevator_id), current_floor);
        }

        FloorCheckQuery(int time, int current_floor, int elevator_id) {
            this.time = time;
            this.current_floor = current_floor;
            this.elevator_id = elevator_id;
        }
    }

    void processQueries(Queue<Query> queries, int current_time) {
        while (!queries.isEmpty()) {
            Query query = queries.peek();
            if (query.time != current_time) {
                return;
            }
            queries.remove();
            try {
                query.doQuery();
            } catch (Exception e) {
                assertTrue(query.is_exception_expected);
            }
        }
    }

    Queue<Query> sort_queries(ElevatorManager elevatorManager, ArrayList<Query> queries) {
        queries.sort(Comparator.comparingInt(a -> a.time));
        Queue<Query> queries_sorted = new LinkedList<>(queries);
        Query.elevatorManager = elevatorManager;
        return queries_sorted;
    }

    void simulate_queries(ElevatorManager elevatorManager, ArrayList<Query> queries) {
        Queue<Query> queries_sorted = sort_queries(elevatorManager, queries);
        while (!queries_sorted.isEmpty()) {
            processQueries(queries_sorted, elevatorManager.getCurrentTime());
            elevatorManager.doSteps(1);
        }
    }


    void simulate(int elevator_number, ArrayList<Query> queries) {
        ElevatorManager elevatorManager = new ElevatorManager(elevator_number);
        simulate_queries(elevatorManager, queries);
    }

    @Test
    void one_person_down_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new FloorCheckQuery(0, 0, 0));
        queries.add(new OutsideClickQuery(0, 4, Direction.DOWN));
        queries.add(new FloorCheckQuery(1, 1, 0));
        queries.add(new FloorCheckQuery(4, 4, 0));
        queries.add(new InsideClickQuery(4, 4, 0, 0, false));
        queries.add(new FloorCheckQuery(5, 3, 0));
        queries.add(new FloorCheckQuery(6, 2, 0));
        queries.add(new FloorCheckQuery(8, 0, 0));
        simulate(1, queries);
    }

    @Test
    void one_person_up_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 4, Direction.UP));
        queries.add(new FloorCheckQuery(4, 4, 0));
        queries.add(new InsideClickQuery(4, 4, 7, 0, false));
        queries.add(new FloorCheckQuery(7, 7, 0));
        simulate(1, queries);
    }

    @Test
    void update_for_not_existing_floor_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 0, Direction.DOWN));
        queries.add(new InsideClickQuery(0, 0, 101, 0, true));
        simulate(1, queries);
    }

    @Test
    void update_for_negative_floor_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 0, Direction.DOWN));
        queries.add(new InsideClickQuery(0, 0, -1, 0, true));
        simulate(1, queries);
    }

    @Test
    void update_not_called_elevator() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new InsideClickQuery(0, 0, 2, 0, true));
        simulate(1, queries);
    }

    @Test
    void update_for_empty_elevator() { // the button clicked not on the floor where elevator is now
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 1, Direction.UP));
        queries.add(new InsideClickQuery(0, 0, 2, 0, true));
        simulate(1, queries);
    }

    @Test
    void update_in_wrong_floor() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 1, Direction.UP));
        queries.add(new InsideClickQuery(1, 2, 3, 0, true));
        simulate(1, queries);
    }

    @Test
    void update_for_same_floor() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 1, Direction.UP));
        queries.add(new InsideClickQuery(1, 1, 1, 0, true));
        simulate(1, queries);
    }

    @Test
    void update_wrong_direction_floor() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 1, Direction.UP));
        queries.add(new InsideClickQuery(1, 1, 0, 0, true));
        simulate(1, queries);
    }

    @Test
    void more_elevators_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 4, Direction.UP));
        queries.add(new OutsideClickQuery(0, 2, Direction.DOWN));
        queries.add(new FloorCheckQuery(2, 2, 0));
        queries.add(new FloorCheckQuery(2, 2, 1));
        queries.add(new InsideClickQuery(2, 2, 0, 1, false));
        simulate(2, queries);
    }

    @Test
    void second_person_already_after_first_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 1, Direction.DOWN));
        queries.add(new InsideClickQuery(1, 1, 0, 0, false));
        queries.add(new FloorCheckQuery(2, 0, 0));
        queries.add(new OutsideClickQuery(2, 0, Direction.UP));
        queries.add(new InsideClickQuery(2, 0, 1, 0, false));
        queries.add(new FloorCheckQuery(3, 1, 0));
        simulate(1, queries);
    }

    @Test
    void few_person_in_one_elevator() { // 2 people are on the 0 floor, and 1 person is in 2nd floor
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 0, Direction.UP));
        queries.add(new InsideClickQuery(0, 0, 1, 0, false));
        queries.add(new InsideClickQuery(0, 0, 4, 0, false));
        queries.add(new FloorCheckQuery(1, 1, 0));
        queries.add(new OutsideClickQuery(1, 2, Direction.UP));
        queries.add(new FloorCheckQuery(2, 2, 0));
        queries.add(new InsideClickQuery(2, 2, 5, 0, false));
        queries.add(new FloorCheckQuery(4, 4, 0));
        queries.add(new FloorCheckQuery(5, 5, 0));
        queries.add(new IsFreeQuery(5, true, 0));
        simulate(1, queries);
    }

    @Test
    void waiting_list_test() {
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 3, Direction.DOWN));
        queries.add(new OutsideClickQuery(0, 5, Direction.DOWN));
        queries.add(new OutsideClickQuery(0, 7, Direction.DOWN));
        queries.add(new InsideClickQuery(3, 3, 0, 0, false));
        queries.add(new FloorCheckQuery(6, 0, 0));
        queries.add(new FloorCheckQuery(11, 5, 0));
        queries.add(new InsideClickQuery(11, 5, 0, 0, false));
        queries.add(new FloorCheckQuery(16, 0, 0));
        queries.add(new FloorCheckQuery(23, 7, 0));
        queries.add(new InsideClickQuery(23, 7, 0, 0, false));
        queries.add(new FloorCheckQuery(30, 0, 0));
        simulate(1, queries);
    }

    @Test
    void time_free_elevator_test() {  // the test where it is more optimal to send another elevator for second person
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 3, Direction.DOWN));
        queries.add(new OutsideClickQuery(0, 1, Direction.DOWN));
        queries.add(new FloorCheckQuery(1, 1, 1));
        queries.add(new InsideClickQuery(1, 1, 0, 1, false));
        queries.add(new FloorCheckQuery(2, 0, 1));
        queries.add(new FloorCheckQuery(3, 3, 0));
        simulate(2, queries);
    }

    @Test
    void time_not_free_elevator_test() {  // the test where it is more optimal to send another elevator for second person
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new OutsideClickQuery(0, 3, Direction.DOWN));
        queries.add(new InsideClickQuery(3, 3, 0, 0, false));
        queries.add(new OutsideClickQuery(3, 2, Direction.DOWN));
        queries.add(new FloorCheckQuery(4, 0, 1));
        simulate(2, queries);
    }

    @Test
    void test_user() {
        ElevatorManager elevatorManager = new ElevatorManager(2);
        ArrayList<Query> queries = new ArrayList<>();
        User user = new User(elevatorManager, 4, Direction.UP);
        queries.add(new UserAssignedElevatorQuery(0, 0, user));
        User user1 = new User(elevatorManager, 2, Direction.DOWN);
        queries.add(new UserAssignedElevatorQuery(0, 1, user1));
        queries.add(new FloorCheckQuery(2, 2, 1));
        queries.add(new UserElevatorArrivedQuery(2, true, user1));
        queries.add(new UserPressButtonQuery(2, 0, user1));
        queries.add(new UserElevatorMoveQuery(3, 1, user1));
        queries.add(new UserElevatorArrivedQuery(4, true, user));
        queries.add(new UserPressButtonQuery(4, 6, user));
        queries.add(new UserElevatorMoveQuery(5, 5, user));
        simulate_queries(elevatorManager, queries);
    }
}
