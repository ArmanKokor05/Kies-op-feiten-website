<template>
  <main class="form-content">
    <div class="form-container">
      <h1>Deel je mening</h1>

      <form @submit.prevent="plaatsBericht">
        <!-- Auteur -->
        <div class="form-group">
          <label for="auteur">Je naam:</label>
          <input
            type="text"
            id="auteur"
            v-model="nieuwBericht.auteur"
            placeholder="Voer je naam in"
            required
          />
        </div>

        <!-- Titel -->
        <div class="form-group">
          <label for="titel">Titel van je bericht:</label>
          <input
            type="text"
            id="titel"
            v-model="nieuwBericht.titel"
            placeholder="Geef een titel aan je bericht"
            required
          />
        </div>

        <!-- Inhoud -->
        <div class="form-group">
          <label for="inhoud">Je bericht:</label>
          <textarea
            id="inhoud"
            v-model="nieuwBericht.inhoud"
            placeholder="Deel je mening"
            rows="4"
            required
          ></textarea>
        </div>

        <!-- Verstuur knop -->
        <button type="submit" class="save-btn" :disabled="isAanHetVersturen">
          {{ isAanHetVersturen ? 'Bezig met versturen...' : 'Verstuur' }}
        </button>
      </form>

      <!-- Berichten lijst -->
      <div class="berichten-lijst">
        <h2>Recente discussies</h2>

        <div v-if="berichten.length === 0" class="geen-berichten">
          Nog geen berichten.
        </div>

        <div v-else class="bericht-item" v-for="bericht in berichten" :key="bericht.id">
          <h3>{{ bericht.title }}</h3>
          <p>{{ bericht.content }}</p>
          <div class="bericht-footer">
            <span>Door: {{ bericht.author }}</span>
            <span>{{ new Date(bericht.createdAt).toLocaleDateString('nl-NL') }}</span>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import {API_BASE_URL} from "@/config/api.config.ts";

// Simpele interface
interface Bericht {
  id: number;
  title: string;
  content: string;
  author: string;
  createdAt: string;
}

const nieuwBericht = ref({
  auteur: '',
  titel: '',
  inhoud: ''
});

const berichten = ref<Bericht[]>([]);
const isAanHetVersturen = ref(false);

async function laadBerichten() {
  try {
    const response = await fetch(`${API_BASE_URL}/api/forum`);
    if (response.ok) {
      berichten.value = await response.json();
    }
  } catch (error) {
    console.error('Fout bij laden berichten:', error);
  }
}

async function plaatsBericht() {
  if (!nieuwBericht.value.auteur || !nieuwBericht.value.titel || !nieuwBericht.value.inhoud) {
    alert('Vul alle velden in');
    return;
  }

  isAanHetVersturen.value = true;

  try {
    const response = await fetch(`${API_BASE_URL}/api/forum`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        title: nieuwBericht.value.titel,
        content: nieuwBericht.value.inhoud,
        author: nieuwBericht.value.auteur
      })
    });

    if (response.ok) {
      // Reset form
      nieuwBericht.value = { auteur: '', titel: '', inhoud: '' };
      // Herlaad berichten
      await laadBerichten();
    } else {
      alert('Fout bij plaatsen bericht');
    }
  } catch (error) {
    alert('Netwerk fout');
  } finally {
    isAanHetVersturen.value = false;
  }
}

onMounted(() => {
  laadBerichten();
});
</script>

<style scoped>
.form-content {
  flex: 1;
  padding: 2rem;
  margin-top: 6rem;
}

.form-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 1rem;
}

h1 {
  color: #1a3aff;
  font-size: 2.2rem;
  margin-bottom: 2rem;
  text-align: center;
}

h2 {
  color: #1a3aff;
  font-size: 1.8rem;
  margin: 3rem 0 1rem 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  display: block;
}

input, textarea {
  padding: 0.7rem;
  font-size: 1rem;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  width: 100%;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  min-height: 100px;
}

.save-btn {
  background-color: #1A3AFF;
  color: #FFF;
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  width: 100%;
}

.save-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.berichten-lijst {
  margin-top: 3rem;
}

.geen-berichten {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.bericht-item {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.bericht-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  color: #666;
  font-size: 0.9rem;
}
</style>
