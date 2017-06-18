package io.anglehack.eso.tknkly.serial.ports;

import java.util.List;

/**
 * Created by sbelkin on 6/18/17.
 */
public interface PortsInterface {

    List<String> getPorts() throws Exception;
}
