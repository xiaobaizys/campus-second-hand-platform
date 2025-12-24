-- 插入测试用户数据
-- 密码都是 "password" 的BCrypt加密值

-- 管理员用户
INSERT IGNORE INTO users (id, username, password, email, role, is_active) 
VALUES (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@campus.com', 'ADMIN', 1);

-- 测试用户
INSERT IGNORE INTO users (id, username, password, email, role, is_active) 
VALUES (2, 'test', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'test@example.com', 'USER', 1);

-- 卖家用户
INSERT IGNORE INTO users (id, username, password, email, role, is_active) 
VALUES (3, 'seller1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'seller1@campus.com', 'USER', 1);

-- 买家用户
INSERT IGNORE INTO users (id, username, password, email, role, is_active) 
VALUES (4, 'buyer1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'buyer1@campus.com', 'USER', 1);

-- 添加分类数据
INSERT IGNORE INTO categories (id, name, description, icon_url, sort_order, is_active) VALUES
(1, '数码产品', '手机、电脑、平板等数码设备', '/icons/digital.png', 1, 1),
(2, '图书教材', '教材、参考书、小说等图书', '/icons/books.png', 2, 1),
(3, '生活用品', '生活日用品、装饰品等', '/icons/lifestyle.png', 3, 1),
(4, '运动健身', '运动器材、健身用品等', '/icons/sports.png', 4, 1),
(5, '服装鞋包', '衣服、鞋子、包包等', '/icons/clothing.png', 5, 1),
(6, '美妆护肤', '化妆品、护肤品等', '/icons/beauty.png', 6, 1),
(7, '食品零食', '零食、饮料、特产等', '/icons/food.png', 7, 1),
(8, '其他', '其他类型的物品', '/icons/other.png', 8, 1);

-- 添加商品数据
INSERT IGNORE INTO products (id, title, description, price, original_price, image_urls, category_id, seller_id, status, view_count, like_count, is_negotiable, is_new, delivery_method, location, contact_info) VALUES
-- 数码产品
(1, 'iPhone 13 128GB', '99新iPhone 13，128GB存储空间，电池健康95%，配件齐全', 4599.00, 5999.00, '/images/iphone13.jpg', 1, 3, 'AVAILABLE', 120, 25, true, false, '自提', '校园内', '13800138000'),
(2, 'MacBook Air M1 2020', '轻薄便携，M1芯片性能强劲，8GB+256GB，几乎全新', 6999.00, 9499.00, '/images/macbookair.jpg', 1, 3, 'AVAILABLE', 85, 18, true, false, '自提', '校园内', '13800138000'),
(3, 'iPad Pro 11英寸', 'iPad Pro 11英寸，2021款，128GB，Wi-Fi版，带Apple Pencil', 5299.00, 6799.00, '/images/ipadpro.jpg', 1, 3, 'AVAILABLE', 68, 15, true, false, '自提', '校园内', '13800138000'),

-- 图书教材
(4, '高等数学同济第七版', '高等数学上下册，同济第七版，几乎全新，无笔记', 35.00, 72.00, '/images/mathbook.jpg', 2, 2, 'AVAILABLE', 210, 45, true, false, '自提', '图书馆门口', '13900139000'),
(5, '大学英语四级真题', '2024年最新版四级真题，含听力音频，仅做过两套', 15.00, 49.80, '/images/englishtest.jpg', 2, 2, 'AVAILABLE', 156, 32, true, false, '自提', '图书馆门口', '13900139000'),
(6, '算法导论第三版', '计算机经典教材，算法导论第三版，英文原版', 80.00, 128.00, '/images/algorithm.jpg', 2, 2, 'AVAILABLE', 98, 22, true, false, '自提', '图书馆门口', '13900139000'),

-- 生活用品
(7, '小米空气净化器2S', '小米空气净化器2S，九成新，过滤效果好', 199.00, 599.00, '/images/airpurifier.jpg', 3, 4, 'AVAILABLE', 75, 16, true, false, '自提', '宿舍楼下', '13700137000'),
(8, '宜家书桌', '宜家简约书桌，120cm×60cm，稳固耐用', 150.00, 399.00, '/images/desk.jpg', 3, 4, 'AVAILABLE', 62, 12, true, false, '自提', '宿舍楼下', '13700137000'),
(9, '美的电热水壶', '美的电热水壶，304不锈钢，1.7L容量', 45.00, 99.00, '/images/kettle.jpg', 3, 4, 'AVAILABLE', 88, 18, true, false, '自提', '宿舍楼下', '13700137000'),

-- 运动健身
(10, '李宁羽毛球拍', '李宁羽毛球拍，碳纤维材质，攻守兼备', 89.00, 199.00, '/images/badminton.jpg', 4, 2, 'AVAILABLE', 112, 24, true, false, '自提', '体育馆', '13900139000'),
(11, '瑜伽垫', '加厚瑜伽垫，6mm厚度，防滑耐用', 25.00, 69.00, '/images/yogamat.jpg', 4, 2, 'AVAILABLE', 145, 30, true, false, '自提', '体育馆', '13900139000'),
(12, '哑铃套装', '20kg哑铃套装，可调节重量，带收纳架', 120.00, 299.00, '/images/dumbbell.jpg', 4, 2, 'AVAILABLE', 95, 20, true, false, '自提', '体育馆', '13900139000'),

-- 服装鞋包
(13, 'Nike Air Max运动鞋', 'Nike Air Max 270，42码，九成新，无磨损', 399.00, 899.00, '/images/nikeair.jpg', 5, 3, 'AVAILABLE', 132, 28, true, false, '自提', '校园内', '13800138000'),
(14, '阿迪达斯运动外套', '阿迪达斯防风外套，M码，黑色，春秋款', 199.00, 499.00, '/images/adidasjacket.jpg', 5, 3, 'AVAILABLE', 78, 16, true, false, '自提', '校园内', '13800138000'),
(15, '帆布包', '简约帆布包，大容量，适合学生党', 25.00, 59.00, '/images/canvasbag.jpg', 5, 3, 'AVAILABLE', 205, 42, true, false, '自提', '校园内', '13800138000'),

-- 美妆护肤
(16, 'SK-II神仙水', 'SK-II神仙水，230ml，全新未开封', 799.00, 1540.00, '/images/sk2.jpg', 6, 4, 'AVAILABLE', 156, 35, true, false, '自提', '女生宿舍楼下', '13700137000'),
(17, '雅诗兰黛小棕瓶', '雅诗兰黛小棕瓶精华，50ml，全新未开封', 599.00, 900.00, '/images/estee.jpg', 6, 4, 'AVAILABLE', 132, 28, true, false, '自提', '女生宿舍楼下', '13700137000'),
(18, 'MAC口红', 'MAC口红，色号chili，仅试色一次', 85.00, 170.00, '/images/maclipstick.jpg', 6, 4, 'AVAILABLE', 189, 42, true, false, '自提', '女生宿舍楼下', '13700137000'),

-- 食品零食
(19, '三只松鼠坚果礼盒', '三只松鼠坚果礼盒，1.5kg，全新未开封', 68.00, 128.00, '/images/nuts.jpg', 7, 2, 'AVAILABLE', 95, 20, true, false, '自提', '校园超市门口', '13900139000'),
(20, '良品铺子零食大礼包', '良品铺子零食大礼包，2kg，全新未开封', 75.00, 148.00, '/images/snacks.jpg', 7, 2, 'AVAILABLE', 112, 24, true, false, '自提', '校园超市门口', '13900139000'),
(21, '星巴克咖啡券', '星巴克中杯咖啡券，共10张，有效期到2025年', 220.00, 320.00, '/images/starbucks.jpg', 7, 2, 'AVAILABLE', 88, 18, true, false, '自提', '校园超市门口', '13900139000'),

-- 其他
(22, '吉他', '民谣吉他，41寸，九成新，送琴包', 299.00, 899.00, '/images/guitar.jpg', 8, 3, 'AVAILABLE', 125, 26, true, false, '自提', '音乐楼', '13800138000'),
(23, '打印机', '惠普激光打印机，黑白打印，打印清晰', 250.00, 799.00, '/images/printer.jpg', 8, 3, 'AVAILABLE', 78, 16, true, false, '自提', '教学楼', '13800138000'),
(24, '行李箱', '24寸行李箱，万向轮，密码锁，坚固耐用', 180.00, 499.00, '/images/luggage.jpg', 8, 3, 'AVAILABLE', 142, 30, true, false, '自提', '校园内', '13800138000');