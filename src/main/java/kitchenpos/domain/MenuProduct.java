package kitchenpos.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.util.ObjectUtils;

@Table(name = "menu_product")
@Entity
public class MenuProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Embedded
    private Quantity quantity;

    protected MenuProduct() {

    }

    public MenuProduct(Product product, int quantity) {
        validProduct(product);
        this.product = product;
        this.quantity = Quantity.of(quantity);
    }

    public MenuProduct(Long id, Menu menu, Product product, Quantity quantity) {
        this.id = id;
        this.menu = menu;
        this.product = product;
        this.quantity = quantity;
    }

    public static MenuProduct of(Product product, int quantity) {
        return new MenuProduct(product, quantity);
    }

    private void validProduct(Product product) {
        if (ObjectUtils.isEmpty(product)) {
            throw new IllegalArgumentException("메뉴에 등록할 상품이 존재하지 않습니다.");
        }
    }

    public void changeMenu(Menu menu) {
        this.menu = menu;
    }
    public void changeSeq(int seq) {
        this.seq = seq;
    }

    public Amount getAmount() {
        return Amount.of(this);
    }

    public Long getSeq() {
        return seq;
    }

    public String getProductName() {
        return product.getName();
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Price getPrice() {
        return product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public Menu getMenu() {
        return menu;
    }
}
