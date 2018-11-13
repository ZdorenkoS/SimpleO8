package Model;

import java.util.ArrayList;

public class O8 {
    private String stock;                           // Склад
    private String delivery;                     // Тип доставки
    private String currency;                     // Валюта
    private String invoice;                        // Номер счета
    private String supplier;                       // Код поставщика
    private String parcel;                         // Номер ТТН (для посылок)
    private String deferment;                      // Отсрочка платежа
    private ArrayList<Goods> goods;



    public O8() {
    }

    public O8(String stock, String delivery, String currency, String invoice, String supplier) {
        this.stock = stock;
        this.delivery = delivery;
        this.currency = currency;
        this.invoice = invoice;
        this.supplier = supplier;
        goods = new ArrayList<>();
        parcel = "";
        deferment = "";

    }

    @Override
    public String toString() {
        return "O8{" +
                "stock='" + stock + '\'' +
                ", delivery='" + delivery + '\'' +
                ", currency='" + currency + '\'' +
                ", invoice='" + invoice + '\'' +
                ", supplier='" + supplier + '\'' +
                ", parcel='" + parcel + '\'' +
                ", deferment='" + deferment + '\'' +
                ", goods=" + goods +
                '}';
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getParcel() {
        return parcel;
    }

    public void setParcel(String parcel) {
        this.parcel = parcel;
    }

    public String getDeferment() {
        return deferment;
    }

    public void setDeferment(String deferment) {
        this.deferment = deferment;
    }

    public ArrayList<Goods> getGoods() {
        return goods;
    }

    public void setGoods(ArrayList<Goods> goods) {
        this.goods = goods;
    }
}


