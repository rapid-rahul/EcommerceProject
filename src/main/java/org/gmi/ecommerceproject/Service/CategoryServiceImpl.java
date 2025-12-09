package org.gmi.ecommerceproject.Service;

import org.gmi.ecommerceproject.Exception.APIException;
import org.gmi.ecommerceproject.Model.Category;
import org.gmi.ecommerceproject.Payload.CategoryDTO;
import org.gmi.ecommerceproject.Payload.CategoryResponse;
import org.gmi.ecommerceproject.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.*;
import java.util.List;

@Service
public  class CategoryServiceImpl implements CategoryService {

//    private final List<Category> categories = new ArrayList<>();
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();


        Pageable  pageDetails = PageRequest.of(pageNumber-1, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
                if(categories.isEmpty()){
                    throw new APIException("Category not Created till now!!");
                }
        List<CategoryDTO> categoriesDTO = categories.stream()
                .map(c -> modelMapper.map(c, CategoryDTO.class))
                .toList();

                CategoryResponse categoryResponse = new CategoryResponse();
                categoryResponse.setContent(categoriesDTO);
                categoryResponse.setTotalElements(categoryPage.getTotalElements());
                categoryResponse.setTotalPages(categoryPage.getTotalPages());
                categoryResponse.setPageSize(pageSize);
                categoryResponse.setPageNumber(pageNumber);
                categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category existingCategory = categoryRepository.findByCategoryName((category.getCategoryName()));
        if (existingCategory != null)
            throw new APIException("Category with name %s already exists!!".formatted(category.getCategoryName()));

          Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO createCategories(List<CategoryDTO> categories) {

        // 1. Get existing category names from the database in a single query.
        List<String> existingNames = categoryRepository.findAll().stream()
                .map(Category::getCategoryName)
                .toList();

        // 2. Separate categories into new and existing ones.
        List<CategoryDTO> newCategoryDTOs = categories.stream()
                .filter(dto -> !existingNames.contains(dto.getCategoryName()))
                .toList();

        List<String> skippedNames = categories.stream()
                .map(CategoryDTO::getCategoryName)
                .filter(existingNames::contains)
                .toList();

        // 3. Handle skipped/duplicate categories first.
        if (!skippedNames.isEmpty()) {
            // Correct way to handle this: throw a ResponseStatusException.
            throw new APIException(
                    "The following categories already exist and were not created: %s".formatted(String.join(", ", skippedNames)));
        }

        // 4. If there are no new categories to add (i.e., all were duplicates).
        if (newCategoryDTOs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No new categories to add.");
        }

        // 5. Map the DTOs to entities and save.
        List<Category> categoriesToSave = newCategoryDTOs.stream()
                .map(dto -> modelMapper.map(dto, Category.class))
                .toList();

        categoryRepository.saveAll(categoriesToSave);
        return  modelMapper.map(categoriesToSave, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("Category with ID: %s Not Found!", categoryId)));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);

    }
     //@Override
//    public String deleteCategory(Long categoryId) {
//        List<Category> categories = categoryRepository.findAll();
//        Category category = categories.stream()
//                .filter(c->c.getCategoryId().equals(categoryId))
//                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found!"));
//        categoryRepository.delete(category);
//        return "Category with ID " +categoryId+ " deleted Successfully!";
//
//    }


    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Category with ID: %s not found!", categoryId)));

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        existingCategory.setCategoryId(categoryId);

        Category savedCategory = categoryRepository.save(existingCategory);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }




//    @Override
//    public String updateCategory(Category category, Long categoryId) {
//        List<Category> categories = categoryRepository.findAll();
//
//        Optional<Category> optionalCategory = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst();
//
//        if (optionalCategory.isPresent()) {
//            Category categoryToUpdate = optionalCategory.get();
//            categoryToUpdate.setCategoryName(category.getCategoryName());
//            categoryRepository.save(categoryToUpdate);
//
//            return "Category with ID " + categoryId + " updated successfully!";
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found!");
//        }
//    }

}