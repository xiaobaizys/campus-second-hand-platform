package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.repository.ProductRepository;
import com.campus.secondhandplatform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public Optional<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // 更新商品信息
                    existingProduct.setTitle(product.getTitle());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setOriginalPrice(product.getOriginalPrice());
                    existingProduct.setImageUrls(product.getImageUrls());
                    existingProduct.setCategory(product.getCategory());
                    existingProduct.setIsNegotiable(product.getIsNegotiable());
                    existingProduct.setIsNew(product.getIsNew());
                    existingProduct.setDeliveryMethod(product.getDeliveryMethod());
                    existingProduct.setLocation(product.getLocation());
                    existingProduct.setContactInfo(product.getContactInfo());
                    
                    return productRepository.save(existingProduct);
                });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByStatus(Product.ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Long categoryId, Pageable pageable) {
        if (categoryId != null && categoryId > 0) {
            return productRepository.findByCategoryIdAndKeywordAndStatus(
                    categoryId, keyword, Product.ProductStatus.AVAILABLE, pageable);
        } else {
            return productRepository.findByKeywordAndStatus(
                    keyword, Product.ProductStatus.AVAILABLE, pageable);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> advancedSearchProducts(
            String keyword, Long categoryId, Double minPrice, Double maxPrice, 
            String location, Boolean isNegotiable, Boolean isNew, 
            String deliveryMethod, Pageable pageable) {
        
        return productRepository.advancedSearchProducts(
                keyword, categoryId, minPrice, maxPrice, location, 
                isNegotiable, isNew, deliveryMethod, Product.ProductStatus.AVAILABLE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getHotProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTopProductsByViewCount(Product.ProductStatus.AVAILABLE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTopProductsByCreatedAt(Product.ProductStatus.AVAILABLE, pageable);
    }
    
    @Override
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setViewCount((product.getViewCount() == null ? 0 : product.getViewCount()) + 1);
            productRepository.save(product);
        });
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setLikeCount((product.getLikeCount() == null ? 0 : product.getLikeCount()) + 1);
            productRepository.save(product);
        });
    }
    
    @Override
    public void decrementLikeCount(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            int currentCount = product.getLikeCount() == null ? 0 : product.getLikeCount();
            product.setLikeCount(Math.max(0, currentCount - 1));
            productRepository.save(product);
        });
    }
    
    @Override
    public boolean updateProductStatus(Long id, Product.ProductStatus status) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setStatus(status);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isProductOwner(Long productId, Long userId) {
        return productRepository.findById(productId)
                .map(product -> product.getSeller().getId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCount() {
        return productRepository.count();
    }
}