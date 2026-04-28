package com.ecommerce.app.service;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ResourceNotFoundException;
import com.ecommerce.app.modal.Cart;
import com.ecommerce.app.modal.Category;
import com.ecommerce.app.modal.Product;
import com.ecommerce.app.payload.CartDTO;
import com.ecommerce.app.payload.ProductDTO;
import com.ecommerce.app.payload.ProductReponse;
import com.ecommerce.app.repositories.CartRepository;
import com.ecommerce.app.repositories.CategoryRepository;
import com.ecommerce.app.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    CartServiceImpl cartService;

    @Autowired
    CartRepository cartRepository;

    @Value("${project.image}")     // getting path value from application.properties
    private String path;

    @Value("${image.source.url}")
    private String imagesSourceUrl;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category", categoryId));

        boolean isProductNotExist = true;
        List<Product> products = category.getProduct();
        for(Product product : products) {

            if(product.getProductName().equals(productDTO.getProductName())) {
                isProductNotExist = false;
                break;
            }

        }

        if(isProductNotExist) {

            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setProductImage("default.png");

            double discountedPrice = product.getProductPrice() - Math.round(((product.getProductPrice() * 0.01) * product.getProductDiscount()));
            product.setProductDiscountedPrice(discountedPrice);

            Product savedProduct = productRepository.save(product);

            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else {
            throw new APIException("Product already exist!!!");
        }



    }

    @Override
    public ProductReponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String orderBy, String keyword, String category) {

        Sort pageSortAndOrderBy = orderBy.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, pageSortAndOrderBy);

        Specification<Product> productSpecification = Specification.where(null);

        if(keyword != null && !keyword.isEmpty()) {

            productSpecification = productSpecification.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%")
                    ));

        }



        if(category != null && !category.isEmpty() && !category.equals("all")) {

            productSpecification = productSpecification.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("categoryName")), category.toLowerCase())
            ));
        }

        Page<Product> productPage = productRepository.findAll(productSpecification, pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty())
            throw new APIException("No products created till now");

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                  ProductDTO  productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setProductImage(getImagesSourceUrl(product.getProductImage()));
                    return productDTO;
                })
                .toList();
        ProductReponse productReponse = new ProductReponse();
        productReponse.setContent(productDTOS);
        productReponse.setPageNumber(productPage.getNumber());
        productReponse.setPageSize(productPage.getSize());
        productReponse.setTotalElements(productPage.getTotalElements());
        productReponse.setTotalPages(productPage.getTotalPages());
        productReponse.setLastPage(productPage.isLast());


        return productReponse;
    }

    public String getImagesSourceUrl(String image)
    {
        return imagesSourceUrl.endsWith("/") ? imagesSourceUrl + image : imagesSourceUrl + "/" + image;
    }



    @Override
    public ProductReponse getProductByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));
        List<Product> products = productRepository.findByCategoryOrderByProductPriceAsc(category);

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        if(productDTOS.isEmpty())
            throw new APIException("No products found with in "+ category.getCategoryName() +" Category");

        ProductReponse productReponse = new ProductReponse();
        productReponse.setContent(productDTOS);

        return productReponse;


    }

    @Override
    public ProductReponse getProductsByKeyword(String keyword) {

        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        if(productDTOS.isEmpty())
            throw new APIException("No products found");

        ProductReponse productReponse = new ProductReponse();
        productReponse.setContent(productDTOS);

        return productReponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        //Get the saved product
        Product savedProductFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);
        //Update the values in existing saved products payload
        savedProductFromDB.setProductName(product.getProductName());
        savedProductFromDB.setProductDescription(product.getProductDescription());
        savedProductFromDB.setProductPrice(product.getProductPrice());
        savedProductFromDB.setProductDiscount(product.getProductDiscount());
        double discountedPrice = product.getProductPrice() - ((product.getProductPrice() * 0.01) * product.getProductDiscount());
        savedProductFromDB.setProductDiscountedPrice(discountedPrice);
        savedProductFromDB.setProductStock(product.getProductStock());

        // saving updated payload
        Product savedProduct = productRepository.save(savedProductFromDB);

        //update all the carts with updated product details

        updateAllCartsWithUpdatedProductDetails(savedProduct);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    private void updateAllCartsWithUpdatedProductDetails(Product product) {

        List<Cart> carts = cartRepository.findCartsByProductId(product.getProductId());

        List<CartDTO> cartDTOList = carts.stream().map(cart -> {

            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS = cart.getCartItems().stream().map(item ->
                    modelMapper.map(item.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOS);

            return cartDTO;

        }).toList();

        cartDTOList.forEach(cartDTO -> cartService.updateProductsInCarts(product.getProductId(), cartDTO.getCartId()));
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // DELETE product from carts
        List<Cart> carts = cartRepository.findCartsByProductId(savedProduct.getProductId());
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), savedProduct.getProductId()));

        productRepository.delete(savedProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        // Get Product from database
        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Upload image to server
        // Get the file name of uploaded image
        String fileName = fileService.uploadImage(path, image);

        // Update the product's image field with the new file name
        savedProduct.setProductImage(fileName);

        // Save updated product
        Product updatedProduct = productRepository.save(savedProduct);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }





}
