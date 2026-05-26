<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import webLogo from './components/webLogo.vue';
import ChatPopup from './components/ChatPopUp.vue';
import notificationComponent from './components/notificationComponent.vue';
import authService from './services/authService';

const router = useRouter();
const isLoggedIn = ref(false);
const showChat = ref(false);

// Dropdown state
const openDropdown = ref<string | null>(null);

// later replace with backend data
const chats = ref([]);

// Check login status bij laden
onMounted(() => {
  isLoggedIn.value = authService.isAuthenticated();
});

// Update login status na route change
router.afterEach(() => {
  isLoggedIn.value = authService.isAuthenticated();
  showChat.value = false; // close chat on navigation
  openDropdown.value = null; // close dropdowns on navigation
});

const handleLogout = () => {
  authService.logout();
  isLoggedIn.value = false;
  showChat.value = false;
  router.push('/');
};

const toggleChat = () => {
  showChat.value = !showChat.value;
};

const openChat = (chatId: string | number) => {
  console.log('Open chat:', chatId);
  // later: open DM modal
};

const toggleDropdown = (dropdown: string) => {
  openDropdown.value = openDropdown.value === dropdown ? null : dropdown;
};

const closeDropdowns = () => {
  openDropdown.value = null;
};
</script>

<template>
  <div class="app-container" @click="closeDropdowns">
    <router-link to="/">
      <webLogo />
    </router-link>

    <nav class="navbar">
      <ul class="nav-links">
        <li><router-link to="/over-ons">Over ons</router-link></li>

        <!-- Ontdek Dropdown -->
        <li class="dropdown" @click.stop>
          <a @click="toggleDropdown('ontdek')" class="dropdown-toggle">
            Ontdek ▾
          </a>
          <ul v-if="openDropdown === 'ontdek'" class="dropdown-menu">
            <li><router-link to="/kaart">Kaart</router-link></li>
            <li><router-link to="/stembureaus">Stembureaus</router-link></li>
            <li><router-link to="/kandidaten">Kandidaten</router-link></li>
          </ul>
        </li>

        <!-- Tools Dropdown -->
        <li class="dropdown" @click.stop>
          <a @click="toggleDropdown('tools')" class="dropdown-toggle">
            Tools ▾
          </a>
          <ul v-if="openDropdown === 'tools'" class="dropdown-menu">
            <li><router-link to="/stemwijzer">Stemwijzer</router-link></li>
            <li><router-link to="/CoalitieMaker">CoalitieMaker</router-link></li>
          </ul>
        </li>

        <li><router-link to="/discussies">Discussies</router-link></li>

        <!-- Account/Login Dropdown -->
        <li class="dropdown" @click.stop>
          <a @click="toggleDropdown('account')" class="dropdown-toggle">
            {{ isLoggedIn ? 'Account' : 'Inloggen' }} ▾
          </a>
          <ul v-if="openDropdown === 'account'" class="dropdown-menu">
            <template v-if="!isLoggedIn">
              <li><router-link to="/login">Login</router-link></li>
              <li><router-link to="/register">Registreer</router-link></li>
            </template>
            <template v-else>
              <li><router-link to="/account">Mijn Account</router-link></li>
              <li><a @click="toggleChat" style="cursor:pointer">Chat</a></li>
              <li><a @click="handleLogout" class="logout-link">Uitloggen</a></li>
            </template>
          </ul>
        </li>

        <!-- notifications -->
        <li v-if="isLoggedIn" @click.stop>
          <notificationComponent />
        </li>

      </ul>
    </nav>

    <ChatPopup
        v-if="showChat"
        :chats="chats"
        @close="showChat = false"
        @select-chat="openChat"
    />

    <router-view />

    <footer class="footer">
      <section class="waarom-section">
        <h2>Waarom KiesOpFeiten?</h2>
        <p>
          Social media en nieuwssites staan vol met desinformatie over politiek.
          Wij controleren alle <br />
          feiten, vermijden dubbele onze bronnen en helpen jongeren een
          weloverwogen<br />
          stemkeuze te maken
        </p>
      </section>
    </footer>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
}

.navbar {
  position: absolute;
  top: 2rem;
  right: 2rem;
  z-index: 1000;
}

.nav-links {
  display: flex;
  gap: 2rem;
  list-style: none;
  margin: 0;
  padding: 0;
  align-items: center;
}

.nav-links > li {
  position: relative;
}

.nav-links a {
  text-decoration: none;
  font-weight: 600;
  color: #1a3aff;
  font-size: 1rem;
  transition: color 0.2s;
  cursor: pointer;
}

.nav-links a:hover {
  color: #0041c2;
}

.nav-links a.router-link-active {
  color: #0041c2;
  border-bottom: 2px solid #0041c2;
}

.logout-link {
  cursor: pointer;
}

/* Dropdown Styles */
.dropdown {
  position: relative;
}

.dropdown-toggle {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  user-select: none;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.5rem;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  list-style: none;
  padding: 0.5rem 0;
  min-width: 180px;
  z-index: 1001;
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-menu li {
  margin: 0;
}

.dropdown-menu a {
  display: block;
  padding: 0.75rem 1.25rem;
  color: #1a3aff;
  font-weight: 500;
  font-size: 0.95rem;
  transition: background-color 0.2s, color 0.2s;
  border: none;
  border-radius: 0;
}

.dropdown-menu a:hover {
  background-color: #f0f4ff;
  color: #0041c2;
}

.dropdown-menu a.router-link-active {
  background-color: #e6edff;
  border: none;
}

.footer {
  background-color: #f5f5f5;
  text-align: center;
  margin-top: auto;
  border-top: 1px solid #e0e0e0;
}

.footer p {
  color: #334155;
  margin: 0;
  font-size: 20px;
}

.waarom-section {
  background-color: #D9D9D9;
  padding: 3rem 4rem;
  text-align: left;
  border-radius: 8px;
}

.waarom-section h2 {
  color: #1a3aff;
  font-size: 1.8rem;
  margin-bottom: 1rem;
  font-weight: 600;
  text-align: center;
}

.waarom-section p {
  color: #334155;
  font-size: 1rem;
  line-height: 1.6;
  text-align: center;
  font-size: 16px;
}

@media (max-width: 768px) {
  .logo {
    width: 140px;
    top: 1rem;
    left: 1rem;
  }

  .navbar {
    position: static;
    margin-top: 5rem;
    text-align: center;
    padding: 1rem;
  }

  .nav-links {
    flex-direction: column;
    gap: 1rem;
    align-items: center;
  }

  .dropdown-menu {
    position: static;
    box-shadow: none;
    border: none;
    margin-top: 0.5rem;
    background: transparent;
  }

  .dropdown-menu a {
    text-align: center;
  }
}

@media (max-width: 480px) {
  .logo {
    width: 120px;
  }
}
</style>
