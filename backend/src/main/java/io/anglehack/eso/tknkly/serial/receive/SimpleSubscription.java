package io.anglehack.eso.tknkly.serial.receive;

/**
 * Created by root on 6/18/17.
 */

import com.satori.rtm.*;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;


public class SimpleSubscription {

    private String channel = "";
    private String userId;
    private SatoriConfig config;
    private RtmClient client;

    public void setConfig(SatoriConfig config, String userId) {
        this.channel = config.getChannel();
        this.config = config;
        this.userId = userId;
    }

    public void initialize() {
        client = new RtmClientBuilder(config.getEndpoint(), config.getAppkey())
                .setAuthProvider(new RoleSecretAuthProvider(config.getRole(), config.getRoleSecretKey()))
                .build();

        client.start();

        String filter = String.format("select count(*) from `tknkly-channel` where `userId` = '%s'", channel, userId);

        SubscriptionAdapter listener = new SubscriptionAdapter() {

            @Override
            public void onSubscriptionError(SubscriptionError error) {
                System.out.println("Subscription is failed: " + error.getError());
            }

            @Override
            public void onSubscriptionData(SubscriptionData data) {
//                for (MotionData msg: data.getMessagesAsType(MotionData.class)) {
//                    System.out.println("Got message: " + msg);
//                }
                System.out.println("data size: " +data.getMessagesAsType(MotionData.class).size());
                System.out.println("data size: " +data.getMessages());
            }
        };

        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener)
                .setFilter(filter);
//                .setAge(600 /* seconds */);

        client.createSubscription(channel, cfg);
    }
}