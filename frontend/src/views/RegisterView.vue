<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '../services/authService';

const router = useRouter();

const name = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const error = ref('');
const loading = ref(false);

const handleRegister = async () => {
  error.value = '';

  // Validatie
  if (!name.value || !email.value || !password.value || !confirmPassword.value) {
    error.value = 'Vul alle velden in';
    return;
  }

  if (password.value.length < 8) {
    error.value = 'Wachtwoord moet minimaal 8 tekens bevatten';
    return;
  }

  if (password.value !== confirmPassword.value) {
    error.value = 'Wachtwoorden komen niet overeen';
    return;
  }

  loading.value = true;

  try {
    await authService.register({
      name: name.value,
      email: email.value,
      password: password.value
    });

    // Redirect naar home na succesvol registreren
    router.push('/');
  } catch (err: any) {
    if (err.response?.data) {
      error.value = err.response.data;
    } else {
      error.value = 'Er is iets misgegaan. Probeer het opnieuw.';
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <h1>Registreren</h1>
      <p class="subtitle">Maak een account aan</p>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">Naam</label>
          <input
            id="name"
            v-model="name"
            type="text"
            placeholder="Jouw naam"
            required
          />
        </div>

        <div class="form-group">
          <label for="email">E-mailadres</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="naam@voorbeeld.nl"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Wachtwoord</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Minimaal 8 tekens"
            required
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">Bevestig wachtwoord</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            placeholder="Herhaal je wachtwoord"
            required
          />
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Bezig met registreren...' : 'Registreren' }}
        </button>
      </form>

      <p class="login-link">
        Heb je al een account?
        <router-link to="/login">Log hier in</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.register-card {
  background: white;
  padding: 3rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
}

h1 {
  color: #1a3aff;
  font-size: 2rem;
  margin-bottom: 0.5rem;
  text-align: center;
}

.subtitle {
  color: #64748b;
  text-align: center;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  color: #334155;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input:focus {
  outline: none;
  border-color: #1a3aff;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.btn-primary {
  width: 100%;
  padding: 0.875rem;
  background-color: #1a3aff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0041c2;
}

.btn-primary:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
  color: #64748b;
}

.login-link a {
  color: #1a3aff;
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-card {
    padding: 2rem;
  }
}
</style>
