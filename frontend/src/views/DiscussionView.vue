<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import ButtonComponent from '@/components/ButtonComponent.vue'
import Form from '@/components/FormComponent.vue'
import PopUp from '@/components/PopUpComponent.vue'
import SearchBarComponent from '@/components/SearchBarComponent.vue'
import ListStructureComponent from '@/components/ListStructureComponent.vue'
import DiscussionListContentComponent from '@/components/DiscussionListContentComponent.vue'
import PaginationComponent from '@/components/PaginationComponent.vue'
import { DiscussionService } from '@/services/DiscussionService'
import FindAllDiscussionsService from "@/services/FindAllDiscussionsService"
import discussionApiService from '@/services/discussions/searchDiscussionsService'
import type { InputField, TextArea } from '../../types'
import {useRouter} from "vue-router";

const router = useRouter()

interface Discussion {
  id: number
  title: string
  content: string
  upvotes: number
  downvotes: number
  createdAt: string
  userName: string
}

const inputFields = ref<InputField[]>([{ key: 'Titel', value: '' }])
const textArea = ref<TextArea[]>([{ key: 'Inhoud van discussie', value: '', rows: 20 }])
const discussionService = new DiscussionService()

const modalOpen = ref(false)
const discussions = ref<Discussion[]>([])
const isLoading = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const itemsPerPage = 20
const titleSearch = ref('')
let debounceTimer: ReturnType<typeof setTimeout> | null = null
const debounceDelay = 300

const popup = ref({
  show: false,
  message: '',
  type: 'info'
})

function triggerPopup(message: string, type: 'info' | 'success' | 'error') {
  popup.value.show = true
  popup.value.message = message
  popup.value.type = type
  setTimeout(() => { popup.value.show = false }, 10000)
}

function goToDiscussion(discussionId: number) {
  router.push(`/discussies/${discussionId}`)
}

async function loadDiscussions() {
  isLoading.value = true
  try {
    const response = await FindAllDiscussionsService.getAllDiscussions(currentPage.value - 1, itemsPerPage)
    discussions.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens het laden van de discussies: ${message}`, 'error')
    discussions.value = []
  } finally {
    isLoading.value = false
  }
}

async function searchDiscussions() {
  isLoading.value = true
  try {
    const response = await discussionApiService.searchDiscussions(titleSearch.value,currentPage.value - 1, itemsPerPage)
    discussions.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (err) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens het zoeken van de discussies: ${message}`, 'error')
    discussions.value = []
  } finally {
    isLoading.value = false
  }
}

async function handleSearch() {
  if (currentPage.value === 1) {
    await searchDiscussions()
  } else {
    currentPage.value = 1
  }
}

watch(currentPage, async () => {
  await searchDiscussions()
})

watch(titleSearch, () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, debounceDelay)
})

async function handleSubmit(payload: { submittableInputFields: InputField[], submittableTextFields: TextArea[] }) {
  const title = payload.submittableInputFields[0]?.value
  const content = payload.submittableTextFields[0]?.value

  if (!title || !content) {
    popup.value.show = true
    popup.value.message = "Titel of inhoud mag niet leeg zijn"
    popup.value.type = 'error'
    return
  }

  try {
    const response = await discussionService.createDiscussion(title, content)
    popup.value.show = true
    popup.value.message = response.message || "Discussion created!"
    popup.value.type = 'success'
    modalOpen.value = false

    if (inputFields.value[0]) {
      inputFields.value[0].value = ''
    }
    if (textArea.value[0]) {
      textArea.value[0].value = ''
    }

    await loadDiscussions()
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    popup.value.show = true
    popup.value.message = message
    popup.value.type = 'error'
  }
}

onMounted(() => {
  loadDiscussions()
})
</script>

<template>
  <main class="main-content">
    <PopUp
      :visible="popup.show"
      :message="popup.message"
      :type="popup.type"
      @close="popup.show = false"
    />

    <div class="create-discussion-button">
      <ButtonComponent
        buttonName="creëer discussie"
        buttonClass="create-discussion-btn"
        @click="modalOpen = true"
      />
    </div>

    <Form
      @close="modalOpen = false"
      @submit="handleSubmit"
      :show="modalOpen"
      :inputFields="inputFields"
      :textAreas="textArea"
      submitButton="Versturen"
      cancelButton="Annuleren"
      width="33.3vw"
    />

    <div class="discussion-header">
      <h2>Discussies</h2>

      <SearchBarComponent
        v-model="titleSearch"
        placeholder="Zoek op titel..."
      />
    </div>

    <div v-if="isLoading" class="loading-state">
      <p>Laden...</p>
    </div>

    <div v-else-if="discussions.length === 0 && titleSearch" class="empty-state">
      <p>Geen discussies gevonden met de naam "{{ titleSearch }}"</p>
    </div>

    <div v-else-if="discussions.length === 0" class="empty-state">
      <p>Geen discussies gevonden</p>
    </div>

    <div v-else class="list-grid">
      <ListStructureComponent
        v-for="discussion in discussions"
        :key="discussion.id"
        @click="goToDiscussion(discussion.id)"
      >
        <DiscussionListContentComponent
          :discussion="discussion"
          :cut="true"
        />
      </ListStructureComponent>
    </div>

    <PaginationComponent
      v-if="totalPages > 1 && !isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      @update:currentPage="currentPage = $event"
    />
  </main>
</template>

<style scoped>
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
}

.discussion-header {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.list-grid {
  display: grid;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-right: 0.5rem;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  color: #6b7280;
  margin-top: 1.5rem;
}

.loading-state {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  color: #6b7280;
  margin-top: 1.5rem;
}

@media (max-width: 768px) {
  .main-content {
    margin-top: 2rem;
    padding: 1rem;
  }
}
</style>

