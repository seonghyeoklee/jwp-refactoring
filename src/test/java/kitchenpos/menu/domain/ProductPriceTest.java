package kitchenpos.menu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductPriceTest {

    @Test
    @DisplayName("가격이 음수 일 경우 에러 반환")
    public void createPriceUnderZeroException() {
        assertThatThrownBy(() -> new ProductPrice(BigDecimal.valueOf(-1))).isInstanceOf(IllegalArgumentException.class);
    }

}