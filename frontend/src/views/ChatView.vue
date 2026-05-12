<script setup>
import { computed, onMounted, ref } from "vue";
import MessageBubble from "../components/MessageBubble.vue";
import { api } from "../lib/api";

const sessions = ref([]);
const activeSessionId = ref(null);
const conversation = ref({ session: null, messages: [], references: [] });
const references = ref([]);
const composer = ref("");
const loadingConversation = ref(false);
const sending = ref(false);
const error = ref("");

const promptSuggestions = [
  "奖学金申请需要准备哪些材料？",
  "宿舍报修怎么提交，多久能处理？",
  "课程补退选在什么时间办理？",
  "校园卡丢了以后怎么挂失和补办？"
];

const activeSession = computed(() => conversation.value.session);
const messageCount = computed(() => conversation.value.messages.length);

async function bootstrap() {
  error.value = "";
  try {
    sessions.value = await api.listSessions();
    if (sessions.value.length === 0) {
      await createSession();
      return;
    }
    await selectSession(sessions.value[0].id);
  } catch (err) {
    error.value = err.message;
  }
}

async function createSession() {
  const session = await api.createSession("");
    sessions.value = [session, ...sessions.value];
    activeSessionId.value = session.id;
    conversation.value = { session, messages: [], references: [] };
    references.value = [];
}

async function selectSession(sessionId) {
  activeSessionId.value = sessionId;
  loadingConversation.value = true;
  error.value = "";
  try {
    conversation.value = await api.getConversation(sessionId);
    references.value = conversation.value.references ?? [];
  } catch (err) {
    error.value = err.message;
  } finally {
    loadingConversation.value = false;
  }
}

async function sendMessage() {
  if (!composer.value.trim() || sending.value) {
    return;
  }

  if (!activeSessionId.value) {
    await createSession();
  }

  const content = composer.value.trim();
  composer.value = "";
  sending.value = true;
  error.value = "";

  try {
    const result = await api.sendMessage(activeSessionId.value, content);
    conversation.value = {
      session: result.session,
      messages: [...conversation.value.messages, result.userMessage, result.assistantMessage]
    };
    references.value = result.references;
    upsertSession(result.session);
  } catch (err) {
    composer.value = content;
    error.value = err.message;
  } finally {
    sending.value = false;
  }
}

async function removeSession(sessionId) {
  await api.deleteSession(sessionId);
  sessions.value = sessions.value.filter((item) => item.id !== sessionId);

  if (activeSessionId.value === sessionId) {
    if (sessions.value.length > 0) {
      await selectSession(sessions.value[0].id);
    } else {
      await createSession();
    }
  }
}

function usePrompt(prompt) {
  composer.value = prompt;
}

function upsertSession(session) {
  const others = sessions.value.filter((item) => item.id !== session.id);
  sessions.value = [session, ...others];
}

function formatTime(value) {
  if (!value) {
    return "--";
  }
  return new Date(value).toLocaleString("zh-CN");
}

onMounted(bootstrap);
</script>

<template>
  <p v-if="error" class="inline-error">{{ error }}</p>

  <section class="workspace-grid">
    <aside class="surface session-rail">
      <div class="surface-head">
        <div>
          <p class="eyebrow">Sessions</p>
          <h4>会话列表</h4>
        </div>
        <button class="ghost-button" @click="createSession">新建</button>
      </div>

      <div class="session-list">
        <button
          v-for="session in sessions"
          :key="session.id"
          class="session-switch"
          :class="{ active: session.id === activeSessionId }"
          @click="selectSession(session.id)"
        >
          <div>
            <strong>{{ session.title }}</strong>
            <p>{{ session.lastMessagePreview }}</p>
          </div>
          <span>{{ session.messageCount }} 条</span>
        </button>
      </div>
    </aside>

    <section class="surface conversation-surface">
      <div class="surface-head conversation-head">
        <div>
          <p class="eyebrow">Conversation Workspace</p>
          <h4>{{ activeSession?.title || "新会话" }}</h4>
        </div>
        <div class="conversation-tools">
          <span>{{ messageCount }} 条消息</span>
          <button
            v-if="activeSessionId"
            class="ghost-button danger"
            @click="removeSession(activeSessionId)"
          >
            删除会话
          </button>
        </div>
      </div>

      <div class="conversation-stream">
        <p v-if="loadingConversation" class="muted-line">正在加载会话内容...</p>
        <p v-else-if="conversation.messages.length === 0" class="empty-copy">
          这里会保留完整上下文。你可以直接问校园卡、奖学金、宿舍报修、选课等问题。
        </p>

        <MessageBubble
          v-for="message in conversation.messages"
          :key="message.id"
          :message="message"
        />

        <article v-if="sending" class="message-row assistant">
          <div class="message-meta">
            <span>AI 助教</span>
            <time>正在生成</time>
          </div>
          <div class="message-bubble loading">
            <p>正在根据当前会话和知识库生成回答...</p>
          </div>
        </article>
      </div>

      <div class="composer-panel">
        <textarea
          v-model="composer"
          class="composer-input"
          placeholder="输入你的校园问题，按 Enter 发送，Shift + Enter 换行。"
          rows="4"
          @keydown.enter.exact.prevent="sendMessage"
        ></textarea>
        <div class="composer-footer">
          <span>连接后端 Spring Boot 与本地模型接口 `/v1/chat/completions`</span>
          <button class="solid-button" :disabled="sending || !composer.trim()" @click="sendMessage">
            {{ sending ? "发送中..." : "发送问题" }}
          </button>
        </div>
      </div>
    </section>

    <aside class="surface reference-rail">
      <div class="surface-head">
        <div>
          <p class="eyebrow">Grounding</p>
          <h4>本轮引用知识</h4>
        </div>
      </div>

      <ul v-if="references.length" class="reference-list">
        <li v-for="item in references" :key="item.id">
          <strong>{{ item.title }}</strong>
          <span>{{ item.category }}</span>
          <p>{{ item.summary || item.content }}</p>
        </li>
      </ul>
      <p v-else class="empty-copy">当前回答暂时没有命中明确的知识条目。</p>

      <div class="prompt-panel">
        <p class="eyebrow">Suggested Prompts</p>
        <button
          v-for="prompt in promptSuggestions"
          :key="prompt"
          class="prompt-link"
          @click="usePrompt(prompt)"
        >
          {{ prompt }}
        </button>
      </div>

      <div class="meta-note">
        <span>当前会话更新时间</span>
        <strong>{{ formatTime(activeSession?.updatedAt) }}</strong>
      </div>
    </aside>
  </section>
</template>
