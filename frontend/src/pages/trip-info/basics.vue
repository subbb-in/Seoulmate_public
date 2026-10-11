<template>
  <div>
    <!-- 공통 탭 추가 -->
    <TripInfoTabs />

    <v-container class="py-0" max-width="1200">
      <!-- 기존 헤더 제거됨 -->
      
      <!-- 1. Weather & Seasons (날씨) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-weather-partly-cloudy</v-icon>
          Weather & Best Time to Visit
        </h2>
        <v-card elevation="2" class="rounded-lg overflow-hidden">
          <v-row no-gutters>
            <v-col cols="12" md="6" class="bg-blue-grey-lighten-5 pa-8 d-flex flex-column justify-center">
              <h3 class="text-h5 font-weight-bold mb-4">Four Distinct Seasons</h3>
              <p class="text-body-1 mb-4">
                Korea has four distinct seasons. 
                <strong>Spring (Apr-Jun)</strong> and <strong>Autumn (Sep-Nov)</strong> are the best times to visit due to the pleasant weather.
              </p>
              <v-list density="compact" bg-color="transparent">
                <v-list-item prepend-icon="mdi-flower-outline" title="Spring: Mild & Cherry Blossoms (10°C ~ 20°C)"></v-list-item>
                <v-list-item prepend-icon="mdi-weather-sunny" title="Summer: Hot & Humid, Rainy Season (25°C ~ 35°C)"></v-list-item>
                <v-list-item prepend-icon="mdi-leaf" title="Autumn: Cool Breeze & Foliage (10°C ~ 25°C)"></v-list-item>
                <v-list-item prepend-icon="mdi-snowflake" title="Winter: Cold & Dry, Snow (-10°C ~ 5°C)"></v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6">
              <!-- 날씨 관련 이미지 -->
               <v-img
                height="100%"
                min-height="300"
                cover
                src="@/assets/climate.jpg"
                class="align-end"
              >
                <!-- 링크 추가 -->
                <div class="bg-black bg-opacity-50 pa-6 text-white text-center d-flex flex-column align-center">
                  <span class="text-h6 mb-3 font-weight-bold">Check the forecast before packing!</span>
                  <v-btn
                    variant="outlined"
                    color="white"
                    href="https://www.kma.go.kr/neng/index.do"
                    target="_blank"
                    prepend-icon="mdi-open-in-new"
                    class="text-capitalize"
                  >
                    Weekly Forecast (KMA)
                  </v-btn>
                </div>
              </v-img>
            </v-col>
          </v-row>
        </v-card>
      </section>
  
      <!-- 2. Currency & Tipping (통화 및 팁) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-cash-multiple</v-icon>
          Currency & Exchange Rates
        </h2>
        
        <v-row>
          <v-col cols="12" lg="8">
            <v-card class="pa-6 rounded-lg" elevation="2" border>
              <div class="d-flex justify-space-between align-center mb-6">
                <div class="d-flex align-center">
                  <v-avatar color="primary" size="50" class="mr-4">
                    <span class="text-h5 font-weight-bold text-white">₩</span>
                  </v-avatar>
                  <div>
                    <div class="text-h6 font-weight-bold">South Korean Won (KRW)</div>
                    <div class="text-caption text-grey">Base Currency for Seoul Visit</div>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-caption text-grey">Latest Update</div>
                  <div class="text-subtitle-2 font-weight-bold">{{ updateDate }}</div>
                </div>
              </div>

              <!-- 환율 데이터 테이블/그리드 -->
              <v-row v-if="loading">
                <v-col cols="12" class="text-center py-10">
                  <v-progress-circular indeterminate color="primary"></v-progress-circular>
                  <div class="mt-2 text-grey">Fetching real-time data...</div>
                </v-col>
              </v-row>

              <v-row v-else-if="rates.length > 0">
                <v-col v-for="rate in rates" :key="rate.cur_unit" cols="12" sm="6" md="4">
                  <v-sheet border rounded="lg" class="pa-4 bg-grey-lighten-5">
                    <div class="text-caption font-weight-bold text-primary mb-1">{{ getCurrencyName(rate.cur_unit) }}</div>
                    <div class="d-flex align-baseline">
                      <span class="text-h6 font-weight-black">{{ rate.deal_bas_r }}</span>
                      <span class="ml-1 text-caption font-weight-medium">KRW</span>
                    </div>
                    <div class="text-caption text-grey">per {{ rate.cur_unit }}</div>
                  </v-sheet>
                </v-col>
              </v-row>

              <v-alert v-else type="warning" variant="tonal" class="mt-4">
                Real-time exchange rate data is currently unavailable. Displaying estimated rates.
              </v-alert>

              <v-divider class="my-6"></v-divider>
              
              <v-alert type="info" variant="tonal" border="start" density="compact">
                <template v-slot:title>Travel Tip</template>
                Credit cards (Visa, Mastercard) are accepted almost everywhere. However, <strong>T-money cards</strong> for public transport usually require <strong>cash</strong> for recharging at subway stations or convenience stores.
              </v-alert>
            </v-card>
          </v-col>

          <v-col cols="12" lg="4">
            <v-card class="pa-6 h-100 rounded-lg d-flex flex-column justify-center text-center" elevation="2" color="primary">
              <v-icon size="64" color="white" class="mb-4 mx-auto">mdi-cancel</v-icon>
              <h3 class="text-h5 font-weight-bold text-white mb-2">No Tipping Culture</h3>
              <p class="text-white text-body-1">
                In Korea, tipping is not customary. Service charges are already included in the price of food and services.
              </p>
              <div class="mt-6">
                <span class="text-body-2 bg-white text-primary px-3 py-2 rounded-pill font-weight-bold">
                  Just say "Kamsahamnida!"
                </span>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </section>
  
      <!-- 3. Electricity (전압) -->
      <section class="mt-16 pt-10 mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-power-plug</v-icon>
          Electricity & Plugs
        </h2>
        <v-sheet class="bg-grey-lighten-4 pa-8 rounded-xl text-center">
          <v-row align="center" justify="center">
            <v-col cols="12" sm="4">
              <v-icon size="100" color="grey-darken-3">mdi-power-socket-eu</v-icon>
              <div class="mt-4 text-h6 font-weight-bold">Type C / F</div>
              <div class="text-caption">Two round pins</div>
            </v-col>
            <v-col cols="12" sm="8" class="text-left">
              <h3 class="text-h5 font-weight-bold mb-2">Standard Voltage: 220V, 60Hz</h3>
              <p class="text-body-1 mb-4">
                Korea uses the standard 220V outlets with two round holes (same as Germany/France). 
                If your device uses 110V (e.g., from US/Japan), you will need a <strong>voltage converter</strong>.
              </p>
              <v-btn
                variant="outlined"
                color="primary"
                prepend-icon="mdi-shopping"
                href="https://www.google.com/search?q=korea+power+adapter"
                target="_blank"
              >
                Check Adapter Types
              </v-btn>
            </v-col>
          </v-row>
        </v-sheet>
      </section>
    </v-container>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import TripInfoTabs from '@/components/trip-info/TripInfoTabs.vue';

const loading = ref(true);
const rates = ref([]);
const updateDate = ref(new Date().toLocaleDateString());

// 통화 코드별 한글/영문 이름 매핑
const getCurrencyName = (unit) => {
  const names = {
    'USD': 'US Dollar(USD)',
    'EUR': 'Euro(EUR)',
    'JPY(100)': 'Japanese Yen(100 JPY)',
    'CNH': 'Chinese Yuan(CNY)',
    'IDR(100)': 'Indonesian Rupiah(100 IDR)',
  };
  return names[unit] || unit;
}

const fetchRates = async () => {
  try {
    const response = await axios.get('/api/exchange');
    if (response.data && response.data.data) {
      rates.value = response.data.data;
    }
  } catch (error) {
    console.error('Error fetching exchange rates:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchRates();
});
</script>
  
<style scoped>
section {
  scroll-margin-top: 100px;
}
</style>