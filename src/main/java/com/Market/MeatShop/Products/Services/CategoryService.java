package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Products.Exceptions.CategoryAlreadyExist;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.EmptyResultDataAccessException;
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

}
