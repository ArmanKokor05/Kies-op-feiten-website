<script setup lang="ts">
import { reactive } from 'vue';
import type { InputField, TextArea } from '../../types'

const props = defineProps<{
  show: boolean
  inputFields?: InputField[]
  textAreas?: TextArea[]
  submitButton: string
  cancelButton: string
  width?: string
  height?: string
}>()

const submittableInputFields = reactive(
  props.inputFields ? [...props.inputFields] : []
)

const submittableTextFields = reactive(
  props.textAreas ? [...props.textAreas] : []
)

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: { submittableInputFields: InputField[], submittableTextFields: TextArea[] }): void
}>()

function handleSubmit() {
  emit('submit', { submittableInputFields, submittableTextFields } )
}
</script>

<template>
  <div v-if="show" class="modal-backdrop">
    <div class="modal-container"
    :style="{ width: width || 'auto', height: height || 'auto' }">
      <form @submit.prevent="handleSubmit()" class="modal-form">
        <div v-for="field in submittableInputFields" :key="field.key" class="form-field">
          <label class="form-label">{{ field.key }}</label>
          <input type="text" v-model="field.value" class="form-input" />
        </div>

        <div v-for="field in submittableTextFields" :key="field.key" class="form-field">
            <label class="form-label">{{ field.key }}</label>
            <textarea
                v-model="field.value"
                class="form-input"
                :rows="field.rows"
            ></textarea>
        </div>

        <div class="form-footer">
          <button type="button" class="modal-btn cancel-btn" @click="$emit('close')">{{ cancelButton }}</button>
          <button type="submit" class="modal-btn submit-btn">{{ submitButton }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.modal-container {
  background-color: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  overflow-y: auto;
  box-shadow: 0 8px 30px rgba(0,0,0,0.2);
  position: relative;
  z-index: 100;
}
.form-field {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.form-label {
  font-weight: 600;
  margin-bottom: 0.25rem;
  color: #111827;
}

.form-input {
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  border: 1px solid #d1d5db;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus {
  border-color: #2563eb;
  outline: none;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.2);
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
}

.modal-btn {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s, transform 0.1s;
}

.cancel-btn {
  background-color: #e5e7eb;
  color: #111827;
}

.cancel-btn:hover {
  background-color: #d1d5db;
  transform: translateY(-1px);
}

.submit-btn {
  background-color: #2563eb;
  color: white;
}

.submit-btn:hover {
  background-color: #1d4ed8;
  transform: translateY(-1px);
}

@media (max-width: 1052px) {
  .modal-container {
    width: 90% !important;
    max-width: 90% !important;
  }
  .form-input {
    font-size: 0.95rem;
  }
  .modal-btn {
    font-size: 0.95rem;
    padding: 0.45rem 0.75rem;
  }
}
</style>
