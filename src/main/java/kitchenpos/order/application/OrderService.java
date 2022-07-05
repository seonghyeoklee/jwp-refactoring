package kitchenpos.order.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItems;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.menu.repository.MenuRepository;
import kitchenpos.order.repository.OrderRepository;
import kitchenpos.table.repository.OrderTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final OrderCreator orderCreator;

    public OrderService(
            final MenuRepository menuRepository,
            final OrderRepository orderRepository,
            final OrderTableRepository orderTableRepository, OrderCreator orderCreator) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.orderCreator = orderCreator;
    }

    @Transactional
    public Order create(final OrderRequest orderRequest) {
        Order order = orderCreator.toOrder(orderRequest);
        OrderLineItems orderLineItems = checkOrderLineNotEmpty(order.getOrderLineItems());

        //저장되지 않은 메뉴 있으면 에러
        checkSavedMenus(orderLineItems);
        OrderTable orderTable = getExistsTable(order);

        order.setOrderTable(orderTable);
        order.setOrderStatus(OrderStatus.COOKING);
        order.setOrderedTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    private OrderLineItems checkOrderLineNotEmpty(OrderLineItems orderLineItems) {
        if (orderLineItems.isEmpty()) {
            throw new IllegalArgumentException("주문내역이 없습니다.");
        }
        return orderLineItems;
    }

    private OrderTable getExistsTable(Order order) {
        OrderTable orderTable = orderTableRepository.findById(order.getOrderTable().getId())
                .orElseThrow(() -> new IllegalArgumentException("주문 테이블이 존재하지 않습니다."));

        if (orderTable.isEmpty()) {
            throw new IllegalArgumentException("주문 테이블이 존재하지 않습니다.");
        }
        return orderTable;
    }

    private void checkSavedMenus(OrderLineItems orderLineItems) {
        final List<Long> menuIds = orderLineItems.getOrderLineItems().stream()
                .map((it) -> it.getMenu().getId())
                .collect(Collectors.toList());

        if (orderLineItems.size() != menuRepository.countByIdIn(menuIds)) {
            throw new IllegalArgumentException("주문내역의 메뉴들 중 등록되지 않은 메뉴가 있습니다.");
        }
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order changeOrderStatus(final Long orderId, final Order order) {
        final Order savedOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        savedOrder.changeOrderStatus(order.getOrderStatus());

        return orderRepository.save(savedOrder);
    }
}
