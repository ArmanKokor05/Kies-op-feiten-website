<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Question, type QuestionResponseDTO } from '../../services/questionService'
import { API_BASE_URL } from '@/config/api.config';
import axios from 'axios'

interface Vote {
  vote: string
  question: QuestionResponseDTO
  questionId: number
}

const router = useRouter()
const questionService = new Question()

const currentQuestion = ref<QuestionResponseDTO | null>(null)
const remainingQuestions = ref<QuestionResponseDTO[]>([])
const votes = ref<Vote[]>([])
const canVote = ref(true)
const currentQuestionNumber = ref(1)
const totalQuestions = ref(0)
const isSubmitting = ref(false)

onMounted(async () => {
  remainingQuestions.value = (await questionService.getQuestions()) || []
  totalQuestions.value = remainingQuestions.value.length

  if (remainingQuestions.value.length > 0) {
    currentQuestion.value = getRandomQuestion()
  } else {
    currentQuestion.value = {
      title: "Geen vragen",
      question: "Er zijn geen vragen beschikbaar",
      sourceUrl: "",
      sourceName: ""
    }
    canVote.value = false
  }
})

// Functies
function getRandomQuestion(): QuestionResponseDTO {
  if (remainingQuestions.value.length === 0) {
    currentQuestion.value = {
      title: "Geen vragen",
      question: "Er zijn geen vragen beschikbaar",
      sourceUrl: "",
      sourceName: ""
    }
    canVote.value = false
    throw new Error('Geen vragen beschikbaar')
  }

  const index = Math.floor(Math.random() * remainingQuestions.value.length)
  const randomQuestion = remainingQuestions.value[index]
  remainingQuestions.value.splice(index, 1)

  if (typeof(randomQuestion) == "undefined"){
    throw new Error('unexpected undefined question')
  }

  return randomQuestion
}

function nextQuestion() {
  if (currentQuestionNumber.value >= totalQuestions.value) {
    calculateAndShowResults()
  } else {
    currentQuestionNumber.value++
    currentQuestion.value = getRandomQuestion()
  }
}

function vote(voteValue: string) {
  if (canVote.value && currentQuestion.value) {
    votes.value.push({ vote: voteValue,
      question: currentQuestion.value,
      questionId: currentQuestionNumber.value})
    nextQuestion()
  }
}

async function calculateAndShowResults() {
  console.log('=== CALCULATE AND SHOW RESULTS CALLED ===')

  if (isSubmitting.value) {
    console.log('Already submitting, aborting')
    return
  }

  isSubmitting.value = true
  canVote.value = false

  try {
    const answers: Record<number, string> = {}

    votes.value.forEach(v => {
      const stance = v.vote === 'eens' ? 'AGREE' :
          v.vote === 'oneens' ? 'DISAGREE' : 'NEUTRAL'
      answers[v.questionId] = stance
    })

    console.log('Submitting answers:', answers)

    // Stap 1: Bereken match
    console.log('Step 1: Calculating match...')
    const calculateResponse = await axios.post(`${API_BASE_URL}/api/stemwijzer/calculate`, {
      answers: answers
    })
    console.log('Match results:', calculateResponse.data)

    // Stap 2: Save
    const token = localStorage.getItem('token')
    console.log('Step 2: Token:', token ? 'EXISTS' : 'NULL')

    if (token) {
      console.log('Calling /save endpoint...')
      const saveResponse = await axios.post(`${API_BASE_URL}/api/stemwijzer/save`, {
        answers: answers
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      })
      console.log('Save response status:', saveResponse.status)
      console.log('Save response data:', saveResponse.data)
    }

    // Stap 3: Navigate
    console.log('Step 3: Navigating to results...')
    router.push({
      name: 'results',
      query: {
        answers: JSON.stringify(answers),
        matches: JSON.stringify(calculateResponse.data)
      }
    })

  } catch (error: unknown) {
    console.error('Full error:', error)

    let errorMessage = 'Er ging iets mis bij het verwerken van de resultaten'

    if (error instanceof Error) {
      errorMessage = error.message
      console.error('Error message:', error.message)
    }

    if (axios.isAxiosError(error)) {
      console.error('Error response:', error.response?.data)
    }

    alert('Er ging iets mis: ' + errorMessage)
    isSubmitting.value = false
    canVote.value = false
  }
}
</script>

<template>
  <div class="container">
    <div class="progress-bar">
      <div class="progress-fill" :style="{ width: (currentQuestionNumber / totalQuestions * 100) + '%' }"></div>
    </div>

    <p class="question-counter">
      Vraag {{ currentQuestionNumber }} van {{ totalQuestions }}
    </p>

    <div v-if="!isSubmitting" class="question-box">
      <h2 class="question-title">{{ currentQuestion?.title }}</h2>
      <p class="question-text">{{ currentQuestion?.question }}</p>

      <div v-if="currentQuestion?.sourceUrl" class="source-info">
        <span class="source-label">Bron:</span>
        <a :href="currentQuestion.sourceUrl" target="_blank" class="source-link" rel="noopener noreferrer">
          {{ currentQuestion.sourceName || 'Bekijk bron' }}
          <span class="external-icon">↗</span>
        </a>
      </div>
    </div>

    <div v-else class="question-box loading-box">
      <div class="spinner"></div>
      <h2 class="question-title">Resultaten berekenen...</h2>
      <p class="question-text">Even geduld, we vergelijken jouw antwoorden met alle partijen.</p>
    </div>

    <div v-if="!isSubmitting" class="vote-buttons">
      <button @click="vote('oneens')" class="btn btn-disagree">Oneens</button>
      <button @click="vote('neutraal')" class="btn btn-neutral">Neutraal</button>
      <button @click="vote('eens')" class="btn btn-agree">Eens</button>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 15px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50, #8BC34A);
  transition: width 0.3s ease;
}

.question-counter {
  text-align: center;
  color: #666;
  margin-bottom: 25px;
  font-size: 14px;
}

.question-box {
  background: white;
  border-radius: 12px;
  padding: 35px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 30px;
  min-height: 200px;
}

.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4CAF50;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.question-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.question-text {
  font-size: 18px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 20px;
}

.source-info {
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.source-label {
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.source-link {
  color: #1976d2;
  text-decoration: none;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  transition: color 0.2s;
}

.source-link:hover {
  color: #1565c0;
  text-decoration: underline;
}

.external-icon {
  font-size: 12px;
}

.vote-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn {
  flex: 1;
  max-width: 200px;
  padding: 15px 25px;
  font-size: 18px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.btn:active {
  transform: translateY(0);
}

.btn-disagree {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
}

.btn-disagree:hover {
  background: linear-gradient(135deg, #c0392b, #a93226);
}

.btn-neutral {
  background: linear-gradient(135deg, #95a5a6, #7f8c8d);
}

.btn-neutral:hover {
  background: linear-gradient(135deg, #7f8c8d, #6c7a7b);
}

.btn-agree {
  background: linear-gradient(135deg, #27ae60, #229954);
}

.btn-agree:hover {
  background: linear-gradient(135deg, #229954, #1e8449);
}

@media (max-width: 768px) {
  .vote-buttons {
    flex-direction: column;
  }

  .btn {
    max-width: 100%;
  }

  .question-box {
    padding: 25px;
  }
}
</style>
