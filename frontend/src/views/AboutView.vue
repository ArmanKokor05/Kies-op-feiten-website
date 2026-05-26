<script setup lang="ts">
import ButtonOne from "@/components/ButtonComponent.vue";

</script>

<template>
  <main class="about-content">
    <div class="about-container">
      <h1>Over KiesOpFeiten</h1>

      <section class="mission-section">
        <p>
          Wij geloven dat bewust stemmen begint bij goede informatie. Op social media wordt vaak desinformatie verspreid, en dat willen wij tegengaan.
          Daarom maken wij een stemwijzer die jongeren helpt om beter te begrijpen waar partijen voor staan.
          Bij elk standpunt geven we betrouwbare bronnen en leggen we ook uit hoe het stemproces werkt.
          Zo maken we stemmen transparanter en eerlijker.
        </p>
      </section>
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

    </div>
  </main>
</template>

<style scoped>
.about-content {
  flex: 1;
  padding: 2rem;
  margin-top: 6rem;
}

.about-container {
  max-width: 800px;  /* smaller voor betere focus op tekst */
  margin: 0 auto;
  padding: 0 1rem;   /* kleine padding aan de zijkanten */
}


h1 {
  color: #1a3aff;
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-align: center;
}

.mission-section p {
  color: #334155;
  font-size: 18px;
  line-height: 1.8;
  text-align: justify;
  margin-bottom: 1.5rem;
  margin-top: 5rem;

}


.button-grid {
  display: flex;
  gap: 25rem;
  justify-content: flex-end;
  margin-top: 6rem;
  width: 100%;
  max-width: 600px;
  margin-left: 10vw;
}

@media (max-width: 768px) {
  .about-content {
    margin-top: 2rem;
    padding: 1rem;
  }

  h1 {
    font-size: 2rem;
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
