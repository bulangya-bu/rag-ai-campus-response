<script setup>
import { computed, onMounted, onUnmounted, ref } from "vue";
import { RouterLink, RouterView, useRoute } from "vue-router";
import StatusPill from "./components/StatusPill.vue";
import { api } from "./lib/api";

const route = useRoute();
const systemStatus = ref(null);
const loadingStatus = ref(true);
const statusError = ref("");

const navItems = [
  { label: "系统总览", to: "/dashboard", tag: "Overview" },
  { label: "AI 会话", to: "/chat", tag: "Chat" },
  { label: "知识库管理", to: "/knowledge", tag: "Knowledge" },
  { label: "模型设置", to: "/model-settings", tag: "Models" }
];

const currentSection = computed(
  () => navItems.find((item) => item.to === route.path)?.label ?? "校园 AI"
);

async function loadSystemStatus() {
  loadingStatus.value = true;
  statusError.value = "";

  try {
    systemStatus.value = await api.getSystemStatus();
  } catch (error) {
    statusError.value = error.message;
  } finally {
    loadingStatus.value = false;
  }
}

function handleStatusRefresh() {
  loadSystemStatus();
}

onMounted(() => {
  loadSystemStatus();
  window.addEventListener("system-status-updated", handleStatusRefresh);
});

onUnmounted(() => {
  window.removeEventListener("system-status-updated", handleStatusRefresh);
});
</script>

<template>
  <div class="app-shell">
    <aside class="app-sidebar">
      <div class="brand-block">
        <div class="brand-mark">AI</div>
        <div>
          <p class="eyebrow">Campus Knowledge Workspace</p>
          <h1>校园 AI 知识问答系统</h1>
        </div>
      </div>

      <nav class="nav-list">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          :class="{ active: route.path === item.to }"
        >
          <span>{{ item.label }}</span>
          <small>{{ item.tag }}</small>
        </RouterLink>
      </nav>

      <div class="sidebar-note">
        <p>本项目覆盖前端、后端、MySQL 与本地大模型接入，适合作为企业级全栈实验样例。</p>
      </div>
    </aside>

    <div class="app-main">
      <header class="topbar">
        <div>
          <p class="eyebrow">Current Section</p>
          <h2>{{ currentSection }}</h2>
        </div>

        <div class="topbar-status">
          <StatusPill
            v-if="systemStatus"
            :ready="systemStatus.aiReady"
            :label="systemStatus.activeConfigName || systemStatus.model"
            :detail="systemStatus.aiReady ? `${systemStatus.model} 在线` : `${systemStatus.model} 离线`"
          />
          <button class="ghost-button" @click="loadSystemStatus">
            {{ loadingStatus ? "刷新中..." : "刷新状态" }}
          </button>
        </div>
      </header>

      <p v-if="statusError" class="inline-error">{{ statusError }}</p>

      <main class="page-container">
        <RouterView />
      </main>
    </div>
  </div>
</template>
