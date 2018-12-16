package model;

import java.util.ArrayList;

public class O8 {
    private String stock;                          // Склад
    private String delivery;                       // Тип доставки
    private String currency;                       // Валюта
    private String invoice;                        // Номер счета
    private String supplier;                       // Код поставщика
    private String parcel;                         // Номер ТТН (для посылок)
    private String deferment;                      // Отсрочка платежа
    private ArrayList<Goods> goods;



    public O8() {}
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

    public void validation() {
        this.setParcel(this.parcel.trim());
        for (int i = 0; i < goods.size(); i++) {
            for (int j = 0; j < goods.size(); j++) {
                if (goods.get(i).getSku().equals(goods.get(j).getSku())
                        && goods.get(i).getPrice().equals(goods.get(j).getPrice())) {
                    goods.get(i).setQuantity(String.valueOf(Integer.parseInt(goods.get(i).getQuantity()) + Integer.parseInt(goods.get(j).getQuantity())));
                    goods.remove(j);
                }
            }
        }
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

    public String getDelivery() {
        return delivery;
    }

    public String getCurrency() {
        return currency;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getParcel() {
        return parcel;
    }

    public String getDeferment() {
        return deferment;
    }

    public ArrayList<Goods> getGoods() {
        return goods;
    }

    public void setParcel(String parcel) {
        this.parcel = parcel;
    }

    public void setDeferment(String deferment) {
        this.deferment = deferment;
    }
}


