package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Shared.Exceptions.AlreadyHaveSame;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {

  private final CategoryRepo categoryRepo;

  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepo categoryRepo, CategoryMapper categoryMapper) {
    this.categoryRepo = categoryRepo;

    this.categoryMapper = categoryMapper;
  }

  public CategoryViewDTO createCategory(CategoryCreateRequest categoryCreateRequest) {

    Category category = new Category();
    category.setName(categoryCreateRequest.name());
    log.info("new category created {}", category);
    return categoryMapper.toCategoryViewDTO(categoryRepo.saveAndFlush(category));
  }

  public List<CategoryViewDTO> viewAllCategories() {
    List<CategoryViewDTO> resp = categoryMapper.toCategoryViewDTOList(categoryRepo.findAll());
    log.info("list categories returned : {}", resp);
    return resp;
  }

  public CategoryViewDTO viewCategoryById(Long id) {
    Optional<Category> category = categoryRepo.findById(id);
    if (category.isPresent()) {

      CategoryViewDTO resp = categoryMapper.toCategoryViewDTO(category.get());
      log.info("categories returned : {}", resp);

      return resp;
    }
    throw new TargetNotFound("Category not found");
  }

  public CategoryViewDTO updateCategory(CategoryCreateRequest categoryCreateRequest, Long id) {
    Optional<Category> category = categoryRepo.findById(id);
    if (category.isPresent()) {
      Category updateCategory = category.get();

      Category originalCopy = categoryMapper.clone(updateCategory);

      categoryMapper.updateFromRequest(categoryCreateRequest, updateCategory);

      if (originalCopy.equals(updateCategory)) {
        throw new IllegalArgumentException("no changes");
      }

      categoryRepo.save(updateCategory);
      CategoryViewDTO resp = categoryMapper.toCategoryViewDTO(updateCategory);
      log.info("categories updated : {}", resp);
      return resp;
    }
    throw new TargetNotFound("Category not found");
  }

  public boolean deleteCategory(Long id) {
    if (!categoryRepo.existsById(id)) {
      throw new TargetNotFound("Category not found");
    }
    log.info("categories deleted : {}", id);
    categoryRepo.deleteById(id);
    return true;
  }
}
