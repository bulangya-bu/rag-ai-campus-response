<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { api } from "../lib/api";

const configs = ref([]);
const selectedId = ref(null);
const loading = ref(true);
const saving = ref(false);
const testing = ref(false);
const error = ref("");
const testResult = ref(null);

const form = reactive(createEmptyForm());

const activeConfig = computed(() => configs.value.find((item) => item.active) ?? null);
const selectedConfig = computed(() => configs.value.find((item) => item.id === selectedId.value) ?? null);

function createEmptyForm() {
  return {
    name: "",
    baseUrl: "",
    apiKey: "",
    model: ""
  };
}

async function loadConfigs(preferredId = selectedId.value) {
  loading.value = true;
  error.value = "";
  try {
    configs.value = await api.listModelConfigs();

    if (configs.value.length === 0) {
      resetForm();
      return;
    }

    const nextSelected =
      configs.value.find((item) => item.id === preferredId) ??
      activeConfig.value ??
      configs.value[0];

    if (nextSelected) {
      applyToForm(nextSelected);
    }
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}

function applyToForm(config) {
  selectedId.value = config.id;
  form.name = config.name ?? "";
  form.baseUrl = config.baseUrl ?? "";
  form.model = config.model ?? "";
  form.apiKey = "";
  testResult.value = null;
}

function resetForm() {
  selectedId.value = null;
  Object.assign(form, createEmptyForm());
  testResult.value = null;
}

async function saveConfig() {
  saving.value = true;
  error.value = "";
  try {
    const payload = { ...form };
    const saved = selectedId.value
      ? await api.updateModelConfig(selectedId.value, payload)
      : await api.createModelConfig(payload);

    await loadConfigs(saved.id);
    window.dispatchEvent(new CustomEvent("system-status-updated"));
  } catch (err) {
    error.value = err.message;
  } finally {
    saving.value = false;
  }
}

async function activateConfig(id = selectedId.value) {
  if (!id) {
    return;
  }

  error.value = "";
  try {
    await api.activateModelConfig(id);
    await loadConfigs(id);
    window.dispatchEvent(new CustomEvent("system-status-updated"));
  } catch (err) {
    error.value = err.message;
  }
}

async function testConfig() {
  testing.value = true;
  error.value = "";
  testResult.value = null;
  try {
    testResult.value = await api.testModelConfig({
      configId: selectedId.value,
      baseUrl: form.baseUrl,
      apiKey: form.apiKey,
      model: form.model
    });
  } catch (err) {
    error.value = err.message;
  } finally {
    testing.value = false;
  }
}

async function deleteConfig() {
  if (!selectedId.value) {
    return;
  }

  error.value = "";
  try {
    await api.deleteModelConfig(selectedId.value);
    resetForm();
    await loadConfigs();
    window.dispatchEvent(new CustomEvent("system-status-updated"));
  } catch (err) {
    error.value = err.message;
  }
}

onMounted(() => loadConfigs());
</script>

<template>
  <p v-if="error" class="inline-error">{{ error }}</p>

  <section class="workspace-grid model-grid">
    <aside class="surface model-rail">
      <div class="surface-head">
        <div>
          <p class="eyebrow">Global Model Configs</p>
          <h4>模型配置列表</h4>
        </div>
        <button type="button" class="ghost-button" @click="resetForm">新建配置</button>
      </div>

      <p v-if="loading" class="muted-line">正在加载模型配置...</p>

      <div class="config-list">
        <article
          v-for="item in configs"
          :key="item.id"
          class="config-row"
          :class="{ active: item.active, selected: item.id === selectedId }"
          @click="applyToForm(item)"
        >
          <div class="config-row-head">
            <div>
              <strong>{{ item.name }}</strong>
              <p>{{ item.model }}</p>
            </div>
            <span v-if="item.active" class="config-badge">当前使用</span>
          </div>

          <p>{{ item.baseUrl }}</p>
          <small>{{ item.maskedApiKey || "未保存 Key" }}</small>

          <div class="config-row-actions">
            <button
              type="button"
              class="ghost-button mini-button"
              :disabled="item.active"
              @click.stop="activateConfig(item.id)"
            >
              {{ item.active ? "已激活" : "设为当前" }}
            </button>
          </div>
        </article>
      </div>
    </aside>

    <form class="surface editor-surface" @submit.prevent="saveConfig">
      <div class="surface-head">
        <div>
          <p class="eyebrow">Config Editor</p>
          <h4>{{ selectedId ? "编辑模型配置" : "新建模型配置" }}</h4>
        </div>
        <div class="conversation-tools">
          <button type="button" class="ghost-button" @click="resetForm">清空</button>
          <button
            v-if="selectedId && selectedConfig && !selectedConfig.active"
            type="button"
            class="ghost-button danger"
            @click="deleteConfig"
          >
            删除
          </button>
        </div>
      </div>

      <div class="surface-tip">
        <strong>当前激活模型：</strong>
        <span v-if="activeConfig">
          {{ activeConfig.name }} / {{ activeConfig.model }}
        </span>
        <span v-else>暂未设置</span>
      </div>

      <div class="editor-grid">
        <label>
          <span>配置名称</span>
          <input
            v-model="form.name"
            class="inline-input"
            placeholder="例如：本地 Gemini / 备用模型"
          />
        </label>
        <label>
          <span>模型名</span>
          <input
            v-model="form.model"
            class="inline-input"
            placeholder="例如：gemini-3-flash"
          />
        </label>
      </div>

      <label class="editor-field">
        <span>模型服务 URL</span>
        <input
          v-model="form.baseUrl"
          class="inline-input"
          placeholder="例如：http://localhost:8317/v1"
        />
      </label>

      <label class="editor-field">
        <span>API Key</span>
        <input
          v-model="form.apiKey"
          type="password"
          class="inline-input"
          :placeholder="selectedId ? '留空表示保持当前 Key 不变' : '请输入 API Key'"
        />
        <small class="field-hint">
          {{ selectedConfig?.hasApiKey ? `当前已保存 Key：${selectedConfig.maskedApiKey}` : "当前尚未保存 Key" }}
        </small>
      </label>

      <div
        v-if="testResult"
        class="result-banner"
        :class="{
          success: testResult.reachable && testResult.modelAvailable,
          warning: testResult.reachable && !testResult.modelAvailable,
          error: !testResult.reachable
        }"
      >
        <strong>{{ testResult.message }}</strong>
        <p v-if="testResult.availableModels?.length">
          可用模型：{{ testResult.availableModels.join("、") }}
        </p>
      </div>

      <div class="composer-footer wrap-actions">
        <span>
          所有聊天都会使用当前激活的全局模型配置；切换后无需重启后端。
        </span>

        <div class="stack-actions">
          <button type="button" class="ghost-button" :disabled="testing" @click="testConfig">
            {{ testing ? "测试中..." : "测试连接" }}
          </button>
          <button
            v-if="selectedId"
            type="button"
            class="ghost-button"
            :disabled="selectedConfig?.active"
            @click="activateConfig()"
          >
            {{ selectedConfig?.active ? "当前已激活" : "设为当前模型" }}
          </button>
          <button type="submit" class="solid-button" :disabled="saving">
            {{ saving ? "保存中..." : selectedId ? "更新配置" : "创建配置" }}
          </button>
        </div>
      </div>
    </form>
  </section>
</template>
