<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  modelValue: string
  placeholder?: string
  rows?: number
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const localValue = ref(props.modelValue)

function updateValue(event: Event) {
  const target = event.target as HTMLTextAreaElement
  localValue.value = target.value
  emit('update:modelValue', target.value)
}
</script>

<template>
  <textarea
    :value="modelValue"
    @input="updateValue"
    :placeholder="placeholder"
    :rows="rows || 4"
    :disabled="disabled"
    class="textarea-input"
  ></textarea>
</template>

<style scoped>
.textarea-input {
  width: 100%;
  padding: 0.75rem;
  font-size: 16px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  outline: none;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  color: #2d3748;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  resize: vertical;
  min-height: 80px;
}

.textarea-input:focus {
  border-color: rgba(59, 130, 246, 0.8);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2), 0 0 0 4px rgba(59, 130, 246, 0.2);
  transform: translateY(-2px);
}

.textarea-input::placeholder {
  color: #718096;
  font-weight: 500;
}

.textarea-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.7);
}
</style>
