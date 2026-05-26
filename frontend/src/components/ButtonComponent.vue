<script setup lang="ts">
defineProps<{
  buttonName: string
  buttonLink?: string
  buttonClass: string
}>()
</script>

<template>
  <router-link v-if="buttonLink" :to="buttonLink"><button type="button" :class="buttonClass" > {{buttonName}} </button></router-link>
  <button v-else type="button" :class="buttonClass" @click="$emit('click')" button> {{buttonName}} </button>
</template>

<style scoped>
button {
  background-color: #2563EB;
}

button:hover {
  transform: translateY(-3px);
  cursor: pointer;
  box-shadow: 0 15px 40px rgba(0,0,0,0.5);
}

.first-class {
  padding: 1rem 2rem;
  min-width: 180px;
  border-radius: 7px;
  font-weight: bold;
  box-shadow: 0 3px 8px rgba(0,0,0,0.9);
  color: white;
}

.second-class {
  background-color: #DBEAFE;
  padding: 1rem 2rem;
  min-width: 180px;
  border-radius: 7px;
  font-weight: bold;
  box-shadow: 0 3px 8px rgba(0,0,0,0.9);
  transition: transform 0.2s, box-shadow 0.2s;
}

.create-discussion-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: 1px solid #ccc; 
  border-radius: 9999px; 
  padding: 0.5rem 1rem;
  font-weight: 600;
  color: white;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1); 
  transition: all 0.15s ease-in-out;
}

.create-discussion-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

@media (max-width: 768px) {
  .first-class,
  .second-class {
    width: 100%;
    min-width: unset;
    padding: 0.875rem 1.5rem;
  }
}

@media (max-width: 480px) {
  .first-class,
  .second-class {
    padding: 0.75rem 1.25rem;
    font-size: 0.9rem;
  }
}
.adminNavbarItem {
  background-color: rgba(255, 0, 0, 0);
  border: none;
  border-radius: 1vw
}

.adminNavbarItem:hover {
cursor: pointer;
  background-color: #5a5a5a;
}

</style>
