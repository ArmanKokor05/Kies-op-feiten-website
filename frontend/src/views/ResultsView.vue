<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import {API_BASE_URL} from "@/config/api.config.ts";

interface MatchResult {
  partyName: string
  partyColor: string
  matchPercentage: number
}

const router = useRouter()
const route = useRoute()
const results = ref<MatchResult[]>([])
const loading = ref(true)
const error = ref('')
const hasPreviousResults = ref(false)
const completedAt = ref<string>('')

onMounted(async () => {
  try {
    const matchesParam = route.query.matches as string

    if (matchesParam) {
      results.value = JSON.parse(matchesParam)
      loading.value = false
      return
    }

    await loadLatestResult()

  } catch {
    error.value = 'Er ging iets mis bij het laden van de resultaten.'
    loading.value = false
  }
})

async function loadLatestResult() {
  try {
    const token = localStorage.getItem('token')

    if (!token) {
      error.value = 'Geen antwoorden gevonden. Doe eerst de stemwijzer!'
      loading.value = false
      return
    }

    console.log('Fetching latest result...')
    const response = await axios.get(`${API_BASE_URL}/api/stemwijzer/latest-result`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (response.status === 200 && response.data) {
      results.value = response.data.matches
      completedAt.value = new Date(response.data.completedAt).toLocaleDateString('nl-NL', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
      hasPreviousResults.value = true
      loading.value = false
    } else {
      error.value = 'Geen eerdere resultaten gevonden. Doe eerst de stemwijzer!'
      loading.value = false
    }

  } catch (err) {
    if (axios.isAxiosError(err) && err.response) {
      if (err.response.status === 401 || err.response.status === 403) {
        localStorage.removeItem('token')
        error.value = 'Je sessie is verlopen. Log opnieuw in.'
      } else if (err.response.status === 404) {
        error.value = 'Geen eerdere resultaten gevonden. Doe eerst de stemwijzer!'
      } else {
        error.value = 'Er ging iets mis bij het laden van de resultaten.'
      }
    } else {
      error.value = 'Er ging iets mis bij het laden van de resultaten.'
    }
    loading.value = false
  }
}

function restartStemwijzer() {
  router.push('/stemwijzer?force=true')
}

function goToLogin() {
  router.push('/login')
}
</script>

<template>
  <div class="results-container">
    <div class="results-header">
      <h1>Jouw Resultaten</h1>
      <p class="subtitle">Dit zijn de partijen die het beste bij jou passen</p>
      <p v-if="hasPreviousResults" class="previous-date">
        Laatste stemwijzer: {{ completedAt }}
      </p>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Resultaten laden...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>{{ error }}</p>
      <div class="error-buttons">
        <button @click="restartStemwijzer" class="restart-btn">
          Stemwijzer doen
        </button>
        <button v-if="error.includes('sessie')" @click="goToLogin" class="login-btn">
          Inloggen
        </button>
      </div>
    </div>

    <div v-else class="results-list">
      <div
          v-for="(result, index) in results"
          :key="result.partyName"
          class="result-item"
          :class="{ 'top-three': index < 3 }"
      >
        <div class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>

        <div class="party-info">
          <span
              class="party-color"
              :style="{ backgroundColor: result.partyColor }"
          ></span>
          <span class="party-name">{{ result.partyName }}</span>
        </div>

        <div class="match-bar-container">
          <div
              class="match-bar"
              :style="{
              width: result.matchPercentage + '%',
              backgroundColor: result.partyColor
            }"
          ></div>
        </div>

        <div class="match-percentage">
          {{ result.matchPercentage.toFixed(1) }}%
        </div>
      </div>

      <div class="actions">
        <button @click="restartStemwijzer" class="restart-btn">
          Opnieuw doen
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.results-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.results-header {
  text-align: center;
  margin-bottom: 40px;
}

.results-header h1 {
  font-size: 36px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.subtitle {
  color: #7f8c8d;
  font-size: 16px;
}

.previous-date {
  color: #95a5a6;
  font-size: 14px;
  font-style: italic;
  margin-top: 8px;
}

.loading, .error {
  text-align: center;
  padding: 60px 20px;
  font-size: 18px;
  color: #7f8c8d;
}

.error {
  color: #e74c3c;
}

.error-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 20px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4CAF50;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.results-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.result-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ecf0f1;
  gap: 15px;
  transition: all 0.3s;
}

.result-item:last-child {
  border-bottom: none;
}

.result-item.top-three {
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 8px;
  margin-bottom: 8px;
  border: 1px solid #e0e0e0;
}

.result-item.top-three:hover {
  transform: translateX(3px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.rank {
  font-size: 24px;
  font-weight: 700;
  color: #95a5a6;
  min-width: 40px;
  text-align: center;
}

.rank-1 {
  color: #f39c12 !important;
  font-size: 28px !important;
}

.rank-2 {
  color: #95a5a6 !important;
  font-size: 26px !important;
}

.rank-3 {
  color: #cd7f32 !important;
  font-size: 26px !important;
}

.party-info {
  display: flex;
  align-items: center;
  flex: 0 0 200px;
}

.party-color {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  margin-right: 10px;
}

.party-name {
  font-weight: 500;
  color: #2c3e50;
  font-size: 15px;
}

.match-bar-container {
  flex: 1;
  height: 24px;
  background: #ecf0f1;
  border-radius: 12px;
  overflow: hidden;
}

.match-bar {
  height: 100%;
  border-radius: 12px;
  transition: width 0.5s ease;
}

.match-percentage {
  font-weight: 700;
  font-size: 18px;
  color: #2c3e50;
  min-width: 60px;
  text-align: right;
}

.actions {
  margin-top: 30px;
  text-align: center;
}

.restart-btn, .login-btn {
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.restart-btn {
  background: #3498db;
}

.restart-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
}

.login-btn {
  background: #27ae60;
}

.login-btn:hover {
  background: #229954;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.4);
}

@media (max-width: 768px) {
  .result-item {
    flex-wrap: wrap;
  }

  .party-info {
    flex: 0 0 150px;
  }

  .match-bar-container {
    flex: 1 1 100%;
    order: 3;
  }

  .match-percentage {
    order: 2;
  }

  .error-buttons {
    flex-direction: column;
  }
}
</style>
