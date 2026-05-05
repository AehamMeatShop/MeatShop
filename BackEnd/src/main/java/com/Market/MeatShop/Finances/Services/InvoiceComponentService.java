package com.Market.MeatShop.Finances.Services;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import com.Market.MeatShop.Finances.Repositories.InvoiceComponentRepo;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceComponentService {
    private final InvoiceComponentRepo invoiceComponentRepo;


    public InvoiceComponentService(InvoiceComponentRepo invoiceComponentRepo) {
        this.invoiceComponentRepo = invoiceComponentRepo;
    }

    public InvoiceComponent findByInvoiceComponentId(Long invoiceComponentId){
        Optional<InvoiceComponent> invComp=invoiceComponentRepo.findById(invoiceComponentId);
        if(invComp.isEmpty()){
            throw new TargetNotFound("invoiceComponentId "+invoiceComponentId + " not found");
        }
        return invComp.get();
    }
}
