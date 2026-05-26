<script setup lang="ts">
import {computed, nextTick, onMounted, ref, watch} from "vue";
import gemeentenSVG from "@/assets/gemeentes.svg?raw";

import provincieSVG from "@/assets/provincies.svg?raw";
import DropdownComponent from "@/components/DropdownComponent.vue";
import {type PartyResultDTO, partyResultService} from "@/services/electionService";

const svgContent = ref<string>(provincieSVG);
const selectedMap = ref<string>("provincies");
const selectedRegion = ref<string>("");
const hoveredAreaName = ref<string | null>(null);
const hoverInfoX = ref<number>(0);
const hoverInfoY = ref<number>(0);
const selectedId = ref<string | null>(null);
const showResults = ref(false);

const regionsData = ref<string[]>([]);
const selectedYear = ref(2021);
const availableYears = ref<number[]>([]);
const electionResults = ref<PartyResultDTO[]>([]);
const loading = ref(false);

let lastSelectedElement: SVGPathElement | null = null;

const regionLabel = computed(() => {
  if (selectedMap.value === "gemeenten") return "Gemeente";
  if (selectedMap.value === "provincies") return "Provincie";
  if (selectedMap.value === "kieskringen") return "Kieskring";
  return "Regio";
});

const regionPlaceholder = computed(() => {
  if (selectedMap.value === 'gemeenten') {
    return 'Selecteer gemeente';
  }
  return `Selecteer ${regionLabel.value.toLowerCase()} (of alle)`;
});

const showMap = computed(() => {
  return selectedMap.value !== "kieskringen";
});

const updateHoverInfoPosition = (e: MouseEvent) => {

  hoverInfoX.value = e.clientX + 10;

  hoverInfoY.value = e.clientY + 10;

};

const clearSelection = () => {

  if (lastSelectedElement) {

    lastSelectedElement.style.fill = "";

  }

  selectedId.value = null;

  lastSelectedElement = null;

};

const normalizeProvinceName = (svgName: string): string => {
  const mapping: { [key: string]: string } = {
    'friesland': 'Friesland',
    'groningen': 'Groningen',
    'drenthe': 'Drenthe',
    'overijssel': 'Overijssel',
    'flevoland': 'Flevoland',
    'gelderland': 'Gelderland',
    'utrecht': 'Utrecht',
    'noord-holland': 'Noord-Holland',
    'noordholland': 'Noord-Holland',
    'zuid-holland': 'Zuid-Holland',
    'zuidholland': 'Zuid-Holland',
    'zeeland': 'Zeeland',
    'noord-brabant': 'Noord-Brabant',
    'noordbrabant': 'Noord-Brabant',
    'limburg': 'Limburg'
  };
  const normalized = svgName.toLowerCase().trim();
  return mapping[normalized] || svgName;
};


const selectMapElementFromRegion = async () => {
  await nextTick();

  if (!selectedRegion.value) {
    clearSelection();
    return;
  }

  let target: SVGPathElement | null = null;

  if (selectedMap.value === "gemeenten") {
    target = document.querySelector(
      `.kaart svg [data-name="${CSS.escape(selectedRegion.value)}"]`
    );
  } else if (selectedMap.value === "provincies") {
    const normalized = normalizeProvinceName(selectedRegion.value);
    target = document.querySelector(
      `.kaart svg #${CSS.escape(normalized)}`
    );
  }

  if (target) {
    if (lastSelectedElement) lastSelectedElement.style.fill = "";
    target.style.fill = "#DBEAFE";
    lastSelectedElement = target;
    selectedId.value = selectedRegion.value;
  }
};

const setupMapInteractions = async () => {

  await nextTick();

  const paths = document.querySelectorAll<SVGPathElement>(".kaart svg path, .kaart svg g path");

  paths.forEach((pathElement) => {

    pathElement.style.cursor = "pointer";

    pathElement.addEventListener("mouseenter", () => {
      hoveredAreaName.value =
        selectedMap.value === "gemeenten"
          ? pathElement.getAttribute("data-name") || "(onbekend)"
          : pathElement.id || pathElement.getAttribute("name") || "(onbekend)";
      if (pathElement !== lastSelectedElement) pathElement.style.fill = "#DBEAFE";
    });

    pathElement.addEventListener("mouseleave", () => {

      hoveredAreaName.value = null;

      if (pathElement !== lastSelectedElement) {

        pathElement.style.fill = "";

      }

    });

    pathElement.addEventListener("click", () => {

      if (lastSelectedElement) {

        lastSelectedElement.style.fill = "";

      }

      pathElement.style.fill = "#DBEAFE";

      lastSelectedElement = pathElement;

      selectedId.value =
        selectedMap.value === "gemeenten"
          ? pathElement.getAttribute("data-name") || "(onbekend)"
          : pathElement.id || pathElement.getAttribute("name") || "(onbekend)";

      if (selectedMap.value === "gemeenten") {
        selectedRegion.value = pathElement.getAttribute("data-name") || "";
      } else if (selectedMap.value === "provincies") {
        selectedRegion.value = normalizeProvinceName(pathElement.id);
      }
    });

  });

};

const changeMap = async () => {

  clearSelection();
  selectedRegion.value = "";
  showResults.value = false;

  if (selectedMap.value === "gemeenten") {

    svgContent.value = gemeentenSVG;
  } else if (selectedMap.value === "provincies") {
    svgContent.value = provincieSVG;

  }

  await loadAvailableRegions();
  if (selectedMap.value === 'provincies' || selectedMap.value === 'kieskringen') {
    await loadElectionResults();
  }

  if (showMap.value) {
    await setupMapInteractions();
  }
};

const loadAvailableRegions = async () => {
  try {
    loading.value = true;

    if (selectedMap.value === "gemeenten") {
      const response = await partyResultService.getAvailableMunicipalities();
      regionsData.value = response.data;
    } else if (selectedMap.value === "provincies") {
      const response = await partyResultService.getAvailableProvinces();
      regionsData.value = response.data;
    } else if (selectedMap.value === "kieskringen") {
      const response = await partyResultService.getAvailableKieskringen();
      regionsData.value = response.data;
    }
  } catch {
    regionsData.value = [];
  } finally {
    loading.value = false;
  }
};

const loadAvailableYears = async () => {
  try {
    const response = await partyResultService.getAvailableYears();
    availableYears.value = response.data;
    if (availableYears.value.length > 0) {
      selectedYear.value = availableYears.value[0]!;
    }
  } catch {
  }
};

const loadElectionResults = async () => {
  loading.value = true;
  showResults.value = false;
  try {
    let response;

    if (selectedMap.value === "gemeenten") {
      const municipality = selectedRegion.value || undefined;
      response = await partyResultService.getSeatsByYearAndMunicipality(selectedYear.value, municipality);
    } else {
      const region = selectedRegion.value || undefined;
      response = await partyResultService.getSeatsByYearAndRegion(selectedYear.value, region);
    }

    electionResults.value = response.data;
    showResults.value = true;
  } catch (error) {
    electionResults.value = [];
    showResults.value = true;
  } finally {
    loading.value = false;
  }
};

watch(selectedMap, () => {
  changeMap();
});

watch(selectedRegion, () => {
  if (selectedMap.value === 'gemeenten' && !selectedRegion.value) {
    clearSelection();
    electionResults.value = [];
    showResults.value = false;
    return;
  }

  selectMapElementFromRegion();
  loadElectionResults();
});

watch(selectedYear, () => {
  loadElectionResults();
});

onMounted(async () => {
  await setupMapInteractions();
  await loadAvailableYears();
  await loadAvailableRegions();
  await loadElectionResults();
});
</script>

<template>
  <main class="main-content">
    <div v-if="showMap" class="map-container" @mousemove="updateHoverInfoPosition">
      <div v-html="svgContent" class="kaart"></div>
      <div v-if="hoveredAreaName" class="hover-info-box" :style="{ top: hoverInfoY + 'px', left: hoverInfoX + 'px' }">
        {{ hoveredAreaName }}
</div>
</div>

    <div class="right-side" :class="{ 'full-width': !showMap }">
      <dropdown-component
        label="Selecteer jaar"
        :options="availableYears"
        v-model="selectedYear"
      />

      <dropdown-component
        label="Selecteer kaart"
        :options="['gemeenten','provincies','kieskringen']"
        v-model="selectedMap"
      />

      <dropdown-component
        v-if="selectedMap === 'gemeenten'"
        label="Filter Gemeente"
        :options="regionsData"
        v-model="selectedRegion"
        :placeholder="regionPlaceholder"
      />

      <dropdown-component
        v-if="selectedMap === 'provincies'"
        label="Filter Provincie"
        :options="regionsData"
        v-model="selectedRegion"
        :placeholder="regionPlaceholder"
      />

      <dropdown-component
        v-if="selectedMap === 'kieskringen'"
        label="Filter Kieskring"
        :options="regionsData"
        v-model="selectedRegion"
        :placeholder="regionPlaceholder"
      />

      <div class="results-box" v-if="showResults && electionResults.length > 0 && !loading">
        <h2>Verkiezingsresultaten {{ selectedYear }}</h2>
        <p v-if="selectedRegion" class="region-info">
          <strong>{{ regionLabel }}:</strong> {{ selectedRegion }}
        </p>
        <p v-else class="region-info">
          <strong>Nationaal</strong>
        </p>

        <div class="results-list">
          <div v-for="result in electionResults" :key="result.id" class="result-item">
            <span class="party-name">{{ result.partyName }}</span>
            <span class="party-votes">{{ result.validVotes?.toLocaleString() }} stemmen</span>
          </div>
        </div>
      </div>

      <div v-else-if="loading" class="results-box">
        <div class="loading">Laden</div>
      </div>

      <div v-else-if="showResults" class="results-box">
        <p class="no-results">
          Geen resultaten gevonden voor {{ selectedYear }}
          <span v-if="selectedRegion"> in {{ selectedRegion }}</span>.
        </p>
      </div>
    </div>
  </main>
</template>

<style scoped>

.main-content {

  display: flex;

  justify-content: center;

  align-items: flex-start;

  gap: 2rem;

  flex: 1;

  padding: 2rem;

  margin-top: 2rem;

  text-align: center;

}

.kaart {

  width: 600px;

  height: auto;

}

.map-container {
  flex-shrink: 0;
}

.right-side {

  display: flex;

  flex-direction: column;

  align-items: flex-start;

  gap: 1rem;

}

.right-side.full-width {
  width: 100%;
  max-width: 1200px;
  align-items: center;
}

.results-box {
  width: 800px;
  max-width: 100%;
  color: #334155;
  background: #ffffff;
  border: 1px solid #ccc;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.results-box h2 {
  margin-top: 0;
  margin-bottom: 0.5rem;
  color: #1e293b;
}

.region-info {
  margin-bottom: 1rem;
  color: #64748b;
  font-size: 0.9rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #64748b;
  font-size: 1.1rem;
}

.no-results {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}

.result-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.party-name {
  font-weight: 600;
  color: #1e293b;
  flex: 2;
}

.party-votes {
  color: #64748b;
  font-size: 0.9rem;
  flex: 1;
  text-align: right;
}

:deep(.kaart svg path),

:deep(.kaart svg g path) {

  fill: #ffffff;

  stroke: #1a3aff;

  stroke-width: 0.8;

  transition: all 0.2s ease-in-out;

}

.hover-info-box {

  position: fixed;

  background: rgba(0, 0, 0, 0.85);

  color: #fff;

  padding: 4px 8px;

  border-radius: 4px;

  font-size: 12px;

  pointer-events: none;

  z-index: 1000;

  white-space: nowrap;

}

@media (max-width: 1052px) {
  .main-content {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
  }
  .kaart {
    max-width: 400px;
    height: auto;
  }

  .kaart svg {
    max-width: 400px;
    height: auto;
    display: block;
  }


  .right-side,
  .results-box {
    width: 100%;
    max-width: 400px;
    align-items: center;
  }

  .right-side ::v-deep(select),
  .right-side ::v-deep(input) {
    max-width: 50%;
    font-size: 0.9rem;
  }
}



</style>

