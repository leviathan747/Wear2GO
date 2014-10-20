
// Possible commands are listed here:
//
// "digital/13"     -> digitalRead(13)
// "digital/13/1"   -> digitalWrite(13, HIGH)
// "analog/2/123"   -> analogWrite(2, 123)
// "analog/2"       -> analogRead(2)
// "mode/13/input"  -> pinMode(13, INPUT)
// "mode/13/output" -> pinMode(13, OUTPUT)

#include <Bridge.h>
#include <YunServer.h>
#include <YunClient.h>

int left = 0;
int right = 0;
int stopblip = 0;
int reroute = 0;

#define VCC1 3

#define LATCHPINL 6
#define CLOCKPINL 7
#define DATAPINL 5
#define LEFTBUZZ 3

#define DONE 8
#define RIGHT 9
#define STOP 10
#define RR 11

//Listen on default port 5555, the webserver on the Yun
// will forward there all the HTTP requests for us.
YunServer server;

void setup() {
  Serial.begin(9600);
  // Bridge startup
  pinMode(13,OUTPUT);
  digitalWrite(13, LOW);
  Bridge.begin();
  digitalWrite(13, HIGH);
  
  pinMode(DONE, INPUT);
  pinMode(RIGHT, OUTPUT);
  pinMode(STOP, OUTPUT);
  pinMode(RR, OUTPUT);
  digitalWrite(RIGHT, LOW);
  digitalWrite(STOP, LOW);
  digitalWrite(RR, LOW);
  
  
  pinMode(CLOCKPINL, OUTPUT);
  pinMode(LATCHPINL, OUTPUT);
  pinMode(DATAPINL, OUTPUT);
  pinMode(LEFTBUZZ, OUTPUT);
  digitalWrite(LEFTBUZZ, HIGH);
  
  /*
  pinMode(LATCHPINR, OUTPUT);
  pinMode(CLOCKPINR, OUTPUT);
  pinMode(DATAPINR, OUTPUT);
  pinMode(RIGHTBUZZ, OUTPUT);
  digitalWrite(RIGHTBUZZ, HIGH);
  */
  
  pinMode(VCC1,  OUTPUT);
  digitalWrite(VCC1, HIGH);
  //pinMode(VCC2,  OUTPUT);
  //digitalWrite(VCC2, HIGH);
  zeroOut();
  
  // Listen for incoming connection only from localhost
  // (no one from the external network could connect)
  server.listenOnLocalhost();
  server.begin();
}

void loop() {
  // Get clients coming from server
  YunClient client = server.accept();

  // There is a new client?
  if (client) {
    // Process request
    process(client);

    // Close connection and free resources.
    client.stop();
  }

  if (left)
  {
    turnLeft();
    left = 0;
  }
  
  if (right)
  {
    digitalWrite(RIGHT,HIGH);
    //turnRight();
    right = 0;
  }
  
  if (stopblip)
  {
    digitalWrite(STOP, HIGH);
    startstop();
    stopblip = 0;
    digitalWrite(STOP, LOW);
  }
  
  if (reroute)
  {
    digitalWrite(RR, HIGH);
    reroutebuzz();
    reroute = 0;
    digitalWrite(RR, LOW);
  }
  
  if(digitalRead(DONE))
    digitalWrite(RIGHT, LOW);
    
  delay(50); // Poll every 50ms
}

void process(YunClient client) {
  // read the command
  String command = client.readStringUntil('/');

  if (command == "command")
    messageCommand(client);
}



void messageCommand(YunClient client) {

  char command = client.read();

  // Send feedback to client
  client.print(F("Message"));
  client.print(F(" set to "));
  client.println(command);

    // is "left" command?
  if (command == 'l') {
    left = 1;
  }

  // is "right" command?
  if (command == 'r') {
    right = 1;
  }

  // is "stopblip" command?
  if (command == 's') {
    stopblip = 1;
  }
  // is "reroute" command?
  if (command == 'c') {
    reroute = 1;
  }
 
    // Update datastore key with the current message value
  String key = "Command";
  Bridge.put(key, (String)command);
}

void turnLeft()
{
  for(int i = 0; i < 3; i++)
  {
    
    digitalWrite(LEFTBUZZ, HIGH);
    if (i == 1)
    {
      digitalWrite(LEFTBUZZ, LOW);
    }
    
    byte led = 0;
    for (int j = 6; j >= 1; j--)
    {
      //ground LATCHPIN and hold low for as long as you are transmitting
      digitalWrite(LATCHPINL, LOW);
      shiftOut(DATAPINL, CLOCKPINL, MSBFIRST, led);  
      //return the latch pin high to signal chip that it
      //no longer needs to listen for information
      digitalWrite(LATCHPINL, HIGH);
      delay(500);
      bitSet(led,j-1);
      //Serial.print(j);
    }    
  }
  zeroOut();
}
/*
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
*/

void zeroOut()
{
  digitalWrite(LATCHPINL, LOW);
  //digitalWrite(LATCHPINR, LOW);
  //shiftOut(DATAPINR, CLOCKPINR, MSBFIRST, 0);
  shiftOut(DATAPINL, CLOCKPINL, MSBFIRST, 0);
  digitalWrite(LATCHPINL, HIGH);
  //digitalWrite(LATCHPINR, HIGH);
  delay(500);
}

void startstop()
{
  digitalWrite(LEFTBUZZ, LOW);
  //digitalWrite(RIGHTBUZZ, LOW);
  delay(1000);
  digitalWrite(LEFTBUZZ, HIGH);
  //digitalWrite(RIGHTBUZZ, HIGH);
  delay(1000);
  digitalWrite(LEFTBUZZ, LOW);
  //digitalWrite(RIGHTBUZZ, LOW);
  delay(1000);
  digitalWrite(LEFTBUZZ, HIGH);
  //digitalWrite(RIGHTBUZZ, HIGH);
  delay(1000);
  digitalWrite(LEFTBUZZ, LOW);
  //digitalWrite(RIGHTBUZZ, LOW);
  delay(1000);
  digitalWrite(LEFTBUZZ, HIGH);
  //digitalWrite(RIGHTBUZZ, HIGH);
}

void reroutebuzz()
{
  digitalWrite(LEFTBUZZ, LOW);
  //digitalWrite(RIGHTBUZZ, LOW);
  delay(2500);
  digitalWrite(LEFTBUZZ, HIGH);
  //digitalWrite(RIGHTBUZZ, HIGH);
}

