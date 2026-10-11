<template>
  <div>
    <!-- 공통 탭 추가 -->
    <TripInfoTabs />

    <v-container class="py-0" max-width="1200">
      <!-- 기존 헤더 제거됨 -->
  
      <!-- 1. K-ETA (Korea Electronic Travel Authorization) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-passport</v-icon>
          K-ETA (Visa-Free Entry)
        </h2>
        
        <v-alert
          type="info"
          variant="tonal"
          border="start"
          class="mb-6"
          title="Temporary Exemption (Until Dec 31, 2025)"
        >
          K-ETA is <strong>temporarily exempted</strong> for 22 countries (including Japan, US, UK, etc.) until December 31, 2025. 
          However, applying is still recommended to skip the arrival card process.
        </v-alert>
  
        <v-card elevation="2" class="rounded-lg pa-6">
          <h3 class="text-h6 font-weight-bold mb-4">What is K-ETA?</h3>
          <p class="text-body-1 mb-4">
            Travelers from visa-waiver countries must obtain K-ETA authorization at least <strong>72 hours before departure</strong>.
          </p>
          
          <v-row>
            <v-col cols="12" md="6">
              <v-list density="compact" bg-color="transparent">
                <v-list-item prepend-icon="mdi-check-circle" title="Valid for 3 years (multiple entry)"></v-list-item>
                <v-list-item prepend-icon="mdi-check-circle" title="Fee: 10,000 KRW (approx. $9 USD)"></v-list-item>
                <v-list-item prepend-icon="mdi-check-circle" title="Apply via Website or Mobile App"></v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6" class="d-flex align-center justify-end">
              <v-btn
                color="primary"
                size="large"
                prepend-icon="mdi-open-in-new"
                href="https://www.k-eta.go.kr/portal/newapply/index.do?locale=EN"
                target="_blank"
                class="w-100 w-md-auto"
              >
                Apply for K-ETA
              </v-btn>
            </v-col>
          </v-row>
        </v-card>
      </section>
  
      <!-- 2. Entry Process (Q-Code) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-airplane-landing</v-icon>
          Q-Code (Quarantine System)
        </h2>
        <v-card elevation="2" class="rounded-lg overflow-hidden">
          <v-row no-gutters>
            <v-col cols="12" md="8" class="pa-8">
              <h3 class="text-h6 font-weight-bold mb-3">Speed up your entry process</h3>
              <p class="text-body-1 mb-4">
                Q-Code allows you to input your health condition and travel history before arrival. 
                <strong>It is not mandatory</strong>, but highly recommended to avoid long queues at the airport.
              </p>
              <div class="d-flex gap-4 flex-wrap">
                <v-chip color="success" label>
                  <v-icon start icon="mdi-qrcode"></v-icon> Get QR Code
                </v-chip>
                <v-chip color="success" label>
                  <v-icon start icon="mdi-fast-forward"></v-icon> Fast Track Entry
                </v-chip>
              </div>
              <div class="mt-6">
                <v-btn
                  variant="outlined"
                  color="primary"
                  href="https://qcode.kdca.go.kr/qco/index.do?lang=en"
                  target="_blank"
                >
                  Go to Q-Code Website
                </v-btn>
              </div>
            </v-col>
            <v-col cols="12" md="4" class="bg-blue-grey-lighten-5 d-flex align-center justify-center pa-4">
              <v-icon size="100" color="blue-grey-lighten-2">mdi-cellphone-nfc</v-icon>
            </v-col>
          </v-row>
        </v-card>
      </section>
  
      <!-- 3. Visa Types -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-file-document-outline</v-icon>
          Common Visa Types
        </h2>
        <v-row>
          <v-col cols="12" md="4">
            <v-card height="100%" class="pa-6 border-t-lg border-primary" elevation="2">
              <div class="text-h5 font-weight-bold mb-2">B-1 / B-2</div>
              <div class="text-subtitle-1 text-primary font-weight-medium mb-4">Visa Exempt / Tourist</div>
              <p class="text-body-2 text-grey-darken-1">
                For tourists from countries with visa waiver agreements. 
                Usually allows stay for 30~90 days.
              </p>
            </v-card>
          </v-col>
          <v-col cols="12" md="4">
            <v-card height="100%" class="pa-6 border-t-lg border-secondary" elevation="2">
              <div class="text-h5 font-weight-bold mb-2">C-3</div>
              <div class="text-subtitle-1 text-secondary font-weight-medium mb-4">Short-Term Visit</div>
              <p class="text-body-2 text-grey-darken-1">
                For general tourism, visiting relatives, or medical treatment for stay up to 90 days.
              </p>
            </v-card>
          </v-col>
          <v-col cols="12" md="4">
            <v-card height="100%" class="pa-6 border-t-lg border-info" elevation="2">
              <div class="text-h5 font-weight-bold mb-2">H-1</div>
              <div class="text-subtitle-1 text-info font-weight-medium mb-4">Working Holiday</div>
              <p class="text-body-2 text-grey-darken-1">
                For young adults (usually 18-30) to travel and work in Korea for up to 1 year.
              </p>
            </v-card>
          </v-col>
        </v-row>
        
        <div class="text-center mt-8">
          <p class="text-caption text-grey mb-2">
            * Visa requirements vary by nationality. Please check the official portal.
          </p>
          <v-btn
            variant="text"
            color="grey-darken-2"
            href="https://www.visa.go.kr/main/openMain.do"
            target="_blank"
            append-icon="mdi-chevron-right"
          >
            Korea Visa Portal
          </v-btn>
        </div>
      </section>
    </v-container>
  </div>
</template>
  
<script setup>
import TripInfoTabs from '@/components/trip-info/TripInfoTabs.vue';
</script>
  
<style scoped>
.gap-4 {
  gap: 16px;
}
.border-t-lg {
  border-top: 4px solid !important;
}
.border-primary {
  border-color: rgb(var(--v-theme-primary)) !important;
}
.border-secondary {
  border-color: rgb(var(--v-theme-secondary)) !important;
}
.border-info {
  border-color: rgb(var(--v-theme-info)) !important;
}
</style>