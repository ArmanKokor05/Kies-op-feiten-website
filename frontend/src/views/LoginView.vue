<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '../services/authService';

const router = useRouter();

const email = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

const handleLogin = async () => {
  error.value = '';

  // Validatie
  if (!email.value || !password.value) {
    error.value = 'Vul alle velden in';
    return;
  }

  loading.value = true;

  try {
    await authService.login(email.value, password.value);

    // Redirect naar home na succesvol inloggen
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
  <div class="login-container">
    <div class="login-card">
      <h1>Inloggen</h1>
      <p class="subtitle">Log in om verder te gaan</p>

      <form @submit.prevent="handleLogin">
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
            placeholder="Voer je wachtwoord in"
            required
          />
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Bezig met inloggen...' : 'Inloggen' }}
        </button>
      </form>

      <p class="register-link">
        Nog geen account?
        <router-link to="/register">Registreer hier</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.login-card {
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

.register-link {
  text-align: center;
  margin-top: 1.5rem;
  color: #64748b;
}

.register-link a {
  color: #1a3aff;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-card {
    padding: 2rem;
  }
}
</style>
