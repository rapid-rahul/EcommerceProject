package org.gmi.ecommerceproject.Service;

import org.gmi.ecommerceproject.Exception.APIException;
import org.gmi.ecommerceproject.Exception.ResourceNotFoundException;
import org.gmi.ecommerceproject.Model.Cart;
import org.gmi.ecommerceproject.Model.Category;
import org.gmi.ecommerceproject.Model.Product;
import org.gmi.ecommerceproject.Payload.CartDTO;
import org.gmi.ecommerceproject.Payload.ProductDTO;
import org.gmi.ecommerceproject.Payload.ProductResponse;
import org.gmi.ecommerceproject.Repository.CartRepository;
import org.gmi.ecommerceproject.Repository.CategoryRepository;
import org.gmi.ecommerceproject.Repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    //field Injection
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartService cartService;

    //Constructor Injection
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//    private final ModelMapper modelMapper;
//    private final FileService fileService;
//
//    public ProductServiceImpl(
//            ProductRepository productRepository,
//            CategoryRepository categoryRepository,
//            ModelMapper modelMapper,
//            FileService fileService
//    ) {
//        this.productRepository = productRepository;
//        this.categoryRepository = categoryRepository;
//        this.modelMapper = modelMapper;
//        this.fileService = fileService;
//    }

    @Value("${project.image}")
    private String imagePath;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for (Product p : products) {
            if(products.contains(p)) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double discountPrice = (product.getPrice() - (product.getPrice() * 0.01 * product.getDiscount()));
            product.setSpecialPrice(discountPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else {
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber-1, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("Product not Created till now!!");
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(c -> modelMapper.map(c, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setPageSize(pageSize);
        productResponse.setPageNumber(pageNumber);
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();



        Pageable pageDetails = PageRequest.of(pageNumber-1, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = pageProducts.getContent();
        if(products.isEmpty())
            throw new APIException(category.getCategoryName() + " category doesn't have any product!!");
        List<ProductDTO> productDTOS = products.stream().map(product->modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setPageSize(pageSize);
        productResponse.setPageNumber(pageNumber);
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;

    }



    @Override
    public ProductResponse searchProductByKeyword(String keyword,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber-1, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameOrDescription(keyword,pageDetails);

        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        if(products.isEmpty()){
            throw new APIException("Product not Found with keyword "+keyword);
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setPageSize(pageSize);
        productResponse.setPageNumber(pageNumber);
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        //Get the existing product from DB
        Product  product = modelMapper.map(productDTO, Product.class);

        Product productFromDB = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setCategory(product.getCategory());
        double specialPrice = productFromDB.getPrice() - (productFromDB.getPrice() * 0.01 * productFromDB.getDiscount());
        productFromDB.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(productFromDB);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOS = carts.stream().map(cart->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS = cart.getCartItems().stream()
                    .map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                    .toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();
        cartDTOS.forEach( cart->cartService.updateProductInCarts(cart.getCartId(),productId));


        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        //DELETE
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart->cartService.deleteProductFromCart(cart.getCartId(),productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);

    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile productImage) throws IOException {
        // 1. Validate the input file.
        if (productImage.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty.");
        }

        // 2. Find the product from the database.
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // 3. Upload the new image and get its unique filename.
         // Define your base path
        String uniqueFileName = fileService.uploadImage(imagePath, productImage);

        // 4. Update the product entity with the new filename.
        productFromDb.setImage(uniqueFileName);

        // 5. Save the updated product to the database.
        Product updatedProduct = productRepository.save(productFromDb);

        // 6. Return the updated DTO.
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

}