package com.generalprocessingunit.hid.megamux;

import java.util.*;

public class MegaMuxHost {
    private static SerialCom serialCom = new SerialCom();

    public static List<AbstractDevice> devices = new ArrayList<>();
    volatile static List<Mux> muxes = new ArrayList<>(15);

    private MegaMuxHost() {
    }

    static {
        for (int i = 0; i < 15; i++) {
            muxes.add(new Mux(i));
        }

        try {
            addDevice(new ExampleDevice());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(Mux m : muxes) {
            if(null == m.mode)
                setMuxMode(m, Mux.Mode.DIGITAL_OUT);
        }

    }

    private static void addDevice(AbstractDevice device) throws MuxPinsOccupiedException, MuxModeConflictException {
        // make sure pins are not taken
        for (Mux m : device.getMuxConfig()) {

            if(null != muxes.get(m.id).mode && muxes.get(m.id).mode.equals(m.mode)) {
                throw new MuxModeConflictException();
            }

            for (Integer p : m.pinsInUse) {
                if (muxes.get(m.id).pinsInUse.contains(p)) {
                    throw new MuxPinsOccupiedException();
                }
            }
        }

        // add all pins in use by the new device
        for (Mux m : device.getMuxConfig()) {
            setMuxMode(muxes.get(m.id), m.mode);
            muxes.get(m.id).pinsInUse.addAll(m.pinsInUse);

//            // reference the canonical model of the mux pins this device uses
//            m.pinVals = muxes.get(m.id).pinVals;
        }

        devices.add(device);
    }

    /**
     * Receives message from Arduino and updates values for a single Mux
     *
     * @param msgFromArduino
     */
    public static void updateInputVals(String msgFromArduino) {
        List<String> vals = Arrays.asList(msgFromArduino.split(","));
        int muxId = Integer.parseInt(vals.get(0));

        for (int i = 0; i < 16; i++) {
            muxes.get(muxId).pinVals.set(i, Integer.parseInt(vals.get(i + 1)));
        }
    }

    /**
     * This should be called once per draw loop. Each device will have accumulated updates to these pin values.
     * then we send a message sample: O140000101100000100 => set output pins to 0,0,0,0,1,0,1,1,0,0,0,0,0,1,0,0 on Mux 14
     */
    // TODO: since output vals are integers, perhaps a crude PWM can be implemented here.
    // TODO: This method should maybe be threaded and update at a constant rate.
    // TODO: Use the integer value to control whether or not a given pin is on
    public static void writeOutputVals() {
        for (Mux m : muxes) {
            // only update muxes that are in use
            if (!Mux.Mode.DIGITAL_OUT.equals(m.mode) || m.pinsInUse.isEmpty())
                continue;

            StringBuilder sb = muxCmd("O", m);
            for (Integer i : m.pinVals) {
                sb.append(i == 0 ? "0" : "1");
            }

            sb.append("\n");
            serialCom.write(sb.toString());
//            System.out.println(sb.toString());
        }
    }

    // M140 => set Mux 14 to DIGITAL_IN mode
    private static void setMuxMode(Mux mux, Mux.Mode mode) {
        if (!mode.equals(mux.mode)) {
            mux.mode = mode;

            StringBuilder sb = muxCmd("M", mux)
                    .append(mode)
                    .append("\n");

            serialCom.write(sb.toString());
        }
    }

    private static StringBuilder muxCmd(String cmd, Mux mux) {
        return new StringBuilder()
                .append(cmd)
                .append(String.format("%02d", mux.id));
    }
}
