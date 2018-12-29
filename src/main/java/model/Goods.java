package model;

public class Goods {
    private String sku;        // Код товара
    private String quantity;   // Количество
    private String price;      // Цена

    public Goods() {}
    public Goods(String sku, String quantity, String price) {
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "sku='" + sku + '\'' +
                ", quantity='" + quantity + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
