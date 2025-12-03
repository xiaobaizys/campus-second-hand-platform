<template>
  <div class="about-container">
    <AppHeader />
    <div class="about-view">
      <div class="container">
        <div class="about-header">
          <h1>关于我们</h1>
          <p class="subtitle">让闲置物品流转起来，创造更多价值</p>
        </div>

        <div class="about-content">
          <section class="about-section">
            <h2>平台简介</h2>
            <p>校园二手交易平台是为大学生提供安全、便捷的二手物品交易服务的平台。我们致力于打造一个值得信赖的交易环境，让同学们能够轻松地买卖二手物品，实现资源的有效利用。</p>
          </section>
          
          <section class="about-section">
            <h2>我们的使命</h2>
            <p>通过提供安全、便捷的二手交易平台，我们希望：</p>
            <ul>
              <li>促进校园内资源的循环利用，减少浪费</li>
              <li>为同学们提供一个可靠的二手物品交易渠道</li>
              <li>培养大学生的环保意识和节约理念</li>
              <li>帮助同学们以更低成本获取所需物品</li>
            </ul>
          </section>
          
          <section class="about-section">
            <h2>平台特色</h2>
            <div class="features">
              <div class="feature-item">
                <el-icon size="40" color="#409eff"><Lock /></el-icon>
                <h3>安全保障</h3>
                <p>实名认证体系，交易更安心</p>
              </div>
              <div class="feature-item">
                <el-icon size="40" color="#409eff"><CircleCheck /></el-icon>
                <h3>便捷交易</h3>
                <p>简单快捷的发布和购买流程</p>
              </div>
              <div class="feature-item">
                <el-icon size="40" color="#409eff"><ChatDotRound /></el-icon>
                <h3>即时沟通</h3>
                <p>买卖双方可直接沟通，交易更透明</p>
              </div>
              <div class="feature-item">
                <el-icon size="40" color="#409eff"><Location /></el-icon>
                <h3>校园专属</h3>
                <p>专注于校园内的二手交易，距离更近</p>
              </div>
            </div>
          </section>
          
          <section class="about-section">
            <h2>联系我们</h2>
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><Message /></el-icon>
                <span>邮箱：contact@campus-secondhand.com</span>
              </div>
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <span>电话：400-123-4567</span>
              </div>
              <div class="contact-item">
                <el-icon><Location /></el-icon>
                <span>地址：北京市海淀区学院路</span>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue'
import { Lock, CircleCheck, ChatDotRound, Location, Message, Phone } from '@element-plus/icons-vue'
</script>

<style scoped>
.about-container {
  min-height: 100vh;
}

.about-view {
  padding: 40px 0;
  background-color: #f9f9f9;
  min-height: calc(100vh - 64px);
}

.about-header {
  text-align: center;
  margin-bottom: 50px;
}

.about-header h1 {
  font-size: 36px;
  color: #303133;
  margin-bottom: 15px;
}

.subtitle {
  font-size: 18px;
  color: #606266;
  max-width: 600px;
  margin: 0 auto;
}

.about-content {
  max-width: 900px;
  margin: 0 auto;
}

.about-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.about-section h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.about-section p {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  margin-bottom: 20px;
}

.about-section ul {
  padding-left: 20px;
}

.about-section li {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  margin-bottom: 10px;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  margin-top: 20px;
}

.feature-item {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.feature-item:hover {
  background-color: #f5f7fa;
  transform: translateY(-5px);
}

.feature-item h3 {
  font-size: 18px;
  color: #303133;
  margin: 15px 0 10px;
}

.feature-item p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.contact-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #606266;
}

.contact-item .el-icon {
  color: #409eff;
}

@media (max-width: 768px) {
  .about-view {
    padding: 20px 0;
  }
  
  .about-header h1 {
    font-size: 28px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .about-section {
    padding: 20px;
  }
  
  .about-section h2 {
    font-size: 20px;
  }
  
  .about-section p,
  .about-section li {
    font-size: 14px;
  }
  
  .features {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .contact-info {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .about-header h1 {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .about-section {
    padding: 15px;
  }
  
  .about-section h2 {
    font-size: 18px;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}
</style>