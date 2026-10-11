<template>
  <v-container class="py-10" max-width="1200">
    <div class="text-center mb-10">
      <h1 class="text-h3 font-weight-bold text-primary mb-3">All Places</h1>
      <p class="text-h6 text-grey-darken-1">Find your perfect spot in Seoul</p>
    </div>

    <!-- 검색 및 필터 바 -->
    <v-sheet class="pa-4 bg-grey-lighten-5 rounded-lg mb-8" elevation="1">
      <v-row align="center">
        <v-col cols="12" md="3">
          <v-select
            v-model="searchParams.contentTypeId"
            :items="categories"
            item-title="text"
            item-value="value"
            label="Category"
            variant="outlined"
            density="compact"
            hide-details
            bg-color="white"
            @update:model-value="onFilterChange"
          ></v-select>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="searchParams.keyword"
            label="Search places..."
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            bg-color="white"
            @keyup.enter="onFilterChange"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-btn color="primary" block height="40" @click="onFilterChange" :loading="loading">
            Search
          </v-btn>
        </v-col>
      </v-row>
    </v-sheet>

    <!-- 로딩 중 -->
    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      <div class="mt-4 text-grey">Loading all places...</div>
    </div>

    <!-- 장소 목록 (Grid) -->
    <v-row v-else-if="paginatedPlaces.length > 0">
      <v-col v-for="place in paginatedPlaces" :key="place.placeNo" cols="12" sm="6" md="3">
        <v-card class="h-100 rounded-lg elevation-1 hover-card" @click="goToDetail(place.placeNo)">
          <!-- 
            [이미지 처리 개선]
            getValidImageUrl에서 이미지가 없으면 빈 문자열을 반환하므로,
            빈 문자열이 아닌 경우에만 이미지를 표시하고, 아니면 placeholder를 표시
          -->
          <template v-if="getValidImageUrl(place)">
            <v-img
              :src="getValidImageUrl(place)"
              height="200"
              cover
              class="align-end bg-grey-lighten-2"
            >
              <template v-slot:placeholder>
                <div class="d-flex align-center justify-center fill-height bg-grey-lighten-2">
                  <v-progress-circular indeterminate color="grey-lighten-4" size="20"></v-progress-circular>
                </div>
              </template>
              <template v-slot:error>
                <!-- 로드 실패 시 보여줄 화면 -->
                <div class="d-flex flex-column align-center justify-center fill-height bg-grey-lighten-3 text-grey text-center pa-2">
                  <v-icon size="40" color="grey-lighten-1" class="mb-2">mdi-image-off</v-icon>
                  <span class="text-caption">Image N/A</span>
                </div>
              </template>
            </v-img>
          </template>

          <!-- 이미지가 아예 없는 경우 (DB 데이터 없음 & 수동 매핑 없음) -->
          <template v-else>
            <v-sheet
              height="200"
              color="grey-lighten-4"
              class="d-flex flex-column align-center justify-center text-center pa-4"
            >
              <v-icon size="48" color="grey-lighten-1" class="mb-2">mdi-camera-plus-outline</v-icon>
              <div class="text-caption text-grey-darken-1 font-weight-medium">No Image Available</div>
              <div class="text-caption text-grey mt-1" style="font-size: 0.7rem !important;">
                Please report a fancy image<br>to CONTACT US
              </div>
            </v-sheet>
          </template>

          <v-card-item>
            <v-card-title class="text-body-1 font-weight-bold text-truncate">{{ place.title }}</v-card-title>
            <v-card-subtitle class="text-truncate">{{ place.addr1 || 'Seoul, Korea' }}</v-card-subtitle>
          </v-card-item>
          
          <v-card-text class="pt-0 pb-2">
            <div class="d-flex align-center justify-space-between">
              <v-chip size="x-small" color="primary" variant="tonal" class="font-weight-bold">
                No. {{ place.placeNo }}
              </v-chip>
              
              <!-- [기능 1] 평점 표시 -->
              <div class="d-flex align-center">
                <v-rating 
                    :model-value="place.rating || 0" 
                    color="amber" 
                    density="compact" 
                    half-increments 
                    readonly 
                    size="small"
                ></v-rating>
                <span class="text-caption text-grey ml-1">({{ place.rating ? Number(place.rating).toFixed(1) : '0.0' }})</span>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 검색 결과 없음 -->
    <div v-else class="text-center py-16 bg-grey-lighten-5 rounded-lg">
      <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-map-search-outline</v-icon>
      <h3 class="text-h6 text-grey-darken-2">No places found.</h3>
      <p class="text-body-2 text-grey">Try adjusting your search filters.</p>
    </div>

    <!-- 페이지네이션 -->
    <div class="text-center mt-8" v-if="totalPages > 1">
      <v-pagination
        v-model="page"
        :length="totalPages"
        :total-visible="7"
        @update:model-value="scrollToTop"
        rounded="circle"
        active-color="primary"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { http } from '@/api/http';

const router = useRouter();
const route = useRoute();
const loading = ref(false);

const allPlaces = ref([]);
const filteredPlaces = ref([]);

const page = ref(1);
const itemsPerPage = 20;

const searchParams = reactive({
  keyword: '',
  contentTypeId: 0,
});

const categories = [
  { text: 'All Types', value: 0 },
  { text: 'Tourist Spot', value: 76 },
  { text: 'Cultural Facility', value: 78 },
  { text: 'Festival/Event', value: 85 },
  { text: 'Leisure/Sports', value: 75 },
  { text: 'Accommodation', value: 80 },
  { text: 'Shopping', value: 79 },
  { text: 'Restaurant', value: 82 },
  { text: 'Transportation', value: 77 },
];

// 수동 이미지 매핑 (Local Folder 파일명)
// placeNo(장소ID): '파일명.jpg'
// 이 파일들은 백엔드의 file.upload-dir 경로에 실제로 존재해야 함.
const manualImageMapping = {
  1: '219f8800-0ae4-4115-b4dd-111b3ecad483.jpg', 
  5: '5_sebit.jpg',
  8: '8_willspa.jpg',
  9: '9_samcheong.jpg',
  12: '12_ttuk.jpg',
  14: '14.jpg',
  2780: '2780.jpg',
  2782: '2782.jpg',
  2783: '2783.jpg',
  2787: '2787.jpg'
};

// [핵심 1] 상태가 변경될 때마다 URL을 동기화하는 함수
const updateUrl = () => {
  router.replace({
    query: {
      keyword: searchParams.keyword || undefined, // 빈 값은 URL에서 제거
      contentTypeId: searchParams.contentTypeId !== 0 ? searchParams.contentTypeId : undefined,
      page: page.value !== 1 ? page.value : undefined // 1페이지는 굳이 URL에 안 남겨도 됨
    }
  });
};

const loadAllPlaces = async () => {
  loading.value = true;
  try {
    const res = await http.get('/places');
    const rawData = res.data.data || []; 
    
    // ID순 정렬
    allPlaces.value = rawData.sort((a, b) => Number(a.placeNo) - Number(b.placeNo));
    filteredPlaces.value = allPlaces.value;
    
  } catch (err) {
    console.error("Failed to load places:", err);
  } finally {
    loading.value = false;
  }
};

// 필터 적용 로직 (resetPage 파라미터 추가)
// resetPage: 새로운 검색 시에는 1페이지로, URL 복구 시에는 해당 페이지 유지
const applyFilter = (resetPage = true) => {
  if (resetPage) {
    page.value = 1;
  }
  
  let result = allPlaces.value;

  if (searchParams.contentTypeId !== 0) {
    result = result.filter(p => p.contentTypeId === searchParams.contentTypeId);
  }

  if (searchParams.keyword.trim()) {
    const k = searchParams.keyword.toLowerCase();
    result = result.filter(p => 
      p.title.toLowerCase().includes(k) || 
      (p.addr1 && p.addr1.toLowerCase().includes(k))
    );
  }

  filteredPlaces.value = result;
  
  // 필터 적용 후 URL 업데이트 (검색 버튼 클릭 시 반영)
  if (resetPage) updateUrl();
};

// 템플릿에서 호출하는 함수 (검색 버튼, 엔터키)
const onFilterChange = () => {
  applyFilter(true); // 새로운 검색이므로 페이지 리셋
};

const totalPages = computed(() => {
  return Math.ceil(filteredPlaces.value.length / itemsPerPage) || 1;
});

const paginatedPlaces = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredPlaces.value.slice(start, end);
});

// 페이지 변경 감지 -> URL 업데이트 및 스크롤
watch(page, (newPage) => {
  updateUrl();
  scrollToTop();
});

// 유효한 이미지 URL 반환 함수 (수동 매핑 포함)
const getValidImageUrl = (place) => {
  // 1. DB에 firstImage가 있는 경우
  if (place.firstImage && place.firstImage.length > 5) {
    if (place.firstImage.startsWith('http')) return place.firstImage;
    return place.firstImage; // API 경로는 아닌 것으로 가정 (일반 URL)
  }
  // 2. DB에 firstImage2가 있는 경우
  if (place.firstImage2 && place.firstImage2.length > 5) {
    if (place.firstImage2.startsWith('http')) return place.firstImage2;
    return place.firstImage2;
  }

  // 수동 매핑 확인 (로컬 파일)
  // 백엔드의 다운로드 API(/api/files/download/)를 사용하여 로컬 파일 서빙
  if (manualImageMapping[place.placeNo]) {
    return `/api/files/download/${manualImageMapping[place.placeNo]}`;
  }

  return ''; // 이미지 없음
};

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const goToDetail = (id) => {
  router.push(`/places/${id}`);
};

// 마운트 시 데이터 로드 후 URL 파라미터 복구
onMounted(async () => {
  await loadAllPlaces(); // 1. 데이터 먼저 다 가져오기
  
  // 2. URL 쿼리 파라미터가 있다면 상태 변수에 복원
  if (route.query.keyword) {
    searchParams.keyword = route.query.keyword;
  }
  if (route.query.contentTypeId) {
    searchParams.contentTypeId = Number(route.query.contentTypeId);
  }
  if (route.query.page) {
    page.value = Number(route.query.page);
  }

  // 3. 복원된 상태로 필터 적용 (페이지 리셋 방지 false)
  applyFilter(false);
});
</script>

<style scoped>
.hover-card {
  transition: transform 0.2s, box-shadow 0.2s;
}
.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 12px rgba(0,0,0,0.15) !important;
}
</style>