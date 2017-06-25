int mPin1A = 4;
int mPin2A = 5;

int mPin1B = 6;
int mPin2B = 7;

int speedA = 50;
int speedB = 50;
int state, speedMin;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(mPin1A, OUTPUT);
  pinMode(mPin2A, OUTPUT);

  pinMode(mPin1B, OUTPUT);
  pinMode(mPin2B, OUTPUT);


}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available()) {
    state = Serial.read();
    Serial.println(state);
//    if (state >= 43) {
//      speedMin = (state - 43) * 25;
//      speedMin = constrain(speedMin, 0, 255);
//      speedA = speedMin;
//      speedB = speedMin;
//    }
    switch (state) {
      case 'S':
        dungMotor();
        break;
      case 'U':
        tienMotorA(speedA);
        tienMotorB(speedB);
        break;
      case 'D':
        luiMotorA(speedA);
        luiMotorB(speedB);
        break;
      //      case 3:
      //        tienMotorA(speedA - 25);
      //        tienMotorB(speedB + 25);
      //        break;
      //      case 4:
      //        tienMotorA(speedA + 25);
      //        tienMotorB(speedB - 25);
      //        break;
      case 'L':
        luiMotorA(speedA);
        tienMotorB(speedB);
        break;
      case 'R':
        tienMotorA(speedA);
        luiMotorB(speedB);
        break;
      default:
        break;
    }

  }
}

void tienMotorA(int speedM) {
  //  analogWrite(mSpeedPinA, speedM);
  digitalWrite(mPin1A, HIGH);
  digitalWrite(mPin2A, LOW);
}

void luiMotorA(int speedM) {
  //  analogWrite(mSpeedPinA, speedM);
  digitalWrite(mPin1A, LOW);
  digitalWrite(mPin2A, HIGH);
}

void tienMotorB(int speedM) {
  //  analogWrite(mSpeedPinB, speedM);
  digitalWrite(mPin1B, HIGH);
  digitalWrite(mPin2B, LOW);
}

void luiMotorB(int speedM) {
  //  analogWrite(mSpeedPinB, speedM);
  digitalWrite(mPin1B, LOW);
  digitalWrite(mPin2B, HIGH);
}
void dungMotor() {
  digitalWrite(mPin1B, LOW);
  digitalWrite(mPin2B, LOW);
  digitalWrite(mPin1A, LOW);
  digitalWrite(mPin2A, LOW);
  //  analogWrite(mSpeedPinB, 0);
  //  analogWrite(mSpeedPinB, 0);
}
