package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuAcceptanceTest.메뉴_생성_요청;
import static kitchenpos.acceptance.MenuGroupAcceptanceTest.메뉴_그룹_생성_요청;
import static kitchenpos.acceptance.ProductAcceptanceTest.제품_생성_요청;
import static kitchenpos.acceptance.TableAcceptanceTest.테이블_주문_번호_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.dto.MenuProductRequest;
import kitchenpos.dto.OrderRequest;
import kitchenpos.dto.OrderResponse;
import kitchenpos.dto.OrderStatusRequest;
import kitchenpos.dto.OrderTableResponse;
import kitchenpos.order.domain.OrderLineItemV2;
import kitchenpos.order.domain.OrderStatusV2;
import kitchenpos.order.domain.OrdersV2;
import kitchenpos.utils.RestAssuredHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("주문 인수테스트 기능")
class OrderAcceptanceTest extends AcceptanceTest {
    private static final String ORDER_URI = "/api/orders";

    @BeforeEach
    void setUp() {
        super.setUp();

        // given
        메뉴_그룹_생성_요청("후라이드세트");
        제품_생성_요청("후라이드", 16_000);
        final List<MenuProductRequest> 메뉴_제품들 = Arrays.asList(new MenuProductRequest(1L, 2));
        메뉴_생성_요청("반반후라이드", 16_000, 1L, 메뉴_제품들);
        테이블_주문_번호_생성_요청(3, false);
    }

    /**
     *  Given 메뉴 그룹이 등록되어 있고
     *    And 제품(상품)이 등록되어 있고
     *    And 메뉴가 등록되어 있고
     *    And 주문 테이블이 존재하고
     *  When 주문을 하면
     *  Then 주문내역에서 조회할 수 있다.
     */
    @Test
    @DisplayName("주문을 하면 주문내역에서 조회할 수 있다.")
    void createOrder() {
        // given
        final OrderTableResponse 주문_테이블_결과 = new OrderTableResponse(1L, null, 3, false);
        final OrdersV2 order = new OrdersV2(1L, 1L, OrderStatusV2.COOKING, LocalDateTime.now(), null);
        final OrderResponse 예상된_주문_결과 = new OrderResponse(1L, 주문_테이블_결과, OrderStatus.COOKING.name(), null, Arrays.asList(new OrderLineItemV2(1L, order, 1L, 2L)));

        // when
        final ExtractableResponse<Response> 주문_요청_결과 = 주문_요청(1L, 1L, 1L);
        주문_요청_결과_확인(주문_요청_결과);

        // then
        final ExtractableResponse<Response> 주문_조회_결과 = 주문_조회();
        주문_조회_결과_확인(주문_조회_결과, Arrays.asList(예상된_주문_결과));
    }

    /**
     *  Given 메뉴 그룹이 등록되어 있고
     *    And 제품(상품)이 등록되어 있고
     *    And 메뉴가 등록되어 있고
     *    And 주문 테이블이 존재하고
     *    And 주문을 하고
     *  When 주문 상태을 변경하면
     *  Then 주문 상태가 변경된다.
     */
    @Test
    @DisplayName("주문 상태를 변경하면 주문 상태가 변경된다.")
    void changeOrderStatus() {
        // given
        final OrderStatusV2 완료_상태 = OrderStatusV2.COMPLETION;
        주문_요청(1L, 1L, 1L);

        // when
        final ExtractableResponse<Response> 주문_상태_변경_결과 = 주문_상태_변경(1L, 완료_상태);

        // then
        주문_상태_변경_결과_확인(주문_상태_변경_결과, 완료_상태);
    }

    public static ExtractableResponse<Response> 주문_요청(Long 테이블_번호, Long 메뉴_번호, Long 갯수) {
        return RestAssuredHelper.post(ORDER_URI, new OrderRequest(테이블_번호, Arrays.asList(메뉴_번호)));
    }

    public static void 주문_요청_결과_확인(ExtractableResponse<Response> 주문_요청_결과) {
        assertThat(주문_요청_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 주문_조회() {
        return RestAssuredHelper.get(ORDER_URI);
    }

    public static void 주문_조회_결과_확인(ExtractableResponse<Response> 주문_조회_결과, List<OrderResponse> 예상된_주문_결과) {
        final List<OrderResponse> 실제_주문 = 주문_조회_결과.body().jsonPath().getList(".", OrderResponse.class);

        assertAll(
                () -> assertThat(주문_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(실제_주문).hasSize(예상된_주문_결과.size()),
                () -> 주문_결과_비교(실제_주문, 예상된_주문_결과)
        );
    }

    private static void 주문_결과_비교(List<OrderResponse> 주문1, List<OrderResponse> 주문2) {
        for (int idx = 0; idx < 주문1.size(); idx++) {
            int innerIdx = idx;
            assertAll(
                    () -> assertThat(주문1.get(innerIdx).getOrderStatus()).isEqualTo(주문2.get(innerIdx).getOrderStatus()),
                    () -> assertThat(주문1.get(innerIdx).getId()).isEqualTo(주문2.get(innerIdx).getId()),
                    () -> assertThat(주문1.get(innerIdx).getOrderTable().getId())
                            .isEqualTo(주문2.get(innerIdx).getOrderTable().getId())
            );
        }
    }

    public static ExtractableResponse<Response> 주문_상태_변경(Long 주문_번호, OrderStatusV2 변경할_상태) {
        final String uri = ORDER_URI + "/{orderId}/order-status";
        return RestAssuredHelper.putContainBody(uri, new OrderStatusRequest(변경할_상태), 주문_번호);
    }

    private void 주문_상태_변경_결과_확인(ExtractableResponse<Response> 주문_상태_변경_결과, OrderStatusV2 상태) {
        final OrderResponse 변경된_주문 = 주문_상태_변경_결과.body().jsonPath().getObject(".", OrderResponse.class);

        assertAll(
                () -> assertThat(주문_상태_변경_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(변경된_주문.getOrderStatus()).isEqualTo(상태.name())
        );
    }
}
