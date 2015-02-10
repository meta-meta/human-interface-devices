package com.generalprocessingunit.hid.megamux;


import java.util.*;

public abstract class AbstractDevice implements Device {

    List<MuxAndPin> inputPins = new ArrayList<>();
    List<MuxAndPin> outputPins = new ArrayList<>();

    public Set<Mux> muxConfig(Mux... muxes) {
        for (Mux m : muxes) {
            for (Integer i : m.pinsInUse) {
                if (m.mode.equals(Mux.Mode.ANALOG_IN)) {
                    inputPins.add(new MuxAndPin(m.id, i));
                } else if (m.mode.equals(Mux.Mode.DIGITAL_OUT)) {
                    outputPins.add(new MuxAndPin(m.id, i));
                }
            }
        }

        return new HashSet<>(Arrays.asList(muxes));
    }

    public void setOuputVal(int pin, Boolean val) {
        MuxAndPin map = outputPins.get(pin);
        Mux mux = MegaMuxHost.muxes.get(map.muxId);
        mux.pinVals.set(map.pin, val ? 1 : 0);
    }

    public int getInputVal(int pin) {
        MuxAndPin map = inputPins.get(pin);
        Mux mux = MegaMuxHost.muxes.get(map.muxId);
        return mux.pinVals.get(map.pin);
    }

    class MuxAndPin {
        int muxId, pin;

        public MuxAndPin(int muxId, int pin) {
            this.muxId = muxId;
            this.pin = pin;
        }
    }
}
