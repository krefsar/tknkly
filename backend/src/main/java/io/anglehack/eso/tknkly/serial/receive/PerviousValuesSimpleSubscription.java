package io.anglehack.eso.tknkly.serial.receive;

/**
 * Created by root on 6/18/17.
 */

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.satori.rtm.*;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import io.anglehack.eso.tknkly.models.ListSatoriConfig;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;

import java.util.Comparator;
import java.util.HashMap;


public class PerviousValuesSimpleSubscription implements ReceiveInterface {

    private String userId;
    private SatoriConfig subscribe;
    private SatoriConfig publish;
    private RtmClient clientSubscribe;
    private RtmClient clientPublish;
    private MotionData lastValue;

    public void setConfig(ListSatoriConfig listSatoriConfig, String userId) {
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

        SubscriptionAdapter listener = new SubscriptionAdapter() {

            @Override
            public void onSubscriptionError(SubscriptionError error) {
                System.out.println("Subscription is failed: " + error.getError());
            }

            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (MotionData msg : data.getMessagesAsType(MotionData.class)) {
                    System.out.println("Got message: " + msg);
//                    multimap.put(userId,msg);
                }
            }
        };
        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener)
                .setCount(1);

        clientSubscribe.createSubscription(subscribe.getChannel(), cfg);
    }

    public boolean isError() {
        Comparator<MotionData> byTime = (e1, e2) -> Long.compare(
                e1.getTime(), e2.getTime());
        return false;
//        return multimap.get(userId).stream()
//                .sorted(byTime)
//                .forEachOrdered(md -> m);
    }
}