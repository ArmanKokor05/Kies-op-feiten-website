<script setup lang="ts">
defineProps<{
  headers: string[]
  rows: Array<Array<string | number>>
}>()
</script>

<template>
  <div class="table">
    <div
      class="row header"
      :style="{ gridTemplateColumns: `repeat(${headers.length}, 1fr)` }"
    >
      <div
        v-for="(header, index) in headers"
        :key="index"
        class="cell"
      >
        {{ header }}
      </div>
    </div>

    <div
      v-for="(row, rowIndex) in rows"
      :key="rowIndex"
      class="row"
      :style="{ gridTemplateColumns: `repeat(${headers.length}, 1fr)` }"
    >
      <div
        v-for="(cell, cellIndex) in row"
        :key="cellIndex"
        class="cell"
      >
        {{ cell }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.table {
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  color-scheme: light;
  margin: 0 auto;
  width: 1500px;
}

.row {
  display: grid;
  padding: 0.75rem 1rem;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  color: #000000;
}

.row.header {
  background: #DBEAFE;
  font-weight: 600;
  color: #000000;
}

.cell {
  padding: 0.25rem 0.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #000000;
}

@media (max-width: 768px) {
  .table{
    width: 90%;
  }
}
</style>
