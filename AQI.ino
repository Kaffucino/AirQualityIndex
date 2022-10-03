#include "PMS.h"
#include <SoftwareSerial.h>
#define pmsTX 5
#define pmsRX 6
#define fanCONTROL 9


// -------------------------- PMS7003 -----------------------------------------------------
const uint8_t  pmsWakeBefore           = 30;    // [SECONDS] Seconds PMS sensor should be active before reading it
bool           pmsSensorOnline         = false;
int            pmsSensorRetry          = 0;
bool           pmsNoSleep              = false;
bool           pmsWoken                = false;
const char     *airQuality, *airQualityRaw;
int            avgPM1, avgPM25, avgPM10;

// -------------------------- FILTER -------------------------------------------------------
int           val                      = 0;
int           writeVal                 = 0;
SoftwareSerial pmsSerial(pmsTX, pmsRX);
PMS pms(pmsSerial);
PMS::DATA data;

void initPMS() {
  pmsSerial.begin(9600);
    pms.wakeUp();
    pms.passiveMode();
    pmsWoken = true;
}

void   initFilter() {
  pinMode(fanCONTROL, OUTPUT);
  digitalWrite(fanCONTROL, LOW);
}

void setup() {  
  Serial.begin(9600);
  initPMS();
  initFilter();
}



void readPMS() { // Function that reads data from the PMS7003
  while (pmsSerial.available()) { pmsSerial.read();}
  pms.requestRead(); // Now get the real data
  
  if (pms.readUntil(data)) {
    int PM1 = data.PM_AE_UG_1_0;
    int PM2_5 = data.PM_AE_UG_2_5;
    int PM10 = data.PM_AE_UG_10_0;

////Print ID pm10 and concentration of pm10
    //Serial.println(1);
    Serial.println(PM10);

//Print ID pm2_5 and concentration of pm2_5
  //  Serial.println(2);
    Serial.println(PM2_5);
    

//     Assign a text value of how good the air is based on current value
//     http://www.amskv.sepa.gov.rs/kriterijumi.php
    if (PM10 <= 20) {
      airQualityRaw = "Good";
      val = 16;
    } else if (PM10 >= 21 && PM10 <= 40) {
      airQualityRaw = "Moderate";
      val = 8;
    } else if (PM10 >= 41 && PM10 <= 50) {
      airQualityRaw = "Unhealthy for sensitive groups";
      val = 16;
    } else if (PM10 >= 51 && PM10 <= 100) {
      airQualityRaw = "Unhealty";
      val = 24;
    } else if (PM10 > 100) {
      airQualityRaw = "Very unhealty";
      val = 24;
    }

  writeVal = map(val, 0, 24, 0, 255);
  analogWrite(fanCONTROL, writeVal);

}
}
void loop() {
    readPMS();
    
 // Serial.print("Air quality ");
//  Serial.println(airQualityRaw);

  delay(30000);
}
