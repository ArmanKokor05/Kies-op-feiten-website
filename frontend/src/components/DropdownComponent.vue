<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from "vue";

const props = defineProps({
  label: {
    type: String,
    default: ""
  },
  options: {
    type: Array as () => Array<string | number>,
    required: true
  },
  modelValue: {
    type: [String, Number],
    default: ""
  },
  placeholder: {
    type: String,
    default: ""
  },
  width: {
    type: String,
    default: "800px"
  }
});

const emits = defineEmits<{
  (e: "update:modelValue", value: string | number): void;
  (e: "change", value: string | number): void;
}>();

const internalValue = ref<string | number>(props.modelValue);

watch(
  () => props.modelValue,
  (val) => {
    internalValue.value = val;
  }
);

watch(internalValue, (val) => {
  emits("update:modelValue", val);
  emits("change", val);
});
</script>

<template>
  <div class="dropdown">
    <label v-if="label">{{ label }}</label>

    <select
      v-model="internalValue"
      :style="{ width }"
    >
      <option v-if="placeholder" value="">
        {{ placeholder }}
      </option>

      <option
        v-for="opt in options"
        :key="opt"
        :value="opt"
      >
        {{ opt }}
      </option>
    </select>
  </div>
</template>

<style scoped>
.dropdown {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 1rem;
}

.dropdown label {
  font-weight: bold;
  margin-bottom: 0.3rem;
  text-align: center;
}

.dropdown select {
  padding: 0.5rem 1rem;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 1rem;
  max-width: 100%;
}
</style>
