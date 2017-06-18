package io.anglehack.eso.tknkly.serial.ports;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sbelkin on 6/18/17.
 */
public class MacOSPort implements PortsInterface {
    @Override
    public List<String> getPorts() throws Exception {
        // possibly its own reader.
        return Arrays.asList("/dev/tty.usbserial-A9007UX1");
    }
}
