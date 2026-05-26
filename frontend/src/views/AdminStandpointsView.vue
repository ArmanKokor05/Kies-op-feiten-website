<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import {API_BASE_URL} from "@/config/api.config.ts";

interface Question {
  id: number
  title: string
  question: string
}

interface Party {
  id: number
  name: string
  color: string
}

interface Standpoint {
  partyId: number
  questionId: number
  stance: string
}

const questions = ref<Question[]>([])
const parties = ref<Party[]>([])
const selectedQuestionId = ref<number | null>(null)
const standpoints = ref<Map<number, string>>(new Map())
const loading = ref(false)
const saveMessage = ref('')

onMounted(async () => {
  await loadQuestions()
  await loadParties()

  console.log("loaded all parties: ", JSON.stringify(parties.value))
  console.log('Questions data structure:', JSON.stringify(questions.value))
})

async function loadQuestions() {
  try {
    const response = await axios.get(`${API_BASE_URL}/questions`)
    questions.value = response.data
  } catch (error) {
    console.error('Failed to load questions:', error)
  }
}

async function loadParties() {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/admin/parties`)
    parties.value = response.data
  } catch (error) {
    console.error('Failed to load parties:', error)
  }
}

async function loadExistingStandpoints() {
  if (!selectedQuestionId.value) return

  try {
    const response = await axios.get(`${API_BASE_URL}/api/admin/standpoints/question/${selectedQuestionId.value}`)
    const existing = response.data

    standpoints.value.clear()

    existing.forEach((sp: any) => {
      standpoints.value.set(sp.party.id, sp.stance)
    })
  } catch (error) {
    console.error('Failed to load standpoints:', error)
  }
}

function onQuestionChange() {
  loadExistingStandpoints()
}

function setStance(partyId: number, stance: string) {
  standpoints.value.set(partyId, stance)
}

function getStance(partyId: number): string {
  return standpoints.value.get(partyId) || ''
}

async function saveStandpoints() {
  if (!selectedQuestionId.value) {
    saveMessage.value = 'Selecteer eerst een vraag!'
    return
  }

  const standpointsList: Standpoint[] = []

  parties.value.forEach(party => {
    const stance = standpoints.value.get(party.id)
    if (stance) {
      standpointsList.push({
        partyId: party.id,
        questionId: selectedQuestionId.value!,
        stance: stance
      })
    }
  })

  if (standpointsList.length === 0) {
    saveMessage.value = 'Vul minstens 1 standpunt in!'
    return
  }

  loading.value = true
  saveMessage.value = ''

  try {
    const response = await axios.post(`${API_BASE_URL}/api/admin/standpoints/bulk`, {
      standpoints: standpointsList
    })

    saveMessage.value = ` ${response.data}`
    setTimeout(() => saveMessage.value = '', 3000)
  } catch (error) {
    console.error('Failed to save:', error)
    saveMessage.value = ' Opslaan mislukt!'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1>Admin Panel - Standpunten Beheren</h1>
      <p class="subtitle">Vul voor elke vraag de standpunten van alle partijen in</p>
    </div>

    <div class="question-selector">
      <label for="question-select">Selecteer vraag:</label>
      <select
        id="question-select"
        v-model="selectedQuestionId"
        @change="onQuestionChange"
        class="question-dropdown"
      >
        <option :value="null">-- Kies een vraag --</option>
        <option
          v-for="question in questions"
          :key="question.id"
          :value="question.id"
        >
          {{ question.title }}
        </option>
      </select>
    </div>

    <div v-if="selectedQuestionId" class="standpoints-section">
      <div class="question-display">
        <h3>{{ questions.find(q => q.id === selectedQuestionId)?.title }}</h3>
        <p>{{ questions.find(q => q.id === selectedQuestionId)?.question }}</p>
      </div>

      <div class="parties-list">
        <div
            v-for="party in parties"
            :key="party.id"
            class="party-row"
        >
          <div class="party-info">
            <span class="party-color" :style="{ backgroundColor: party.color }"></span>
            <span class="party-name">{{ party.name }}</span>
          </div>

          <div class="stance-buttons">
            <button
                @click="setStance(party.id, 'DISAGREE')"
                :class="['stance-btn', 'btn-disagree', { active: getStance(party.id) === 'DISAGREE' }]"
            >
              Oneens
            </button>
            <button
                @click="setStance(party.id, 'NEUTRAL')"
                :class="['stance-btn', 'btn-neutral', { active: getStance(party.id) === 'NEUTRAL' }]"
            >
              Neutraal
            </button>
            <button
                @click="setStance(party.id, 'AGREE')"
                :class="['stance-btn', 'btn-agree', { active: getStance(party.id) === 'AGREE' }]"
            >
              Eens
            </button>
          </div>
        </div>
      </div>

      <div class="save-section">
        <button
            @click="saveStandpoints"
            class="save-btn"
            :disabled="loading"
        >
          {{ loading ? 'Opslaan...' : 'Opslaan' }}
        </button>
        <p v-if="saveMessage" class="save-message">{{ saveMessage }}</p>
      </div>
    </div>

    <div v-else class="no-question">
      <p>👆 Selecteer een vraag om te beginnen</p>
    </div>
  </div>
</template>

<style scoped>
.admin-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.admin-header {
  text-align: center;
  margin-bottom: 40px;
}

.admin-header h1 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.subtitle {
  color: #7f8c8d;
  font-size: 16px;
}

.question-selector {
  margin-bottom: 30px;
}

.question-selector label {
  display: block;
  font-weight: 600;
  margin-bottom: 10px;
  color: #34495e;
}

.question-dropdown {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 8px;
  background: white;
  cursor: pointer;
}

.question-dropdown:focus {
  outline: none;
  border-color: #3498db;
}

.question-display {
  background: #ecf0f1;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.question-display h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.question-display p {
  color: #555;
}

.parties-list {
  margin-bottom: 30px;
}

.party-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 10px;
}

.party-row:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.party-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.party-color {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 12px;
}

.party-name {
  font-weight: 500;
  color: #2c3e50;
}

.stance-buttons {
  display: flex;
  gap: 10px;
}

.stance-btn {
  padding: 8px 20px;
  border: 2px solid transparent;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
  background: #ecf0f1;
  color: #7f8c8d;
}

.stance-btn:hover {
  transform: translateY(-2px);
}

.stance-btn.active {
  border-color: currentColor;
  transform: scale(1.05);
}

.btn-disagree.active {
  background: #e74c3c;
  color: white;
}

.btn-neutral.active {
  background: #95a5a6;
  color: white;
}

.btn-agree.active {
  background: #27ae60;
  color: white;
}

.save-section {
  text-align: center;
  margin-top: 30px;
}

.save-btn {
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 600;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.save-btn:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
}

.save-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.save-message {
  margin-top: 15px;
  font-size: 16px;
  font-weight: 600;
}

.no-question {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
  font-size: 18px;
}

@media (max-width: 768px) {
  .party-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .stance-buttons {
    width: 100%;
    margin-top: 15px;
  }

  .stance-btn {
    flex: 1;
  }
}
</style>
