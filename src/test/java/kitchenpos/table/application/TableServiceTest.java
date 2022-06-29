package kitchenpos.table.application;

import kitchenpos.application.TableService;
import kitchenpos.dao.OrderDao;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.OrderTableRepository;
import kitchenpos.tablegroup.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TableServiceTest {

    private TableService tableService;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        tableService = new TableService(orderDao, orderTableRepository);
    }

    @DisplayName("주문 테이블 생성한다.")
    @Test
    void create() {
        Mockito.when(orderTableRepository.save(any())).thenReturn(new OrderTable(1L, 3, false));

        // when
        OrderTable created = tableService.create(new OrderTable(3, false));

        // then
        assertThat(created).isNotNull();
    }

    @DisplayName("주문 테이블 목록을 조회한다.")
    @Test
    void list() {
        Mockito.when(orderTableRepository.findAll()).thenReturn(createOrderTableList());

        // when
        List<OrderTable> list = tableService.list();

        // then
        assertThat(list).isNotNull();
    }

    @DisplayName("주문 테이블 이용 여부를 변경한다.")
    @Test
    void changeEmpty() {
        Mockito.when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable(1L, 3, false)));
        Mockito.when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).thenReturn(false);
        Mockito.when(orderTableRepository.save(any())).thenReturn(new OrderTable(1L, 3, true));

        // when
        OrderTable changeOrderTable = new OrderTable(1L, 3, true);
        OrderTable changed = tableService.changeEmpty(1L, changeOrderTable);

        // then
        assertThat(changed).isNotNull();
        assertThat(changed.isEmpty()).isTrue();
    }

    @DisplayName("[예외] 저장되지 않은 주문 테이블의 주문 상태 변경한다.")
    @Test
    void changeEmpty_without_save() {
        Mockito.when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> {
            OrderTable changeOrderTable = new OrderTable(1L, 3, true);
            tableService.changeEmpty(1L, changeOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 테이블 그룹에 매핑된 주문 테이블의 상태를 변경한다.")
    @Test
    void changeEmpty_with_mapping_with_table_group() {
        Mockito.when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable(1L,new TableGroup(1L), 3, false)));

        // when, then
        assertThatThrownBy(() -> {
            OrderTable changeOrderTable = new OrderTable(1L, 3, true);
            tableService.changeEmpty(1L, changeOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 요리 중인 주문 테이블의 상태를 변경한다.")
    @Test
    void changeEmpty_with_cooking_order_table() {
        Mockito.when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable(1L, 3, false)));
        Mockito.when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> {
            OrderTable changeOrderTable = new OrderTable(1L, 3, true);
            tableService.changeEmpty(1L, changeOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블의 손님 수 변경한다.")
    @Test
    void changeNumberOfGuests() {
        Mockito.when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable(1L, 3, false)));
        Mockito.when(orderTableRepository.save(any())).thenReturn(new OrderTable(1L, 5, false));

        // when
        OrderTable orderTable = tableService.changeNumberOfGuests(1L, new OrderTable(5, false));

        // then
        assertThat(orderTable).isNotNull();
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(5);
    }

    @DisplayName("[예외] 현재 비어 있는 주문 테이블의 손님 수 변경한다.")
    @Test
    void changeNumberOfGuests_empty() {
        Mockito.when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable(1L, 3, true)));

        // when, then
        assertThatThrownBy(() -> {
            tableService.changeNumberOfGuests(1L, new OrderTable(5, true));
        }).isInstanceOf(IllegalArgumentException.class);
    }

    public static List<OrderTable> createOrderTableList() {
        return Arrays.asList(new OrderTable(1L, 3, false), new OrderTable(2L, 2, false));
    }

}
