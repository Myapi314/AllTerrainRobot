
#define Lpwm_pin  5     //pin of controlling speed---- ENA of motor driver board
#define Rpwm_pin  6    //pin of controlling speed---- ENB of motor driver board
int pinLB=2;             //pin of controlling turning---- IN1 of motor driver board
int pinLF=4;             //pin of controlling turning---- IN2 of motor driver board
int pinRB=7;            //pin of controlling turning---- IN3 of motor driver board
int pinRF=8;            //pin of controlling turning---- IN4 of motor driver board

int state = 0;

void setup()
{
  pinMode(pinLB,OUTPUT); // /pin 2
  pinMode(pinLF,OUTPUT); // pin 4
  pinMode(pinRB,OUTPUT); // pin 7
  pinMode(pinRF,OUTPUT);  // pin 8
  pinMode(Lpwm_pin,OUTPUT);  // pin 5 (PWM) 
  pinMode(Rpwm_pin,OUTPUT);  // pin 6(PWM)   

  Serial.begin(9600); // Default communication rate of the Bluetooth module
}


void loop()
  {
  if(Serial.available() > 0){ // Checks whether data is comming from the serial port
    state = Serial.read(); // Reads the data from the serial port
//    Serial.print(state);
  }

  switch (state)
  {
    case '0':
      // go forward
      Serial.println("Begin Moving Forward...");
      go_forward(100);
      break;
    case '1':
      // go backward
      Serial.println("Begin Moving Backward...");
      go_backward(100);
      break;
    case '2':
      // go left
      Serial.println("Begin Moving Left...");
      rotate_left(100);
      break;
    case '3':
      // go right
      Serial.println("Begin Moving Right...");
      rotate_right(100);
      break;
    case '4':
      // stop
      Serial.println("STOP");
      stopp();
      break;
    default:
      break;
  }
  state = 0;
  
//  if (state == '0') {
//  go_backward(100);
//  delay(2000);
//  stopp();
//  Serial.println("GO BACK"); // Send back, to the phone, the String "LED: ON"
////  Serial.print("LED: OFF");
//  state = 0;
//  }
//  else if (state == '1') {
//  go_forward(100);
//  delay(2000);
//  stopp();
//  Serial.println("GO FORWARD");
////  Serial.print("GO FORWARD");
//  state = 0;
//  } 

  // Test functions
// go_forward(100);
// delay(2000);
// go_backward(100);
// delay(2000);
// rotate_left(150);
// delay(2000);
// rotate_right(150);
// delay(2000);
// stopp();
// delay(2000);
 }


void go_forward(unsigned char speed_val)    // speed_val：0~255
    {digitalWrite(pinRB,HIGH); 
     digitalWrite(pinRF,LOW);
     digitalWrite(pinLB,HIGH);
     digitalWrite(pinLF,LOW);
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
     
      
    }

void go_backward(unsigned char speed_val)    // speed_val：0~255
    {
     digitalWrite(pinRB,LOW);  
     digitalWrite(pinRF,HIGH);
     digitalWrite(pinLB,LOW);  
     digitalWrite(pinLF,HIGH);
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
    }
    
void rotate_left(unsigned char speed_val)        // speed_val：0~255
    {digitalWrite(pinRB,HIGH);
     digitalWrite(pinRF,LOW );  
     digitalWrite(pinLB,LOW); 
     digitalWrite(pinLF,HIGH);
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
      
     
    }
void rotate_right(unsigned char speed_val)    // speed_val：0~255
    {
      digitalWrite(pinRB,LOW);  
     digitalWrite(pinRF,HIGH);
     digitalWrite(pinLB,HIGH);
     digitalWrite(pinLF,LOW);  
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
     
    }    
void stopp()        //stop
    {
     digitalWrite(pinRB,HIGH);
     digitalWrite(pinRF,HIGH);
     digitalWrite(pinLB,HIGH);
     digitalWrite(pinLF,HIGH);
    }