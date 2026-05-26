<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import ConfirmationModalComponent from '../components/ConfirmationModalComponent.vue'
import FormComponent from '../components/FormComponent.vue'
import DropdownComponent from '../components/DropdownComponent.vue'
import PaginationComponent from '../components/PaginationComponent.vue'
import AccountChatListContentComponent from '../components/AccountChatListContentComponent.vue'
import DiscussionListContentComponent from '../components/DiscussionListContentComponent.vue'
import ListStructureComponent from '../components/ListStructureComponent.vue'
import PopUp from '@/components/PopUpComponent.vue'
import deleteAccountService from "@/services/account/deleteAccountService.ts"
import editUsernameService from "@/services/account/editUsernameService.ts"
import accountChatService from "@/services/account/accountChatService.ts"
import accountDiscussionService from "@/services/account/accountDiscussionService.ts"
import getUserService from "@/services/account/getUserService.ts"
import type { InputField } from '../../types'

interface Chat {
  id: number
  discussion_id: number
  content: string
  upvotes: number
  downvotes: number
  created_at: string
  discussion_title: string
}

interface Discussion {
  id: number
  title: string
  content: string
  upvotes: number
  downvotes: number
  createdAt: string
  userName: string
}

const router = useRouter()

function extractUserIdFromToken(): number | null {
  const token = localStorage.getItem('token')
  if (!token) return null

  try {
    const parts = token.split('.')
    if (parts.length !== 3 || !parts[1]) return null

    const payload = JSON.parse(atob(parts[1]))
    return payload.sub ? parseInt(payload.sub, 10) : null
  } catch {
    return null
  }
}

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

const showDropdown = ref(false)
const dropdownRef = ref<HTMLDivElement | null>(null)

function handleClickOutside(event: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    showDropdown.value = false
  }
}

const itemsPerPage = 20

const myChats = ref<Chat[]>([])
const chatCurrentPage = ref(1)
const chatTotalPages = ref(1)
const chatTotalElements = ref(0)
const isLoadingChats = ref(false)

const myDiscussions = ref<Discussion[]>([])
const discussionsCurrentPage = ref(1)
const discussionsTotalPages = ref(1)
const discussionsTotalElements = ref(0)
const isLoadingDiscussions = ref(false)
const username = ref<string>('')
const userCreatedAt = ref<string>('')
const isLoadingUserInfo = ref(false)

const isDeleting = ref(false)
const isEditingName = ref(false)

async function loadUserInfo() {
  const userId = extractUserIdFromToken()

  if (!userId) {
    triggerPopup('Kan gebruiker niet verifiëren. Log opnieuw in.', 'error');
    return
  }

  isLoadingUserInfo.value = true

  try {
    const userInfo = await getUserService.getUserInfo(userId)
    username.value = userInfo.name
    userCreatedAt.value = new Date(userInfo.createdAt).toLocaleDateString('nl-NL', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens het ophalen van gebruikersinfo: ${message}`, 'error')
    username.value = 'Onbekend'
  } finally {
    isLoadingUserInfo.value = false
  }
}

async function loadMyChats() {
  const userId = extractUserIdFromToken()

  if (!userId) {
    triggerPopup('Kan gebruiker niet verifiëren. Log opnieuw in.', 'error');
    return
  }

  isLoadingChats.value = true

  try {
    const response = await accountChatService.getChatsOfUser(userId, chatCurrentPage.value - 1, itemsPerPage)
    myChats.value = response.content
    chatTotalPages.value = response.totalPages
    chatTotalElements.value = response.totalElements
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens het ophalen van je chats: ${message}`, 'error')
  } finally {
    isLoadingChats.value = false
  }
}

async function loadMyDiscussions() {
  const userId = extractUserIdFromToken()

  if (!userId) {
    triggerPopup('Kan gebruiker niet verifiëren. Log opnieuw in.', 'error');
    return
  }

  isLoadingDiscussions.value = true

  try {
    const response = await accountDiscussionService.getDiscussionsOfUser(userId, discussionsCurrentPage.value - 1, itemsPerPage)
    myDiscussions.value = response.content
    discussionsTotalPages.value = response.totalPages
    discussionsTotalElements.value = response.totalElements
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens het ophalen van je discussies: ${message}`, 'error')
  } finally {
    isLoadingDiscussions.value = false
  }
}

const selectedView = ref<string>('Mijn discussies')
const viewOptions = ['Mijn discussies', 'Mijn reacties']

watch(selectedView, (val) => {
  if (val === "Mijn reacties") {
    chatCurrentPage.value = 1
    loadMyChats()
  }
  else if (val === "Mijn discussies") {
    discussionsCurrentPage.value = 1
    loadMyDiscussions()
  }
})

watch(chatCurrentPage, () => {
  if (selectedView.value === "Mijn reacties") {
    loadMyChats()
  }
})

watch(discussionsCurrentPage, () => {
  if (selectedView.value === "Mijn discussies") {
    loadMyDiscussions()
  }
})

function goToDiscussion(discussionId: number) {
  router.push(`/discussies/${discussionId}`)
}

const showChangeNameModal = ref(false)
const newNameFields = ref<InputField[]>([
  { key: 'Nieuwe naam', value: '' }
])

function handleChangeName() {
  showDropdown.value = false
  showChangeNameModal.value = true
}

async function handleSubmitChangeName(payload: { submittableInputFields: InputField[], submittableTextFields: any[] }) {
  const newName = payload.submittableInputFields[0]?.value
  const userId = extractUserIdFromToken()

  if (!newName) {
    triggerPopup('Voer alstublieft een geldige naam in', 'error');
    return
  }

  if (!userId) {
    triggerPopup('Kan gebruiker niet verifiëren. Log opnieuw in.', 'error');
    return
  }

  isEditingName.value = true

  try {
    await editUsernameService.changeName(userId, newName)
    triggerPopup('Je naam is aangepast! Je word nu doorgestuurd naar de home pagina...', 'success')
    showChangeNameModal.value = false
    setTimeout(() => { window.location.href = '/' }, 2000)
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens het veranderen van je naam: ${message}`, 'error')
  } finally {
    isEditingName.value = false
  }
}

const showConfirmModal = ref(false)

function handleDeleteClick() {
  showDropdown.value = false
  showConfirmModal.value = true
}

async function confirmDelete() {
  showConfirmModal.value = false
  const userId = extractUserIdFromToken()

  if (!userId) {
    triggerPopup('Kan gebruiker niet verifiëren. Log opnieuw in.', 'error');
    return
  }

  isDeleting.value = true

  try {
    await deleteAccountService.deleteAccount(userId)
    triggerPopup('Account is verwijderd! Je word nu doorgestuurd naar de home pagina...', 'success')
    localStorage.clear()
    setTimeout(() => { window.location.href = '/' }, 2000)
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err)
    triggerPopup(`Er was een error tijdens verwijderen van je account: ${message}`, 'error')
  } finally {
    isDeleting.value = false
  }
}

function cancelDelete() {
  showConfirmModal.value = false
}

onMounted(() => {
  loadUserInfo()
  loadMyDiscussions()

  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
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

    <section class="user-info-section">
      <div>
        <h2>Welkom {{ username }}</h2>
        <p v-if="userCreatedAt" class="member-since">Lid sinds: {{ userCreatedAt }}</p>
      </div>
      <div class="dropdown-container" ref="dropdownRef">
        <button class="dropdown-toggle" @click="showDropdown = !showDropdown" :disabled="isDeleting || isEditingName">
          <span class="setting">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path d="M259.1 73.5C262.1 58.7 275.2 48 290.4 48L350.2 48C365.4 48 378.5 58.7 381.5 73.5L396 143.5C410.1 149.5 423.3 157.2 435.3 166.3L503.1 143.8C517.5 139 533.3 145 540.9 158.2L570.8 210C578.4 223.2 575.7 239.8 564.3 249.9L511 297.3C511.9 304.7 512.3 312.3 512.3 320C512.3 327.7 511.8 335.3 511 342.7L564.4 390.2C575.8 400.3 578.4 417 570.9 430.1L541 481.9C533.4 495 517.6 501.1 503.2 496.3L435.4 473.8C423.3 482.9 410.1 490.5 396.1 496.6L381.7 566.5C378.6 581.4 365.5 592 350.4 592L290.6 592C275.4 592 262.3 581.3 259.3 566.5L244.9 496.6C230.8 490.6 217.7 482.9 205.6 473.8L137.5 496.3C123.1 501.1 107.3 495.1 99.7 481.9L69.8 430.1C62.2 416.9 64.9 400.3 76.3 390.2L129.7 342.7C128.8 335.3 128.4 327.7 128.4 320C128.4 312.3 128.9 304.7 129.7 297.3L76.3 249.8C64.9 239.7 62.3 223 69.8 209.9L99.7 158.1C107.3 144.9 123.1 138.9 137.5 143.7L205.3 166.2C217.4 157.1 230.6 149.5 244.6 143.4L259.1 73.5zM320.3 400C364.5 399.8 400.2 363.9 400 319.7C399.8 275.5 363.9 239.8 319.7 240C275.5 240.2 239.8 276.1 240 320.3C240.2 364.5 276.1 400.2 320.3 400z"/></svg>
          </span>
        </button>
        <div v-show="showDropdown" class="dropdown-menu">
          <button class="dropdown-item" @click="handleChangeName" :disabled="isEditingName">Verander naam</button>
          <button class="dropdown-item delete-item" @click="handleDeleteClick" :disabled="isDeleting">Verwijder account</button>
        </div>
      </div>
    </section>

    <section class="account-content-wrapper">
      <section class="discussion-section">
        <div class="discussion-header">
          <h2>Mijn activiteiten</h2>
          <DropdownComponent
            label="Selecteer weergave"
            :options="viewOptions"
            v-model="selectedView"
            class="my-dropdown"
            style="width: 100%; color: inherit;"
          />
        </div>

        <div class="discussion-container">
          <div v-if="selectedView === 'Mijn discussies'" class="discussion-view">
            <h3>Mijn discussies</h3>

            <div v-if="isLoadingDiscussions" class="loading-state">
              <p>Laden...</p>
            </div>

            <div v-else-if="myDiscussions.length === 0" class="empty-state">
              <p>Je hebt nog geen discussies aangemaakt.</p>
            </div>

            <div v-else class="list-grid">
              <ListStructureComponent
                v-for="discussion in myDiscussions"
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
              v-if="discussionsTotalPages > 1 && !isLoadingDiscussions"
              :currentPage="discussionsCurrentPage"
              :totalPages="discussionsTotalPages"
              @update:currentPage="discussionsCurrentPage = $event"
            />
          </div>

          <div v-if="selectedView === 'Mijn reacties'" class="discussion-view">
            <h3>Mijn reacties</h3>

            <div v-if="isLoadingChats" class="loading-state">
              <p>Laden...</p>
            </div>

            <div v-else-if="myChats.length === 0" class="empty-state">
              <p>Je hebt nog geen reacties geplaatst.</p>
            </div>

            <div v-else class="list-grid">
              <ListStructureComponent
                v-for="resp in myChats"
                :key="resp.id"
                @click="goToDiscussion(resp.discussion_id)"
              >
                <AccountChatListContentComponent
                  :response="resp"
                />
              </ListStructureComponent>
            </div>

            <PaginationComponent
              v-if="chatTotalPages > 1 && !isLoadingChats"
              :currentPage="chatCurrentPage"
              :totalPages="chatTotalPages"
              @update:currentPage="chatCurrentPage = $event"
            />
          </div>
        </div>
      </section>

      <section class="stemwijzer-section">
        <h2>Mijn stemwijzer resultaten</h2>

        <div class="stemwijzer-view">

        </div>
      </section>
    </section>

    <ConfirmationModalComponent
      :show="showConfirmModal"
      title="Verwijder account"
      message="Weet je zeker dat je jouw account wilt verwijderen? Deze actie kan je niet terugdraaien."
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />

    <FormComponent
      @close="showChangeNameModal = false"
      @submit="handleSubmitChangeName"
      :show="showChangeNameModal"
      :inputFields="newNameFields"
      :submitButton="isEditingName ? 'Bezig...' : 'Bevestigen'"
      cancelButton="Annuleren"
      width="33.3vw"
      :disabled="isEditingName"
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
  margin-top: 3rem;
}

.user-info-section {
  padding: 2rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 20px;
}

.user-info-section h2{
  font-weight: bold;
}

.member-since {
  color: inherit;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.dropdown-container {
  position: relative;
}

.dropdown-toggle {
  background: none;
  border: none;
  font-size: 35px;
  cursor: pointer;
  color: inherit;
  transition: opacity 0.2s;
}

.setting svg {
  width: 25px;
  height: 25px;
  fill: currentColor;
}

.setting {
  color: inherit;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: #dbeafe;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 250px;
  z-index: 10;
}

.dropdown-item {
  display: block;
  width: 100%;
  text-align: left;
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
  color: #2563eb;
}

.dropdown-item:hover {
  background-color: #c2dcfd;
}

.delete-item {
  color: #dc3545;
}

.discussion-section,
.stemwijzer-section {
  flex: 1;
}

.account-content-wrapper {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.discussion-section {
  padding: 2rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

.stemwijzer-section {
  padding: 2rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

.discussion-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 2rem;
}

.discussion-container {
  min-height: 300px;
}

.discussion-view {
  animation: fadeIn 0.3s ease-in-out;
}

.list-grid {
  display: grid;
  gap: 1rem;
  margin-top: 1.5rem;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 0.5rem;
}

.list-grid::-webkit-scrollbar {
  width: 10px;
}

.list-grid::-webkit-scrollbar-track {
  background: #DBEAFE;
  border-radius: 4px;
}

.list-grid::-webkit-scrollbar-thumb {
  background: #1a3aff;
  border-radius: 4px;
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

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 1024px) {
  .main-content {
    margin-top: 2rem;
    padding: 1rem;
  }

  .discussion-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .account-content-wrapper {
    flex-direction: column;
    gap: 1rem;
  }

  .discussion-section,
  .stemwijzer-section {
    width: 100%;
    max-width: none;
  }
}
</style>
