package com.Market.MeatShop.Products.Mappers;

import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.Entities.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryViewDTO categoryViewDTO);
    CategoryViewDTO toCategoryViewDTO(Category category);
    List<CategoryViewDTO> toCategoryViewDTOList(List<Category> categoryList);
    List<Category> toCategoryList(List<CategoryViewDTO> categoryViewDTOList);
}
