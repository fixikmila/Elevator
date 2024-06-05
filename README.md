# Elevator

Usage:

In SimpleTest.java there are tests checking the functionality of the classes.

You can use class ElevatorManagerAbstract and its implementation ElevatorManager. The functions you can use are defined in ElevatorManagerInterface and they are:

void pickup(int floor, boolean direction); -- the button is pressed on the floor and its direction is false if person wants to go down and true if up

void update(int id, int current_floor, int destination_floor) throws Exception; -- in the elevator with id on the current_floor inside elevator was pressed button destination_floor

void do_steps(int number); -- you can do number steps at one time (one step is when elevator can move for 0 or 1 floors up or down)

ArrayList<Elevator> status(); -- it will return the list of elevators and it should be used in following way: getCurrentFloor() method tells us where this elevator currently is, getFloors() gives us a list of tuple and this tuple is the orders for this elevator: current_floor(from where it was called), destination_floor(to where), time_call(and when it was called).

Algorithm:

We want to minimize maximum waiting time as much as we can knowing current information. We assume that there is no basement floors.

The elevator has 3 states: 

1. free -- it means this elevator is not called by anyone anyone at this moment and it is not going anywhere. the function isFree() will return true only in this case
2. called by someone and not reached him for now -- so it has first_floor_pickup variable equals to the floor it is going to.
3. picked up some person and it is going in this direction -- so it has first_floor_pickup_variable equals to -1.

If a person press the button, we consider case 1, case 2 when our current floor is lower or equal than first_floor_pickup(so it can pick up us when taking another person in this direction) and case 3 when the our current_floor is lower or equal than this elevator's current floor. If there several possibility, we choose the nearest one. If none of the options are availible then we put this person in the waiting list and in function step when we have available elevators, we can pick this person(we start with the one who waits for the most).
