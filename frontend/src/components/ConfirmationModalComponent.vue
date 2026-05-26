<script setup lang="ts">
defineProps<{
  show: boolean
  title: string
  message: string
}>()

defineEmits<{
  confirm: []
  cancel: []
}>()
</script>

<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-content">
      <h2>{{ title }}</h2>
      <p>{{ message }}</p>
      <div class="modal-buttons">
        <button class="btn-confirm" @click="$emit('confirm')">Bevestigen</button>
        <button class="btn-cancel" @click="$emit('cancel')">Annuleren</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #d9d9d9;
  color: #334155;
  padding: 32px;
  border-radius: 10px;
  text-align: center;
  max-width: 670px;
  width: 90%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

h2 {
  margin: 0;
  font-size: 24px;
  color: #1a3aff;
}

p {
  margin: 0;
  font-size: 16px;
}

.modal-buttons {
  display: flex;
  gap: 20px;
  flex-direction: column;
  width: 100%;
  align-items: center;
}

.modal-buttons button {
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  cursor: pointer;
  width: 100%;
  font-size: 16px;
  font-weight: 600;
}

.btn-confirm {
  background-color: #1a3aff;
  color: white;
}

.btn-confirm:hover {
  background-color: #1530cc;
}

.btn-cancel {
  background-color: #dbeafe;
  color: #2563eb;
}

.btn-cancel:hover {
  background-color: #c2dcfd;
}
</style>
