package io.anglehack.eso.tknkly.serial.receive;

/**
 * Created by root on 6/18/17.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.satori.rtm.*;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import io.anglehack.eso.tknkly.models.ListSatoriConfig;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;
import io.anglehack.eso.tknkly.serial.send.SatoriSend;


public class PitchSimpleSubscription implements ReceiveInterface {

    private String userId;
    private SatoriConfig subscribe;
    private SatoriConfig publish;
    private RtmClient clientSubscribe;
    private RtmClient clientPublish;
    boolean recentChange = false;
    Long changeTime = 0L;
//    can make this smarter... this actually ends up more complex.
    public void setConfig(ListSatoriConfig listSatoriConfig, String userId) {
//        this.subscribe = getConfig(listSatoriConfig, SatoriSend.class);
//        this.publish = getConfig(listSatoriConfig);
        this.subscribe = listSatoriConfig.getConfigs().get(0);
        this.publish = listSatoriConfig.getConfigs().get(1);
        this.userId = userId;
    }

    public void initialize() {
        clientSubscribe = new RtmClientBuilder(subscribe.getEndpoint(), subscribe.getAppkey())
                .setAuthProvider(new RoleSecretAuthProvider(subscribe.getRole(), subscribe.getRoleSecretKey()))
                .build();

        clientPublish = new RtmClientBuilder(publish.getEndpoint(), publish.getAppkey())
                .setAuthProvider(new RoleSecretAuthProvider(publish.getRole(), publish.getRoleSecretKey()))
                .build();

        clientSubscribe.start();
        clientPublish.start();

        String filter = String.format("select AVG(roll) as roll from `%s` where `userId` = '%s'", subscribe.getChannel(), userId);

        SubscriptionAdapter listener = new SubscriptionAdapter() {

            @Override
            public void onSubscriptionError(SubscriptionError error) {
                System.out.println("Subscription is failed: " + error.getError());
            }

            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (MotionData msg : data.getMessagesAsType(MotionData.class)) {
                    System.out.println("Got message: " + msg);
                    Double roll = msg.getRoll();
                    if (roll > 10.5 && !recentChange) {
                        clientPublish.publish(publish.getChannel(),"Roll above expected value" + roll, Ack.NO);
                        recentChange = true;
                        changeTime = System.currentTimeMillis() + 1000;
                    } else if (recentChange && System.currentTimeMillis() > changeTime) {
                        recentChange = false;
                    }
                }
            }
        };
        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener)
                .setFilter(filter);

        clientSubscribe.createSubscription(subscribe.getChannel(), cfg);
    }

    public boolean isError() {
        return recentChange;
    }
}