<script setup>
defineProps({
  ready: {
    type: Boolean,
    default: false
  },
  label: {
    type: String,
    default: ""
  },
  detail: {
    type: String,
    default: ""
  }
});
</script>

<template>
  <div class="status-pill" :class="{ ready }">
    <span class="status-dot"></span>
    <div>
      <strong>{{ label }}</strong>
      <small>{{ detail }}</small>
    </div>
  </div>
</template>
