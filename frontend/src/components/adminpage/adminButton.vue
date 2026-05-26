<script setup lang="ts">
import { useRouter } from 'vue-router'
import {ToggleItems} from "@/scripts/ToggleItems.ts";

const props = defineProps<{
  buttonName: string
  buttonLink: string
  buttonClass: string
}>()

const router = useRouter()

function handleClick() {
  console.log("clicked", props.buttonLink);
  ToggleItems(router, props.buttonLink)
}

</script>

<template>
  <button type="button" :class="buttonClass" @click="handleClick()" > {{buttonName}} </button>
</template>

<style scoped>

.first-class {
  background-color: #2563EB;
  height: 6vh;
  width: 12vw;
  border-radius: 7px;
  font-weight: bold;
  box-shadow: 0 3px 8px rgba(0,0,0,0.9);
  color: white;

}
.button-grid button:hover {
  transform: translateY(-3px);
  cursor: pointer;
  box-shadow: 0 15px 40px rgba(0,0,0,0.5);
}

.second-class {
  background-color: #DBEAFE;
  height: 6vh;
  width: 12vw;
  border-radius: 7px;
  font-weight: bold;
  box-shadow: 0 3px 8px rgba(0,0,0,0.9);
  color: #2563EB;
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
