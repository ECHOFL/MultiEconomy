package me.fliqq.multieconomy.object;

import java.math.BigDecimal;

public class Currency {
    private final String id;
    private String name;
    private String symbol;
    private BigDecimal startingBalance;
    private boolean isDefault;
    private boolean transferable;
    private final boolean withdrawable;

    private final String withdrawItem;
    private final String withdrawName;
    private final String[] withdrawLore;

    public Currency(String id, String name, String symbol, BigDecimal startingBalance, boolean isDefault, boolean transferable,
    boolean withdrawable, String withdrawItem, String withdrawName, String[] withdrawLore){
        this.id=id;
        this.name=name;
        this.symbol=symbol;
        this.startingBalance=startingBalance;
        this.isDefault=isDefault;
        this.transferable=transferable;
        this.withdrawable = withdrawable;
        this.withdrawItem = withdrawItem;
        this.withdrawName = withdrawName;
        this.withdrawLore = withdrawLore;
    }


    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getSymbol(){
        return symbol;
    }
    public BigDecimal getStartingBalance(){
        return startingBalance;
    }
    public boolean isDefault(){
        return isDefault;
    }
    public boolean isTransferable(){
        return transferable;
    }

    public boolean isWithdrawable() {
        return withdrawable;
    }


    public String getWithdrawItem() {
        return withdrawItem;
    }

    public String getWithdrawName() {
        return withdrawName;
    }

    public String[] getWithdrawLore() {
        return withdrawLore;
    }

    public void setName(String name) { this.name = name; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setStartingBalance(BigDecimal startingBalance) { this.startingBalance = startingBalance; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public void setTransferable(boolean transferable) { this.transferable = transferable; }


    //UTILITIES
    public String format(BigDecimal amount){
        return symbol + amount.toPlainString();
    }

    @Override
    public boolean equals(Object o){
        if(this==o)
            return true;
        if(o==null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return id.equals(currency.id);
    }
    @Override
    public int hashCode(){
        return id.hashCode();
    }
    @Override
    public String toString(){
        return "Currency{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", startingBalance=" + startingBalance + 
                ", isDefault=" + isDefault +
                ", transferable=" + transferable + '}';
    }
}
