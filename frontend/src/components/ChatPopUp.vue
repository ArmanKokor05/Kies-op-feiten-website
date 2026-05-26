<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { DirectMessageService } from '@/services/DirectMessageService'
import { UserService } from '@/services/userService'
import type { ConversationDisplayDTO, ConversationDTO, SearchUserDTO, MessageDTO } from '../../types'
import { WebSocketService } from '@/services/WebSocketService'

interface ActiveConversation {
  conversationId: number|null;
  userId: number
  username: string
}

const emit = defineEmits<{
  (e: 'close'): void
}>()

const myUserId = ref<number | null>(null)

const wsService = new WebSocketService();
const topic = 'topic/conversation';

// Chat search
const searchQuery = ref('')

// People search
const showPeopleSearch = ref(false)
const peopleSearchQuery = ref('')
const users = ref<SearchUserDTO[]>([])
const loadingUsers = ref(false)
let debounceTimer: number | undefined

// Chats
const chats = ref<ConversationDisplayDTO[]>([])
const loadingChats = ref(false)

// Active conversation
const activeConversation = ref<ActiveConversation | null>(null)
const messages = ref<MessageDTO[]>([])
const newMessage = ref('')

// Ref for chat container
const chatContentRef = ref<HTMLElement | null>(null)

// Filter chats
const filteredChatsBySearchQuery = computed(() =>
  chats.value.filter(chat =>
    chat.conversationName.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
)

// People search
watch(peopleSearchQuery, (value) => {
  users.value = []
  if (!value || value.length < 2) return

  clearTimeout(debounceTimer)
  debounceTimer = window.setTimeout(async () => {
    loadingUsers.value = true
    users.value = await UserService.searchUsers(value)
    loadingUsers.value = false
  }, 300)
})

function connectToWebSocket() {
  const conversationId = activeConversation.value?.conversationId

  wsService.disconnect()

  if (conversationId === null) return

  wsService.connect(
    [`/${topic}/${conversationId}`],
    (msg) => {
      const body = JSON.parse(msg.body) as MessageDTO;
      messages.value.push(body);
      scrollToBottom();
    }
  );
}

const getConversations = async () => {
  loadingChats.value = true
  chats.value = await DirectMessageService.getConversations()
  loadingChats.value = false
}

function scrollToBottom() {
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
    }
  })
}

async function openChat(chat: ConversationDisplayDTO) {
  const conversation: ConversationDTO = await DirectMessageService.getConversationById(chat.conversationId)
  activeConversation.value = {
    conversationId: conversation.conversationId,
    userId: conversation.userId,
    username: conversation.conversationName
  }
  messages.value = await DirectMessageService.getMessages(chat.conversationId)

  scrollToBottom()

  connectToWebSocket()
}

async function startChatWithUser(user: SearchUserDTO) {
  const existingChat = chats.value.find(chat => chat.userId === user.userId)
  messages.value = []
  if (existingChat) {
    openChat(existingChat)
  } else {
    activeConversation.value = {
      conversationId: null,
      userId: user.userId,
      username: user.name
    }
  }
}

async function sendMessage() {
  if (!newMessage.value.trim() || !activeConversation.value) return

  console.log(activeConversation.value.userId)

  const message: MessageDTO =
        await DirectMessageService.createDirectMessage(activeConversation.value.userId,
                                                       newMessage.value)

  if (activeConversation.value.conversationId === null) {
    activeConversation.value.conversationId = message.conversationId;
    connectToWebSocket();
    messages.value = await DirectMessageService.getMessages(message.conversationId)
  }

  newMessage.value = '';
  scrollToBottom()
}

// Show people search
const handlePeopleSearch = () => showPeopleSearch.value = true
const closePeopleSearch = () => {
  showPeopleSearch.value = false
  peopleSearchQuery.value = ''
  users.value = []
  getConversations();
}

const closeActiveConversation = () => {
  wsService.disconnect();
  activeConversation.value = null
  messages.value = []
  newMessage.value = ''
  getConversations();
}

onMounted(async () => {
  myUserId.value = await UserService.getUserId();
  getConversations()
})
</script>

<template>
  <div class="chat-popup">
    <header class="chat-header">
      <button
        v-if="showPeopleSearch || activeConversation"
        class="back-btn"
        @click="activeConversation ? closeActiveConversation() : closePeopleSearch()"
      >
        ←
      </button>

      <strong>
        {{ activeConversation
          ? activeConversation.username
          : showPeopleSearch
          ? 'Search People'
          : 'Chats' }}
      </strong>

      <button class="close-btn" @click="emit('close')">✕</button>
    </header>

    <!-- Search bar -->
    <div class="chat-search" v-if="!activeConversation">
      <input
        v-if="!showPeopleSearch"
        v-model="searchQuery"
        type="text"
        placeholder="Search chats..."
      />
      <input
        v-else
        v-model="peopleSearchQuery"
        type="text"
        placeholder="Search people..."
      />
    </div>

    <!-- Content -->
    <div class="chat-content-wrapper">
      <!-- Active conversation -->
      <div v-if="activeConversation" class="conversation-container">
        <div class="chat-content" ref="chatContentRef">
          <div
            v-for="msg in messages"
            :key="msg.createdAt + msg.senderId"
            class="message"
            :class="msg.senderId === myUserId ? 'sender' : 'receiver'"
          >
            <strong>{{ msg.content }}</strong>
          </div>
          <p v-if="messages.length === 0" class="placeholder">
            No messages yet. Say hi! 👋
          </p>
        </div>

        <!-- Message input bar -->
        <div class="message-input-bar">
          <input
            type="text"
            v-model="newMessage"
            @keyup.enter="sendMessage"
            placeholder="Type a message..."
          />
          <button @click="sendMessage" :disabled="!newMessage.trim()">Send</button>
        </div>
      </div>

      <!-- Chats view -->
      <div v-else-if="!showPeopleSearch">
        <p v-if="loadingChats" class="placeholder">Loading chats...</p>
        <div
          v-else
          v-for="chat in filteredChatsBySearchQuery"
          :key="chat.conversationId"
          class="chat-item"
          @click="openChat(chat)"
        >
          <strong>{{ chat.conversationName }}</strong>
          <p v-if="chat.lastMessage">{{ chat.lastMessage }}</p>
        </div>

        <p v-if="!loadingChats && !filteredChatsBySearchQuery.length" class="placeholder">
          No chats found
        </p>
      </div>

      <!-- People search view -->
      <div v-else>
        <p v-if="loadingUsers" class="placeholder">Searching...</p>

        <div
          v-for="user in users"
          :key="user.userId"
          class="people-item"
        >
          <span>{{ user.name }}</span>
          <button @click="startChatWithUser(user)">Start Chat</button>
        </div>

        <p v-if="!loadingUsers && peopleSearchQuery.length >= 2 && users.length === 0" class="placeholder">
          No users found
        </p>
      </div>
    </div>

    <!-- Footer -->
    <div class="chat-footer" v-if="!showPeopleSearch && !activeConversation">
      <button class="search-people-btn" @click="handlePeopleSearch">
        ➕ Search People
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-popup {
  position: fixed;
  top: 5rem;
  right: 2rem;
  width: 320px;
  height: 420px;
  background: #ffffff;
  border-radius: 14px;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.18);
  z-index: 3000;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.95rem;
}
.back-btn, .close-btn {
  background: transparent;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  color: #64748b;
}
.back-btn:hover, .close-btn:hover { color: #0f172a; }

.chat-search {
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid #e5e7eb;
}
.chat-search input {
  width: 100%;
  padding: 0.45rem 0.6rem;
  border-radius: 8px;
  border: 1px solid #cbd5f5;
  font-size: 0.85rem;
}
.chat-search input:focus { outline: none; border-color: #1a3aff; }

.chat-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.conversation-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  height: 100%;
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem 0.75rem;
  display: flex;
  flex-direction: column;
}

.message {
  padding: 0.4rem 0.6rem;
  margin-bottom: 0.35rem;
  border-radius: 8px;
  max-width: 75%;
  word-wrap: break-word;
}

/* Sender messages (me) */
.message.sender {
  align-self: flex-end;
  background-color: #22c55e; /* green */
  color: #ffffff;
}

/* Receiver messages */
.message.receiver {
  align-self: flex-start;
  background-color: #e5e7eb; /* grey */
  color: #0f172a;
}

.placeholder {
  color: #94a3b8;
  font-size: 0.9rem;
  text-align: center;
  margin-top: 1rem;
}

.message-input-bar {
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem;
  border-top: 1px solid #e5e7eb;
  background: #fff;
}
.message-input-bar input {
  flex: 1;
  padding: 0.45rem 0.6rem;
  border-radius: 8px;
  border: 1px solid #cbd5f5;
  font-size: 0.85rem;
}
.message-input-bar input:focus { outline: none; border-color: #1a3aff; }
.message-input-bar button {
  padding: 0.45rem 0.6rem;
  border-radius: 8px;
  border: none;
  background: #1a3aff;
  color: #fff;
  cursor: pointer;
}
.message-input-bar button:hover { background: #0041c2; }

.chat-item, .people-item {
  padding: 0.5rem 0.6rem;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
}
.chat-item:hover { background: #f8fafc; }
.people-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #cbd5f5;
  border-radius: 6px;
  padding: 0.45rem 0.6rem;
}
.people-item button {
  background: #1a3aff;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.25rem 0.5rem;
  cursor: pointer;
}
.people-item button:hover { background: #0041c2; }

.chat-footer {
  padding: 0.5rem 0.75rem;
  border-top: 1px solid #e5e7eb;
}
.search-people-btn {
  width: 100%;
  padding: 0.45rem 0.6rem;
  background: #1a3aff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.85rem;
}
.search-people-btn:hover { background: #0041c2; }
</style>
