<script setup lang="ts">
import adminItem from './adminItem.vue';

defineProps< {
  viewName: string
}>()
</script>

<template>
  <adminItem
    item-title="upload XML file"
    input-type="file"
  />
</template>
