# AllTerrainRobot
Code and documentation for All Terrain Robot and Android App for control.

This robot was developed in order to attempt to create a robot which could climb up stairs as well as travel over several different types of rough terrain. The design for the body of the robot was inspired by the Rocker-Bogie design Nasa used to create the Mars Rover. We decided to use an Arduino Uno to drive the robot and to control it using a Bluetooth connection. 

In order to accomplish this one member of the team developed an Android app with a simple interface to connect to the Arduino and send commands serially. The Arduino is able to process this as it would any other UART communication by using a Bluetooth HC-05 Module. Once the app is installed on an Android device, the connect to Bluetooth button is the only button enabled until a connection has been established. Once the connection is made the user can press the different buttons controlling the direction and speed of the robot and see messages sent back to the phone from the Arduino. These messages basically echo back to the user what command the Arduino is processing.

One thing to note is that the HC-05 Component seems to struggle with sending the whole message back to the application and sometimes only catches part of the message.

[Demo of Project]()

# Design 
![image](https://github.com/Myapi314/AllTerrainRobot/assets/97209406/b97788fb-4cd8-41e9-adc1-b6ae49ee54ee)


# Schematic
![image](https://github.com/Myapi314/AllTerrainRobot/assets/97209406/531eaf50-87f6-4443-ad10-3425787e5822)
[View most up-to-date schematic](https://crcit.net/c/e8763686cd944002bac02424c5b18bc1)

Use voltage divider for Rx pin on bluetooth module in order to get incoming voltage below 3.3V instead of 5V. Used resistors of 220 and 330 ohms.

# Development Environment
The code for the Arduino was developed using the Arduino IDE.

Development for the App was done in Android Studio using Java as the main language. 

# Resources
- [How to Mechatronics - Arduino Bluetooth Tutorial](https://howtomechatronics.com/tutorials/arduino/arduino-and-hc-05-bluetooth-module-tutorial/)
- [Obstacle Avoidance Arduino Smart Car Kit](https://drive.google.com/drive/folders/1x-4Q7ejT96UbP0u8h9a7TKwRvs_PbmB8?usp=drive_link)
- [Arduino Project Hub](https://projecthub.arduino.cc/Serge144/simple-bluetooth-lamp-controller-using-android-and-arduino-0903d8)
- [Geeks for Geeks - Connecting HC-05 Module](https://www.geeksforgeeks.org/all-about-hc-05-bluetooth-module-connection-with-android/)
- [Android Studio Developers](https://developer.android.com/)
- [Medium - Create Bluetooth Android App](https://medium.com/swlh/create-custom-android-app-to-control-arduino-board-using-bluetooth-ff878e998aa8)
- [Github - Android Simple Bluetooth Example](https://github.com/bauerjj/Android-Simple-Bluetooth-Example/tree/master)

# Future Development
- Additional sensor data

    It would be helpful to add sensors, such as an ultrasonic sensor to help the robot avoid certain obstacles. Additional sensor data could also be sent to the App and there could be additional screens that could show speed or distance from an object.
  
- Increase communication speed 

    The Bluetooth module being used is functional, however there may be better options that would handle the communication faster and allow for the robot to react quicker to messages from the app.
  
- Improve app interaction 

    Currently, the app handles the commands through single pushes to the buttons, however it would be interesting to implement the robot only moving when a directional button is held in. There is also some additional Bluetooth related handling that could be added to the app such as if Bluetooth is disabled there could be a pop-up to turn it on. In addition to that, if another Bluetooth module is used, it might be helpful to add a list of paired devices to the connection options so the user can pick which device they need to connect to.
  
