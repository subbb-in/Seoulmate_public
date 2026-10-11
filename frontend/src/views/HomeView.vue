<template>
  <v-main class="pa-0">
    <!-- Section 1: Hero Banner -->
    <!-- asset 이미지를 src에 연결하세요 (예: src="@/assets/main_banner.jpg") -->
    <v-sheet
      class="d-flex align-center justify-center position-relative"
      color="gray-light"
      height="600"
    >
      <!-- 배경 이미지가 있다면 아래 v-img 주석 해제 후 사용 -->
      <v-img
        class="position-absolute w-100 h-100"
        cover
        src="@/assets/Seoul_1 1.png"
        style="opacity: 0.9;"
      >
        <div class="fill-height" style="background: rgba(0,0,0,0.3);"></div>
      </v-img>

      <!-- Hero Content -->
      <div class="text-center position-relative z-index-1 px-4">
        <h1 class="text-h2 font-weight-black text-primary mb-2">
          SEOUL MATE
        </h1>
        <h2 class="text-h4 font-weight-medium text-white mb-6" style="text-shadow: 0 2px 4px rgba(0,0,0,0.5);">
          Discover the Soul of Seoul
        </h2>
        <p class="text-h6 text-white mb-8" style="text-shadow: 0 1px 2px rgba(0,0,0,0.5);">
          Create your own journey in the heart of Korea.
        </p>

        <div class="d-flex justify-center gap-4">
          <v-btn
            class="px-8 font-weight-bold"
            color="primary"
            elevation="4"
            rounded="pill"
            size="x-large"
            to="/itinerary/create"
          >
            Start Journey
            <v-icon end icon="mdi-arrow-right" />
          </v-btn>
          <v-btn
            class="px-8 font-weight-bold bg-white text-primary"
            color="white"
            rounded="pill"
            size="x-large"
            to="/places"
            variant="outlined"
          >
            Explore Places
          </v-btn>
        </div>
      </div>
    </v-sheet>

    <!-- Section 2: Popular Places (Grid) -->
    <v-container class="py-16">
      <div class="text-center mb-12">
        <h3 class="text-h4 font-weight-bold text-black mb-2">Popular Attractions</h3>
        <p class="text-body-1 text-text-sub">
          Must-visit places recommended by locals and travelers
        </p>
        <v-divider class="mx-auto mt-4" color="primary" length="60" thickness="3" />
      </div>

      <v-row>
        <v-col
          v-for="i in 4"
          :key="i"
          cols="12"
          md="3"
          sm="6"
        >
          <v-card class="mx-auto rounded-lg elevation-2 hover-card" max-width="400">
            <!-- Place Image (Placeholder) -->
            <v-img
              class="align-end text-white"
              cover
              height="200"
              src="https://cdn.vuetifyjs.com/images/cards/docks.jpg"
            >
              <v-card-title class="font-weight-bold" style="text-shadow: 0 2px 4px rgba(0,0,0,0.8);">
                Place Name {{ i }}
              </v-card-title>
            </v-img>

            <v-card-text class="pt-4">
              <div class="d-flex align-center mb-2">
                <v-rating
                  color="warning"
                  density="compact"
                  half-increments
                  :model-value="4.5"
                  readonly
                  size="small"
                />
                <span class="text-caption text-text-sub ml-2">(4.5)</span>
              </div>
              <div class="text-body-2 text-text-sub text-truncate">
                Experience the traditional beauty of Korea in the middle of the city.
              </div>
            </v-card-text>

            <v-card-actions>
              <v-spacer />
              <v-btn
                class="font-weight-bold"
                color="primary"
                variant="text"
              >
                View Details
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <div class="text-center mt-12">
        <v-btn
          color="text-sub"
          rounded="pill"
          size="large"
          to="/places"
          variant="outlined"
        >
          View All Places
        </v-btn>
      </div>
    </v-container>

    <!-- Section 3: Features / Banner -->
    <v-sheet class="py-16" color="accent">
      <v-container>
        <v-row align="center">
          <v-col cols="12" md="6">
            <h3 class="text-h3 font-weight-black text-primary mb-4">
              AI Itinerary Planner
            </h3>
            <p class="text-h6 text-black mb-6">
              Not sure where to go? <br>
              Let our AI recommend the best route for you based on your preferences.
            </p>
            <v-list bg-color="transparent">
              <v-list-item class="px-0">
                <template #prepend>
                  <v-icon color="primary" icon="mdi-check-circle" />
                </template>
                <v-list-item-title class="text-body-1 font-weight-medium">Optimized Routes</v-list-item-title>
              </v-list-item>
              <v-list-item class="px-0">
                <template #prepend>
                  <v-icon color="primary" icon="mdi-check-circle" />
                </template>
                <v-list-item-title class="text-body-1 font-weight-medium">Personalized Recommendations</v-list-item-title>
              </v-list-item>
              <v-list-item class="px-0">
                <template #prepend>
                  <v-icon color="primary" icon="mdi-check-circle" />
                </template>
                <v-list-item-title class="text-body-1 font-weight-medium">Time & Budget Saving</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>
          <v-col class="text-center" cols="12" md="6">
            <!-- Feature Image Placeholder -->
            <v-icon color="white" icon="mdi-robot-happy" size="200" />
          </v-col>
        </v-row>
      </v-container>
    </v-sheet>

    <!-- Section 4: Trusted By (Partner Logos) -->
    <v-container class="py-10">
      <p class="text-center text-subtitle-1 text-text-sub font-weight-medium mb-8">
        TRUSTED BY
      </p>
      <v-row align="center" class="px-md-16" justify="center">
        <v-col
          v-for="n in 4"
          :key="n"
          class="d-flex justify-center"
          cols="6"
          md="3"
        >
          <!-- 이미지 플레이스홀더: 회색 박스로 표시됨 -->
          <v-sheet
            class="d-flex align-center justify-center rounded-lg"
            color="white"
            height="80"
            width="160"
          >
            <v-img contain :src="`/src/assets/trusted_logo_${n}.png`" width="120" />
            <!-- <span class="text-caption text-text-sub">Partner Logo {{ n }}</span> -->
          </v-sheet>
        </v-col>
      </v-row>
      <!-- 섹션 구분선 -->
      <v-divider class="mt-10 mx-auto" color="gray-light" length="100" />
    </v-container>
  </v-main>
</template>

<script setup>
// 로직은 추후 추가
</script>

<style scoped>
.hover-card {
  transition: transform 0.3s ease;
}
.hover-card:hover {
  transform: translateY(-8px);
}
.gap-4 {
  gap: 16px;
}
.z-index-1 {
  z-index: 1;
}
</style>
