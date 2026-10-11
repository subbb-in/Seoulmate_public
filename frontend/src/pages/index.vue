<template>
  <v-main class="pa-0">
    <!-- Section 1: Hero Banner -->
    <v-sheet class="d-flex align-center justify-center position-relative" color="gray-light" height="600">
      <v-img class="position-absolute w-100 h-100" cover src="@/assets/Seoul_1 1.png" style="opacity: 0.9;">
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
          <!-- Start Journey 경로를 /itineraries/createItinerary 로 명확히 지정 -->
          <v-btn class="px-8 font-weight-bold" color="primary" elevation="4" rounded="pill" size="x-large"
            to="/itineraries/createItinerary">
            Start Journey
            <v-icon end icon="mdi-arrow-right" />
          </v-btn>
          <v-btn class="px-8 font-weight-bold bg-white text-primary" color="white" rounded="pill" size="x-large"
            to="/places" variant="outlined">
            Explore Places
          </v-btn>
        </div>
      </div>
      <div class="position-absolute bottom-0 mb-8 text-center w-100 z-index-1">
        <v-btn icon="mdi-chevron-down" variant="text" color="white" size="x-large" class="bounce-animation"
          @click="scrollToContent"></v-btn>
      </div>
    </v-sheet>

    <!-- Section 2: Popular Places (Grid) -->
    <v-container class="py-16" id="target-section">
      <div class="text-center mb-12">
        <h3 class="text-h4 font-weight-bold text-black mb-2">Popular Attractions</h3>
        <p class="text-body-1 text-text-sub">
          Must-visit places recommended by locals and travelers
        </p>
        <v-divider class="mx-auto mt-4" color="primary" length="60" thickness="3" />
      </div>

      <!-- 로딩 중 -->
      <div v-if="loading" class="text-center py-10">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </div>

      <v-row v-else>
        <!-- 실제 데이터 연동 -->
        <v-col v-for="place in popularPlaces" :key="place.placeNo" cols="12" md="3" sm="6">
          <v-card class="mx-auto rounded-lg elevation-2 hover-card h-100" max-width="400"
            @click="goToDetail(place.placeNo)">
            <!-- Place Image -->
            <v-img class="align-end text-white bg-grey-lighten-2" cover height="200" :src="getValidImageUrl(place)">
              <v-card-title class="font-weight-bold" style="text-shadow: 0 2px 4px rgba(0,0,0,0.8);">
                {{ place.title }}
              </v-card-title>

              <template v-slot:error>
                <!-- 이미지 로드 실패 시 로고 표시 -->
                <div class="d-flex align-center justify-center fill-height bg-grey-lighten-3">
                  <v-img src="@/assets/SeoulMate_logo_1.png" width="100" contain></v-img>
                </div>
              </template>
            </v-img>

            <v-card-text class="pt-4">
              <div class="d-flex align-center mb-2">
                <v-rating color="warning" density="compact" half-increments :model-value="place.rating || 0" readonly
                  size="small" />
                <span class="text-caption text-text-sub ml-2">({{ place.rating ? Number(place.rating).toFixed(1) : '0.0'
                  }})</span>
              </div>
              <div class="text-body-2 text-text-sub text-truncate">
                {{ place.addr1 || 'Seoul, South Korea' }}
              </div>
            </v-card-text>

            <v-card-actions>
              <v-spacer />
              <v-btn class="font-weight-bold" color="primary" variant="text">
                View Details
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <div class="text-center mt-12">
        <v-btn color="text-sub" rounded="pill" size="large" to="/places" variant="outlined">
          View All Places
        </v-btn>
      </div>
    </v-container>

    <!-- Section 3: Features / Banner -->
    <v-sheet class="py-16 overflow-hidden position-relative" color="#f9fafb">
      <v-container style="max-width: 1200px;">
        <v-row align="center" justify="space-between">

          <v-col cols="12" md="6" class="pl-md-4">
            <v-chip color="primary" variant="tonal" size="small" class="mb-6 font-weight-bold px-4"
              style="letter-spacing: 0.5px;">
              AI BETA
            </v-chip>

            <h2 class="text-h3 font-weight-bold text-grey-darken-4 mb-6" style="line-height: 1.3;">
              AI Itinerary Planner
            </h2>

            <p class="text-h6 text-grey-darken-1 font-weight-regular mb-8" style="line-height: 1.6;">
              Not sure where to go? <br class="d-none d-md-block">
              Let our AI recommend the best route for you based on your preferences.
            </p>

            <div class="d-flex flex-column gap-3 mb-10">
              <div class="d-flex align-center">
                <v-icon icon="mdi-check-circle" color="primary" class="mr-3" size="24" />
                <span class="text-h6 text-grey-darken-3 font-weight-medium">Optimized Routes</span>
              </div>
              <div class="d-flex align-center">
                <v-icon icon="mdi-check-circle" color="primary" class="mr-3" size="24" />
                <span class="text-h6 text-grey-darken-3 font-weight-medium">Personalized Recommendations</span>
              </div>
              <div class="d-flex align-center">
                <v-icon icon="mdi-check-circle" color="primary" class="mr-3" size="24" />
                <span class="text-h6 text-grey-darken-3 font-weight-medium">Time & Budget Saving</span>
              </div>
            </div>

            <v-hover v-slot="{ isHovering, props }">
              <div class="d-inline-block" v-bind="props">
                <v-btn color="primary" size="x-large" rounded="pill" prepend-icon="mdi-robot"
                  to="/itineraries/create-ai" :elevation="isHovering ? 8 : 0"
                  class="px-8 font-weight-bold transition-all" height="64">
                  Try AI Itinerary Planner
                </v-btn>
              </div>
            </v-hover>
          </v-col>

          <v-col cols="12" md="5" class="text-center position-relative mt-10 mt-md-0">
            <div class="blob-bg"></div>

            <v-card class="floating-card d-flex align-center justify-center mx-auto bg-white" elevation="0" rounded="xl"
              width="100%" max-width="400" aspect-ratio="1" style="border: 1px solid rgba(0,0,0,0.05);">
              <v-icon color="primary" icon="mdi-rocket-launch-outline" size="150" />
            </v-card>
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
        <v-col v-for="n in 4" :key="n" class="d-flex justify-center" cols="6" md="3">
          <v-sheet class="d-flex align-center justify-center rounded-lg" color="white" height="80" width="160">
            <v-img contain :src="`/src/assets/trusted_logo_${n}.png`" width="120" />
          </v-sheet>
        </v-col>
      </v-row>
      <v-divider class="mt-10 mx-auto" color="gray-light" length="100" />
    </v-container>
  </v-main>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { http } from '@/api/http';
import logoImg from '@/assets/SeoulMate_logo_1.png'; // 로고 임포트

const router = useRouter();
const popularPlaces = ref([]);
const loading = ref(false);

const scrollToContent = () => {
  const element = document.getElementById('target-section');
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

// 이미지 매핑 (index.vue와 동일하게 유지)
const manualImageMapping = {
  1: '219f8800-0ae4-4115-b4dd-111b3ecad483.jpg',
};

// 배열 섞기 함수 (Fisher-Yates Shuffle)
const shuffleArray = (array) => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
};

const fetchPopularPlaces = async () => {
  loading.value = true;
  try {
    // API 호출
    const res = await http.get('/places');
    let data = res.data.data || [];

    // 데이터를 랜덤하게 섞고 앞에서 4개 자름
    if (data.length > 0) {
      data = shuffleArray(data);
      popularPlaces.value = data.slice(0, 4);
    } else {
      popularPlaces.value = [];
    }

  } catch (e) {
    console.error("Failed to load popular places", e);
  } finally {
    loading.value = false;
  }
};

const getValidImageUrl = (place) => {
  if (place.firstImage && place.firstImage.length > 5) {
    if (place.firstImage.startsWith('http')) return place.firstImage;
    return place.firstImage;
  }
  if (place.firstImage2 && place.firstImage2.length > 5) {
    if (place.firstImage2.startsWith('http')) return place.firstImage2;
    return place.firstImage2;
  }
  if (manualImageMapping[place.placeNo]) {
    return `/api/files/download/${manualImageMapping[place.placeNo]}`;
  }
  // 이미지가 없으면 로고 반환 (v-img src에 바인딩 될 값)
  // 단, v-img의 error 슬롯에서도 처리하고 있으므로, 여기선 빈 문자열을 반환하여 error 슬롯이 트리거되게 하거나
  // 직접 로고 경로를 반환할 수도 있음. 여기선 로고 경로 반환.
  return logoImg;
};

const goToDetail = (id) => {
  router.push(`/places/${id}`);
};

onMounted(() => {
  fetchPopularPlaces();
});
</script>

<style scoped>
.hover-card {
  transition: transform 0.3s ease;
  cursor: pointer;
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

/* 위아래 움직이는 애니메이션 정의 */
@keyframes bounce {

  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }

  40% {
    transform: translateY(-10px);
  }

  60% {
    transform: translateY(-5px);
  }
}

/* [추가] 애니메이션 클래스 적용 */
.bounce-animation {
  animation: bounce 2s infinite;
  opacity: 0.8;
  transition: opacity 0.3s;
}

.bounce-animation:hover {
  opacity: 1;
  cursor: pointer;
}

/* 잘리는 헤더 해결 */
#target-section {
  scroll-margin-top: 50px;
  /* 100px 정도 여유를 주면 헤더에 가리지 않고 딱 보기 좋습니다 */
}

.gap-3 {
  gap: 12px;
}

/* 버튼 트랜지션 부드럽게 */
.transition-all {
  transition: all 0.3s ease;
}

/* 둥둥 떠다니는 애니메이션 (Floating Effect) */
@keyframes float {
  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-20px);
  }

  100% {
    transform: translateY(0px);
  }
}

.floating-card {
  animation: float 6s ease-in-out infinite;
  /* 부드럽고 넓게 퍼지는 그림자 */
  box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.12) !important;
}

/* 배경 장식용 흐릿한 원 */
.blob-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(var(--v-theme-primary), 0.15) 0%, rgba(255, 255, 255, 0) 70%);
  z-index: 0;
  pointer-events: none;
}
</style>