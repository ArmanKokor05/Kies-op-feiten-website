<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import {API_BASE_URL} from "@/config/api.config";

const router = useRouter();

const getWebSocketUrl = () => {
  return API_BASE_URL + '/ws-notifications';
};

interface Notification {
  id: number;
  message: string;
  createdAt: string;
  isRead: boolean;
  actionUrl: string;
}

const showDropdown = ref(false);
const notificationCount = ref(0);
const notifications = ref<Notification[]>([]);
let stompClient: Client | null = null;

const toggleDropdown = (event: Event) => {
  event.stopPropagation();
  showDropdown.value = !showDropdown.value;
};

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

const getUserIdFromToken = (): string | null => {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const parts = token.split('.');
    if (parts.length !== 3 || !parts[1]) return null;

    const payload = JSON.parse(atob(parts[1]));
    return payload.userId || payload.sub || payload.id || null;
  } catch (error) {
    console.error('Failed to decode token:', error);
    return null;
  }
};

const loadNotifications = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/notifications`, {
      headers: getAuthHeaders()
    });
    notifications.value = response.data.data;
  } catch (error) {
    console.error('Failed to load notifications:', error);
  }
};

const loadUnreadCount = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/notifications/unread/count`, {
      headers: getAuthHeaders()
    });
    notificationCount.value = response.data.data;
  } catch (error) {
    console.error('Failed to load unread count:', error);
  }
};

const handleNotificationClick = async (notification: Notification) => {
  await markAsRead(notification.id);

  showDropdown.value = false;

  if (notification.actionUrl) {
    const chatIdMatch = notification.actionUrl.match(/#chat-(\d+)/);
    const chatId = chatIdMatch ? chatIdMatch[1] : null;

    const urlParts = notification.actionUrl.split('#');
    const basePath = urlParts[0] || notification.actionUrl;

    if (chatId) {
      await router.push({
        path: basePath,
        query: { scrollToChatId: chatId }
      });
    } else {
      await router.push(basePath);
    }
  }
};

const markAsRead = async (id: number) => {
  try {
    const notification = notifications.value.find(n => n.id === id);
    if (notification && !notification.isRead) {
      notification.isRead = true;
      notificationCount.value = Math.max(0, notificationCount.value - 1);
    }

    await axios.patch(`${API_BASE_URL}/notifications/${id}/read`, {}, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    console.error('Failed to mark notification as read:', error);
    await loadNotifications();
    await loadUnreadCount();
  }
};

const markAllAsRead = async () => {
  try {
    notifications.value.forEach(notification => {
      notification.isRead = true;
    });
    notificationCount.value = 0;

    await axios.patch(`${API_BASE_URL}/notifications/read-all`, {}, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    console.error('Failed to mark all as read:', error);
    await loadNotifications();
    await loadUnreadCount();
  }
};

const deleteAll = async () => {
  try {
    await axios.delete(`${API_BASE_URL}/notifications/delete-all`, {
      headers: getAuthHeaders()
    });
    notifications.value = [];
    notificationCount.value = 0;
    showDropdown.value = false;
  } catch (error) {
    console.error('Failed to delete all notifications:', error);
  }
};

const formatTime = (timestamp: string) => {
  const date = new Date(timestamp);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffInSeconds < 60) return 'Zojuist';
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m geleden`;
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}u geleden`;
  return `${Math.floor(diffInSeconds / 86400)}d geleden`;
};

const connectWebSocket = () => {
  const userId = getUserIdFromToken();
  console.log('User ID from token:', userId);
  if (!userId) {
    console.error('No user ID found, cannot connect to WebSocket');
    return;
  }

  const token = localStorage.getItem('token');
  if (!token) {
    console.error('No JWT token found, cannot connect to WebSocket');
    return;
  }

  const wsUrl = getWebSocketUrl() + '?token=' + encodeURIComponent(token);
  console.log('Connecting to WebSocket with token');

  stompClient = new Client({
    webSocketFactory: () => new SockJS(wsUrl),
    debug: (str) => {
      console.log('STOMP:', str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  stompClient.onConnect = () => {
    console.log('WebSocket connected successfully');

    stompClient?.subscribe(`/user/queue/notifications`, (message) => {
      console.log('Received notification:', message.body);

      try {
        const notification = JSON.parse(message.body);

        notifications.value.unshift(notification);

        notificationCount.value++;

        console.log('Notification added to UI');
      } catch (error) {
        console.error('Failed to parse notification:', error);
      }
    });
  };

  stompClient.onStompError = (frame) => {
    console.error('STOMP error:', frame);
  };

  stompClient.onWebSocketError = (event) => {
    console.error('WebSocket error:', event);
  };

  stompClient.activate();
};

const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    console.log('WebSocket disconnected');
  }
};

onMounted(() => {
  loadNotifications();
  loadUnreadCount();
  connectWebSocket();
});

onUnmounted(() => {
  disconnectWebSocket();
});
</script>

<template>
  <div class="notification-container" @click.stop>
    <button class="notification-bell" @click="toggleDropdown">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
      </svg>
      <span v-if="notificationCount > 0" class="notification-badge">
        {{ notificationCount > 99 ? '99+' : notificationCount }}
      </span>
    </button>

    <div v-if="showDropdown" class="notification-dropdown">
      <div class="dropdown-header">
        <h3>Meldingen</h3>
        <button
          v-if="notifications.length > 0 && notificationCount > 0"
          @click="markAllAsRead"
          class="mark-read-btn"
        >
          Alles gelezen markeren
        </button>
      </div>

      <div class="notification-list">
        <div
          v-for="notification in notifications"
          :key="notification.id"
          class="notification-item"
          :class="{ 'unread': !notification.isRead }"
          @click="handleNotificationClick(notification)"
        >
          <div class="notification-content">
            <p class="notification-message">{{ notification.message }}</p>
            <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
          </div>
          <div v-if="!notification.isRead" class="unread-indicator"></div>
        </div>

        <div v-if="notifications.length === 0" class="no-notifications">
          <p>Geen meldingen</p>
        </div>
      </div>

      <div v-if="notifications.length > 0" class="dropdown-footer">
        <button @click="deleteAll" class="clear-all-btn">
          verwijder alle meldingen
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notification-container {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.notification-bell {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  color: #1a3aff;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-bell:hover {
  color: #0041c2;
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #ff4444;
  color: white;
  border-radius: 10px;
  padding: 0.125rem 0.375rem;
  font-size: 0.75rem;
  font-weight: 600;
  min-width: 18px;
  text-align: center;
}

.notification-dropdown {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  width: 360px;
  max-height: 500px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1001;
  display: flex;
  flex-direction: column;
}

.dropdown-header {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-header h3 {
  margin: 0;
  color: #1a3aff;
  font-size: 1.1rem;
  font-weight: 600;
}

.mark-read-btn {
  background: none;
  border: none;
  color: #1a3aff;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  transition: color 0.2s;
}

.mark-read-btn:hover {
  color: #0041c2;
  text-decoration: underline;
}

.notification-list {
  overflow-y: auto;
  max-height: 360px;
}

.notification-item {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.notification-item:hover {
  background-color: #f8f9ff;
}

.notification-item.unread {
  background-color: #f0f4ff;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-message {
  margin: 0 0 0.25rem 0;
  color: #334155;
  font-size: 0.9rem;
  line-height: 1.4;
}

.notification-time {
  color: #94a3b8;
  font-size: 0.8rem;
}

.unread-indicator {
  width: 8px;
  height: 8px;
  background-color: #1a3aff;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 0.5rem;
}

.no-notifications {
  padding: 3rem 1.25rem;
  text-align: center;
}

.no-notifications p {
  color: #94a3b8;
  margin: 0;
  font-size: 0.95rem;
}

.dropdown-footer {
  padding: 0.75rem 1.25rem;
  border-top: 1px solid #e0e0e0;
  text-align: center;
}

.clear-all-btn {
  background: none;
  border: none;
  color: #ff4444;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.5rem;
  transition: color 0.2s;
  font-weight: 500;
}

.clear-all-btn:hover {
  color: #cc0000;
  text-decoration: underline;
}

:deep(.highlight-flash) {
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
