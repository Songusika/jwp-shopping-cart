package woowacourse.shoppingcart.dao;

public class CartEntity {
    private final Long id;
    private final String name;
    private final int price;
    private final String imageUrl;

    public CartEntity(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}