<template>
  <v-container class="pb-0" max-width="1200">
    <!-- Page Title -->
    <div class="text-center mb-8 mt-10">
      <h1 class="text-h3 font-weight-bold text-primary mb-3">Trip Information</h1>
      <p class="text-h6 text-grey-darken-1">
        Everything you need to know for your Seoul trip
      </p>
    </div>

    <!-- Navigation Tab -->
    <v-tabs
      v-model="activeTab"
      align-tabs="center"
      color="primary"
      grow
      class="mb-10 border-b"
    >
      <v-tab value="/trip-info/basics" to="/trip-info/basics" class="text-body-1 font-weight-bold">
        Travel Basics
      </v-tab>
      <v-tab value="/trip-info/visa" to="/trip-info/visa" class="text-body-1 font-weight-bold">
        Visa & Entry
      </v-tab>
      <v-tab value="/trip-info/transportation" to="/trip-info/transportation" class="text-body-1 font-weight-bold">
        Transportation
      </v-tab>
      <v-tab value="/trip-info/emergency" to="/trip-info/emergency" class="text-body-1 font-weight-bold">
        Emergency
      </v-tab>
    </v-tabs>
  </v-container>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const activeTab = ref(route.path);

// 라우트 변경 시 탭 상태 동기화
watch(() => route.path, (newPath) => {
  activeTab.value = newPath;
});
</script>