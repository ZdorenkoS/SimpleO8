package project.model;

import java.util.Objects;

public class Goods {
    private String sku;        // Код товара
    private String quantity;   // Количество
    private String price;      // Цена
    private String name;       // Наименование товара


    public Goods() {}
    public Goods(String name, String sku, String quantity, String price) {
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Goods)) return false;
        Goods goods = (Goods) o;
        return Objects.equals(getSku(), goods.getSku()) &&
                Objects.equals(getPrice(), goods.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSku(), getPrice());
    }

    //TODO проверка скю больше 100 000
    public boolean goodsValidate(){
            if (sku.contains(" "))  sku = sku.replaceAll(" ", "");
            if (quantity.contains(" "))  quantity = quantity.replaceAll(" ", "");
            if (price.contains(" "))  price = price.replaceAll(" ", "");
            if (quantity.contains(",")) quantity = quantity.substring(0,quantity.indexOf(","));
            if (quantity.contains(".")) quantity = quantity.substring(0,quantity.indexOf("."));
            if (price.contains(".")) price = price.replace(".",",");
            try {
                Integer.parseInt(sku);
                Double.parseDouble(price.replace("," , "."));
                if (Integer.parseInt(quantity) < 1) throw new Exception();
            } catch (Exception ex) {
                return false;
            }
            return true;
    }

    @Override
    public String toString() {
        return "Goods{" +

                "name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                ", price='" + price + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}
