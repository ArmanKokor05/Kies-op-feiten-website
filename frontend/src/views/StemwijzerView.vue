<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import StemwijzerQuestion from "@/components/Stemwijzer/StemwijzerQuestion.vue"
import {API_BASE_URL} from "@/config/api.config.ts";

interface MatchResult {
  partyName: string
  partyColor: string
  matchPercentage: number
}

interface LatestResult {
  id: number
  completedAt: string
  matches: MatchResult[]
}

const router = useRouter()
const route = useRoute()
const isLoggedIn = ref(false)
const showStemwijzer = ref(false)
const showResults = ref(false)
const loading = ref(true)
const latestResult = ref<LatestResult | null>(null)

onMounted(async () => {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token

  const forceRetake = route.query.force === 'true'

  if (isLoggedIn.value && !forceRetake) {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/stemwijzer/latest-result`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })

      if (response.status === 200 && response.data) {
        latestResult.value = response.data
        showResults.value = true
      } else {
        showStemwijzer.value = true
      }
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        showStemwijzer.value = true
      } else {
        showStemwijzer.value = true
      }
    }
  } else {
    showStemwijzer.value = true
  }

  loading.value = false
})

function goToLogin() {
  router.push('/login')
}

function restartStemwijzer() {
  showResults.value = false
  showStemwijzer.value = true
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('nl-NL', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<template>
  <main class="stemwijzer-content">
    <h1>Stemwijzer</h1>
    <p class="subtitle">Ontdek welke partij het beste bij je past</p>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Laden...</p>
    </div>

    <div v-else-if="!isLoggedIn" class="login-required">
      <div class="login-box">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
        </svg>
        <h2>Inloggen vereist</h2>
        <p>Je moet ingelogd zijn om de stemwijzer in te vullen.</p>
        <button @click="goToLogin" class="login-btn">Inloggen</button>
        <p class="register-link">
          Nog geen account? <router-link to="/register">Registreer hier</router-link>
        </p>
      </div>
    </div>

    <div v-else-if="showResults && latestResult" class="previous-results">
      <div class="results-card">
        <div class="results-header">
          <h2>Je hebt de stemwijzer al ingevuld!</h2>
          <p class="date-info">Laatste keer: {{ formatDate(latestResult.completedAt) }}</p>
        </div>

        <div class="top-three">
          <h3>Jouw top 3:</h3>
          <div class="result-list">
            <div
                v-for="(result, index) in latestResult.matches.slice(0, 3)"
                :key="result.partyName"
                class="result-item"
                :class="'rank-' + (index + 1)"
            >
              <div class="rank">{{ index + 1 }}</div>
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
          </div>
        </div>

        <div class="action-buttons">
          <button @click="router.push('/results')" class="view-full-btn">
            Bekijk alle resultaten
          </button>
          <button @click="restartStemwijzer" class="restart-btn">
            Opnieuw doen
          </button>
        </div>
      </div>
    </div>

    <StemwijzerQuestion v-else-if="showStemwijzer" />
  </main>
</template>

<style scoped>
.stemwijzer-content {
  flex: 1;
  padding: 2rem;
  margin-top: 6rem;
  text-align: center;
}

.subtitle {
  color: #666;
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.loading {
  padding: 60px 20px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
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

.login-required {
  display: flex;
  justify-content: center;
  min-height: 60vh;
}

.login-box {
  background: white;
  border-radius: 16px;
  padding: 60px 40px;
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.login-box svg {
  color: #3498db;
  margin-bottom: 24px;
}

.login-box h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 16px;
}

.login-box p {
  color: #7f8c8d;
  font-size: 16px;
  margin-bottom: 32px;
}

.login-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 16px 48px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
}

.register-link {
  margin-top: 24px;
  font-size: 14px;
}

.register-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 600;
}

.previous-results {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.results-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.results-header h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 8px;
}

.date-info {
  color: #7f8c8d;
  font-size: 14px;
  margin-bottom: 30px;
}

.top-three h3 {
  text-align: left;
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 20px;
}

.result-list {
  margin-bottom: 30px;
}

.result-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 12px;
  gap: 15px;
  background: #f8f9fa;
  transition: all 0.3s;
}

.result-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.result-item.rank-1 {
  background: linear-gradient(135deg, #fff9e6, #ffffff);
  border: 2px solid #f39c12;
}

.result-item.rank-2 {
  background: linear-gradient(135deg, #f5f5f5, #ffffff);
  border: 2px solid #95a5a6;
}

.result-item.rank-3 {
  background: linear-gradient(135deg, #fff5e6, #ffffff);
  border: 2px solid #cd7f32;
}

.rank {
  font-size: 32px;
  font-weight: 700;
  min-width: 50px;
  text-align: center;
}

.rank-1 .rank {
  color: #f39c12;
}

.rank-2 .rank {
  color: #95a5a6;
}

.rank-3 .rank {
  color: #cd7f32;
}

.party-info {
  display: flex;
  align-items: center;
  flex: 0 0 200px;
}

.party-color {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 12px;
}

.party-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
}

.match-bar-container {
  flex: 1;
  height: 28px;
  background: #e0e0e0;
  border-radius: 14px;
  overflow: hidden;
}

.match-bar {
  height: 100%;
  border-radius: 14px;
  transition: width 0.5s ease;
}

.match-percentage {
  font-weight: 700;
  font-size: 20px;
  color: #2c3e50;
  min-width: 70px;
  text-align: right;
}

.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
}

.view-full-btn,
.restart-btn {
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.view-full-btn {
  background: linear-gradient(135deg, #27ae60, #229954);
  color: white;
}

.view-full-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(39, 174, 96, 0.4);
}

.restart-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.restart-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
}

@media (max-width: 768px) {
  .results-card {
    padding: 25px;
  }

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

  .action-buttons {
    flex-direction: column;
  }

  .view-full-btn,
  .restart-btn {
    width: 100%;
  }
}
</style>
