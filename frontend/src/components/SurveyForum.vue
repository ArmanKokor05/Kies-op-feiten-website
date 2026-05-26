<template>
  <div class="survey-forum">
    <h1>Discussie Forum</h1>

    <!-- Formulier voor nieuwe survey -->
    <div class="survey-form">
      <h2>Start een nieuwe discussie</h2>
      <form @submit.prevent="createSurvey">
        <div class="form-group">
          <label for="author">Je naam:</label>
          <input
            type="text"
            id="author"
            v-model="newSurvey.author"
            placeholder="Voer je naam in"
            required
          />
        </div>

        <div class="form-group">
          <label for="title">Titel:</label>
          <input
            type="text"
            id="title"
            v-model="newSurvey.title"
            placeholder="Geef een titel aan je bericht"
            required
          />
        </div>

        <div class="form-group">
          <label for="content">Bericht:</label>
          <textarea
            id="content"
            v-model="newSurvey.content"
            placeholder="Deel je mening..."
            rows="4"
            required
          ></textarea>
        </div>

        <button type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? 'Bezig met plaatsen...' : 'Plaats Bericht' }}
        </button>
      </form>
    </div>

    <!-- Lijst van surveys -->
    <div class="surveys-list">
      <h2>Discussies</h2>

      <div v-if="loading" class="loading">Berichten laden...</div>

      <div v-else-if="surveys.length === 0" class="no-surveys">
        Nog geen discussies. Start de eerste!
      </div>

      <div v-else class="survey-item" v-for="survey in surveys" :key="survey.id">
        <div class="survey-main">
          <h3>{{ survey.title }}</h3>
          <p class="survey-content">{{ survey.content }}</p>
          <div class="survey-meta">
            <span class="author">Door: {{ survey.author }}</span>
            <span class="date">{{ formatDate(survey.createdAt) }}</span>
          </div>

          <!-- Reactie knop -->
          <button @click="toggleReplyForm(survey.id)" class="reply-btn">
            {{ showReplyForm === survey.id ? 'Annuleren' : 'Reageer' }}
          </button>
        </div>

        <!-- Reactie formulier -->
        <div v-if="showReplyForm === survey.id" class="reply-form">
          <textarea
            v-model="replyContent[survey.id]"
            placeholder="Schrijf je reactie..."
            rows="3"
          ></textarea>
          <div class="reply-actions">
            <button
              @click="addReply(survey.id)"
              :disabled="!replyContent[survey.id]?.trim()"
              class="submit-reply"
            >
              Plaats Reactie
            </button>
          </div>
        </div>

        <!-- Toon reacties -->
        <div v-if="survey.replies && survey.replies.length > 0" class="replies-list">
          <div v-for="reply in survey.replies" :key="reply.id" class="reply-item">
            <p class="reply-content">{{ reply.content }}</p>
            <div class="reply-meta">
              <span class="reply-author">{{ reply.author }}</span>
              <span class="reply-date">{{ formatDate(reply.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {API_BASE_URL} from "@/config/api.config.ts";

export default {
  name: 'SurveyForum',
  data() {
    return {
      surveys: [],
      newSurvey: {
        title: '',
        content: '',
        author: ''
      },
      replyContent: {},
      showReplyForm: null,
      loading: false,
      isSubmitting: false
    }
  },
  async mounted() {
    await this.loadSurveys();
  },
  methods: {
    // GET request: Haal alle surveys op
    async loadSurveys() {
      this.loading = true;
      try {
        const response = await fetch(`${API_BASE_URL}/api/surveys`);
        if (response.ok) {
          this.surveys = await response.json();
        } else {
          console.error('Fout bij laden surveys');
        }
      } catch (error) {
        console.error('Fout bij laden surveys:', error);
      } finally {
        this.loading = false;
      }
    },

    // POST request: Maak nieuwe survey aan
    async createSurvey() {
      if (!this.newSurvey.title.trim() || !this.newSurvey.content.trim() || !this.newSurvey.author.trim()) {
        return;
      }

      this.isSubmitting = true;
      try {
        const response = await fetch(`${API_BASE_URL}/api/surveys`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.newSurvey)
        });

        if (response.ok) {
          // Reset form
          this.newSurvey = { title: '', content: '', author: '' };
          // Herlaad surveys
          await this.loadSurveys();
        } else {
          console.error('Fout bij aanmaken survey');
        }
      } catch (error) {
        console.error('Fout bij aanmaken survey:', error);
      } finally {
        this.isSubmitting = false;
      }
    },

    // POST request: Voeg reactie toe
    async addReply(surveyId) {
      const content = this.replyContent[surveyId];
      if (!content?.trim()) return;

      try {
        const response = await fetch(`${API_BASE_URL}/api/surveys/${surveyId}/replies`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            content: content,
            author: this.newSurvey.author || 'Anoniem'
          })
        });

        if (response.ok) {
          // Reset reactie form
          this.replyContent[surveyId] = '';
          this.showReplyForm = null;
          // Herlaad surveys
          await this.loadSurveys();
        } else {
          console.error('Fout bij plaatsen reactie');
        }
      } catch (error) {
        console.error('Fout bij plaatsen reactie:', error);
      }
    },

    toggleReplyForm(surveyId) {
      this.showReplyForm = this.showReplyForm === surveyId ? null : surveyId;
      if (!this.replyContent[surveyId]) {
        this.replyContent[surveyId] = '';
      }
    },

    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('nl-NL', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }
}
</script>

<style scoped>
.survey-forum {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.survey-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input, textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.survey-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  background: white;
}

.survey-meta, .reply-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.9em;
  margin-top: 10px;
}

.reply-form {
  margin-top: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.replies-list {
  margin-top: 15px;
  padding-left: 20px;
  border-left: 2px solid #eee;
}

.reply-item {
  background: #f9f9f9;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
}

.reply-btn {
  background: #6c757d;
  margin-top: 10px;
}

.submit-reply {
  background: #28a745;
  margin-top: 10px;
}
</style>
