<script setup lang="ts">
defineEmits(["delete"])
</script>

<template>
  <button class="trash-btn" @click="$emit('delete')" aria-label="delete">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="30px" height="30px"><path fill="currentColor" d="M22 5a1 1 0 0 1-1 1H3a1 1 0 0 1 0-2h5V3a1 1 0 0 1 1-1h6a1 1 0 0 1 1 1v1h5a1 1 0 0 1 1 1zM4.934 21.071 4 8h16l-.934 13.071a1 1 0 0 1-1 .929H5.931a1 1 0 0 1-.997-.929zM15 18a1 1 0 0 0 2 0v-6a1 1 0 0 0-2 0zm-4 0a1 1 0 0 0 2 0v-6a1 1 0 0 0-2 0zm-4 0a1 1 0 0 0 2 0v-6a1 1 0 0 0-2 0z"/></svg>
  </button>
</template>

<style scoped>
.trash-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  color: #e11d48;
  transition: all 0.15s ease-in-out;
}

.trash-btn:hover {
  color: #9d1432;
  transform: translateY(-3px);
}
</style>
