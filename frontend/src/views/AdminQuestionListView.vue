<script setup lang="ts">
import { ref } from "vue";
import {API_BASE_URL} from "@/config/api.config.ts";

const title = ref("");
const description = ref("");

const alertMessage = ref("");
const showAlert = ref(false);

const showConfirmModal = ref(false);

// Show the alert and for how long
function triggerAlert(message: string) {
  alertMessage.value = message;
  showAlert.value = true;
  setTimeout(() => {
    showAlert.value = false;
  }, 5000);
}

// Validate the form before sending
function validateQuestion(): boolean {
  if (!title.value.trim()) {
    triggerAlert("⚠️ Titel is verplicht");
    return false;
  }
  if (!description.value.trim()) {
    triggerAlert("⚠️ Beschrijving is verplicht");
    return false;
  }
  return true;
}

// Ask to save (shows modal if form is valid)
function askToSave() {
  if (validateQuestion()) {
    showConfirmModal.value = true;
  }
}

// This function sends the question data from the Vue to the backend.
// It checks if everything is filled in, sends the data, and then clears the inputs.
async function saveQuestion() {
  try {
    const response = await fetch(`${API_BASE_URL}/api/survey`, {
      method: "POST",
      headers: { "Content-Type": "application/json" }, // Tell backend we send JSON
      body: JSON.stringify({
        title: title.value,
        description: description.value
      })
    });

    // Check if the server responded with an error
    if (!response.ok) {
      triggerAlert("❌ Serverfout bij het opslaan van de vraag");
    }

    // Show success message to the user
    triggerAlert("✅ Vraag succesvol opgeslagen!");

    // Clear all fields so the form is ready for a new question
    title.value = "";
    description.value = "";
  }
  // If something goes wrong (like backend not running), show an error
  catch (err: any) {
    triggerAlert(`❌ Kon vraag niet opslaan: ${err.message}`);
  }
}

// Confirm save via modal
function confirmSave() {
  showConfirmModal.value = false;
  saveQuestion();
}
</script>

<template>
  <main class="form-content">
    <div class="form-container">
      <h1>Nieuwe Vraag Toevoegen</h1>

      <!-- Alert box -->
      <div v-if="showAlert" class="alert-box">
        {{ alertMessage }}
      </div>

      <form @submit.prevent="saveQuestion">
        <!-- Title -->
        <div class="form-group">
          <label for="title">Titel van de vraag:</label>
          <input type="text" v-model="title" id="title" placeholder="Bijv. Wat vind jij van ...."/>
        </div>

        <!-- description -->
        <div class="form-group">
          <label for="description">Beschrijving:</label>
          <textarea id="description" v-model="description" placeholder="Beschrijf waar de vraag over gaat" rows="4"></textarea>
        </div>

        <!-- Save question -->
        <button type="button" class="save-btn" @click="askToSave">Vraag opslaan</button>
      </form>
    </div>

    <!-- Confirmation Modal -->
    <div v-if="showConfirmModal" class="modal-overlay">
      <div class="modal-content">
        <p>Weet je zeker dat je deze vraag wilt opslaan?</p>
        <div class="modal-buttons">
          <button @click="confirmSave">Opslaan</button>
          <button @click="showConfirmModal = false">Annuleren</button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.form-content {
  flex: 1;
  padding: 2rem;
  margin-top: 6rem;
}

.form-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 1rem;
}

h1 {
  color: #1a3aff;
  font-size: 2.2rem;
  margin-bottom: 2rem;
  text-align: center;
}

.alert-box {
  background-color: #DBEAFE;
  color: #2563EB;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-weight: 600;
  animation: fadeIn 0.3s ease, fadeOut 0.5s ease 4.5s forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-5px);
  }
}

.form-group {
  margin-bottom: 1.5rem;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  margin-bottom: 0.5rem;
}

input[type="text"], textarea {
  padding: 0.7rem;
  font-size: inherit;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  flex: 1;
}

textarea {
  font-family: inherit;
  resize: vertical;
  min-height: 200px;
}

.save-btn {
  background-color: #DBEAFE;
  color: #2563EB;
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  width: 100%;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #D9D9D9;
  color: #334155;
  font-size: 20px;
  padding: 32px;
  border-radius: 10px;
  text-align: center;
  max-width: 670px;
  height: 250px;
  width: 90%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

.modal-buttons {
  display: flex;
  gap: 20px;
  flex-direction: column;
  width: 100%;
  align-items: center;
}

.modal-buttons button {
  background-color: #1a3aff;
  color: white;
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  cursor: pointer;
  width: 100%;
  font-size: 16px;
}
@media (max-width: 768px) {
  .form-content {
    margin-top: 2rem;
    padding: 1rem;
  }

  h1 {
    font-size: 1.8rem;
  }
}
</style>
