<template>
  <span v-html="highlightedText"></span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  text: string
  query: string
  className?: string
}

const props = withDefaults(defineProps<Props>(), {
  className: 'highlight'
})

// 高亮文本
const highlightedText = computed(() => {
  if (!props.query.trim()) return props.text
  
  // 转义特殊字符
  const escapedQuery = props.query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  
  // 创建正则表达式
  const regex = new RegExp(`(${escapedQuery})`, 'gi')
  
  // 替换匹配的文本
  return props.text.replace(regex, `<span class="${props.className}">$1</span>`)
})
</script>

<style scoped>
:deep(.highlight) {
  background-color: #fff2e8;
  color: #ff6700;
  font-weight: bold;
  padding: 0 2px;
  border-radius: 2px;
}
</style>