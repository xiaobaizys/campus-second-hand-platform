package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.dto.ProductCreateDTO;
import com.campus.secondhandplatform.dto.ProductDTO;
import com.campus.secondhandplatform.dto.ProductUpdateDTO;
import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.entity.Category;
import com.campus.secondhandplatform.entity.Favorite;
import com.campus.secondhandplatform.service.ProductService;
import com.campus.secondhandplatform.service.FavoriteService;
import com.campus.secondhandplatform.repository.UserRepository;
import com.campus.secondhandplatform.repository.CategoryRepository;
import com.campus.secondhandplatform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.function.Function;
import java.util.Iterator;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = { "http://localhost:*" })
public class ProductController {

    private final ProductService productService;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(ProductService productService,
            FavoriteService favoriteService,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.productService = productService;
        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("用户未认证");
        }

        String name = authentication.getName();

        // 尝试先将name作为用户ID处理
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            // 如果不是数字，说明是用户名，需要根据用户名查找用户ID
            User user = userRepository.findByUsername(name)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + name));
            return user.getId();
        }
    }

    /**
     * 将Product实体转换为ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());

        // 将String类型的imageUrls转换为List<String>类型
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            try {
                // 假设imageUrls是JSON格式的字符串
                // 这里简化处理，实际应该使用JSON解析库
                String[] urls = product.getImageUrls().split(",");
                List<String> urlList = new ArrayList<>();
                for (String url : urls) {
                    urlList.add(url.trim());
                }
                dto.setImageUrls(urlList);
            } catch (Exception e) {
                // 如果解析失败，设置为空列表
                dto.setImageUrls(new ArrayList<>());
            }
        } else {
            dto.setImageUrls(new ArrayList<>());
        }

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getSeller() != null) {
            dto.setSellerId(product.getSeller().getId());
            // 如果用户没有昵称，使用用户名作为fallback
            String sellerName = product.getSeller().getNickname();
            if (sellerName == null || sellerName.isEmpty()) {
                sellerName = product.getSeller().getUsername();
            }
            dto.setSellerName(sellerName);
            dto.setSellerAvatar(product.getSeller().getAvatar());
        }

        dto.setIsNegotiable(product.getIsNegotiable());
        dto.setIsNew(product.getIsNew());
        dto.setDeliveryMethod(product.getDeliveryMethod());
        dto.setLocation(product.getLocation());
        dto.setContactInfo(product.getContactInfo());
        dto.setStatus(product.getStatus().toString());
        dto.setViewCount(product.getViewCount());
        dto.setLikeCount(product.getLikeCount());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    /**
     * 将ProductCreateDTO转换为Product实体
     */
    private Product convertToEntity(ProductCreateDTO dto) {
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());

        // 将List<String>类型的imageUrls转换为String类型
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            // 简化处理，使用逗号分隔的字符串
            StringBuilder sb = new StringBuilder();
            for (String url : dto.getImageUrls()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(url);
            }
            product.setImageUrls(sb.toString());
        } else {
            product.setImageUrls("");
        }

        product.setIsNegotiable(dto.getIsNegotiable());
        product.setIsNew(dto.getIsNew());
        product.setDeliveryMethod(dto.getDeliveryMethod());
        product.setLocation(dto.getLocation());
        product.setContactInfo(dto.getContactInfo());

        return product;
    }

    /**
     * 获取所有商品（分页）
     */
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Product> products;

        // 如果有分类ID或关键词，使用搜索功能
        if (categoryId != null || (keyword != null && !keyword.trim().isEmpty())) {
            products = productService.searchProducts(keyword, categoryId, pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        Page<ProductDTO> productDTOs = products.map(this::convertToDTO);

        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 高级搜索商品
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<ProductDTO>> advancedSearchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean isNegotiable,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) String deliveryMethod) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Product> products = productService.advancedSearchProducts(
                keyword, categoryId, minPrice, maxPrice, location,
                isNegotiable, isNew, deliveryMethod, pageable);

        Page<ProductDTO> productDTOs = products.map(this::convertToDTO);

        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 上传商品图片
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                response.put("error", "请选择要上传的图片");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "只能上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("error", "图片大小不能超过5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // 创建上传目录
            String uploadDir = "uploads/products/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件访问路径
            String fileUrl = uploadDir + newFilename;
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "图片上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            // 增加浏览量
            productService.incrementViewCount(id);
            ProductDTO productDTO = convertToDTO(product.get());
            return ResponseEntity.ok(productDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 上传商品图片
     */
    @PostMapping("/upload-image")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "请选择要上传的图片");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 创建上传目录
            String uploadDir = "uploads/products/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件访问URL
            String fileUrl = "/uploads/products/" + newFilename;
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "图片上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 创建新商品
     */
    @PostMapping
    // @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestBody ProductCreateDTO productCreateDTO) {
        System.out.println("进入createProduct方法");

        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("当前认证对象: " + authentication);

            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("用户未认证");
                return ResponseEntity.status(401).build();
            }

            // 检查用户权限
            boolean hasUserRole = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
            boolean hasAdminRole = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            System.out.println("用户权限检查结果：hasUserRole=" + hasUserRole + ", hasAdminRole=" + hasAdminRole);

            // 获取当前登录用户ID作为卖家ID
            Long currentUserId = getCurrentUserId();
            System.out.println("当前登录用户ID: " + currentUserId);

            // 获取用户和分类对象
            Optional<User> userOptional = userRepository.findById(currentUserId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Category> categoryOptional = categoryRepository.findById(productCreateDTO.getCategoryId());
            if (!categoryOptional.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            Product product = convertToEntity(productCreateDTO);
            // 设置初始状态为可用
            product.setStatus(Product.ProductStatus.AVAILABLE);
            // 初始化浏览量和点赞数
            product.setViewCount(0);
            product.setLikeCount(0);

            // 设置卖家和分类
            product.setSeller(userOptional.get());
            product.setCategory(categoryOptional.get());

            Product savedProduct = productService.createProduct(product);
            ProductDTO productDTO = convertToDTO(savedProduct);

            return ResponseEntity.ok(productDTO);
        } catch (Exception e) {
            System.out.println("创建商品失败，错误信息: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateDTO productUpdateDTO,
            @RequestParam(required = false) Long userId) {

        // 如果不是管理员，检查是否为商品所有者
        if (userId != null) {
            boolean isOwner = productService.isProductOwner(id, userId);
            if (!isOwner) {
                return ResponseEntity.status(403).build();
            }
        }

        // 获取现有商品
        Optional<Product> existingProductOpt = productService.getProductById(id);
        if (!existingProductOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product existingProduct = existingProductOpt.get();

        // 更新商品信息
        existingProduct.setTitle(productUpdateDTO.getTitle());
        existingProduct.setDescription(productUpdateDTO.getDescription());
        existingProduct.setPrice(productUpdateDTO.getPrice());
        existingProduct.setOriginalPrice(productUpdateDTO.getOriginalPrice());

        // 将List<String>类型的imageUrls转换为String类型
        if (productUpdateDTO.getImageUrls() != null && !productUpdateDTO.getImageUrls().isEmpty()) {
            // 简化处理，使用逗号分隔的字符串
            StringBuilder sb = new StringBuilder();
            for (String url : productUpdateDTO.getImageUrls()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(url);
            }
            existingProduct.setImageUrls(sb.toString());
        } else {
            existingProduct.setImageUrls("");
        }

        existingProduct.setIsNegotiable(productUpdateDTO.getIsNegotiable());
        existingProduct.setIsNew(productUpdateDTO.getIsNew());
        existingProduct.setDeliveryMethod(productUpdateDTO.getDeliveryMethod());
        existingProduct.setLocation(productUpdateDTO.getLocation());
        existingProduct.setContactInfo(productUpdateDTO.getContactInfo());

        // TODO: 更新分类
        // if (productUpdateDTO.getCategoryId() != null) {
        // existingProduct.setCategory(categoryService.getCategoryById(productUpdateDTO.getCategoryId()));
        // }

        Optional<Product> updatedProduct = productService.updateProduct(id, existingProduct);
        return updatedProduct.map(product -> ResponseEntity.ok(convertToDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        // 如果不是管理员，检查是否为商品所有者
        if (userId != null) {
            boolean isOwner = productService.isProductOwner(id, userId);
            if (!isOwner) {
                return ResponseEntity.status(403).build();
            }
        }

        boolean deleted = productService.deleteProduct(id);
        Map<String, String> response = new HashMap<>();

        if (deleted) {
            response.put("message", "商品删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "商品不存在或删除失败");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据卖家ID获取商品
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductDTO>> getProductsBySeller(@PathVariable Long sellerId) {
        List<Product> products = productService.getProductsBySellerId(sellerId);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 获取当前用户的商品
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProductDTO>> getMyProducts() {
        Long currentUserId = getCurrentUserId();

        List<Product> products = productService.getProductsBySellerId(currentUserId);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 根据分类ID获取商品
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productService.searchProducts(keyword, categoryId, pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDTO);

        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    public ResponseEntity<List<ProductDTO>> getHotProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getHotProducts(limit);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 获取最新商品
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ProductDTO>> getLatestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getLatestProducts(limit);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 点赞商品
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> likeProduct(@PathVariable Long id) {
        productService.incrementLikeCount(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "点赞成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 取消点赞商品
     */
    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> unlikeProduct(@PathVariable Long id) {
        productService.decrementLikeCount(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "取消点赞成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 更新商品状态
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Product.ProductStatus status,
            @RequestParam(required = false) Long userId) {

        // 如果不是管理员，检查是否为商品所有者
        if (userId != null) {
            boolean isOwner = productService.isProductOwner(id, userId);
            if (!isOwner) {
                return ResponseEntity.status(403).build();
            }
        }

        boolean updated = productService.updateProductStatus(id, status);
        Map<String, String> response = new HashMap<>();

        if (updated) {
            response.put("message", "商品状态更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "商品不存在或状态更新失败");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户收藏的商品
     */
    @GetMapping("/favorites")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<ProductDTO>> getFavoriteProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Long currentUserId = getCurrentUserId();

        // 获取用户收藏的所有商品
        List<Favorite> allFavorites = favoriteService.getUserFavorites(currentUserId);
        List<Product> allFavoriteProducts = allFavorites.stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());

        // 创建商品ID到收藏时间的映射
        Map<Long, LocalDateTime> favoriteCreatedAtMap = allFavorites.stream()
                .collect(Collectors.toMap(
                        favorite -> favorite.getProduct().getId(),
                        Favorite::getCreatedAt));

        // 根据关键词过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            allFavoriteProducts = allFavoriteProducts.stream()
                    .filter(product -> product.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                            product.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // 根据分类过滤
        if (categoryId != null) {
            allFavoriteProducts = allFavoriteProducts.stream()
                    .filter(product -> product.getCategory() != null
                            && product.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        // 计算分页
        int total = allFavoriteProducts.size();
        int start = Math.min(page * size, total);
        int end = Math.min(start + size, total);

        List<Product> pagedProducts = allFavoriteProducts.subList(start, end);

        // 转换为DTO
        List<ProductDTO> productDTOs = pagedProducts.stream()
                .map(product -> {
                    ProductDTO dto = convertToDTO(product);
                    // 设置收藏时间
                    dto.setFavoriteCreatedAt(favoriteCreatedAtMap.get(product.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        // 创建分页响应
        Page<ProductDTO> pageResponse = new Page<ProductDTO>() {
            @Override
            public int getTotalPages() {
                return (int) Math.ceil((double) total / size);
            }

            @Override
            public long getTotalElements() {
                return total;
            }

            @Override
            public int getNumber() {
                return page;
            }

            @Override
            public int getSize() {
                return size;
            }

            @Override
            public int getNumberOfElements() {
                return pagedProducts.size();
            }

            @Override
            public List<ProductDTO> getContent() {
                return productDTOs;
            }

            @Override
            public boolean hasContent() {
                return !productDTOs.isEmpty();
            }

            @Override
            public boolean isFirst() {
                return page == 0;
            }

            @Override
            public boolean isLast() {
                return page >= getTotalPages() - 1;
            }

            @Override
            public boolean hasNext() {
                return !isLast();
            }

            @Override
            public boolean hasPrevious() {
                return !isFirst();
            }

            @Override
            public Pageable nextPageable() {
                return PageRequest.of(page + 1, size);
            }

            @Override
            public Pageable previousPageable() {
                return page > 0 ? PageRequest.of(page - 1, size) : PageRequest.of(0, size);
            }

            @Override
            public <U> Page<U> map(Function<? super ProductDTO, ? extends U> converter) {
                return null;
            }

            @Override
            public Iterator<ProductDTO> iterator() {
                return productDTOs.iterator();
            }

            @Override
            public Sort getSort() {
                return Sort.by(Sort.Direction.DESC, "id");
            }
        };

        return ResponseEntity.ok(pageResponse);
    }

    /**
     * 管理员获取商品数量
     */
    @GetMapping("/admin/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getProductCount() {
        long count = productService.getProductCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}