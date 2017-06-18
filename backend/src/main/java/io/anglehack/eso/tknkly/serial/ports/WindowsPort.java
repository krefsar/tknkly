package io.anglehack.eso.tknkly.serial.ports;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sbelkin on 6/18/17.
 */
public class WindowsPort implements PortsInterface {
    @Override
    public List<String> getPorts() throws Exception {
        // possibly generate through x?
        return Arrays.asList("COM3");
    }
}
