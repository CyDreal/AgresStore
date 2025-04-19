package com.example.agresstore.model;

public class CartProduct {

    private String nama;
    private String harga;
    private String stok;
    private String foto;
    private int quantity;
    private double subtotal;

    public CartProduct(String nama, String harga, String stok, String foto, int i) {
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.foto = foto;
        this.quantity = i;
        this.subtotal = Double.parseDouble(harga);
    }

    public String getNama() {
        return nama;
    }

    public String getHarga() {
        return harga;
    }

    public String getStok() {
        return stok;
    }

    public String getFoto() {
        return foto;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void incrementQuantity() {
        this.quantity++;
        this.subtotal = quantity * Double.parseDouble(harga);
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            this.subtotal = quantity * Double.parseDouble(harga);
        }
    }
}
