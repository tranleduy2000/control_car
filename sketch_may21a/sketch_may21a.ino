#include <AFMotor.h>

AF_DCMotor motorLeft(2);
AF_DCMotor motorRight(3);

int currentSpeed = 255; // 0 -> 255

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial.println("Started ");
  currentSpeed = 255;

}

void test() {
  moveForward();
  delay(1000);
  moveLeft();
  delay(1000);
  moveRight();
  delay(1000);
  moveBackward();
  delay(1000);
  stopMotor();
}


void loop() {
  // put your main code here, to run repeatedly:
  checkCommand();
}

/**
   Kiểm tra có lệnh gửi tới hay không
*/
void checkCommand() {
  if (!Serial.available()) return;
  String cmd = "";

  char c = Serial.read();
  cmd = cmd + c;

 // Serial.println(cmd);

  if (cmd == "U") {
    moveForward();

  } else if (cmd == "R") {
    moveRight();

  } else if (cmd == "L") {
    moveLeft();

  } else if (cmd == "D") {
    moveBackward();

  } else if (cmd == "S") {

    stopMotor();
  }

}




void stopMotor() {
  motorLeft.run(RELEASE);
  motorRight.run(RELEASE);
}

/**
  Di chuyển sang trái, lùi motor trái, tiến motor phải
*/
void moveLeft(int speed) {
  motorLeft.setSpeed(speed);
  motorRight.setSpeed(speed);

  motorLeft.run(BACKWARD);
  motorRight.run(FORWARD);
}

void moveLeft() {
  moveLeft(currentSpeed);
}

/**
  Di chuyển sang phải, lùi motor phải, tiến motor trái
*/
void moveRight(int speed) {
  motorLeft.setSpeed(speed);
  motorRight.setSpeed(speed);

  motorLeft.run(FORWARD);
  motorRight.run(BACKWARD);
}

void moveRight() {
  moveRight(currentSpeed);
}

/**
  Tiến về phía trước, cả hai motor đều tiến
*/
void moveForward(int speed) {
  motorLeft.setSpeed(speed);
  motorRight.setSpeed(speed);

  motorLeft.run(FORWARD);
  motorRight.run(FORWARD);
}

void moveForward() {
  moveForward(currentSpeed);
}

/**
  Lùi về phía sau, cả hai motor đều lùi
*/
void moveBackward(int speed) {
  setSpeed(speed);

  motorLeft.run(BACKWARD);
  motorRight.run(BACKWARD);
}

void moveBackward() {
  moveBackward(currentSpeed);
}

/**
   Chỉnh tốc độ của hai động cơ
*/
void setSpeed(int speed) {
  motorLeft.setSpeed(speed);
  motorRight.setSpeed(speed);
}

