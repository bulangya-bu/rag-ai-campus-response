<script setup>
const props = defineProps({
  message: {
    type: Object,
    required: true
  }
});

function formatTime(value) {
  if (!value) {
    return "";
  }
  return new Date(value).toLocaleString("zh-CN", {
    hour: "2-digit",
    minute: "2-digit",
    month: "2-digit",
    day: "2-digit"
  });
}

const isAssistant = props.message.role === "assistant";
</script>

<template>
  <article class="message-row" :class="{ assistant: isAssistant, user: !isAssistant }">
    <div class="message-meta">
      <span>{{ isAssistant ? "AI 助教" : "当前用户" }}</span>
      <time>{{ formatTime(message.createdAt) }}</time>
    </div>

    <div class="message-bubble">
      <p>{{ message.content }}</p>
      <ul v-if="message.citations?.length" class="message-citations">
        <li v-for="citation in message.citations" :key="citation">{{ citation }}</li>
      </ul>
    </div>
  </article>
</template>
