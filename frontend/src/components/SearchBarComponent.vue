<script setup lang="ts">
const { modelValue, placeholder = 'Zoeken...' } = defineProps<{
  placeholder?: string
  modelValue?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const handleInput = (e: Event) => {
  const value = (e.target as HTMLInputElement).value
  emit('update:modelValue', value)
}
</script>

<template>
  <div class="search-input-wrapper">
    <input
      type="text"
      :value="modelValue"
      @input="handleInput"
      :placeholder="placeholder"
      class="search-input"
    />
  </div>
</template>
<style scoped>
.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.search-input-wrapper:focus-within {
  border-color: #1a3aff;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 1rem;
  color: #374151;
  background: transparent;
}

.search-input::placeholder {
  color: #9ca3af;
}
</style>
