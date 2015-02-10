package com.generalprocessingunit.hid.megamux;

import java.util.*;

public class Mux {
    Mode mode = null;
    int id;

    List<Integer> pinVals = new ArrayList<>();
    Set<Integer> pinsInUse = new LinkedHashSet<>();

    public Mux(int id) {
        this.id = id;

        for (int i = 0; i < 16; i++) {
            pinVals.add(0);
        }
    }

    public Mux(int id, Mode mode, int ... pinsInUse) {
        this.id = id;
        this.mode = mode;

        for(int p : pinsInUse) {
            this.pinsInUse.add(p);
        }
    }

    public enum Mode {
        DIGITAL_IN(0),
        DIGITAL_OUT(1),
        ANALOG_IN(2),
        DIGITAL_IN_PULLUP(3);

        private final Integer val;

        Mode(int val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val.toString();
        }

    }
}
