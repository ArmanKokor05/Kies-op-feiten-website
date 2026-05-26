<script setup lang="ts">
defineProps<{
  show: boolean
  message: string
}>()
</script>

<template>
  <div v-if="show" class="alert-message">
    {{ message }}
  </div>
</template>

<style scoped>
.alert-message {
  background-color: #dbeafe;
  color: #2563eb;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-weight: 600;
  animation: fadeIn 0.3s ease, fadeOut 0.5s ease 4.5s forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-5px);
  }
}
</style>
