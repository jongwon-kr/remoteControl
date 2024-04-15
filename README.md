# 원격통신 및 제어 프로그램
### 프로젝트 기간
2022.10 ~ 2022. 12
### 개발 인원
개인프로젝트
### 사용언어 및 개발환경
Java, Eclipse, Github
### 프로젝트 목적
PC에 문제가 생겨 원격으로 PC조작이 필요한 상황처럼 PC를 원격에서 이용해야 되는 상황이 발생하게 되었을 때 사용자에게 네트워크를 통해 원격으로 PC를 조작하고 파일을 전송하고 받을 수 있고 음성 통화를 지원하기 위해 제작 되었다.
### 주요 구현 내용
- 화면 전송 : Robot클래스안에 있는 화면데이터를 송신 측 클라이언트1에서 서버로 보내고 서버에서 수신받은 화면 데이터를 클라이언트1과 연결되어있는 클라이언트에게 전달하고 반대로 전송도 하며 화면 공유 구현
- 원격 조작 : 키보드나 마우스의 동작을 캐치하고 클라이언트 측에 있는 화면의 해상도의 비율을 측정하여 조작 구현
- 음성 통화 : 음성 통화가 시작되면 각 클라이언트에서 Phone클래스와 PhoneServer클래스로 서로 데이터를 전송하게 하여 음성 통화 구현
- 파일 전송 : 원격 제어를 하는 클라이언트 측에서 파일 전송이 가능하고 P2P_server, P2P_connect와 Download클래스를 사용하여 파일전송 구현
### 느낀점 및 문제 해결
- TCP/IP와 Java의 Robot클래스를 주로 사용하여 구현하였고 이전에 구현해본 p2p 파일전송을 제외하고 전부 처음 구현해보는 기능들이라 각종 구현에 있어 막막함이 있었지만 결과적으로 구현을 마무리 했다.
- 화면 전송을 구현할 때 Robot 클래스의 screencapture를 통해 전송했지만 속도가 너무 느렸고 Stream을 통해 화면 데이터를 보내는 것으로 바꿨더니 초당 15프레임 정도로 성능이 올라갔었다.
- 원격 조작을 구현할 때 각 클라이언트의 해상도가 다를 경우 제 기능을 못하였고 각 클라이언트의 해상도와 마우스의 좌표를 계산식을 통해 해상도에 따라 맞는 좌표로 이동하게 구현하였다.
## 이미지
## 서버
### 클라이언트 접속 X
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/c0adb825-b8f6-4aca-bd91-a46421446315" width="200" height="50">

### 클라이언트 접속 O
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/82861886-5146-476f-b33d-892201d4c067" width="200" height="50">

## 클라이언트
### 메인화면
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/9fb858ae-b2d1-4dcc-8ea8-d0c0af74dd24" width="600" height="350">

### 로그인 화면 원격코드(고유키) 발급
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/07604e86-3b27-458c-b270-57456409f1df" width="600" height="350">

### 원격 접속 시도
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/77ce3a3d-b70d-4cd2-b52b-08e10aad836f" width="600" height="350">


### 원격 접속 화면 해당 화면에 마우스 및 키보드로 상대 컴퓨터를 원격 제어 가능
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/bd109bf1-c7e0-4333-aff9-07b4aa86a498" width="600" height="350">

### 우측 아래 음성 연결 시
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/0dcbf7c3-e926-47a7-9e67-fbb364dffb2a" width="600" height="350">

### 파일 전송
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/4f56a7d1-c038-4296-829a-6ca1e5d686b6" width="600" height="350">
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/bdf1a204-72ce-4c15-a5d8-3240eda731cd" width="600" height="350">

### 파일전송 완료 좌측 상단에 파일 생성
<img src="https://github.com/jongwon-kr/remoteControl/assets/76871947/fc0f0a1a-eb57-4260-af71-a4ba0269f520" width="600" height="350">

## END
