package io.anglehack.eso.tknkly.serial.ports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbelkin on 6/18/17.
 */
public class LinuxPort implements PortsInterface{

    private final String DEFAULT_PATH = "/dev/";
    private final String DEFAULT_FORMAT = "ttyACM";

    @Override
    public List<String> getPorts() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime()
                .exec("ls /dev");
        BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = bf.readLine();
        List<String> ports =  new ArrayList<>();
        while (line != null) {
            if (line.startsWith(DEFAULT_FORMAT)) {
                ports.add(String.format("%s%s",DEFAULT_PATH,line));
            }
            line = bf.readLine();
        }
        int exitCode = process.waitFor();
        assert exitCode == 0;
        return ports;
    }
}
