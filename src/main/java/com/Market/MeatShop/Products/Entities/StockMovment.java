package com.Market.MeatShop.Products.Entities;
import com.Market.MeatShop.Finances.Entities.Invoice;
import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.security.PrivateKey;

@Table(name= "stock_movments")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "invoice_component_id", nullable = true)
    private InvoiceComponent invoiceComponent;



}
