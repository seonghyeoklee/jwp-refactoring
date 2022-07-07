package kitchenpos.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Embeddable
public class Price {

    @Column(nullable = false)
    private BigDecimal price;

    protected Price() {
    }

    public Price(BigDecimal price) {
        this.price = validate(price);
    }

    private BigDecimal validate(BigDecimal price) {
        requireNonNull(price, "price");
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격이 0미만입니다.");
        }
        return price;
    }

    public boolean isGatherThan(BigDecimal sum) {
        return price.compareTo(sum) > 0;
    }

    public BigDecimal getValue() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Objects.equals(price, price1.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}
