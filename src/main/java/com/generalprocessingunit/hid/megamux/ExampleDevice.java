package com.generalprocessingunit.hid.megamux;


import java.util.Set;

public class ExampleDevice extends AbstractDevice {

    Set<Mux> config = muxConfig(
            new Mux(0, Mux.Mode.ANALOG_IN, 0, 1, 2, 3, 4, 5, 6, 7, 8, 10),
            new Mux(1, Mux.Mode.DIGITAL_OUT, 0, 2)
    );

    @Override
    public Set<Mux> getMuxConfig() {
        return config;
    }
}
