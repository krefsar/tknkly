package io.anglehack.eso.tknkly.serial.receive;

/**
 * Created by root on 6/18/17.
 */

import com.satori.rtm.*;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import io.anglehack.eso.tknkly.models.ErrorMessage;
import io.anglehack.eso.tknkly.models.ListSatoriConfig;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;

import java.util.ArrayList;
import java.util.List;


public class YawSimpleSubscription implements ReceiveInterface {

    private String userId;
    private SatoriConfig subscribe;
    private SatoriConfig publish;
    private RtmClient clientSubscribe;
    private RtmClient clientPublish;
    boolean error = false;
    double threshold = 3.5;
    List<MotionData> list;
//    can make this smarter... this actually ends up more complex.
    public void setConfig(ListSatoriConfig listSatoriConfig, String userId) {
//        this.subscribe = getConfig(listSatoriConfig, SatoriSend.class);
//        this.publish = getConfig(listSatoriConfig);
        this.subscribe = listSatoriConfig.getConfigs().get(0);
        this.publish = listSatoriConfig.getConfigs().get(1);
        this.userId = userId;
        this.list = new ArrayList<>();
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

        String filter = String.format("select yaw as roll from `%s` where `userId` = '%s'", subscribe.getChannel(), userId);

        SubscriptionAdapter listener = new SubscriptionAdapter() {

            @Override
            public void onSubscriptionError(SubscriptionError error) {
                System.out.println("Subscription is failed: " + error.getError());
            }

            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (MotionData msg : data.getMessagesAsType(MotionData.class)) {
                    list.add(msg);
                    if (list.size() > 20) {
                        error = false;
                        int pointer = 1;
                        while (pointer < list.size()-2) {
                            Double tempDiff = Math.abs(list.get(pointer).getYaw() - list.get(pointer-1).getYaw());
                            if (tempDiff > threshold) {
                                clientPublish.publish(publish.getChannel(),new ErrorMessage("yaw", "Yaw change too fast"), Ack.NO);
                                error = true;
                            }
                            if (error) {
                                list.clear();
                                return;
                            }
                        }
                    }
                }
            }
        };
        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener)
                .setFilter(filter);

        clientSubscribe.createSubscription(subscribe.getChannel(), cfg);
    }

    public boolean isError() {
        return error;
    }
}