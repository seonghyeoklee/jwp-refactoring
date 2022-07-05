package kitchenpos.menu.domain;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Embedded
    private MenuPrice price = new MenuPrice();

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuGroup menuGroup;

    @Embedded
    private MenuProducts menuProducts = new MenuProducts();

    public Menu(String name, BigDecimal price, MenuGroup menuGroup, MenuProducts menuProducts) {
        this.name = name;
        this.price = new MenuPrice(price);
        this.menuGroup = menuGroup;
        this.menuProducts = menuProducts;
    }

    public Menu(String name, MenuPrice price, MenuGroup menuGroup) {
        this.name = name;
        this.price = price;
        this.menuGroup = menuGroup;
    }

    public Menu(MenuPrice price, MenuProducts menuProducts) {
        this.price = price;
        this.menuProducts = menuProducts;
    }

    public List<MenuProduct> getMenuProductList() {
        return menuProducts.getMenuProducts();
    }

    public void validateMenuGroup() {
        if (menuGroup != null && menuGroup.getId() == null) {
            throw new IllegalArgumentException();
        }
    }
}
