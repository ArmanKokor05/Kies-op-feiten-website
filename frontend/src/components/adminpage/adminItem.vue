<script setup lang="ts">
defineProps<{
  itemTitle: string
  inputType: string
}>()
</script>

<template>
  <div class="adminItemContainer">
    <div class="itemTitle"> {{itemTitle}} </div>
    <div class="itemInput"> <input :type="inputType" ></div>
  </div>
</template>

<style scoped>
.adminItemContainer {
  display: flex;
  flex-direction: column;
  background-color: #9c9b9b;
  height: fit-content;
  width: fit-content;
  padding: 1vh;
  align-items: center;
  margin-top: 2vh
}
</style>
