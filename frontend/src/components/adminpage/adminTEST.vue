<script setup lang="ts">
import adminItem from './adminItem.vue';

defineProps< {
  viewName: string
}>()
</script>

<template>
  <adminItem
    item-title="TEST"
    input-type="file"
  />
</template>
