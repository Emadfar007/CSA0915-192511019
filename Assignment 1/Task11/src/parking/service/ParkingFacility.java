package parking.service;

import parking.model.Vehicle;

public final class ParkingFacility {
    private final Vehicle[][] slots;
    private int occupancy;

    public ParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0) {
            throw new IllegalArgumentException("at least one floor is required");
        }
        slots = new Vehicle[slotsPerFloor.length][];
        for (int floor = 0; floor < slotsPerFloor.length; floor++) {
            if (slotsPerFloor[floor] <= 0) {
                throw new IllegalArgumentException("each floor needs a positive slot count");
            }
            slots[floor] = new Vehicle[slotsPerFloor[floor]];
        }
    }

    public int[] allocate(Vehicle vehicle) {
        for (int floor = 0; floor < slots.length; floor++) {
            for (int slot = 0; slot < slots[floor].length; slot++) {
                if (slots[floor][slot] == null) {
                    slots[floor][slot] = vehicle;
                    occupancy++;
                    return new int[]{floor, slot};
                }
            }
        }
        return null;
    }

    public boolean release(int floor, int slot) {
        if (!isValid(floor, slot) || slots[floor][slot] == null) {
            return false;
        }
        slots[floor][slot] = null;
        occupancy--;
        return true;
    }

    public boolean isValid(int floor, int slot) {
        return floor >= 0 && floor < slots.length
                && slot >= 0 && slot < slots[floor].length;
    }

    public int getOccupancy() {
        return occupancy;
    }
}
