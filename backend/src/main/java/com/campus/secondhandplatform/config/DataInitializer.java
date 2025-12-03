package com.campus.secondhandplatform.config;

import com.campus.secondhandplatform.entity.*;
import com.campus.secondhandplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlatformParamRepository platformParamRepository;
    private final PickupPointRepository pickupPointRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository, UserRepository userRepository, 
                          RoleRepository roleRepository, PlatformParamRepository platformParamRepository, 
                          PickupPointRepository pickupPointRepository, PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.platformParamRepository = platformParamRepository;
        this.pickupPointRepository = pickupPointRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化角色数据
        initializeRoles();
        
        // 初始化分类数据
        initializeCategories();
        
        // 初始化平台参数
        initializePlatformParams();
        
        // 初始化自提点
        initializePickupPoints();
        
        // 初始化用户数据
        initializeUsers();
    }
    
    private void initializeRoles() {
        // 检查是否已有角色数据
        if (roleRepository.count() == 0) {
            // 初始化角色数据
            List<Role> roles = Arrays.asList(
                createRole("ROLE_SUPER_ADMIN", "超级管理员，拥有所有权限"),
                createRole("ROLE_ADMIN", "普通管理员，拥有部分权限"),
                createRole("ROLE_USER", "普通用户，拥有基本权限")
            );

            roleRepository.saveAll(roles);
            System.out.println("初始化角色数据完成！");
        }
    }
    
    private void initializeCategories() {
        // 检查是否已有分类数据
        if (categoryRepository.count() == 0) {
            // 初始化分类数据
            List<Category> categories = Arrays.asList(
                createCategory("数码产品", "手机、电脑、相机等数码设备", "Iphone", 1),
                createCategory("图书教材", "教材、小说、参考书等", "Reading", 2),
                createCategory("生活用品", "日用品、装饰品等", "Notebook", 3),
                createCategory("运动户外", "运动器材、户外装备等", "Basketball", 4),
                createCategory("服装鞋包", "衣服、鞋子、包包等", "ShoppingBag", 5),
                createCategory("美妆护肤", "化妆品、护肤品等", "Camera", 6),
                createCategory("食品零食", "零食、饮料等", "Food", 7),
                createCategory("其他", "其他类别商品", "More", 8)
            );

            categoryRepository.saveAll(categories);
            System.out.println("初始化分类数据完成！");
        }
    }
    
    private void initializePlatformParams() {
        // 检查是否已有平台参数数据
        if (platformParamRepository.count() == 0) {
            // 初始化平台参数数据
            List<PlatformParam> params = Arrays.asList(
                createPlatformParam("review_time", "24", "商品审核时效（小时）"),
                createPlatformParam("trade_rules", "请遵守平台交易规则，诚信交易", "平台交易规则"),
                createPlatformParam("max_product_images", "5", "商品最大图片数量"),
                createPlatformParam("max_comment_length", "500", "评论最大长度"),
                createPlatformParam("default_avatar", "https://via.placeholder.com/150", "默认头像URL")
            );

            platformParamRepository.saveAll(params);
            System.out.println("初始化平台参数数据完成！");
        }
    }
    
    private void initializePickupPoints() {
        // 检查是否已有自提点数据
        if (pickupPointRepository.count() == 0) {
            // 初始化自提点数据
            List<PickupPoint> pickupPoints = Arrays.asList(
                createPickupPoint("图书馆自提点", "图书馆一楼大厅", "张同学", "13800138000"),
                createPickupPoint("食堂自提点", "第一食堂门口", "李同学", "13900139000"),
                createPickupPoint("宿舍区自提点", "学生宿舍1号楼楼下", "王同学", "13700137000")
            );

            pickupPointRepository.saveAll(pickupPoints);
            System.out.println("初始化自提点数据完成！");
        }
    }
    
    private void initializeUsers() {
        // 检查是否已有用户数据
        if (userRepository.count() == 0) {
            // 获取角色
            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseThrow(() -> new RuntimeException("ROLE_SUPER_ADMIN role not found"));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("ROLE_ADMIN role not found"));
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER role not found"));
            
            // 初始化用户数据
            List<User> users = Arrays.asList(
                createUser("admin", "admin@campus.com", "超级管理员", new HashSet<>(Arrays.asList(superAdminRole))),
                createUser("manager", "manager@campus.com", "普通管理员", new HashSet<>(Arrays.asList(adminRole))),
                createUser("test", "test@example.com", "测试用户", new HashSet<>(Arrays.asList(userRole))),
                createUser("seller1", "seller1@campus.com", "卖家用户", new HashSet<>(Arrays.asList(userRole))),
                createUser("buyer1", "buyer1@campus.com", "买家用户", new HashSet<>(Arrays.asList(userRole)))
            );

            userRepository.saveAll(users);
            System.out.println("初始化用户数据完成！");
        }
    }

    private Category createCategory(String name, String description, String iconUrl, int sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIconUrl(iconUrl);
        category.setSortOrder(sortOrder);
        category.setIsActive(true);
        return category;
    }
    
    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return role;
    }
    
    private PlatformParam createPlatformParam(String paramKey, String paramValue, String description) {
        PlatformParam param = new PlatformParam();
        param.setParamKey(paramKey);
        param.setParamValue(paramValue);
        param.setDescription(description);
        return param;
    }
    
    private PickupPoint createPickupPoint(String name, String address, String contact, String phone) {
        PickupPoint pickupPoint = new PickupPoint();
        pickupPoint.setName(name);
        pickupPoint.setAddress(address);
        pickupPoint.setContact(contact);
        pickupPoint.setPhone(phone);
        pickupPoint.setIsActive(true);
        return pickupPoint;
    }
    
    private User createUser(String username, String email, String realName, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password")); // 默认密码为 "password"
        user.setEmail(email);
        user.setRealName(realName);
        user.setRoles(roles);
        user.setIsActive(true);
        return user;
    }
}