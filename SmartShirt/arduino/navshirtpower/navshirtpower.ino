
#define DONE 8
#define RIGHT 9
#define STOP 10
#define RR 11

#define VCC 2
#define RIGHTBUZZ 3
#define DATAPINR 5
#define LATCHPINR 6
#define CLOCKPINR 7

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(DONE, OUTPUT);
  pinMode(RIGHT, INPUT);
  pinMode(STOP, INPUT);  
  pinMode(RR, INPUT);
  
  digitalWrite(RIGHT, LOW);
  digitalWrite(STOP, LOW);
  digitalWrite(RR, LOW);
  
  pinMode(LATCHPINR, OUTPUT);
  pinMode(CLOCKPINR, OUTPUT);
  pinMode(DATAPINR, OUTPUT);
  pinMode(RIGHTBUZZ, OUTPUT);
  digitalWrite(RIGHTBUZZ, HIGH);
  
  pinMode(VCC, OUTPUT);
  digitalWrite(VCC, HIGH);  
  zeroOut();
}

void loop() {
  if(digitalRead(RIGHT))
  {
    digitalWrite(DONE, HIGH);
    turnRight();
  }
  if(digitalRead(STOP))
  {
    Serial.print("hi\n");
    startstop();
  }
  if(digitalRead(RR))
    reroutebuzz();
}

void turnRight()
{ 
  for(int i = 0; i < 3; i++)
  {
    
    digitalWrite(RIGHTBUZZ,HIGH);
    if (i == 1)
    {
      digitalWrite(RIGHTBUZZ, LOW);
    }
    
    byte led = 0;
    for (int j = 1; j < 6; j++)
    {
      //ground LATCHPIN and hold low for as long as you are transmitting
      digitalWrite(LATCHPINR, LOW);
      shiftOut(DATAPINR, CLOCKPINR, MSBFIRST, led);  
      //return the latch pin high to signal chip that it
      //no longer needs to listen for information
      digitalWrite(LATCHPINR, HIGH);
      delay(500);
      bitSet(led,j);
    }    
  }
  zeroOut();
}

void zeroOut()
{
  digitalWrite(LATCHPINR, LOW);
  shiftOut(DATAPINR, CLOCKPINR, MSBFIRST, 0);
  digitalWrite(LATCHPINR, HIGH);
  delay(500);
}

void startstop()
{
  digitalWrite(RIGHTBUZZ, LOW);
  delay(1000);
  digitalWrite(RIGHTBUZZ, HIGH);
  delay(1000);
  digitalWrite(RIGHTBUZZ, LOW);
  delay(1000);
  digitalWrite(RIGHTBUZZ, HIGH);
  delay(1000);
  digitalWrite(RIGHTBUZZ, LOW);
  delay(1000);
  digitalWrite(RIGHTBUZZ, HIGH);
}

void reroutebuzz()
{
  digitalWrite(RIGHTBUZZ, LOW);
  delay(2500);
  digitalWrite(RIGHTBUZZ, HIGH);
}
