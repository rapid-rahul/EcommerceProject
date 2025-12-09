package org.gmi.ecommerceproject.Controller;

import jakarta.validation.Valid;
import org.gmi.ecommerceproject.Config.AppConstants;
import org.gmi.ecommerceproject.Payload.CategoryDTO;
import org.gmi.ecommerceproject.Payload.CategoryResponse;
import org.gmi.ecommerceproject.Service.CategoryService;
import org.gmi.ecommerceproject.Model.Category;
import org.gmi.ecommerceproject.Service.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

     @GetMapping("/public/categories")
     public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(value = "pageNumber" ,defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                              @RequestParam(value = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                              @RequestParam(value = "sortBy",defaultValue =AppConstants.SORT_CATEGORIES_BY,required = false ) String sortBy,
                                                              @RequestParam(value = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder){
         CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
         return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
     }
     @PostMapping("/public/categories")
     public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
         return new ResponseEntity<>(savedCategoryDTO,HttpStatus.CREATED);
     }



    @PostMapping("/public/categories/bulk")
    public ResponseEntity<String> addCategories(@Valid @RequestBody List<CategoryDTO> categories) {
        categoryService.createCategories(categories);
        return new ResponseEntity<>("Categories added Successfully!", HttpStatus.CREATED);}

     @DeleteMapping("/admin/categories/{categoryId}")
     public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
         CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
         return new ResponseEntity<>(categoryDTO,HttpStatus.OK);
     }

     @PutMapping("/admin/categories/{categoryId}")
     public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){
            CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(updatedCategoryDTO,HttpStatus.OK);
            //return ResponseEntity.ok(categoryService.updateCategory(categoryId));
           // return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
     }
}
