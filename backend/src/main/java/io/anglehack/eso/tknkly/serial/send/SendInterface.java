package io.anglehack.eso.tknkly.serial.send;

import io.anglehack.eso.tknkly.models.MotionData;

import java.io.IOException;

/**
 * Created by sbelkin on 6/17/17.
 */
public interface SendInterface {

    void initialize();

    void sendModelData(MotionData motionData, String userId) throws IOException;
}
