package project.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Properties;

public class O8 {
    private String stock;                          // Склад
    private String delivery;                       // Тип доставки
    private String currency;                       // Валюта
    private String invoice;                        // Номер счета
    private String supplier;                       // Код поставщика
    private String parcel;                         // Номер ТТН (для посылок)
    private String deferment;                      // Отсрочка платежа
    private String date;                           // Дата прихода
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
        date = "";
    }

    public String buildString (int i){
         StringBuilder sb = new StringBuilder();
            for (int j = 0; j < goods.size(); j++) {
                sb.append(i + "\t");
                if (stock.equals("3001")) sb.append("P3001\t");
                if (stock.equals("5005")) sb.append("M5005\t");
                if (currency.equalsIgnoreCase("БЕЗНАЛ")) sb.append("UAH\t");
                else if (currency.equalsIgnoreCase("USD")) sb.append("USD\t");
                else sb.append("UA2\t");
                sb.append(supplier + "\t");
                sb.append(goods.get(j).getSku() + "\t");
                sb.append(goods.get(j).getQuantity() + "\t");
                sb.append(goods.get(j).getPrice() + "\t");

                if (supplier.equals("1669419") || supplier.equals("1400434")) {
                    if (invoice.contains("52/")) sb.append("DP");
                    else sb.append("CP");
                }
                sb.append("\t");
                try {
                    if (delivery.equalsIgnoreCase("КУРЬЕР")) sb.append("01\t\t");
                    else if (parcel.length()>2) sb.append("02" + "\t").append(parcel + "\t");
                    else sb.append("\t\t");
                } catch (NumberFormatException ex) {
                    sb.append("\t\t");
                }

                sb.append(invoice + "\t");
                try {
                    if (Integer.parseInt(deferment) > 0) sb.append(deferment + "\t");
                } catch (NumberFormatException ex) {
                    sb.append("\t");
                }
                try {if (!date.equals("")) sb.append(date + "\t");
                } catch (Exception ex) {
                    sb.append("\t");
                }

                sb.append("\n");
            }

        return sb.toString();
    }

    public void validation(ArrayList<O8> o8s,ArrayList<O8> o8Fail) {
        ArrayList<O8> o8ToRemove = new ArrayList<>();
        ListIterator<O8> iterator = o8s.listIterator();
        O8 o8;
        while (iterator.hasNext()){
            o8 = iterator.next();

            try {
                Integer.parseInt(o8.supplier);
                if (!o8.stock.equals("3001") && !o8.stock.equals("5005")) throw new NumberFormatException();
                if (o8.goods.isEmpty()) throw new NumberFormatException();
            }catch (NumberFormatException ex) {
                o8Fail.add(o8);
                o8ToRemove.add(o8);
                continue;
            }

            if (o8.deferment.equals("_")) o8.deferment = "";

            if (o8.invoice.contains("від")) o8.invoice = o8.invoice.substring(0,o8.invoice.indexOf("від"));
            if (o8.invoice.contains("от")) o8.invoice = o8.invoice.substring(0,o8.invoice.indexOf("от"));
            if (o8.invoice.equals("_")) o8.invoice = "";
            o8.invoice = o8.invoice.replaceAll("_", "");
            o8.invoice = o8.invoice.replaceAll("№", "");
            o8.invoice = o8.invoice.replaceAll("No", "");
            o8.invoice = o8.invoice.replace("Рахунок-фактура", "");
            o8.invoice = o8.invoice.replace("Рахунок на оплату по замовленню", "");
            o8.invoice = o8.invoice.replace("Рахунок на оплату за замовленням", "");
            o8.invoice = o8.invoice.replace("Рахунок на оплату", "");
            o8.invoice = o8.invoice.replace("Рахунок", "");
            o8.invoice = o8.invoice.replace("Счет", "");
            o8.invoice = o8.invoice.replace(" ", "");
            o8.supplier = o8.supplier.replaceAll(" ", "");

            if (o8.parcel.equals("_")) o8.parcel = "";
            o8.setParcel(o8.parcel.replaceAll(" ", ""));
            o8.setParcel(o8.parcel.replace("_", ""));
            o8.setParcel(o8.parcel.replace("ТТН", ""));
            o8.setParcel(o8.parcel.replace("НП", ""));
            o8.setParcel(o8.parcel.replace(",00", ""));
             try{
              if (o8.parcel.substring(0,6).matches("^\\D*$") && o8.parcel.length()>1) o8.parcel = "";}
             catch (StringIndexOutOfBoundsException ex) { }

             if (o8.date.contains(",")) o8.date = o8.date.replaceAll(",", ".");
             if (!o8.date.matches("^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*$")) o8.date = "";


             o8.goods.trimToSize();
             for (int i = o8.goods.size()-1; i > 0; i--) {                                                         // группируем одинаковые товары с одинаковыми ценами
                for (int j = i-1; j >= 0; j--) {
                    Goods goods1 = o8.goods.get(i);
                    Goods goods2 = o8.goods.get(j);

                    if (goods1.equals(goods2)) {
                        goods2.setQuantity(String.valueOf(Integer.parseInt(goods1.getQuantity()) + Integer.parseInt(goods2.getQuantity())));
                        o8.goods.remove(i);
                        break;
                    }
                }
             }


            for (int i = 0; i <o8.goods.size() ; i++) {
                if (!o8.goods.get(i).goodsValidate()) {
                    o8Fail.add(o8);
                    o8ToRemove.add(o8);
                    break;
                }
            }
        }

        o8s.removeAll(o8ToRemove);
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
    // TODO написать проверку и добавление новых поставщиков
    public String o8ForView() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream("src/main/resources/supplier.properties"),"cp1251"));}
        catch (IOException ex){
            System.out.println("Не удалось загрузить список поставщиков");
        }
        DecimalFormat myFormatter = new DecimalFormat("###,###.##");
        String summ = myFormatter.format(this.getSumm());
        String supp = properties.getProperty(supplier);


        StringBuilder sb = new StringBuilder();
        try {
        if (supp.length()<25) {
            sb.append(supp);
            for (int i = 0; i <24 - supp.length() ; i++) {
                sb.append(" ");
            }
        } else sb.append(supp.substring(0,24));}
        catch (NullPointerException ex){
            sb.append(supplier);
        }
        sb.append("\t");
        if (summ.length()<15){
            for (int i = 0; i <15-summ.length() ; i++) {
                sb.append(" ");
            }
            sb.append(summ);
        }else sb.append(summ);
        sb.append("\n");
       return sb.toString();
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
                ", date='" + date + '\'' +
                ", goods=" + goods +
                '}';
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

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        O8 o8 = (O8) o;
        return Objects.equals(stock, o8.stock) &&
                Objects.equals(currency, o8.currency) &&
                Objects.equals(invoice, o8.invoice) &&
                Objects.equals(supplier, o8.supplier) &&
                Objects.equals(parcel, o8.parcel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stock, currency, invoice, supplier, parcel);
    }
}


