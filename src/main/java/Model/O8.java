package Model;

import java.util.ArrayList;

public class O8 {
    private Stock stock;                           // Склад
    private Delivery delivery;                     // Тип доставки
    private Currency currency;                     // Валюта
    private String invoice;                        // Номер счета
    private String supplier;                       // Код поставщика
    private String parcel;                         // Номер ТТН (для посылок)
    private String deferment;                      // Отсрочка платежа
    private ArrayList<Goods> goods;

    public enum Stock {MAIN,SECOND}
    public enum Currency {UAH,UA2,USD}
    public enum Delivery {SELF, COURIER, PARCEL}

    public O8() {
    }

    public O8(Stock stock, Delivery delivery, Currency currency, String invoice, String supplier, String parcel, String deferment, ArrayList<Goods> goods) {
        this.stock = stock;
        this.delivery = delivery;
        this.currency = currency;
        this.invoice = invoice;
        this.supplier = supplier;
        this.parcel = parcel;
        this.deferment = deferment;
        goods = new ArrayList<>();
    }
}


