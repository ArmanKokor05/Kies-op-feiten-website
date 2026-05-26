<script setup lang="ts">
import { computed } from "vue";

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  pageWindow: {
    type: Number,
    default: 2 // how many pages to show to the right of current
  }
});

const emits = defineEmits(["update:currentPage"]);

const goToPage = (page: number) => {
  if (page >= 1 && page <= props.totalPages) {
    emits("update:currentPage", page);
  }
};

const isFirstPage = computed(() => props.currentPage === 1);
const isLastPage = computed(() => props.currentPage === props.totalPages);

// Compute the page numbers to show
const visiblePages = computed(() => {
  const pages: (number | string)[] = [];
  const { currentPage, totalPages, pageWindow } = props;

  // always include first page
  pages.push(1);

  // add previous page if current is not first
  if (currentPage > 1 && currentPage !== 2) pages.push(currentPage - 1);

  // add current page (if not first or last)
  if (currentPage !== 1 && currentPage !== totalPages) pages.push(currentPage);

  // add pages to the right of current
  for (let i = currentPage + 1; i <= Math.min(totalPages - 1, currentPage + pageWindow); i++) {
    pages.push(i);
  }

  // right ellipsis
  if (currentPage + pageWindow < totalPages - 1) pages.push("...");

  // always include last page if more than 1
  if (totalPages > 1) pages.push(totalPages);

  return pages;
});
</script>

<template>
  <div class="pagination">
    <button
      @click="goToPage(currentPage - 1)"
      :disabled="isFirstPage"
      class="pagination-btn"
    >
      ← Vorige
    </button>

    <div class="page-numbers">
      <button
        v-for="page in visiblePages"
        :key="page.toString() + Math.random()"
        @click="typeof page === 'number' && goToPage(page)"
        class="page-btn"
        :disabled="page === '...' || page === currentPage"
        :style="
          currentPage === page
            ? { background: '#1a3aff', color: 'white' }
            : { background: '#f3f4f6', color: '#374151' }
        "
      >
        {{ page }}
      </button>
    </div>

    <button
      @click="goToPage(currentPage + 1)"
      :disabled="isLastPage"
      class="pagination-btn"
    >
      Volgende →
    </button>
  </div>
</template>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  padding: 1rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  background: #1a3aff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
  font-weight: 500;
}

.pagination-btn:hover:not(:disabled) {
  background: #1a3aff;
}

.pagination-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.page-btn {
  width: 36px;
  height: 36px;
  padding: 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  transform: scale(1.05);
}

.page-btn:disabled {
  cursor: default;
  transform: none;
}
</style>
