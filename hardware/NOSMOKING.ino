#include "CS_MQ7.h"
#include <SoftwareSerial.h>

 
int Tx=4;
int Rx=5;
int sensorValue[1] = {0};

SoftwareSerial BTSerial(Tx, Rx);
CS_MQ7 MQ7(3, 13);  // 12 = digital Pin connected to "tog" from sensor board
int CoSensorOutput = 0; //analog Pin connected to "out" from sensor board
int CoData = 0;         //analog sensor data
 

void setup(){
  BTSerial.begin(9600);
  Serial.begin(9600);
}

void loop(){

  MQ7.CoPwrCycler();  
  sensorValue[0] = analogRead(CoSensorOutput);
  if(0<sensorValue[0] && sensorValue[0] < 23){
    BTSerial.write(Serial.read());
    BTSerial.println("0");
//    BTSerial.println(" ");
    Serial.println("0");
  }
  else if(23 <= sensorValue[0] && sensorValue[0] <= 60){
    BTSerial.write(Serial.read());
    BTSerial.println("1");
//    BTSerial.println(" ");
    Serial.println("1");
  }
//  else if(sensorValue[0] > 75){
else{
    BTSerial.write(Serial.read());
    BTSerial.println("2");
//    BTSerial.println(" ");
    Serial.println("2");
  }
  
//  
//  Serial.print(sensorValue[0]);
//  Serial.println();
  
  delay(500);    
}
