<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'

import DropdownComponent from '@/components/DropdownComponent.vue'
import DynamicListComponent from '@/components/ListComponent.vue'
import Pagination from '@/components/PaginationComponent.vue'

import { Election2Service, type Election } from '@/services/Election2Service'
import { PartyService } from '@/services/PartyService'
import { CandidateService, type PaginatedCandidates } from '@/services/CandidateService'

import type { Party, Candidate } from '../../types'

const electionService = new Election2Service()
const partyService = new PartyService()
const candidateService = new CandidateService()

const elections = ref<Election[]>([])
const parties = ref<Party[]>([])
const candidates = ref<Candidate[]>([])

const selectedElectionYear = ref<number | ''>('')
const selectedPartyAcronym = ref<string | ''>('')

const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

const electionYears = computed(() =>
  elections.value.map(e => e.year)
)

const partyAcronyms = computed(() =>
  parties.value.map(p => p.acronym)
)

const headers = ['Naam', 'Initialen', 'Geslacht', 'Adres']

const rows = computed(() =>
  candidates.value.map(c => [
    `${c.firstName} ${c.lastName}`,
    c.initials,
    c.gender,
    c.qualifyingAdress
  ])
)

onMounted(async () => {
  elections.value = await electionService.getAll()
  parties.value = await partyService.getAll()
})

const fetchCandidates = async (page: number) => {
  if (!selectedElectionYear.value || !selectedPartyAcronym.value) {
    candidates.value = []
    totalPages.value = 1
    return
  }

  const election = elections.value.find(
    e => e.year === selectedElectionYear.value
  )

  const party = parties.value.find(
    p => p.acronym === selectedPartyAcronym.value.trim()
  )

  if (!election || !party) {
    candidates.value = []
    totalPages.value = 1
    return
  }

  const response: PaginatedCandidates =
    await candidateService.getByElectionAndPartyPaginated(
      election.id,
      party.id,
      page - 1,
      pageSize
    )

  candidates.value = response.content
  totalPages.value = response.totalPages
}

watch(selectedElectionYear, async () => {
  currentPage.value = 1
  await fetchCandidates(currentPage.value)
})

watch(selectedPartyAcronym, async () => {
  currentPage.value = 1
  await fetchCandidates(currentPage.value)
})

const onPageChange = async (page: number) => {
  currentPage.value = page
  await fetchCandidates(page)
}
</script>

<template>
  <div class="filters">
    <DropdownComponent
      :options="electionYears"
      v-model="selectedElectionYear"
      placeholder="Kies verkiezingsjaar"
      width="30rem"
    />

    <DropdownComponent
      :options="partyAcronyms"
      v-model="selectedPartyAcronym"
      placeholder="Kies partij"
      width="30rem"
    />
  </div>

  <DynamicListComponent
    v-if="rows.length"
    :headers="headers"
    :rows="rows"
  />

  <Pagination
    v-if="totalPages > 1"
    :currentPage="currentPage"
    :totalPages="totalPages"
    @update:currentPage="onPageChange"
  />
</template>

<style scoped>
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 1.5rem;
  margin-top: 25px;
}

@media (max-width: 1052px) {
  .filters ::v-deep(select),
  .filters ::v-deep(input) {
    width: 80%;
    max-width: 80%;
    font-size: 0.9rem;
  }

}
</style>
