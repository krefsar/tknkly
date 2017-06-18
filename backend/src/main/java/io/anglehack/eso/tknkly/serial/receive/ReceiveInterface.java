package io.anglehack.eso.tknkly.serial.receive;

import io.anglehack.eso.tknkly.models.ListSatoriConfig;
import io.anglehack.eso.tknkly.models.SatoriConfig;

/**
 * https://github.com/satori-com/satori-rtm-sdk-java/tree/master/satori-rtm-sdk/src/main/java/examples
 * Created by root on 6/18/17.
 */
public interface ReceiveInterface {

    default SatoriConfig getConfig(ListSatoriConfig listSatoriConfig) {
        return getConfig(listSatoriConfig,this.getClass());
    }

    default SatoriConfig getConfig(ListSatoriConfig listSatoriConfig, Class clazz) {
        return listSatoriConfig.getConfigs().stream()
                .filter(satoriConfig -> satoriConfig.getName() == clazz.getSimpleName())
                .findFirst().get();
    }

    boolean isError();
}
