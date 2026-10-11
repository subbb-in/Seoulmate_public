<template>
  <div>
    <!-- 공통 탭 추가 -->
    <TripInfoTabs />

    <v-container class="py-0" max-width="1200">
  
      <!-- 1. Climate Card (Tourist Pass) - NEW & HOT -->
      <section class="mb-16">
        <div class="d-flex align-center mb-6">
          <h2 class="text-h4 font-weight-bold mr-4">
            <v-icon color="primary" class="mr-2">mdi-infinity</v-icon>
            Climate Card (Tourist Pass)
          </h2>
          <v-chip color="error" class="font-weight-bold" variant="flat">NEW</v-chip>
        </div>
  
        <v-card elevation="2" class="rounded-lg overflow-hidden">
          <v-row no-gutters>
            <v-col cols="12" md="7" class="pa-8">
              <h3 class="text-h5 font-weight-bold mb-3">Unlimited Rides in Seoul!</h3>
              <p class="text-body-1 mb-6 text-grey-darken-2">
                The best option if you plan to stay in Seoul and use the subway/bus frequently.
                Offers <strong>unlimited rides</strong> on Seoul subway lines and city buses for a fixed period.
              </p>
  
              <v-table density="comfortable" class="border rounded-lg mb-6">
                <thead>
                  <tr>
                    <th class="font-weight-bold bg-grey-lighten-4">Duration</th>
                    <th class="font-weight-bold bg-grey-lighten-4">Price (KRW)</th>
                    <th class="font-weight-bold bg-grey-lighten-4">Recommended For</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>1-Day Pass</td>
                    <td>₩5,000</td>
                    <td>Day trippers</td>
                  </tr>
                  <tr>
                    <td>2-Day Pass</td>
                    <td>₩8,000</td>
                    <td>Weekend getaways</td>
                  </tr>
                  <tr>
                    <td>3-Day Pass</td>
                    <td>₩10,000</td>
                    <td class="text-primary font-weight-bold">Most Popular</td>
                  </tr>
                  <tr>
                    <td>5-Day Pass</td>
                    <td>₩15,000</td>
                    <td>Week-long stays</td>
                  </tr>
                  <tr>
                    <td>7-Day Pass</td>
                    <td>₩20,000</td>
                    <td>Longer vacations</td>
                  </tr>
                </tbody>
              </v-table>
  
              <v-alert type="warning" variant="tonal" density="compact" border="start" class="text-body-2 mb-6">
                <strong>Note:</strong> Physical card costs <strong>₩3,000</strong> (Cash only at convenience stores or subway info centers).
                <br>Does NOT cover: Shinbundang Line, Airport Railroad (AREX) outside Seoul, or Red buses.
              </v-alert>

              <!-- 상세 정보 버튼 -->
              <v-btn
                variant="outlined"
                color="primary"
                prepend-icon="mdi-open-in-new"
                href="https://english.seoul.go.kr/policy/transportation/climate-card/"
                target="_blank"
                rel="noopener noreferrer"
              >
                Find out more about Climate Card
              </v-btn>

            </v-col>
            
            <!-- Image Replacement Area: Icon & Text Info -->
            <v-col cols="12" md="5" class="bg-blue-grey-lighten-5 d-flex flex-column align-center justify-center pa-8 text-center">
              <v-sheet
                color="white"
                elevation="3"
                rounded="xl"
                width="240"
                height="360"
                class="d-flex flex-column align-center justify-center pa-6 position-relative"
              >
                <!-- Card Design Mockup -->
                <div class="text-h6 font-weight-black text-blue-grey-darken-3 mb-2">Climate Card</div>
                <div class="text-caption text-grey mb-6">Tourist Pass</div>
                
                <v-icon size="80" color="teal-lighten-1" class="mb-4">mdi-subway-variant</v-icon>
                <v-icon size="40" color="orange-lighten-1" class="position-absolute" style="bottom: 80px; right: 60px;">mdi-bus</v-icon>
                
                <v-divider class="w-100 my-6"></v-divider>
                
                <div class="d-flex align-center gap-2">
                  <v-icon size="small" color="primary">mdi-check-circle</v-icon>
                  <span class="text-caption font-weight-bold">Unlimited Access</span>
                </div>
                <div class="text-caption text-grey mt-1">Seoul Subway & Bus</div>
              </v-sheet>
              
              <div class="mt-6">
                <p class="text-body-2 font-weight-bold text-blue-grey-darken-3">Where to Buy?</p>
                <p class="text-caption text-grey-darken-1">
                  Subway Station Info Centers (Lines 1-8)<br>
                  Convenience Stores (GS25, CU) near stations
                </p>
              </div>
            </v-col>
          </v-row>
        </v-card>
      </section>
  
      <!-- 2. T-money (Standard Card) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-credit-card-chip</v-icon>
          T-money (Standard Card)
        </h2>
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="pa-6 h-100" elevation="2" border>
              <div class="text-subtitle-1 text-primary font-weight-bold mb-2">Pay-As-You-Go</div>
              <h3 class="text-h5 font-weight-bold mb-4">Why choose T-money?</h3>
              <v-list density="compact">
                <v-list-item prepend-icon="mdi-check" title="Use Nationwide (Bus, Subway, Taxi, AREX)"></v-list-item>
                <v-list-item prepend-icon="mdi-check" title="Use at Convenience Stores & Vending Machines"></v-list-item>
                <v-list-item prepend-icon="mdi-check" title="No expiry date on balance"></v-list-item>
              </v-list>
              <v-divider class="my-4"></v-divider>
              <p class="text-body-2 text-grey-darken-1">
                <strong>Price:</strong> Card starts from ₩3,000 (Empty) + Top-up needed.<br>
                <strong>Where to buy:</strong> Any convenience store (CU, GS25, 7-Eleven) or subway vending machines.
              </p>
            </v-card>
          </v-col>
          <v-col cols="12" md="6">
            <v-card class="pa-6 h-100 bg-grey-lighten-4" elevation="0">
              <h3 class="text-h6 font-weight-bold mb-4">Basic Fares (Seoul)</h3>
              <div class="d-flex justify-space-between mb-2">
                <span>Subway</span>
                <strong>₩1,550 ~</strong>
              </div>
              <div class="d-flex justify-space-between mb-4">
                <span>Bus (Blue/Green)</span>
                <strong>₩1,500 ~</strong>
              </div>
              <v-alert icon="mdi-transfer" density="compact" variant="outlined" color="grey-darken-2">
                <strong>Free Transfer:</strong> Available within 30 mins (up to 4 times) when transferring between subway and bus. 
                <br><em>*Must tap card when getting off!</em>
              </v-alert>
            </v-card>
          </v-col>
        </v-row>
      </section>
  
      <!-- 3. Taxi (Kakao T) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-taxi</v-icon>
          Taxi & Ride Hailing
        </h2>
        <v-card elevation="2" class="rounded-lg pa-6 border-l-lg border-primary" style="border-left-width: 6px !important;">
          <v-row align="center">
            <v-col cols="12" md="8">
              <h3 class="text-h6 font-weight-bold mb-2">Use "Kakao T" or "Uber (UT)" App</h3>
              <p class="text-body-1 mb-4">
                You don't need a Korean phone number or credit card to use <strong>Kakao T</strong>.
              </p>
              <div class="d-flex gap-4 mb-4">
                <v-chip prepend-icon="mdi-check-circle" color="success">English Support</v-chip>
                <v-chip prepend-icon="mdi-cash" color="warning">Pay to Driver (Cash/Card)</v-chip>
              </div>
              <p class="text-body-2 text-grey-darken-2">
                * Select <strong>"General Taxi"</strong> and choose <strong>"Pay to Driver"</strong> as payment method if you don't have a Korean card registered.
              </p>
            </v-col>
            <v-col cols="12" md="4" class="text-center">
              <div class="text-h4 font-weight-bold text-primary mb-1">₩4,800</div>
              <div class="text-caption text-grey">Base Fare (Daytime)</div>
              <div class="text-h5 font-weight-bold text-grey-darken-3 mt-4">₩6,700~</div>
              <div class="text-caption text-grey">Base Fare (Late Night 10PM-4AM)</div>
            </v-col>
          </v-row>
        </v-card>
      </section>
  
      <!-- 4. Airport to City (AREX) -->
      <section class="mb-16">
        <h2 class="text-h4 font-weight-bold mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-airplane-takeoff</v-icon>
          Airport to Seoul (AREX)
        </h2>
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="h-100" variant="outlined">
              <v-card-item>
                <v-card-title class="d-flex align-center text-orange-darken-2 font-weight-bold">
                  <v-icon start>mdi-train-variant</v-icon> Express Train
                </v-card-title>
                <v-card-subtitle>Non-stop: Incheon Int'l Airport ↔ Seoul Station</v-card-subtitle>
              </v-card-item>
              <v-card-text>
                <div class="text-h5 font-weight-bold mb-2">~43 mins</div>
                <p class="mb-2">Most comfortable and fastest way.</p>
                <v-chip size="small" color="orange-lighten-4" class="text-orange-darken-4 font-weight-bold">Reserved Seat</v-chip>
                <v-chip size="small" color="orange-lighten-4" class="text-orange-darken-4 font-weight-bold">Free WiFi</v-chip>
                <div class="mt-4 text-h6">₩13,000 <span class="text-caption text-grey">(Discounts available online)</span></div>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col cols="12" md="6">
            <v-card class="h-100" variant="outlined">
              <v-card-item>
                <v-card-title class="d-flex align-center text-blue-darken-2 font-weight-bold">
                  <v-icon start>mdi-subway</v-icon> All-stop Train
                </v-card-title>
                <v-card-subtitle>Stops at all stations (Hongik Univ, Gongdeok, etc.)</v-card-subtitle>
              </v-card-item>
              <v-card-text>
                <div class="text-h5 font-weight-bold mb-2">~59 mins</div>
                <p class="mb-2">Cheaper, works like a regular subway.</p>
                <v-chip size="small" color="blue-lighten-4" class="text-blue-darken-4 font-weight-bold">T-money Accepted</v-chip>
                <v-chip size="small" color="blue-lighten-4" class="text-blue-darken-4 font-weight-bold">Crowded</v-chip>
                <div class="mt-4 text-h6">₩4,750 ~ ₩5,350</div>
              </v-card-text>
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
.gap-4 {
  gap: 16px;
}
.border-l-lg {
  border-left-style: solid;
}
.border-primary {
  border-color: rgb(var(--v-theme-primary)) !important;
}
section {
  scroll-margin-top: 100px;
}
</style>