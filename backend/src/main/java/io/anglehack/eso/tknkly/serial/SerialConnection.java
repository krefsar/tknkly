package io.anglehack.eso.tknkly.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import io.anglehack.eso.tknkly.models.MotionDataObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public class SerialConnection implements SerialPortEventListener {

    private String userId = "TEST_1";
    SerialPort serialPort;
    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
//            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM1", // Raspberry Pi
//            "COM3", // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */

    private List<MotionDataObject> data = Collections.synchronizedList(new ArrayList());


    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 100;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;

    public void initialize() {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM1");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
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
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine();
                parseSend(inputLine);
//                parseAdd(inputLine);
//                if (data.size() > 10) {
//                    SendData.sendList(data, userId);
//                    data.clear();
//                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    private void parseAdd(String raw) {
        String[] parsed = raw.split(",");
        if (parsed.length == 7) {
            Long time = Long.valueOf(parsed[0]);
            Double accX = Double.valueOf(parsed[1]);
            Double accY = Double.valueOf(parsed[2]);
            Double accZ = Double.valueOf(parsed[3]);
            Double roll = Double.valueOf(parsed[4]);
            Double pitch = Double.valueOf(parsed[5]);
            Double yaw = Double.valueOf(parsed[6]);
            data.add( new MotionDataObject(time, accX, accY, accZ, roll, pitch, yaw));
        } else if (parsed.length == 6) {
            Long time = System.currentTimeMillis();
            Double accX = Double.valueOf(parsed[0]);
            Double accY = Double.valueOf(parsed[1]);
            Double accZ = Double.valueOf(parsed[2]);
            Double roll = Double.valueOf(parsed[3]);
            Double pitch = Double.valueOf(parsed[4]);
            Double yaw = Double.valueOf(parsed[5]);
            data.add( new MotionDataObject(time, accX, accY, accZ, roll, pitch, yaw));
        }
    }

    private void parseSend(String raw) throws IOException {
        String[] parsed = raw.split(",");
        if (parsed.length == 7) {
            Long time = Long.valueOf(parsed[0]);
            Double accX = Double.valueOf(parsed[1]);
            Double accY = Double.valueOf(parsed[2]);
            Double accZ = Double.valueOf(parsed[3]);
            Double roll = Double.valueOf(parsed[4]);
            Double pitch = Double.valueOf(parsed[5]);
            Double yaw = Double.valueOf(parsed[6]);
            SendData.sendOne( new MotionDataObject(time, accX, accY, accZ, roll, pitch, yaw), userId);
        } else if (parsed.length == 6) {
            Long time = System.currentTimeMillis();
            Double accX = Double.valueOf(parsed[0]);
            Double accY = Double.valueOf(parsed[1]);
            Double accZ = Double.valueOf(parsed[2]);
            Double roll = Double.valueOf(parsed[3]);
            Double pitch = Double.valueOf(parsed[4]);
            Double yaw = Double.valueOf(parsed[5]);
            SendData.sendOne( new MotionDataObject(time, accX, accY, accZ, roll, pitch, yaw), userId);
        }
    }

    public static void main(String[] args) throws Exception {
        SerialConnection main = new SerialConnection();
        main.initialize();
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
}
