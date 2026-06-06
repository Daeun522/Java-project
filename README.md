# JAVA-PROJECT : 쇼핑몰? 만들기

1. 개발 목적: 지금까지 수업에서 배운 내용을 기반으로 백엔드 기반 쇼핑몰 구현을 진행하고자 했는데 수업 목적과 학습 내용과의 연결성을 강화하기 위해 쇼핑몰의 구조를 띈 인터랙티브 콘텐츠를 만들게 됐습니다... 그러나 기본 골조는 쇼핑몰과 유사하게 만들고자 합니다. 앱의 컨셉이 된 자료 및 출처에 대해서는 issue를 참고해주시길 바랍니다.

2. 프로젝트 폴더 구성: 현재까지 구성한 폴더입니다.
   > 역할(기능)에 따라 명확하게 패키지를 분리해 유지보수성을 높였습니다.

Project
 ┣ 📂 main
 │ ┣ 📜 SpaceMallApp.java         # 프로그램의 메인 진입점 (JFrame / 화면 전환 관리자)
 │ ┣ 📂 model                     # 데이터의 구조를 정의하는 객체 (VO/DTO 역할)
 │ │ ┣ 📜 Product.java            # 상품 정보 (이름, 이미지, 가격, 재고 수량, 설명)
 │ │ ┗ 📜 User.java               # 유저 정보 (ID, PW, 전화번호, 주소, 누적금액, VIP 여부)
 │ ┣ 📂 db                        # 데이터를 메모리에 저장하고 관리하는 가짜(인메모리) DB 시스템
 │ │ ┣ 📜 CartDatabase.java       # 장바구니 내역 관리
 │ │ ┣ 📜 ProductDatabase.java    # 전체 상품 목록 및 재고 상태 관리
 │ │ ┣ 📜 PurchaseDatabase.java   # 유저별 결제/구매 내역 관리
 │ │ ┗ 📜 UserDatabase.java       # 회원 정보 관리
 │ ┗ 📂 ui                        # 사용자에게 보여지는 화면 (View 역할)
 │   ┣ 📜 IntroPanel.java         # 앱 실행 시 첫 시작 화면
 │   ┣ 📜 MainPanel.java          # 상품 목록 출력 및 쇼핑 메인 화면
 │   ┣ 📜 LoginPanel.java         # 로그인 화면
 │   ┣ 📜 RegisterPanel.java      # 회원가입 화면
 │   ┣ 📜 MyPagePanel.java        # 내 정보 확인 및 수정, 로그아웃 기능
 │   ┣ 📜 CartPanel.java          # 장바구니 리스트 및 결제 진행 화면
 │   ┣ 📜 CheckoutPanel.java      # 최종 결제 영수증 출력 및 배송 애니메이션 화면
 │   ┗ 📜 PurchaseHistoryPanel.java # 구매 완료된 내역(나의 물건) 확인 화면
 ┣ 📜 products.txt                # 초기 상품 데이터를 로드하기 위한 텍스트 파일
 ┗ 🖼️ background.png, apple.jpg... # UI 배경 및 상품 이미지 리소스


 > 기존 계획과는 다르게 Oracle 데이터베이스를 연결하느 대신, 프로그램이 실행되는 동안 메모리 상에서 데이터를 유지하는 인메모리 방식을 사용했습니다. 이때, Singleton 패턴을 적용해 각 db클래스가 프로그램 내에 오직 1개의 인스턴스만 생성되도록 설계해, 모든 패널이 항상 동일한 데이터를 바라보고 수정할 수 있습니다.
> 초기 상품데이터 파일 products.txt에 대해, ProductDatabase가 이 파일을 읽어와 객체리스트로 변환합니다.

[주요 DB 클래스 역할]
- UserDatabase: 회원가입시 List<User>에 계정을 추가하고, 로그인 시 ID/PW를 검증합니다.
- ProductDatabase: 텍스트파일에서 불러온 전체 상품을 관리합니다. 상품 재고가 0이 되어 완전 품절처리되면 상품을 삭제합니다.
- CartDatabase: 사용자가 담은 상품들을 임시저장합니다. 결제가 완료되거나 삭제하면 리스트를 비웁니다.
- PurchaseDatabase: 결제가 완료된 데이터를 보관합니다. 유저 ID를 키로 사용하는 Map<String, List<Product>> 구조를 채택해, 특정 유저의 구매 내역만 빠르게 필터링하여 불러올 수 있습니다.


3. 화면(Panel) 연결 및 전환 로직
> 화면 전환은 SpaceMallApp 내부의 CardLayout을 통해 이루어집니다.

[화면 이동 흐름도]
> 1. 시작: Intro -> 입장하기 클릭 -> MainPanel
> 2. 비로그인 상태 메인화면:
>    -> 로그인 -> LoginPanel -> (성공시)메인화면
>    -> 회원가입 -> RegisterPanel -> (성공시)메인화면
> 3. 로그인 상태 메인화면:
>    -> 장바구니 담기 -> 해당 상품 재고 1 감소 및 CartDatabase에 추가
>    -> 장바구니 보기 -> CartPanel
>    -> 내 정보 -> MyPagePanel
> 4. 결제 및 구매 내역:
>    -> CartPanel에서 상품 선택 후 결제 -> CheckoutPanel
>    -> CheckoutPanel -> 결제완료 -> (타이머) -> 구매데이터 저장, 장바구니 비움 -> MainPanel 복귀
>    -> MyPagePanel -> 나의물건 보기 -> PurchaseHistoryPanel -> 뒤로가기 -> MyPagePanel

4. 기타 로직
- 메인에서 장바구니에 상품을 담으면 즉시 남은 수량이 1 감소하며, 장바구니에서 해당 상품을 삭제하면 다시 수량 복구됨
- 품절 처리: 남은 수량이 0인 상품을 장바구니에 더 담을 수 없음. 수량이 0인 상품을 최종결제하면 상품 리스트에서 삭제하며, 모든 상품이 다 팔리면 메인화면에 이벤트 발생
- VIP 시스템: 일정 누적 구매 금액을 넘기면 결제 시점에서 VIP 팝업 발생. 이후 마이페이지 내 정보에 ~VIP~ 호칭이 부여되며, 특전 아이템이 자동으로 지급됨. 이는 나의 물건에서 확인 가능.

5. ~ 향후 추가 구현 목표 ~
> 단순한 쇼핑몰 앱 구현에서 유저의 상호작용에 따른 다양한 이벤트를 포함시킨 인터랙티브 콘텐츠로서 세부 목표가 변경됐습니다. 따라서 해당 컨셉에 맞게 더 다양한 이벤트를 추가해보고자 합니다.
> ing...
