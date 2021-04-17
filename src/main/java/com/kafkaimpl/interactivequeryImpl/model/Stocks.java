package com.kafkaimpl.interactivequeryImpl.model;

import java.util.Objects;

public  class Stocks {
    private String symbol;
    private double price;
    private int shares;
    private String exchange;
    private String assetClass;


    public Stocks(String symbol, double price, int shares, String exchange, String assetClass) {
        this.symbol = symbol;
        this.price = price;
        this.shares = shares;
        this.exchange = exchange;
        this.assetClass = assetClass;
    }

    public Stocks() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }


    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stocks stocks = (Stocks) o;
        return Double.compare(stocks.price, price) == 0 && shares == stocks.shares && symbol.equals(stocks.symbol) && exchange.equals(stocks.exchange) && assetClass.equals(stocks.assetClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, price, shares, exchange, assetClass);
    }

    @Override
    public String toString() {
        return "Stocks{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", shares=" + shares +
                ", exchange='" + exchange + '\'' +
                '}';
    }
}
