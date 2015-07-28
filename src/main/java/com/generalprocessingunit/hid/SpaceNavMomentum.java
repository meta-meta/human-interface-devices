package com.generalprocessingunit.hid;

import com.generalprocessingunit.processing.MomentumVector;
import com.generalprocessingunit.processing.MomentumYawPitchRoll;
import com.generalprocessingunit.processing.space.YawPitchRoll;
import processing.core.PApplet;
import processing.core.PVector;

public class SpaceNavMomentum extends SpaceNavigator {
    private MomentumVector momentum;
    private MomentumYawPitchRoll rotMomentum;
    private float coefTranslation = 0.004f;
    private float coefRotation = 0.015f;
    private boolean invertedControl = false;

    public SpaceNavMomentum(PApplet p5) {
        super(p5);

        momentum = new MomentumVector(p5, 0.0125f);
        rotMomentum = new MomentumYawPitchRoll(p5, 0.0125f);
    }

    /***
     *
     * @param friction default is 0.0125f
     */
    public void setFrictionTranslation(float friction) {
        momentum.setFriction(friction);
    }

    /***
     *
     * @param friction default is 0.0125f
     */
    public void setFrictionRotation(float friction) {
        rotMomentum.setFriction(friction);
    }

    /***
     *
     * @param coefficient default is 0.004f
     */
    public void setCoefTranslation(float coefficient) {
        coefTranslation = coefficient;
    }

    /***
     *
     * @param coefficient default is 0.015f
     */
    public void setCoefRotation(float coefficient) {
        coefRotation = coefficient;
    }

    @Override
    public void poll() {
        super.poll();

        int i = invertedControl ? -1 : 1;
        momentum.add(PVector.mult(super.getTranslation(), i * coefTranslation));
        rotMomentum.add(PVector.mult(super.getRotation(), i * coefRotation));

        momentum.friction();
        rotMomentum.friction();
    }

    public PVector getTranslation() {
        return momentum.getValue();
    }

    public YawPitchRoll getRotation() {
        return rotMomentum.getValue();
    }

    public void invertControl(){
        invertedControl = !invertedControl;
    }
}
