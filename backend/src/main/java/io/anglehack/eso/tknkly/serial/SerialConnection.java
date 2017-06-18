package io.anglehack.eso.tknkly.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import io.anglehack.eso.tknkly.models.DeviceHand;
import io.anglehack.eso.tknkly.models.ListSatoriConfig;
import io.anglehack.eso.tknkly.models.MotionData;
import io.anglehack.eso.tknkly.models.SatoriConfig;
import io.anglehack.eso.tknkly.serial.ports.LinuxPort;
import io.anglehack.eso.tknkly.serial.receive.*;
import io.anglehack.eso.tknkly.serial.send.SatoriSend;
import org.apache.commons.cli.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;


public class SerialConnection implements SerialPortEventListener {

    public static String userId = "TEST_2";
    public static boolean send = true;
    public static DeviceHand deviceHandOverwrite = DeviceHand.UNKNOWN;
    SerialPort serialPort;
    static SatoriSend sendInterface;
    static PitchSimpleSubscription pitchSimpleSubscription;
    static PerviousValuesSimpleSubscription perviousValuesSimpleSubscription;
    static SimpleSubscription simpleSubscription;
    static PitchFastChangeThresholdSubscription pitchFastChangeThresholdSubscription;
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */

    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 100;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;

    public void initialize(List<String> ports) {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", ports.get(0));

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : ports) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and send or print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine= input.readLine();

                Optional<MotionData> op = parse(inputLine);
                if (send && op.isPresent()) {
                    sendInterface.sendModelData(op.get(),userId);
                    writeToPort();
                } else {
                    System.out.println(op.get());
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    private Optional<MotionData> parse(String raw) throws IOException {
        String[] parsed = raw.split(",");
        Long time = System.currentTimeMillis();
        if (parsed.length == 7) {
            // Everything is empty before length 7.
            DeviceHand deviceHand = DeviceHand.valueOf(parsed[0]);
            Double accX = Double.valueOf(parsed[1]);
            Double accY = Double.valueOf(parsed[2]);
            Double accZ = Double.valueOf(parsed[3]);
            Double roll = Double.valueOf(parsed[4]);
            Double pitch = Double.valueOf(parsed[5]);
            Double yaw = Double.valueOf(parsed[6]);
            if (!deviceHandOverwrite.equals(DeviceHand.UNKNOWN)) {
                deviceHand = deviceHandOverwrite;
            }
            return Optional.of(new MotionData(time, accX, accY, accZ, roll, pitch, yaw, deviceHand));
        }

        return Optional.empty();
    }

    public void writeToPort() {
        for (ReceiveInterface receiveInterface : classList()) {
            if (receiveInterface.isError()) {
                try {
                    String send = "ERROR \n";
                    output.write(send.getBytes());
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<ReceiveInterface> classList() {
//        return Arrays.asList(pitchSimpleSubscription, perviousValuesSimpleSubscription, simpleSubscription);
        return Arrays.asList(pitchFastChangeThresholdSubscription);
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( getOptions(), args);

        Yaml yaml = new Yaml();
        File file = new File(cmd.getOptionValue("satori"));
        ListSatoriConfig config = yaml.loadAs(new FileInputStream(file), ListSatoriConfig.class);

        if (cmd.hasOption("userId")) {
            userId = cmd.getOptionValue("userId");
        }
        if (cmd.hasOption("send")) {
            send = Boolean.parseBoolean(cmd.getOptionValue("send"));
        }if (cmd.hasOption("deviceHandOverwrite")) {
            deviceHandOverwrite = DeviceHand.valueOf(cmd.getOptionValue("deviceHandOverwrite"));
        }

        sendInterface = new SatoriSend();
        sendInterface.setConfig(config);
        sendInterface.initialize();
        simpleSubscription = new SimpleSubscription();
        simpleSubscription.setConfig(config,userId);
        simpleSubscription.initialize();
        pitchSimpleSubscription = new PitchSimpleSubscription();
        pitchSimpleSubscription.setConfig(config, userId);
        pitchSimpleSubscription.initialize();
        perviousValuesSimpleSubscription = new PerviousValuesSimpleSubscription();
        perviousValuesSimpleSubscription.setConfig(config, userId);
        perviousValuesSimpleSubscription.initialize();
        pitchFastChangeThresholdSubscription =  new PitchFastChangeThresholdSubscription();
        pitchFastChangeThresholdSubscription.setConfig(config, userId);
        pitchFastChangeThresholdSubscription.initialize();
        List<String> ports = new LinuxPort().getPorts();
        if (ports.isEmpty()) {
            throw new IllegalArgumentException("Ports are empty");
        }
        System.out.println("Found ports: " + ports);
        SerialConnection main = new SerialConnection();
        main.initialize(ports);
//        Thread t= new Thread() {
//            public void run() {
//                //the following line will keep this app alive for 1000 seconds,
//                //waiting for events to occur and responding to them (printing incoming messages to console).
//                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
//            }
//        };
//        t.start();
        System.out.println("Started");
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("satori", true, "The satori config file.");
        // currently registering users with device is more work so it can happen here.
        options.addOption("userId", false, "UserId that the current run is associated with.");
        options.addOption("deviceHandOverwrite", false, "Specify hand overwrite if set.");
        options.addOption("send", false, "Default is false. If set to true can send data.");
        return options;
    }


}
