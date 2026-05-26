<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'

import DropdownComponent from '@/components/DropdownComponent.vue'
import DynamicListComponent from '@/components/ListComponent.vue'
import Pagination from '@/components/PaginationComponent.vue'

import { Election2Service, type Election } from '@/services/Election2Service'
import { PollingStationService } from '@/services/PollingStationService'
import { MunicipalityService }  from '@/services/MunicipalityService'
import type { PollingStation, Municipality, PaginatedPollingStations } from '../../types'

const electionService = new Election2Service()
const pollingStationService = new PollingStationService()
const municipalityService = new MunicipalityService()

const elections = ref<Election[]>([])
const selectedElectionYear = ref<number | ''>('')

const municipalities = ref<Municipality[]>([])
const selectedMunicipality = ref<string | ''>('')

const pollingStations = ref<PollingStation[]>([])

const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

const electionYears = computed(() =>
  elections.value.map(e => e.year)
)

const municipalityNames = computed(() =>
  municipalities.value.map(m => m.name)
)

const headers = ['Stembureau', 'Postcode', 'Gemeente']

const rows = computed(() =>
  pollingStations.value.map(ps => [ps.name, ps.zipcode, ps.municipality])
)

onMounted(async () => {
  elections.value = await electionService.getAll()
  municipalities.value = await municipalityService.getAll();
})

const fetchPollingStations = async (page: number) => {
  if (!selectedElectionYear.value) {
    pollingStations.value = []
    totalPages.value = 1
    return
  }

  const election = elections.value.find(e => e.year === selectedElectionYear.value)
  if (!election) {
    pollingStations.value = []
    totalPages.value = 1
    return
  }

  const municipality = municipalities.value.find(m => m.name === selectedMunicipality.value.trim())
  console.log(municipality);

  let response: PaginatedPollingStations

  if (municipality) {
    console.log("passes municipality check")

      response = await pollingStationService.getByElectionAndMunicipalityPaginated(
        election.id,
        municipality.id,
        page - 1,
        pageSize
    )
  } else {
      response = await pollingStationService.getByElectionIdPaginated(
      election.id,
      page - 1,
      pageSize
    )
  }

  pollingStations.value = response.content
  totalPages.value = response.totalPages
}

watch(selectedMunicipality, async () => {
  currentPage.value = 1
  await fetchPollingStations(currentPage.value)
})

watch(selectedElectionYear, async () => {
  currentPage.value = 1
  await fetchPollingStations(currentPage.value)
})

const onPageChange = async (page: number) => {
  currentPage.value = page
  await fetchPollingStations(page)
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
      v-if="selectedElectionYear"
      :options="municipalityNames"
      v-model="selectedMunicipality"
      placeholder="Kies gemeente"
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
