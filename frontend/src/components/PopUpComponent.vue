<template>
  <transition name="fade">
    <div v-if="props.visible" class="popup">
      <p :class="type">{{ message }}</p>
      <button @click="close">✖</button>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { watch } from "vue";

const props = defineProps({
  visible: { type: Boolean, required: true },
  message: { type: String, required: true },
  type: { type: String, default: "info" },
  duration: { type: Number, default: 3000 }
});

const emit = defineEmits(["close"]);

watch(
  () => props.visible,
  (val) => {
    if (val) {
      setTimeout(() => close(), props.duration)
    }
  }
)

function close() {
  emit("close");
}
</script>

<style scoped>
.popup {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 9999;
}

.popup p {
  margin: 0;
}

.popup p.success { color: green; }
.popup p.error { color: red; }
.popup p.info
{
   color: #333;
}

.popup button {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 16px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
