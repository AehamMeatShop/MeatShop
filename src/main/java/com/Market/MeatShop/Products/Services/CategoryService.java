package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Shared.Exceptions.AlreadyHaveSame;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    private final CategoryMapper categoryMapper;
    public CategoryService(CategoryRepo categoryRepo , CategoryMapper categoryMapper) {
        this.categoryRepo = categoryRepo;

        this.categoryMapper = categoryMapper;
    }

    public CategoryViewDTO createCategory(CategoryCreateRequest categoryCreateRequest) {

        Category category = new Category();
        category.setName(categoryCreateRequest.name());

        return categoryMapper.toCategoryViewDTO( categoryRepo.saveAndFlush(category));
    }

    public List<CategoryViewDTO> viewAllCategories() {
        return categoryMapper.toCategoryViewDTOList(categoryRepo.findAll());
    }

    public CategoryViewDTO viewCategoryById(Long id) {
        Optional<Category> category = categoryRepo.findById(id);
        if (category.isPresent()) {return categoryMapper.toCategoryViewDTO(category.get());}
        throw new TargetNotFound("Category not found");
    }



    public CategoryViewDTO updateCategory(CategoryCreateRequest categoryCreateRequest , Long id) {
       Optional<Category> category = categoryRepo.findById(id);
       if (category.isPresent()) {

           Category updateCategory= category.get();
           categoryMapper.updateFromRequest(categoryCreateRequest , updateCategory );
           Category orginalCopy = categoryMapper.clone(updateCategory);
           if(orginalCopy.equals(category.get())){
               throw new IllegalArgumentException("no changes");
           }
           categoryRepo.saveAndFlush(updateCategory);
           return categoryMapper.toCategoryViewDTO(updateCategory);
       }
       throw new TargetNotFound("Category not found");
    }

    public boolean deleteCategory(Long id) {
       if(!categoryRepo.existsById(id)){
         throw new TargetNotFound("Category not found");
       }

        categoryRepo.deleteById(id);
        return true;
    }

}
