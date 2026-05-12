<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { api } from "../lib/api";

const documents = ref([]);
const searchQuery = ref("");
const category = ref("");
const selectedId = ref(null);
const loading = ref(true);
const saving = ref(false);
const error = ref("");

const form = reactive(createEmptyForm());

const categories = computed(() =>
  [...new Set(documents.value.map((item) => item.category).filter(Boolean))].sort()
);

function createEmptyForm() {
  return {
    title: "",
    category: "",
    tags: "",
    source: "",
    summary: "",
    content: ""
  };
}

async function loadDocuments() {
  loading.value = true;
  error.value = "";
  try {
    documents.value = await api.listKnowledgeDocuments({
      query: searchQuery.value,
      category: category.value
    });

    if (selectedId.value) {
      const matched = documents.value.find((item) => item.id === selectedId.value);
      if (matched) {
        applyToForm(matched);
      }
    }
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}

function applyToForm(document) {
  selectedId.value = document.id;
  form.title = document.title ?? "";
  form.category = document.category ?? "";
  form.tags = document.tags ?? "";
  form.source = document.source ?? "";
  form.summary = document.summary ?? "";
  form.content = document.content ?? "";
}

function resetForm() {
  selectedId.value = null;
  Object.assign(form, createEmptyForm());
}

async function saveDocument() {
  saving.value = true;
  error.value = "";
  try {
    const payload = { ...form };
    const saved = selectedId.value
      ? await api.updateKnowledgeDocument(selectedId.value, payload)
      : await api.createKnowledgeDocument(payload);

    await loadDocuments();
    const fullDocument = await api.getKnowledgeDocument(saved.id);
    applyToForm(fullDocument);
  } catch (err) {
    error.value = err.message;
  } finally {
    saving.value = false;
  }
}

async function deleteCurrent() {
  if (!selectedId.value) {
    return;
  }
  try {
    await api.deleteKnowledgeDocument(selectedId.value);
    resetForm();
    await loadDocuments();
  } catch (err) {
    error.value = err.message;
  }
}

watch([searchQuery, category], loadDocuments);
onMounted(loadDocuments);
</script>

<template>
  <p v-if="error" class="inline-error">{{ error }}</p>

  <section class="workspace-grid knowledge-grid">
    <aside class="surface knowledge-rail">
      <div class="surface-head">
        <div>
          <p class="eyebrow">Repository</p>
          <h4>知识条目</h4>
        </div>
        <button class="ghost-button" @click="resetForm">新建条目</button>
      </div>

      <div class="filter-stack">
        <input v-model="searchQuery" class="inline-input" placeholder="搜索标题、标签或内容" />
        <select v-model="category" class="inline-input">
          <option value="">全部分类</option>
          <option v-for="item in categories" :key="item" :value="item">{{ item }}</option>
        </select>
      </div>

      <p v-if="loading" class="muted-line">正在加载知识库...</p>

      <div class="knowledge-summary-list">
        <button
          v-for="item in documents"
          :key="item.id"
          class="knowledge-row"
          :class="{ active: item.id === selectedId }"
          @click="applyToForm(item)"
        >
          <strong>{{ item.title }}</strong>
          <span>{{ item.category }}</span>
          <p>{{ item.summary || item.content }}</p>
        </button>
      </div>
    </aside>

    <section class="surface editor-surface">
      <div class="surface-head">
        <div>
          <p class="eyebrow">Editor</p>
          <h4>{{ selectedId ? "编辑知识条目" : "新建知识条目" }}</h4>
        </div>
        <div class="conversation-tools">
          <button class="ghost-button" @click="resetForm">清空</button>
          <button
            v-if="selectedId"
            class="ghost-button danger"
            @click="deleteCurrent"
          >
            删除
          </button>
        </div>
      </div>

      <div class="editor-grid">
        <label>
          <span>标题</span>
          <input v-model="form.title" class="inline-input" placeholder="例如：奖学金评定时间与申请材料" />
        </label>
        <label>
          <span>分类</span>
          <input v-model="form.category" class="inline-input" placeholder="例如：学生事务" />
        </label>
        <label>
          <span>标签</span>
          <input v-model="form.tags" class="inline-input" placeholder="例如：奖学金,申请,材料" />
        </label>
        <label>
          <span>来源</span>
          <input v-model="form.source" class="inline-input" placeholder="例如：学生工作处通知" />
        </label>
      </div>

      <label class="editor-field">
        <span>摘要</span>
        <textarea
          v-model="form.summary"
          class="composer-input compact"
          rows="3"
          placeholder="一句话概括这个知识条目的主要内容"
        ></textarea>
      </label>

      <label class="editor-field">
        <span>正文内容</span>
        <textarea
          v-model="form.content"
          class="composer-input large"
          rows="14"
          placeholder="这里填写完整的校园知识内容，AI 回答会优先参考这里。"
        ></textarea>
      </label>

      <div class="composer-footer">
        <span>知识库更新后，新的对话会立刻使用这些内容做检索增强。</span>
        <button class="solid-button" :disabled="saving" @click="saveDocument">
          {{ saving ? "保存中..." : selectedId ? "更新条目" : "创建条目" }}
        </button>
      </div>
    </section>
  </section>
</template>
