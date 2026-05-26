<script lang="ts" setup>
import { useRoute } from "vue-router";
import { DiscussionService } from "@/services/DiscussionService.ts";
import createChatService from "@/services/createChatService.ts";
import getChatsByDiscussionService from "@/services/getChatsByDiscussionService.ts";
import type { Discussion, Chat } from "../../types.ts";
import { onMounted, onUpdated, ref, watch } from "vue";
import TextAreaComponent from "@/components/TextAreaComponent.vue";
import PopUp from "@/components/PopUpComponent.vue";
import PaginationComponent from "@/components/PaginationComponent.vue";
import ListStructureComponent from "@/components/ListStructureComponent.vue";
import DiscussionListContentComponent from "@/components/DiscussionListContentComponent.vue";
import ChatListContentComponent from "@/components/ChatListContentComponent.vue";

const discussionService = new DiscussionService()

const route = useRoute()
const id = ref(Number(route.params.id))
const scrollToChatId = ref(route.query.scrollToChatId ? Number(route.query.scrollToChatId) : null)

const discussion = ref<Discussion | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);
const chatContent = ref('');
const submitting = ref(false);
const chats = ref<Chat[]>([]);
const loadingChats = ref(false);
const currentPage = ref(1);
const totalPages = ref(1);
const itemsPerPage = 5;
const targetChatFound = ref(false);
const isSearchingForChat = ref(false);

const popup = ref({
  show: false,
  message: '',
  type: 'info' as 'info' | 'success' | 'error'
})

function triggerPopup(message: string, type: 'info' | 'success' | 'error') {
  popup.value.show = true
  popup.value.message = message
  popup.value.type = type
  setTimeout(() => { popup.value.show = false }, 5000)
}

async function loadChats() {
  loadingChats.value = true;
  try {
    const response = await getChatsByDiscussionService.getChatsByDiscussion(id.value, currentPage.value - 1, itemsPerPage);
    chats.value = response.content || [];
    totalPages.value = response.totalPages || 1;

    console.log(`Page ${currentPage.value} chat IDs:`, chats.value.map(c => c.id));

    if (scrollToChatId.value && !targetChatFound.value && isSearchingForChat.value) {
      const chatInArray = chats.value.find(c => c.id === scrollToChatId.value);
      console.log(`Chat ${scrollToChatId.value} in chats array:`, !!chatInArray);

      if (!chatInArray && currentPage.value < totalPages.value) {
        currentPage.value++;
      } else if (!chatInArray && currentPage.value >= totalPages.value) {
        console.warn(`Chat ${scrollToChatId.value} not found in any page`);
        isSearchingForChat.value = false;
      }
    }
  } catch (err) {
    console.error('Failed to load chats:', err);
  } finally {
    loadingChats.value = false;
  }
}

async function scrollToTargetChat(): Promise<boolean> {
  if (!scrollToChatId.value) return false;

  const allChatElements = document.querySelectorAll('[id^="chat-"]');
  console.log(`DOM contains ${allChatElements.length} chat elements:`,
    Array.from(allChatElements).map(el => el.id));

  const chatElement = document.getElementById(`chat-${scrollToChatId.value}`);
  console.log(`Looking for #chat-${scrollToChatId.value}, found:`, !!chatElement);

  if (chatElement) {
    targetChatFound.value = true;
    isSearchingForChat.value = false;

    console.log(`Found chat ${scrollToChatId.value}, scrolling to it`);

    chatElement.scrollIntoView({
      behavior: 'smooth',
      block: 'center'
    });

    chatElement.classList.add('highlight-flash');
    setTimeout(() => {
      chatElement.classList.remove('highlight-flash');
    }, 2000);

    return true;
  }

  return false;
}

async function submitChat() {
  if (!chatContent.value.trim()) {
    triggerPopup('Reactie mag niet leeg zijn', 'error');
    return;
  }

  submitting.value = true;
  try {
    await createChatService.createChat(id.value, chatContent.value.trim());
    triggerPopup('Reactie succesvol geplaatst!', 'success');
    chatContent.value = '';
    currentPage.value = 1;
    targetChatFound.value = false;
    isSearchingForChat.value = false;
    await loadChats();
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err);
    triggerPopup(`Fout bij het plaatsen van reactie: ${message}`, 'error');
  } finally {
    submitting.value = false;
  }
}

watch(currentPage, async () => {
  await loadChats();
});

watch(() => route.query.scrollToChatId, (newScrollToChatId) => {
  if (newScrollToChatId) {
    console.log('Route query changed, new scrollToChatId:', newScrollToChatId);
    scrollToChatId.value = Number(newScrollToChatId);
    targetChatFound.value = false;
    isSearchingForChat.value = true;
    currentPage.value = 1;
    loadChats();
  }
});

onUpdated(() => {
  if (scrollToChatId.value && isSearchingForChat.value && !targetChatFound.value && !loadingChats.value) {
    console.log('onUpdated: Attempting to find and scroll to chat');
    scrollToTargetChat();
  }
});

onMounted(async () => {
  if (isNaN(id.value)) {
    error.value = "Invalid discussion ID";
    loading.value = false;
    return;
  }

  try {
    discussion.value = await discussionService.getDiscussionById(id.value);

    if (scrollToChatId.value) {
      isSearchingForChat.value = true;
      console.log(`Starting search for chat ${scrollToChatId.value}`);
    }

    await loadChats();
  } catch (err) {
    error.value = "Failed to load discussion";
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="contentContainer">
    <PopUp
      :visible="popup.show"
      :message="popup.message"
      :type="popup.type"
      @close="popup.show = false"
    />

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
    </div>

    <div v-else-if="error" class="error-message">{{ error }}</div>

    <div v-else-if="discussion" class="discussion-wrapper">
      <ListStructureComponent>
        <DiscussionListContentComponent
          :discussion="discussion"
          :cut="false"
        />
      </ListStructureComponent>
    </div>

    <div class="commentContainer">
      <div class="commentField">
        <TextAreaComponent
          v-model="chatContent"
          placeholder="Schrijf een reactie..."
          :rows="3"
          :disabled="submitting"
        />
        <button
          @click="submitChat"
          class="submit-chat-btn"
          :disabled="submitting || !chatContent.trim()"
        >
          {{ submitting ? 'Verzenden...' : 'Reageer' }}
        </button>
      </div>
    </div>

    <div v-if="loadingChats" class="loading-container">
      <div class="spinner"></div>
    </div>

    <div v-else-if="chats.length > 0" class="chats-section">
      <h3 class="chats-title">Reacties</h3>
      <div class="chats-list">
        <div
          v-for="chat in chats"
          :key="chat.id"
          :id="`chat-${chat.id}`"
          class="chat-wrapper"
        >
          <ListStructureComponent class="chat-item">
            <ChatListContentComponent :chat="chat" />
          </ListStructureComponent>
        </div>
      </div>

      <PaginationComponent
        v-if="totalPages > 1"
        :currentPage="currentPage"
        :totalPages="totalPages"
        @update:currentPage="currentPage = $event"
      />
    </div>

    <div v-else class="no-chats">
      <p>Nog geen reacties. Wees de eerste!</p>
    </div>
  </div>
</template>

<style scoped>
.contentContainer {
  display: flex;
  flex-direction: column;
  flex: 1;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
}

.discussion-wrapper {
  width: 90%;
}

.error-message {
  padding: 1rem;
  background: #fee;
  color: #c00;
  border-radius: 8px;
}

.submit-chat-btn {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background: #1a3aff;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-chat-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.submit-chat-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.chats-section {
  width: 90%;
  margin-top: 1rem;
}

.chats-title {
  color: inherit;
  margin-bottom: 1rem;
  font-size: 1.5rem;
}

.chats-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chat-wrapper {
  scroll-margin-top: 100px;
}

.chat-item {
  transition: background-color 0.3s ease;
}

.no-chats {
  width: 90%;
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 12px;
  color: #718096;
  margin-top: 1rem;
}

.commentContainer {
  margin-top: 1rem;
  width: 90%;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
}

.commentField {
  width: 100%;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  flex: 1;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.chat-wrapper.highlight-flash .chat-item {
  animation: highlight-pulse 2s ease-in-out;
}

.chat-wrapper.highlight-flash {
  animation: highlight-pulse 2s ease-in-out;
}

@keyframes highlight-pulse {
  0%, 100% {
    background-color: transparent;
  }
  50% {
    background-color: #fef3c7;
  }
}
</style>
