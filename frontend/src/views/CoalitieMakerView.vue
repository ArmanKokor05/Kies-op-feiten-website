<script setup lang="ts">
import { partyResultService } from '@/services/electionService'
import { onMounted, ref, computed } from 'vue'

interface Partij {
  naam: string
  zetels: number
  kleur: string
}

const geselecteerdePartijen = ref(new Set<Partij>());
const partijen = ref<Partij[]>([]);
const beschikbareJaren = ref<number[]>([]);
const geselecteerdJaar = ref<number | null>(null);

const totaleZetels = computed(() => {
  let totaal = 0
  for (const partij of geselecteerdePartijen.value) {
    totaal += partij.zetels
  }
  return totaal
})

const heeftMeerderheid = computed(() => {
  if (totaleZetels.value >= 76)
    return "Meerderheid"
  else return "Minderheid"
})

const togglePartij = (partij: Partij) => {
  if (geselecteerdePartijen.value.has(partij)) {
    geselecteerdePartijen.value.delete(partij)
  }
  else {
    geselecteerdePartijen.value.add(partij)
  }
}

const loading = ref(false);
const error = ref<string | null>(null);

const fetchAvailableYears = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await partyResultService.getAvailableYears();
    beschikbareJaren.value = response.data;

    if (beschikbareJaren.value.length > 0) {
      geselecteerdJaar.value = beschikbareJaren.value[0]!;
      await laadPartijen();
    }
  } catch (err: unknown) {
    error.value = 'Fout bij ophalen jaren: ' + (err instanceof Error ? err.message : String(err));    console.error(err);
  } finally {
    loading.value = false;
  }
};

const laadPartijen = async () => {
  if (!geselecteerdJaar.value) return;

  loading.value = true;
  error.value = null;
  try {
    const response = await partyResultService.getSeatsByYear(geselecteerdJaar.value);

    partijen.value = response.data.map((p: any): Partij => ({
      naam: p.partyName,
      zetels: p.seats || 0,
      kleur: p.color
    }));

    console.log('Partijen geladen voor jaar', geselecteerdJaar.value, ':', partijen.value);
    geselecteerdePartijen.value.clear();
  } catch (err: any) {
    error.value = 'Fout bij ophalen partition: ' + err.message;
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const alleZetels = computed(() => {
  const zetels = [];

  for (const partij of geselecteerdePartijen.value) {
    for (let i = 0; i < partij.zetels; i++) {
      zetels.push({
        kleur: partij.kleur,
        partij: partij.naam
      });
    }
  }

  while (zetels.length < 150) {
    zetels.push({
      kleur: '#e0e0e0',
      partij: 'Leeg'
    });
  }

  return zetels;
});

onMounted(() => {
  fetchAvailableYears();
});
</script>

<template>
  <div class="container">
    <h1>De Coalitie Maker!</h1>

    <div v-if="loading" class="loading">Laden...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="beschikbareJaren.length > 0" class="filter">
      <label for="jaar-select">Selecteer verkiezingsjaar:</label>
      <select
          id="jaar-select"
          v-model="geselecteerdJaar"
          @change="laadPartijen"
          class="jaar-select"
      >
        <option v-for="jaar in beschikbareJaren" :key="jaar" :value="jaar">
          {{ jaar }}
        </option>
      </select>
    </div>

    <div class="info">
      <p>Totaal: {{ totaleZetels }} / 75 zetels</p>
      <p v-if="heeftMeerderheid === 'Meerderheid'" class="meerderheid">✓ Meerderheid!</p>
      <p v-else class="minderheid">✗ Minderheid</p>
    </div>

    <div class="hemicycle">
      <div
          v-for="(zetel, index) in alleZetels"
          :key="index"
          class="zetel"
          :style="{ backgroundColor: zetel.kleur }"
          :title="zetel.partij"
      ></div>
    </div>

    <div class="partijen">
      <div
          v-for="partij in partijen"
          :key="partij.naam"
          @click="togglePartij(partij)"
          class="partij"
          :class="{ actief: geselecteerdePartijen.has(partij) }"
      >
        <div class="partij-kleur" :style="{ backgroundColor: partij.kleur }"></div>
        <div class="partij-info">
          <h3>{{ partij.naam }}</h3>
          <p>{{ partij.zetels }} zetels</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem;
}

h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  padding: 1rem;
  color: #666;
}

.error {
  text-align: center;
  padding: 1rem;
  color: #e74c3c;
  background: #fadbd8;
  border-radius: 4px;
  margin: 1rem 0;
}

.info {
  text-align: center;
  margin: 2rem 0;
  font-size: 1.5rem;
}

.filter {
  text-align: center;
  margin: 1.5rem 0;
}

.filter label {
  font-weight: 600;
  margin-right: 0.5rem;
}

.jaar-select {
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border: 2px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  background: white;
}

.jaar-select:hover {
  border-color: #27ae60;
}

.meerderheid {
  color: #27ae60;
  font-weight: bold;
}

.minderheid {
  color: #e67e22;
}

.hemicycle {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 700px;
  margin: 2rem auto;
  padding: 2rem;
  background: linear-gradient(to bottom, #f8f9fa 0%, #ffffff 100%);
  border-radius: 50% 50% 0 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.zetel {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin: 3px;
  transition: all 0.2s;
  cursor: pointer;
}

.zetel:hover {
  transform: scale(1.3);
  box-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.partijen {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 2rem;
}

.partij {
  display: flex;
  align-items: center;
  padding: 1rem;
  border: 2px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.partij:hover {
  background: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.partij.actief {
  border-color: #27ae60;
  background: #e8f5e9;
}

.partij-kleur {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 1rem;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.partij-info {
  flex: 1;
}

.partij-info h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1.1rem;
  color: #2c3e50;
}

.partij-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 0.9rem;
}
</style>