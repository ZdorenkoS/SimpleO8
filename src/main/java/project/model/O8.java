package project.model;

import java.util.ArrayList;
import java.util.ListIterator;

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

    public String buildString (int i){
        StringBuilder sb = new StringBuilder();
            for (int j = 0; j < goods.size(); j++) {
                sb.append(i + "\t");
                if (stock.equals("3001")) sb.append("P3001\t");
                if (stock.equals("5005")) sb.append("M5005\t");
                if (currency.equalsIgnoreCase("БЕЗНАЛ")) sb.append("UAH\t");
                if (currency.equalsIgnoreCase("НАЛ")) sb.append("UA2\t");
                if (currency.equalsIgnoreCase("USD")) sb.append("USD\t");
                sb.append(supplier + "\t");
                sb.append(goods.get(j).getSku() + "\t");
                sb.append(goods.get(j).getQuantity() + "\t");
                sb.append(goods.get(j).getPrice() + "\t");
                sb.append(" \t");
                try {
                    if (delivery.equalsIgnoreCase("КУРЬЕР")) sb.append("01\t");
                    else if (Long.parseLong(parcel) > 0) sb.append("02" + "\t").append(parcel + "\t");
                    else sb.append("\t");
                } catch (NumberFormatException ex) {
                    sb.append(" \t \t");
                }

                sb.append(invoice + "\t");
                try {
                    if (Integer.parseInt(deferment) > 0) sb.append(deferment + "\t");
                } catch (NumberFormatException ex) {
                    sb.append("\t");
                }
                sb.append("\n");
            }
        return sb.toString();
    }

    public void validation(ArrayList<O8> o8s,ArrayList<O8> o8Fail) {
        ListIterator<O8> iterator = o8s.listIterator();
        O8 o8;
        while (iterator.hasNext()){
            o8 = iterator.next();

            if (o8.parcel != null) o8.setParcel(o8.parcel.replaceAll(" ", ""));
            for (int i = 1; i < o8.goods.size(); i++) {
                if (o8.goods.get(i).getPrice().contains(" ")) o8.goods.get(i).setPrice(o8.goods.get(i).getPrice().replaceAll(" ",""));
            }
            for (int i = 1; i < o8.goods.size(); i++) {
                if (o8.goods.get(0).getSku().equals(o8.goods.get(i).getSku()) && o8.goods.get(0).getPrice().equals(o8.goods.get(i).getPrice())) {
                    o8.goods.get(0).setQuantity(String.valueOf(Integer.parseInt(o8.goods.get(0).getQuantity()) + Integer.parseInt(o8.goods.get(i).getQuantity())));
                    o8.goods.remove(i);
                    try {
                        Integer.parseInt(o8.goods.get(i).getSku());
                        Double.parseDouble(o8.goods.get(i).getPrice());
                        if (Integer.parseInt(o8.goods.get(i).getQuantity()) < 1) throw new Exception();
                    } catch (Exception e) {
                        iterator.remove();
                        o8Fail.add(o8);
                    }
                }
            }
        }
    }

    private float getSumm (){
        float f = 0.0f;
        for (Goods good: this.goods) {
           try {
               f += Float.parseFloat(good.getPrice().replaceAll(",",".")) * Float.parseFloat(good.getQuantity());
           } catch (NumberFormatException ex){
               System.out.println("ошибка при расчете суммы по О8, исходные данные-> код товара: " + good.getSku()+ " цена: " + good.getPrice()+" количество: "+ good.getQuantity());
           }
        }
        return f;
    }

    public String o8ForView() {
       return String.format("Поставщик: %6s \tСумма: %.2f\n", supplier, this.getSumm());
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

