# 🍳 키친포스 

## 요구 사항

- [X] kitchenpos 패키지의 코드를 보고 키친포스의 요구 사항을 README.md에 작성한다.
- [X] 정리한 키친포스의 요구 사항을 토대로 테스트 코드를 작성한다.

### Product (상품)

```text
# 상품 생성
POST /api/products
```

- 상품을 등록할 수 있다.
  - 상품의 가격은 반드시 존재해야 한다.
  - 상품의 가격은 0 원 이상이어야 한다.

```text
# 상품 목록 조회
GET /api/products
```

- 상품의 목록을 조회할 수 있다.

### Menu (메뉴)

```text
# 메뉴 생성
POST /api/menus
```

- 메뉴를 등록할 수 있다.
  - 메뉴의 가격은 무조건 존재해야 한다.
  - 상품의 가격은 0 원 이상이어야 한다.
  - 메뉴 그룹 아이디가 존재하지 않으면 등록할 수 없다.
  - 메뉴 상품은 모두 등록된 상품이어야 한다.
  - 메뉴의 가격은 메뉴 상품들의 가격의 총 가격보다 클 수 없다.

```text
# 메뉴 목록 조회
GET /api/menus
```

- 메뉴를 조회할 수 있다.

### MenuGroup (메뉴 그룹)

```text
# 메뉴 그룹 생성
POST /api/menu-groups
```

- 메뉴 그룹을 생성할 수 있다.

```text
# 메뉴 그룹 목록 조회
GET /api/menu-groups
```

- 메뉴 그룹을 조회할 수 있다.

### Table (주문 테이블)

```text
# 주문 테이블 생성
POST /api/tables
```

- 주문 테이블을 생성할 수 있다.

```text
# 주문 테이블 목록 조회
GET /api/tables
```

- 주문 테이블 목록을 조회할 수 있다.

```text
# 주문 테이블이 비어있는지 여부 변경
PUT /api/tables/{orderTableId}/empty
```

- 주문 테이블이 비어있는지 여부를 변경할 수 있다.
  - 주문 테이블은 반드시 등록되어 있어야 한다.
  - 주문 테이블은 단체 지정이 되어 있지 않아야 한다.
  - 주문 테이블의 주문 상태는 조리 중이거나 식사 중이면 안된다.

```text
# 주문 테이블에 방문한 손님 수 변경
PUT /api/tables/{orderTableId]/number-of-guests
```

- 주문 테이블에 방문한 손님 수를 변경할 수 있다.
  - 주문 테이블의 방문한 손님 수가 0명 이상이어야 한다.
  - 주문 테이블은 등록되어 있어야 한다.
  - 주문 테이블은 빈 테이블이 아니어야 한다.

### TableGroup (단체 지정)

```text
# 단체 지정 등록
POST /api/table-groups
```

- 단체 지정을 등록한다.
  - 주문 테이블들이 2개 이상 있어야 한다.
  - 주문 테이블들은 모두 등록된 주문 테이블이어야 한다.
  - 주문 테이블들은 빈 테이블이어야 한다.
  - 이미 단체 지정된 주문 테이블은 단체 지정할 수 없다.

```text
# 단체 지정 삭제
DELETE /api/table-groups/{tableGroupId}
```

- 단체 지정을 삭제할 수 있다.
  - 단체 내 주문 테이블들의 상태는 조리 중이거나 식사중이면 안된다.

### Order (주문)

```text
# 주문 생성
POST /api/orders
```

- 주문을 생성할 수 있다.
  - 주문 항목은 최소 1개 이상 있어야 한다.
  - 주문 항목 속 메뉴들은 모두 등록된 메뉴여야 한다.
  - 주문 테이블은 등록된 테이블이어야 한다.
  - 주문 테이블이 빈 테이블이 아니어야 한다.

```text
# 주문 목록 조회
GET /api/orders
```

- 주문 목록을 조회할 수 있다.

```text
# 주문 상태 변경
PUT /api/orders/{orderId}/order-status
```

- 주문 상태를 변경할 수 있다.
  - 주문은 등록된 주문이어야 한다.
  - 이미 완료된 주문은 상태를 수정할 수 없다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |
