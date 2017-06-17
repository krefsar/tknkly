package io.anglehack.eso.tknkly.models;

/**
 * Created by root on 6/17/17.
 */
public class MotionDataObject {

    private long time;
    private double accX;
    private double accY;
    private double accZ;
    private double roll;
    private double pitch;
    private double yaw;

    public MotionDataObject() {
    }

    public MotionDataObject(long time, double accX, double accY, double accZ, double roll, double pitch, double yaw) {
        this.time = time;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getAccX() {
        return accX;
    }

    public void setAccX(double accX) {
        this.accX = accX;
    }

    public double getAccY() {
        return accY;
    }

    public void setAccY(double accY) {
        this.accY = accY;
    }

    public double getAccZ() {
        return accZ;
    }

    public void setAccZ(double accZ) {
        this.accZ = accZ;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    @Override
    public String toString() {
        return "MotionDataObject{" +
                "accX=" + accX +
                ", accY=" + accY +
                ", accZ=" + accZ +
                ", roll=" + roll +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}';
    }
}
