package io.anglehack.eso.tknkly.serial.send;

import com.satori.rtm.*;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;

import java.io.IOException;

public class SatoriSend implements SendInterface {

    private String channel = "";
    private SatoriConfig config;
    private RtmClient client;

    public void setConfig(SatoriConfig config) {
        this.channel = config.getChannel();
        this.config = config;
    }

    @Override
    public void initialize() {
        client = new RtmClientBuilder(config.getEndpoint(), config.getAppkey())
                .setAuthProvider(new RoleSecretAuthProvider(config.getRole(), config.getRoleSecretKey()))
                .build();
        client.start();
    }

    public void shutdown() {
        client.removeSubscription(channel);
        client.shutdown();
    }

    @Override
    public void sendModelData(MotionData motionData, String userId) throws IOException {
        motionData.setUserId(userId);
        client.publish(channel, motionData, Ack.NO);
    }
}