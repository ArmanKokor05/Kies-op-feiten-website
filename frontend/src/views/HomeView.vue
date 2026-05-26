<script setup lang="ts">
import HelloWorld from '../components/HelloWorld.vue'
import ButtonOne from '../components/ButtonComponent.vue'
</script>

<template>
  <main class="main-content">
    <HelloWorld msg="Stem bewust, stem geïnformeerd" />
    <div class="button-grid">
      <ButtonOne
        button-class="first-class"
        button-link="/stemwijzer"
        button-name="Start Stemwijzer"
      />
      <ButtonOne
        button-class="second-class"
        button-link="/kaart"
        button-name="Bekijk de Kaart"
      />
    </div>
  </main>
</template>

<style scoped>
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  margin-top: 6rem;
}

.button-grid {
  display: flex;
  gap: 4rem;
  justify-content: center;
  margin-top: 3rem;
  width: 100%;
  max-width: 600px;
}



@media (max-width: 768px) {
  .main-content {
    margin-top: 2rem;
  }

  .waarom-section {
    margin-left: 2rem;
    margin-right: 2rem;
    padding: 2rem;
  }

  .button-grid {
    flex-direction: column;
    gap: 1.5rem;
    max-width: 300px;
  }
}

@media (max-width: 480px) {
  .button-grid {
    gap: 1rem;
  }
}
</style>
