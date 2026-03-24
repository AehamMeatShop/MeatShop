package com.Market.MeatShop.Products.Services;


import com.Market.MeatShop.Products.DTOs.ComponentRatioDTO;
import com.Market.MeatShop.Products.DTOs.ProdCompViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CreateProdCompsRequest;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Entities.ProductComponent;
import com.Market.MeatShop.Products.Enums.ProductTypes;
import com.Market.MeatShop.Products.Mappers.ProductComponentMapper;
import com.Market.MeatShop.Products.Repositories.ProductComponentRepo;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import com.Market.MeatShop.Shared.Exceptions.AlreadyHaveComponents;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Shared.Exceptions.TypeError;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class ProductComponentService {

 private final ProductRepo productRepo;
 private ProductComponentRepo productComponentRepo;
 private final ProductComponentMapper productComponentMapper;
   public ProductComponentService(ProductRepo productRepo, ProductComponentRepo productComponentRepo,ProductComponentMapper productComponentMapper) {
       
         this.productRepo=productRepo;
         this.productComponentRepo=productComponentRepo;
         this.productComponentMapper=productComponentMapper;
         }

         @Transactional
         public List<ProdCompViewDTO> createProductComponentList(CreateProdCompsRequest createProdCompsRequest) {
             List<ProdCompViewDTO>  response = new ArrayList<>();
          Product parentProduct = productRepo.findByIdWithComponents(createProdCompsRequest.parentProductId()).
                  orElseThrow (() ->  new TargetNotFound("the product : " +  createProdCompsRequest.parentProductId() + " doesn't exist"));
         if(!parentProduct.getProductType().equals(ProductTypes.COMPOSITE) ){
           throw new TypeError("the product type is not COMPOSITE");
         }
             if (!parentProduct.getComponents().isEmpty()) {
                 throw new AlreadyHaveComponents("the product already have components, if you want edit delete them then enter the new components!");
             }

          List<ComponentRatioDTO> compRatios  = createProdCompsRequest.components();

          for (ComponentRatioDTO compRatio : compRatios) {
              Product componentProduct = productRepo.findById(compRatio.componentProductID())
                      .orElseThrow(() -> new TargetNotFound("the product component : " +  createProdCompsRequest.parentProductId() + " doesn't exist"));
                  ProductComponent component = new ProductComponent();
                  //we most check if there are  circular dependency that the component re consest its self undirectly
                   component.setComponentProduct(componentProduct);
                   component.setParentProduct(parentProduct);
                   component.setRatioInKg(compRatio.ratioInKg());
                   productComponentRepo.save(component);
                   response.add(productComponentMapper.toViewDTO(component));
          }


          return response;
         }


}
