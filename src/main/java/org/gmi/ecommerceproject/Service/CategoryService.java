package org.gmi.ecommerceproject.Service;

import org.gmi.ecommerceproject.Model.Category;
import org.gmi.ecommerceproject.Payload.CategoryDTO;
import org.gmi.ecommerceproject.Payload.CategoryResponse;

import java.util.List;

public interface CategoryService {


    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);


    CategoryDTO deleteCategory(Long categoryId);


    CategoryDTO createCategories(List<CategoryDTO> categories);


    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

}
