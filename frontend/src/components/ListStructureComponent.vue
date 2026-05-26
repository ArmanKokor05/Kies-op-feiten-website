<script setup lang="ts">
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  clickable: {
    type: Boolean,
    default: true
  }
});

const emits = defineEmits(["click"]);

const handleClick = () => {
  if (props.clickable) {
    emits("click");
  }
};
</script>

<template>
  <div class="list" @click="handleClick" :class="{ 'list-clickable': clickable }">
    <slot />
  </div>
</template>

<style scoped>
.list {
  background: white;
  color: #1a3aff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: box-shadow 0.2s, transform 0.2s;
}

.list-clickable {
  cursor: pointer;
}

.list-clickable:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}
</style>
