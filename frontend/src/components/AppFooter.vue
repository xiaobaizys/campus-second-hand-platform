<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>校园二手交易平台是为大学生提供安全、便捷的二手物品交易服务的平台，致力于让闲置物品流转起来，创造更多价值。</p>
        </div>
        
        <div class="footer-section">
          <h3>快速链接</h3>
          <ul class="footer-links">
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/products">商品列表</router-link></li>
            <li><router-link to="/about">关于我们</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>帮助中心</h3>
          <ul class="footer-links">
            <li><a href="#how-to-buy">如何购买</a></li>
            <li><a href="#how-to-sell">如何出售</a></li>
            <li><a href="#safety">交易安全</a></li>
            <li><a href="#contact">联系我们</a></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>联系方式</h3>
          <div class="contact-info">
            <p><el-icon><Message /></el-icon> contact@campus-secondhand.com</p>
            <p><el-icon><Phone /></el-icon> 400-123-4567</p>
            <p><el-icon><Location /></el-icon> 北京市海淀区学院路</p>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} 校园二手交易平台. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Message, Phone, Location } from '@element-plus/icons-vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.app-footer {
  background-color: #2c3e50;
  color: #ecf0f1;
  padding: 50px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #fff;
  position: relative;
  padding-bottom: 10px;
}

.footer-section h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 2px;
  background-color: #409eff;
}

.footer-section p {
  line-height: 1.6;
  color: #bdc3c7;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #409eff;
}

.contact-info p {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.contact-info .el-icon {
  margin-right: 10px;
  color: #409eff;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  text-align: center;
  color: #95a5a6;
}

@media (max-width: 768px) {
  .app-footer {
    padding: 30px 0 15px;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 25px;
  }
  
  .footer-section h3 {
    font-size: 16px;
    margin-bottom: 15px;
  }
  
  .footer-section p {
    font-size: 14px;
  }
  
  .footer-links li {
    margin-bottom: 8px;
  }
  
  .footer-links a {
    font-size: 14px;
  }
  
  .contact-info p {
    font-size: 14px;
    margin-bottom: 8px;
  }
  
  .footer-bottom {
    padding-top: 15px;
  }
  
  .footer-bottom p {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .app-footer {
    padding: 20px 0 10px;
  }
  
  .footer-content {
    gap: 20px;
  }
  
  .footer-section h3 {
    font-size: 15px;
    margin-bottom: 12px;
  }
  
  .footer-section p {
    font-size: 13px;
  }
  
  .footer-links a {
    font-size: 13px;
  }
  
  .contact-info p {
    font-size: 13px;
  }
}
</style>