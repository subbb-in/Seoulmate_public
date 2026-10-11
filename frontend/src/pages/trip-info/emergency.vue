<template>
  <div>
    <!-- 공통 탭 추가 -->
    <TripInfoTabs />

    <v-container class="py-0" max-width="1200">
      <!-- 1. Emergency Numbers (Grid Layout) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="error" class="mr-3">mdi-phone-alert</v-icon>
          Emergency Numbers
        </h2>
        <v-row>
          <!-- Police -->
          <v-col cols="12" md="4">
            <v-card class="h-100 text-center py-8" elevation="2" border>
              <v-icon size="64" color="blue-darken-3" class="mb-4">mdi-police-badge</v-icon>
              <div class="text-h2 font-weight-black text-blue-darken-3 mb-2">112</div>
              <div class="text-h6 font-weight-bold">Police</div>
              <p class="text-body-2 text-grey mt-2">Theft, assault, and other crimes</p>
            </v-card>
          </v-col>
          <!-- Fire & Ambulance -->
          <v-col cols="12" md="4">
            <v-card class="h-100 text-center py-8" elevation="2" border>
              <v-icon size="64" color="error" class="mb-4">mdi-ambulance</v-icon>
              <div class="text-h2 font-weight-black text-error mb-2">119</div>
              <div class="text-h6 font-weight-bold">Fire & Ambulance</div>
              <p class="text-body-2 text-grey mt-2">Fire, medical emergencies, rescue</p>
            </v-card>
          </v-col>
          <!-- Travel Helpline -->
          <v-col cols="12" md="4">
            <v-card class="h-100 text-center py-8 bg-blue-grey-lighten-5" elevation="0">
              <v-icon size="64" color="teal-darken-2" class="mb-4">mdi-face-agent</v-icon>
              <div class="text-h2 font-weight-black text-teal-darken-2 mb-2">1330</div>
              <div class="text-h6 font-weight-bold">Travel Helpline</div>
              <p class="text-body-2 text-grey-darken-2 mt-2">
                Multi-language assistance (24/7)<br>
                Interpretation & Complaints
              </p>
            </v-card>
          </v-col>
        </v-row>
        <v-alert
          type="info"
          variant="tonal"
          icon="mdi-information"
          class="mt-6"
          border="start"
        >
          <strong>Tip:</strong> 1330 provides interpretation services. If you have trouble communicating with 112 or 119, call 1330 for 3-way interpretation help.
        </v-alert>
      </section>
  
      <!-- 2. Medical Assistance -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-hospital-box</v-icon>
          Medical Assistance
        </h2>
        <v-card elevation="2" class="rounded-lg overflow-hidden">
          <v-row no-gutters>
            <v-col cols="12" md="7" class="pa-8">
              <h3 class="text-h5 font-weight-bold mb-4">Hospitals & Pharmacies</h3>
              <v-list lines="two">
                <v-list-item prepend-icon="mdi-hospital-marker">
                  <v-list-item-title class="font-weight-bold">International Clinics</v-list-item-title>
                  <v-list-item-subtitle>
                    Major university hospitals in Seoul (Severance, Asan, Samsung, SNUH) operate International Health Centers with English-speaking staff.
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item prepend-icon="mdi-pill">
                  <v-list-item-title class="font-weight-bold">Pharmacies (Yak-guk)</v-list-item-title>
                  <v-list-item-subtitle>
                    Look for the "약" (Yak) sign. Mild symptoms (cold, digestion) can be treated with over-the-counter medicine.
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item prepend-icon="mdi-clock-time-four-outline">
                  <v-list-item-title class="font-weight-bold">Holiday/Late-night Pharmacies</v-list-item-title>
                  <v-list-item-subtitle>
                    Call <strong>119</strong> or <strong>120</strong> (Dasan Call Center) to find open pharmacies near you.
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="5" class="bg-red-lighten-5 d-flex flex-column align-center justify-center pa-8 text-center">
              <v-icon size="80" color="red-lighten-1" class="mb-4">mdi-medical-bag</v-icon>
              <div class="text-h6 font-weight-bold text-red-darken-2">Travel Insurance</div>
              <p class="text-body-2 text-grey-darken-2 mt-2">
                Korean healthcare is excellent but can be expensive for uninsured foreigners. 
                <br><strong>Always carry your travel insurance policy number.</strong>
              </p>
            </v-col>
          </v-row>
        </v-card>
      </section>
  
      <!-- 3. Safety Guidelines -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-shield-check</v-icon>
          Safety Guidelines
        </h2>
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="h-100 pa-6" elevation="2" border>
              <div class="d-flex align-center mb-4">
                <v-icon color="warning" size="large" class="mr-3">mdi-alert-circle</v-icon>
                <h3 class="text-h6 font-weight-bold">Lost & Found</h3>
              </div>
              <p class="text-body-1 mb-4">
                Korea has a very high recovery rate for lost items. If you lose something:
              </p>
              <v-list density="compact" bg-color="transparent">
                <v-list-item prepend-icon="mdi-subway" title="Subway/Bus: Contact the station office or lost & found center."></v-list-item>
                <v-list-item prepend-icon="mdi-taxi" title="Taxi: Check your receipt for the driver's contact or taxi company."></v-list-item>
                <v-list-item prepend-icon="mdi-web" title="Police Lost & Found Portal (LOST112)"></v-list-item>
              </v-list>
              <v-btn
                variant="outlined"
                color="primary"
                href="https://www.lost112.go.kr/manyLanguage.do?langType=en"
                target="_blank"
                class="mt-2"
                block
              >
                Visit LOST112 Website
              </v-btn>
            </v-card>
          </v-col>
          <v-col cols="12" md="6">
            <v-card class="h-100 pa-6" elevation="2" border>
              <div class="d-flex align-center mb-4">
                <v-icon color="success" size="large" class="mr-3">mdi-map-marker-radius</v-icon>
                <h3 class="text-h6 font-weight-bold">Safe Korea</h3>
              </div>
              <p class="text-body-1 mb-4">
                Seoul is one of the safest cities in the world.
              </p>
              <v-expansion-panels variant="accordion">
                <v-expansion-panel title="Night Safety">
                  <v-expansion-panel-text>
                    It is generally safe to walk alone at night. Most streets are well-lit and monitored by CCTV (24/7).
                  </v-expansion-panel-text>
                </v-expansion-panel>
                <v-expansion-panel title="Emergency Bells">
                  <v-expansion-panel-text>
                    Look for yellow "Emergency Bell" signs in parks, public toilets, and alleyways. Pressing it connects you to the police immediately.
                  </v-expansion-panel-text>
                </v-expansion-panel>
                <v-expansion-panel title="Scams">
                  <v-expansion-panel-text>
                    Be cautious of strangers approaching you on the street asking for directions or cultural exchange ("Do-or-Myeong"). Politely decline and walk away.
                  </v-expansion-panel-text>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-card>
          </v-col>
        </v-row>
      </section>
    </v-container>
  </div>
</template>
  
<script setup>
import TripInfoTabs from '@/components/trip-info/TripInfoTabs.vue';
</script>
  
<style scoped>
section {
  scroll-margin-top: 100px;
}
</style>