package io.anglehack.eso.tknkly.serial.receive;

import com.satori.rtm.*;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import io.anglehack.eso.tknkly.models.ErrorMessage;
import io.anglehack.eso.tknkly.models.ListSatoriConfig;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 6/18/17.
 */
public class PerviousValuesSimpleSubscription implements ReceiveInterface {

    private String userId;
    private SatoriConfig subscribe;
    private SatoriConfig publish;
    private RtmClient clientSubscribe;
    private RtmClient clientPublish;
    private List<MotionData> motionDatas;
    double minPitch = -30;
    double maxPitch = 30;
    Long timeMinMax = 5*1000L;
    boolean collect = false;
    boolean error = false;
    public void setConfig(ListSatoriConfig listSatoriConfig, String userId) {
        this.subscribe = listSatoriConfig.getConfigs().get(0);
        this.publish = listSatoriConfig.getConfigs().get(1);
        this.userId = userId;
        this.motionDatas = new ArrayList<>();
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
                    if (!collect && msg.getPitch() < minPitch) {
                        collect = true;
                        error = false;
                    }

                    if (collect) {
                        motionDatas.add(msg);
                    }

                    if (collect && msg.getPitch() > maxPitch) {
                        collect = false;
                        Long tempTimeMinMax = motionDatas.get(motionDatas.size()-1).getTime() - motionDatas.get(0).getTime();
                        if (tempTimeMinMax < timeMinMax) {
                            clientPublish.publish(publish.getChannel(), new ErrorMessage("pitch", "The user has done the curl too fast"), Ack.NO);
                            error = true;
                            motionDatas.clear();
                        }
                    }
                }
            }
        };

//        String filter = String.format("SELECT * FROM `%s` WHERE `userId` = %s", subscribe.getChannel(), userId);
        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener)
                .setPeriod(10);

        clientSubscribe.createSubscription(subscribe.getChannel(), cfg);
    }

    public boolean isError() {
        return error;
    }
}