package org.gmi.ecommerceproject.Controller;
import jakarta.validation.Valid;
import org.gmi.ecommerceproject.Config.AppConstants;
import org.gmi.ecommerceproject.Payload.ProductDTO;
import org.gmi.ecommerceproject.Payload.ProductResponse;
import org.gmi.ecommerceproject.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        ProductDTO  updatedproductDTO = productService.addProduct(categoryId,productDTO);
        return new ResponseEntity<>(updatedproductDTO, HttpStatus.CREATED);
    }
    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(value = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                          @RequestParam(value = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                          @RequestParam(value = "sortBy",defaultValue =AppConstants.SORT_PRODUCT_BY,required = false ) String sortBy,
                                                          @RequestParam(value = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);

     }
     @GetMapping("/public/categories/{categoryId}/products")
     public ResponseEntity<ProductResponse> getAllProductsByCategory(@PathVariable Long categoryId,
                                                                     @RequestParam(value = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                     @RequestParam(value = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                                     @RequestParam(value = "sortBy",defaultValue =AppConstants.SORT_PRODUCT_BY,required = false ) String sortBy,
                                                                     @RequestParam(value = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
     }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(value = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                @RequestParam(value = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                                @RequestParam(value = "sortBy",defaultValue =AppConstants.SORT_PRODUCT_BY,required = false ) String sortBy,
                                                                @RequestParam(value = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Long productId) {
       ProductDTO upadatedProductDTO =  productService.updateProduct(productId,productDTO);
       return new ResponseEntity<>(upadatedProductDTO, HttpStatus.OK);

    }
    @DeleteMapping("/admin/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
    @PutMapping("/admin/{productId}/Image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("Image") MultipartFile productImage) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId,productImage);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
