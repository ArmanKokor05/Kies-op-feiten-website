<script setup lang="ts">
  import webLogo from './components/webLogo.vue';
  import adminButton from "@/components/adminpage/adminButton.vue";
  import {type Component, computed} from "vue";
  import { useRoute } from 'vue-router'

  import adminXML from "@/components/adminpage/adminXML.vue";
  import adminStemwijzer from "@/components/adminpage/adminStemwijzer.vue";
  import adminTEST from "@/components/adminpage/adminTEST.vue";

  const panelMap: Record<string, Component> = {
    adminXML,
    adminStemwijzer,
    adminTEST,
  }

  const route = useRoute()
  const panels = computed(() => {
    const p = route.params.panels
    return p ? (Array.isArray(p) ? p : [p]) : []
  })
</script>

<template>
  <div class="pageContainer">

    <div class="sideBar">

      <div class="adminLogoContainer">
        <webLogo/>
        <div class="logoAdmin">
          administrator
        </div>
      </div>

      <div class="sidebarButtonContainer">
        <adminButton
        button-name="XML"
        button-link="adminXML"
        button-class="adminNavbarItem"
        />
        <adminButton
        button-name="Stemwijzer"
        button-class="adminNavbarItem"
        button-link="adminStemwijzer"
        />
        <adminButton
        button-name="test"
        button-class="adminNavbarItem"
        button-link="adminTEST"
        />
      </div>

    </div>
    <div id="dynamicContainer">
      <component
        v-for="panel in panels"
        :is="panelMap[panel]"
        :key="panel"
      />
    </div>


  </div>
</template>

<style scoped>

.pageContainer {
  display: flex;
}

.sideBar {
  background-color: #9c9b9b;
  width: fit-content;
  height: 98vh;
  margin-right: 2vw;
}

.adminLogoContainer {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: fit-content;
  padding-left: 2vw;
  padding-right: 2vw;
  margin-bottom: 4vh;
}

.logoAdmin {
  color: #1a3aff;
  font-weight: bold;
}

.sidebarButtonContainer {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  padding-right: 1vw;
}

</style>
