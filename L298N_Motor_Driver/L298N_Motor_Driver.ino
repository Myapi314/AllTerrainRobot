
// Because of how wheels are hooked up, the functions for moving will be slightly counterintuitive

#define Lpwm_pin  5     //pin of controlling speed---- ENA of motor driver board
#define Rpwm_pin  6    //pin of controlling speed---- ENB of motor driver board
int pinLB=2;             //pin of controlling turning---- IN1 of motor driver board
int pinLF=4;             //pin of controlling turning---- IN2 of motor driver board
int pinRB=7;            //pin of controlling turning---- IN3 of motor driver board
int pinRF=8;            //pin of controlling turning---- IN4 of motor driver board

int state = 0;
int norm_speed = 80;
int speed_val = 80;
int speed_inc = 20;
int min_speed = 50;
int max_speed = 255;
int turn_speed = 120;

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
    case 'f':
      // go forward
      go_forward(speed_val);
      Serial.print("Forward at ");
      Serial.println(speed_val);
      break;
    case 'b':
      // go backward
      go_backward(speed_val);
      Serial.print("Backward at ");
      Serial.println(speed_val);
      break;
    case 'l':
      // go left
      rotate_left(turn_speed);
      Serial.print("Left at ");
      Serial.println(turn_speed);
      break;
    case 'r':
      // go right
      rotate_right(turn_speed);
      Serial.print("Right at ");
      Serial.println(turn_speed);
      break;
    case 's':
      // stop
      speed_val = norm_speed;
      stopp();
      Serial.println("STOP");
      break;
    case 'u':
      // up speed
      speed_val += speed_inc;
      if (speed_val >= max_speed)
      {
        speed_val = max_speed;
      }
      update_speed(speed_val);
      Serial.print("Up Speed to ");
      Serial.println(speed_val);
      break;
    case 'd':
      // down speed
      speed_val -= speed_inc;
      if (speed_val <= min_speed)
      {
        stopp();
      }
      update_speed(speed_val);
      Serial.print("Slow Down to ");
      Serial.println(speed_val);
      break;
    default:
      break;
  }





  state = 0;
  
 }

void update_speed(unsigned char speed_val)
  {
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
  }

void rotate_left(unsigned char speed_val)    // speed_val：0~255
    {digitalWrite(pinRB,HIGH); 
     digitalWrite(pinRF,LOW);
     digitalWrite(pinLB,HIGH);
     digitalWrite(pinLF,LOW);
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
     
      
    }

void rotate_right(unsigned char speed_val)    // speed_val：0~255
    {
     digitalWrite(pinRB,LOW);  
     digitalWrite(pinRF,HIGH);
     digitalWrite(pinLB,LOW);  
     digitalWrite(pinLF,HIGH);
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
    }
    
void go_backward(unsigned char speed_val)        // speed_val：0~255
    {digitalWrite(pinRB,HIGH);
     digitalWrite(pinRF,LOW );  
     digitalWrite(pinLB,LOW); 
     digitalWrite(pinLF,HIGH);
     analogWrite(Lpwm_pin,speed_val);
     analogWrite(Rpwm_pin,speed_val);
      
     
    }
void go_forward(unsigned char speed_val)    // speed_val：0~255
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
