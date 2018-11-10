package Model;

public class O8 {
    private String supplier;                       // Код поставщика
    private String invoice;                        // Номер счета
    private String deferment;                      // Отсрочка платежа
    private String parcel;                         // Номер ТТН (для посылок)
    private int [] sku;                            // Перечень кодов товаров
    private int [] quantity;                       // Перечень количеств товаров
    private int [] price;                          // Перечень цен товаров
    private int summ;                              // Сумма по заказу
    private Stock stock;                           // Склад
    private Currency currency;                     // Валюта
    private Delivery delivery;                     // Тип доставки

    public enum Stock {MAIN,SECOND}
    public enum Currency {UAH,UA2,USD}
    public enum Delivery {SELF, COURIER, PARCEL}


}


