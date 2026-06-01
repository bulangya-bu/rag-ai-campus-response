<script setup>
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";
import { api } from "../lib/api";

const overview = ref(null);
const loading = ref(true);
const error = ref("");

async function loadOverview() {
  loading.value = true;
  error.value = "";
  try {
    overview.value = await api.getOverview();
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}

function formatTime(value) {
  if (!value) {
    return "--";
  }
  return new Date(value).toLocaleString("zh-CN");
}

onMounted(loadOverview);
</script>

<template>
  <section class="hero-band">
    <div class="hero-copy">
      <p class="eyebrow">Enterprise Lab Demo</p>
      <h3>把校园知识、AI 会话和业务数据放进同一个可运行的全栈系统里。</h3>
      <p>
        这个工作台覆盖了 Spring Boot 接口、Vue3 前端、MySQL 存储和本地大模型接入，适合直接拿来演示“企业级项目是怎么分层和落地的”。
      </p>
    </div>

    <div class="hero-actions">
      <RouterLink to="/chat" class="solid-button">进入 AI 会话</RouterLink>
      <RouterLink to="/knowledge" class="ghost-button">维护知识库</RouterLink>
    </div>
  </section>

  <p v-if="error" class="inline-error">{{ error }}</p>
  <p v-if="loading" class="muted-line">正在加载系统概览...</p>

  <template v-if="overview">
    <section class="metric-strip">
      <div class="metric-cell">
        <span>会话数</span>
        <strong>{{ overview.sessionCount }}</strong>
      </div>
      <div class="metric-cell">
        <span>消息数</span>
        <strong>{{ overview.messageCount }}</strong>
      </div>
      <div class="metric-cell">
        <span>知识条目</span>
        <strong>{{ overview.knowledgeCount }}</strong>
      </div>
      <div class="metric-cell">
        <span>模型状态</span>
        <strong>{{ overview.systemStatus.aiReady ? "在线" : "离线" }}</strong>
      </div>
    </section>

    <section class="dashboard-grid">
      <div class="surface">
        <div class="surface-head">
          <div>
            <p class="eyebrow">Recent Sessions</p>
            <h4>最近会话</h4>
          </div>
          <RouterLink to="/chat" class="text-link">打开会话区</RouterLink>
        </div>

        <ul class="session-list">
          <li v-for="session in overview.recentSessions" :key="session.id" class="session-item">
            <div>
              <strong>{{ session.title }}</strong>
              <p>{{ session.lastMessagePreview }}</p>
            </div>
            <small>{{ formatTime(session.updatedAt) }}</small>
          </li>
        </ul>
      </div>

      <div class="surface">
        <div class="surface-head">
          <div>
            <p class="eyebrow">Featured Knowledge</p>
            <h4>知识库重点条目</h4>
          </div>
          <RouterLink to="/knowledge" class="text-link">前往维护</RouterLink>
        </div>

        <ul class="knowledge-summary-list">
          <li v-for="item in overview.featuredKnowledge" :key="item.id">
            <strong>{{ item.title }}</strong>
            <span>{{ item.category }}</span>
            <p>{{ item.summary || item.content }}</p>
          </li>
        </ul>
      </div>
    </section>

    <section class="surface">
      <div class="surface-head">
        <div>
          <p class="eyebrow">System Readiness</p>
          <h4>运行环境确认</h4>
        </div>
      </div>

      <div class="readiness-grid">
        <div>
          <span>数据库</span>
          <strong>{{ overview.systemStatus.database }}</strong>
        </div>
        <div>
          <span>激活配置</span>
          <strong>{{ overview.systemStatus.activeConfigName }}</strong>
        </div>
        <div>
          <span>模型</span>
          <strong>{{ overview.systemStatus.model }}</strong>
        </div>
        <div>
          <span>模型地址</span>
          <strong>{{ overview.systemStatus.baseUrl }}</strong>
        </div>
        <div>
          <span>检查时间</span>
          <strong>{{ formatTime(overview.systemStatus.checkedAt) }}</strong>
        </div>
      </div>
    </section>
  </template>
</template>
