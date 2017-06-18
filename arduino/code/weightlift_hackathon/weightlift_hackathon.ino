#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BNO055.h>
#include <math.h>
#include <utility/imumaths.h>
#include <EEPROM.h>
#include <Adafruit_NeoPixel.h>

/* Setting delay between fresh samples */
#define BNO055_SAMPLERATE_DELAY_MS (20)

/* Create BNO object with id, address (123 for left arm, 345 for right arm)*/
Adafruit_BNO055 bno = Adafruit_BNO055(123, 0x28);
// Neopixel
Adafruit_NeoPixel strip = Adafruit_NeoPixel(1, 6);

/* debug mode */
boolean debug = false;
/* if we want to clear EEPROM */
boolean clearEEPROM = false;
// timekeeping for error LED
unsigned long timeTurnedRed; 
/* pin for LED */
const int ledPin = LED_BUILTIN;
int ledState = LOW;
String side = "LEFT";
boolean error = false;

void blinkLED (void) {
  if (ledState == LOW) {
    ledState = HIGH;  
  } else {
    ledState = LOW;
  }
  digitalWrite(ledPin, ledState);
}

void displaySensorOffsets(const adafruit_bno055_offsets_t &calibData)
{
  Serial.print("\nAccelerometer: ");
  Serial.print(calibData.accel_offset_x); Serial.print(" ");
  Serial.print(calibData.accel_offset_y); Serial.print(" ");
  Serial.print(calibData.accel_offset_z); Serial.print(" ");

  Serial.print("\nGyro: ");
  Serial.print(calibData.gyro_offset_x); Serial.print(" ");
  Serial.print(calibData.gyro_offset_y); Serial.print(" ");
  Serial.print(calibData.gyro_offset_z); Serial.print(" ");

  Serial.print("\nMag: ");
  Serial.print(calibData.mag_offset_x); Serial.print(" ");
  Serial.print(calibData.mag_offset_y); Serial.print(" ");
  Serial.print(calibData.mag_offset_z); Serial.print(" ");

  Serial.print("\nAccel Radius: ");
  Serial.print(calibData.accel_radius);

  Serial.print("\nMag Radius: ");
  Serial.print(calibData.mag_radius);
}

void displayCalStatus(void)
{
  /* Printing calibration status to serial for sys, gyro, acc, mag */
  /* 0 = not Calibrated, 3 = fully calibrated */
  uint8_t sys, gyro, acc, mag;
  sys = gyro = acc = mag = 0;
  bno.getCalibration(&sys, &gyro, &acc, &mag);

  if (!sys)
  {
    Serial.print("!     ");
  }

  Serial.print("Sys:");
  Serial.print(sys, DEC);
  Serial.print("   G:");
  Serial.print(gyro, DEC);
  Serial.print("   A:");
  Serial.print(acc, DEC);
  Serial.print("   M:");
  Serial.print(mag, DEC);
}


/* Method that calibrates sensor; this should only happen once */
boolean calibrateSensor(void)
{
   sensors_event_t event;
   bno.getEvent(&event);

   while (!bno.isFullyCalibrated())
   {
      bno.getEvent(&event);

      /* Display calibration status for sys, gyro, acc, mag */
      displayCalStatus();
      Serial.print("\n");      

      delay(BNO055_SAMPLERATE_DELAY_MS);
   }

   Serial.println("\n\nCalibration complete!");
   Serial.println("\n\nCalibration results:");

   adafruit_bno055_offsets_t newCalib;
   bno.getSensorOffsets(newCalib);
   displaySensorOffsets(newCalib);

   Serial.println("\n\nStoring calibration data to EEPROM...");

   int eeAddress = 0;
   long bnoID;
   sensor_t sensor;
   bno.getSensor(&sensor);
   bnoID = sensor.sensor_id;

   EEPROM.put(eeAddress, bnoID);
   Serial.println("\nData stored to EEPROM");

   delay(500);
   return true;
}


void setup() {
  strip.begin();
  strip.setBrightness(128);
  strip.show();
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  Serial.begin(9600);
  delay(1000);

  if (!bno.begin())
  {
    if (debug)
    {
      Serial.println("No BNO055 detected");
    }
    while (1);
  }

  /* check if calibration data is on EEPROM */
  int eeAddress = 0;
  long bnoID;
  boolean foundCalib = false;

  /* Setting mode to NDOF */
  bno.setMode(bno.OPERATION_MODE_NDOF);
  
  EEPROM.get(eeAddress, bnoID);

  adafruit_bno055_offsets_t calibrationData;
  sensor_t sensor;

  bno.getSensor(&sensor);
  if (bnoID != sensor.sensor_id)
  {
    if (debug) 
    {
      Serial.println("\nNo Calibration Data found on EEPROM");
    }
    delay(500);

    /* since no calibration found, need to calibrate */
    foundCalib = calibrateSensor();
  } 
  else
  {
    if (debug)
    {
      Serial.println("\nFound calibration data on EEPROM");
    }
    
    eeAddress += sizeof(long);
    EEPROM.get(eeAddress, calibrationData);

    if (debug) 
    {
      Serial.println("\n\nRestoring calibration data...");
    }
    bno.setSensorOffsets(calibrationData);

    if (debug) {
      Serial.println("\nCalibration data loaded");
    }
    foundCalib = true;
  }

  delay(1000);

  bno.setExtCrystalUse(true);

  sensors_event_t event;

  if (foundCalib) 
  {
    /* Need to wait a bit so that gyroscope recalibrates on its own */
    while (!bno.isFullyCalibrated())
    {
      bno.getEvent(&event);
      if (debug) {
        displayCalStatus();
        Serial.println("");
      }
      blinkLED();
      delay(BNO055_SAMPLERATE_DELAY_MS);
    }
    digitalWrite(ledPin, HIGH);
    strip.setPixelColor(0, 0, 255, 0);
    strip.show();
    Serial.println("START");
  }
  else 
  {
    if (debug)
    {
      Serial.println("Error during calibration setup");
    }
  }

  if (debug) 
  {
    Serial.println("Setup is complete!");
  }
}

void loop() {
  /* 
   * Send acceleration raw data and Quat derived roll, pitch, yaw
   * Accelerometer data in x, y, z direction
   * Units: m/s^2, rad  
   */

  imu::Vector<3> accData = bno.getVector(Adafruit_BNO055::VECTOR_ACCELEROMETER);
  imu::Vector<3> euler = bno.getVector(Adafruit_BNO055::VECTOR_EULER);
  imu::Quaternion quat = bno.getQuat();

  float roll, pitch, yaw;

  // Use euler angles
  yaw = euler.x();
  pitch = euler.y();
  roll = euler.z();
  
  // Use quaternion for pitch, roll, yaw -- wasn't working so need to read more about this
  //  roll = atan2((2*quat.y()*quat.w() + 2*quat.x()*quat.z()), (1 - 2*quat.y()*quat.y() - 2*quat.z()*quat.z()));
  //  pitch = asin(2*quat.x()*quat.y() + 2*quat.z()*quat.z());
  //  yaw = atan2((2*quat.x()*quat.w() + 2*quat.y()*quat.z()), (1 - 2*quat.x()*quat.x() - 2*quat.z()*quat.z()));

  // Easy to read 
//  Serial.print(timeNow); Serial.print("\t\t");
//  Serial.print("X: "); Serial.print(accData.x()); Serial.print("\t");
//  Serial.print("Y: "); Serial.print(accData.y()); Serial.print("\t");
//  Serial.print("Z: "); Serial.print(accData.z()); Serial.print("\t\t\t");
//
//  Serial.print("roll: "); Serial.print(roll); Serial.print("\t");
//  Serial.print("pitch: "); Serial.print(pitch); Serial.print("\t");
//  Serial.print("yaw: "); Serial.print(yaw); Serial.print("\n");

  // csv for project
  Serial.print(side); Serial.print(", ");
  Serial.print(accData.x()); Serial.print(", ");
  Serial.print(accData.y()); Serial.print(", ");
  Serial.print(accData.z()); Serial.print(", ");
  // Roll goes from -180 to +180 (confusing me atm)
  // Pitch goes from -90 (arm down) to +90 (arm up)
  // Need to analyze change in pitch -- too fast bad, too lowe value is bad, too high bad
  // Yaw goes from 0 to 360 (depends on arm side)
  // Yaw needs to stay semi-consistent -- change is bad
  Serial.print(roll); Serial.print(", ");
  Serial.print(pitch); Serial.print(", ");
  Serial.print(yaw); Serial.print("\n");

  delay(BNO055_SAMPLERATE_DELAY_MS);

  unsigned long currentTime = millis();
  if ((error) && (currentTime - timeTurnedRed >= 2500)) {
    error = !error;
    strip.setPixelColor(0, 0, 255, 0);
    strip.show();
  }
}

/* Listen for serial events (errors from computer)
 *  
 */
void serialEvent() {
  Serial.flush();
  while (Serial.available()) {
    byte bufer[1];
    Serial.readBytes(bufer, 1);
    if (bufer[0] != NULL) {
      error = true;
      strip.setPixelColor(0, 255, 0, 0);
      strip.show();
      timeTurnedRed = millis();
    } else {
      error = false;    
    }
  }
}

