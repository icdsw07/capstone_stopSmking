/* 
 Citizen Sensor
 http://citizensensor.cc
 MQ-7 Carbon Monoxide Sensor
 Power Cycle + Analog Read
 
 for this example:
 - the "tog" pin of the breakout should be connected to digital pin 12.
 - the "out" pin of the breakout should be connected to analog pin 0.
 
 >> When the sensor is receiving 5v of power, the LED Indicator (Pin 13) will
 >> be ON.  During this time data the data being output is NOT readable.
 >> When the sensor is receiving 1.4v or power, the LED Indicator will be
 >> OFF.  This when the data being output is USABLE. 
 
 */

#include "CS_MQ7.h"
#include <SoftwareSerial.h>


int Tx=4;
int Rx=5;

SoftwareSerial BTSerial(Tx, Rx);CS_MQ7 MQ7(3, 13);  // 12 = digital Pin connected to "tog" from sensor board
                     // 13 = digital Pin connected to LED Power Indicator
int CoSensorOutput = 0; //analog Pin connected to "out" from sensor board
int CoData = 0;         //analog sensor data

void setup(){
  BTSerial.begin(9600);
  Serial.begin(9600);
}

void loop(){
  MQ7.CoPwrCycler();  

    CoData = analogRead(CoSensorOutput);
    //Serial.println(CoData);
    //if(CoData>=100){
    //    Serial.println(CoData);
    //      }
    if(BTSerial.available()){
      Serial.write(BTSerial.read());
      }
    if(Serial.available()){
      BTSerial.write(Serial.read());
    }
    
  delay(300);      
}
