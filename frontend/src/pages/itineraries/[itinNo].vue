<template>
  <!-- [Layout] 전체 높이 고정 및 스크롤 제거 -->
  <v-container fluid class="pa-0 bg-grey-lighten-4" style="height: 100vh; overflow: hidden;">
    <v-row no-gutters class="fill-height">

      <!-- =========================================================================
           [왼쪽 패널] 여행 일정 관리 (Drag & Drop, 삭제)
           ========================================================================= -->
      <v-col cols="12" md="7" class="d-flex flex-column border-e bg-white h-100">

        <!-- 툴바 -->
        <v-toolbar color="white" elevation="1" density="compact" class="px-4 flex-shrink-0 border-b">
          <v-btn icon="mdi-arrow-left" @click="$router.back()" variant="text" size="small"></v-btn>
          <v-toolbar-title class="font-weight-bold text-subtitle-1">
            Plan Your Schedule
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <div class="text-caption text-grey mr-3">
            Total locations: {{ totalAddedPlaces }}
          </div>
          <!-- 저장 상태 표시 -->
          <v-chip v-if="isProcessing" color="warning" variant="tonal" size="x-small" prepend-icon="mdi-sync">
            Saving…
          </v-chip>
          <v-chip v-else color="success" variant="tonal" size="x-small" prepend-icon="mdi-check">
            Saved successfully
          </v-chip>
        </v-toolbar>

        <!-- Day 탭 (백엔드 데이터 기준) -->
        <div class="bg-grey-lighten-5 border-b flex-shrink-0">
          <v-tabs v-model="selectedDayIdx" color="primary" show-arrows align-tabs="start" density="compact">
            <v-tab v-for="(dayItems, index) in itineraryDays" :key="index" :value="index" class="text-caption">
              Day {{ index + 1 }}
            </v-tab>
          </v-tabs>
        </div>

        <!-- 
          [드롭 영역] 
          왼쪽 패널의 빈 공간이나 리스트 전체를 드롭 타겟으로 설정
        -->
        <div class="flex-grow-1 overflow-y-auto pa-4 bg-grey-lighten-5" @dragover.prevent
          @drop="onContainerDrop($event)">
          <!-- 장소가 없을 때 표시 -->
          <div v-if="!itineraryDays[selectedDayIdx] || itineraryDays[selectedDayIdx].length === 0"
            class="d-flex flex-column align-center justify-center h-100 text-grey">
            <v-icon size="48" color="grey-lighten-2" class="mb-3">mdi-map-marker-path</v-icon>
            <p class="text-subtitle-2 text-grey-darken-1">Day {{ selectedDayIdx + 1 }} The schedule is empty.</p>
            <p class="text-caption">You can search for a place on the right and add it.</p>
          </div>

          <!-- 타임라인 (Drag & Drop 리스트) -->
          <v-timeline v-else side="end" density="compact" class="pa-2">
            <v-timeline-item v-for="(item, idx) in itineraryDays[selectedDayIdx]"
              :key="item.itinPlaceNo || ('temp-' + idx)" dot-color="primary" size="x-small" width="100%"
              class="draggable-item transition-swing">
              <template v-slot:icon>
                <span class="text-caption font-weight-bold text-white">{{ idx + 1 }}</span>
              </template>

              <!-- 
                [드래그 가능한 카드]
                draggable="true" 속성 필수
              -->
              <v-card elevation="1" class="mb-2 cursor-move" :class="{ 'opacity-50': draggedItem === item }"
                draggable="true" @dragstart="onDragStart($event, item, selectedDayIdx, idx)" @dragover.prevent
                @drop.stop.prevent="onItemDrop($event, selectedDayIdx, idx)">
                <div class="d-flex align-center">
                  <!-- 핸들 아이콘 -->
                  <div class="pl-2 pr-1 text-grey-lighten-1 cursor-grab">
                    <v-icon size="small">mdi-drag-vertical</v-icon>
                  </div>

                  <v-avatar size="60" rounded="0" class="flex-shrink-0 my-1 ml-1 rounded">
                    <!-- getValidImageUrl 사용 -->
                    <v-img :src="getValidImageUrl(item)" cover class="bg-grey-lighten-2">
                      <template v-slot:placeholder>
                        <div class="d-flex align-center justify-center fill-height">
                          <v-progress-circular indeterminate size="16"></v-progress-circular>
                        </div>
                      </template>
                      <template v-slot:error>
                        <v-icon size="small" color="grey-lighten-1">mdi-image-off</v-icon>
                      </template>
                    </v-img>
                  </v-avatar>

                  <div class="flex-grow-1 pa-2 d-flex flex-column justify-center" style="min-width: 0;">
                    <div class="d-flex justify-space-between align-start">
                      <div class="text-subtitle-2 font-weight-bold text-truncate">{{ item.title }}</div>
                      <!-- 삭제 버튼 -->
                      <v-btn icon="mdi-close" size="x-small" variant="text" color="grey" class="ml-1"
                        :loading="isProcessing === item.itinPlaceNo" @mousedown.stop
                        @click.stop="removeFromItinerary(item)"></v-btn>
                    </div>
                    <div class="text-caption text-grey text-truncate">{{ item.addr1 }}</div>
                    <div class="mt-1 d-flex align-center">
                      <v-chip size="x-small" label color="secondary" density="compact" style="height: 20px;">
                        {{ getCategoryName(item.contentTypeId) }}
                      </v-chip>
                      <!-- PK 확인용 (디버깅) -->
                      <span class="text-caption text-grey-lighten-1 ml-2" style="font-size: 0.6rem;">
                        #{{ item.itinPlaceNo }}
                      </span>
                    </div>
                  </div>
                </div>
              </v-card>
            </v-timeline-item>
          </v-timeline>
        </div>
      </v-col>


      <!-- =========================================================================
           [오른쪽 패널] 장소 검색 및 북마크
           ========================================================================= -->
      <v-col cols="12" md="5" class="d-flex flex-column bg-white border-l h-100">
        
        <!-- [탭] 검색 vs 북마크 -->
        <div class="border-b bg-white flex-shrink-0">
          <v-tabs v-model="rightPanelTab" grow color="primary" density="compact" @update:model-value="onTabChange">
            <v-tab value="search">Search Places</v-tab>
            <v-tab value="bookmarks">My Bookmarks</v-tab>
          </v-tabs>
        </div>

        <!-- [검색 필터] 검색 탭일 때만 표시 -->
        <div v-if="rightPanelTab === 'search'" class="pa-3 border-b bg-white flex-shrink-0">
          <v-row dense>
            <v-col cols="4">
              <v-select v-model="searchCategory" :items="categories" item-title="text" item-value="value"
                label="categories" variant="outlined" density="compact" hide-details bg-color="white"
                @update:model-value="onSearch"></v-select>
            </v-col>
            <v-col cols="8">
              <v-text-field v-model="searchKeyword" label="search by keyword..." prepend-inner-icon="mdi-magnify"
                variant="outlined" density="compact" hide-details clearable bg-color="white"
                @input="onSearch"></v-text-field>
            </v-col>
          </v-row>
        </div>

        <!-- [북마크 안내] 북마크 탭일 때 표시 -->
        <div v-if="rightPanelTab === 'bookmarks'" class="pa-3 border-b bg-grey-lighten-5 flex-shrink-0 d-flex justify-space-between align-center">
          <span class="text-caption font-weight-bold text-primary">
            <v-icon size="small" class="mr-1">mdi-bookmark</v-icon>
            My Saved Places ({{ bookmarkedPlaces.length }})
          </span>
          <v-btn size="x-small" variant="text" icon="mdi-refresh" @click="loadBookmarks"></v-btn>
        </div>

        <!-- 리스트 영역 -->
        <div class="flex-grow-1 overflow-y-auto pa-2 bg-grey-lighten-5" id="search-results-area">
          <div v-if="loading" class="d-flex justify-center align-center py-10">
            <v-progress-circular indeterminate color="primary"></v-progress-circular>
          </div>

          <!-- 통합 리스트 렌더링 (검색 결과 & 북마크 공용) -->
          <div v-else-if="paginatedPlaces.length > 0">
            <v-card v-for="place in paginatedPlaces" :key="place.placeNo" class="mb-3 hover-card rounded-lg"
              elevation="0" border @click="openMap(place)" style="cursor: pointer; transition: all 0.2s;">
              <div class="d-flex align-center pa-2">
                <v-avatar size="90" class="rounded-lg mr-4 border" rounded="0">
                  <!-- getValidImageUrl 사용 -->
                  <v-img :src="getValidImageUrl(place)" cover class="bg-grey-lighten-2">
                    <template v-slot:error>
                      <v-icon color="grey-lighten-1">mdi-image-off</v-icon>
                    </template>
                  </v-img>
                </v-avatar>

                <div class="flex-grow-1 py-1" style="min-width: 0;">
                  <div class="d-flex justify-space-between align-start mb-1">
                    <div class="text-subtitle-1 font-weight-bold text-truncate text-grey-darken-3"
                      style="max-width: 180px;">
                      {{ place.title }}
                    </div>
                    <div class="d-flex align-center bg-amber-lighten-5 px-2 rounded-pill">
                      <v-icon size="14" color="amber" class="mr-1">mdi-star</v-icon>
                      <span class="text-caption font-weight-bold text-grey-darken-2">
                        {{ place.rating ? Number(place.rating).toFixed(1) : '0.0' }}
                      </span>
                    </div>
                  </div>

                  <div class="text-caption text-grey mb-2 text-truncate">
                    {{ place.addr1 }}
                  </div>

                  <div class="d-flex align-center justify-space-between">
                    <v-chip size="x-small" label color="grey-darken-1" class="font-weight-medium bg-grey-lighten-4"
                      style="height: 20px;">
                      {{ getCategoryName(place.contentTypeId) }}
                    </v-chip>

                    <div class="d-flex align-center">
                      <v-btn size="small" color="primary" variant="tonal" rounded="pill" prepend-icon="mdi-plus"
                        class="font-weight-bold px-4" :loading="isProcessing === place.placeNo"
                        @click.stop="addToItinerary(place)">
                        Add
                      </v-btn>
                    </div>
                  </div>
                </div>
              </div>
            </v-card>
          </div>

          <!-- 데이터 없음 (검색 결과 없음 / 북마크 없음) -->
          <div v-else class="text-center py-10 text-grey">
            <v-icon size="40" class="mb-2">
              {{ rightPanelTab === 'search' ? 'mdi-alert-circle-outline' : 'mdi-bookmark-off-outline' }}
            </v-icon>
            <p class="text-caption">
              {{ rightPanelTab === 'search' ? 'No Search Result.' : 'No bookmarked places found.' }}
            </p>
            <v-btn v-if="rightPanelTab === 'bookmarks'" to="/places" variant="text" size="small" color="primary">
              Explore Places
            </v-btn>
          </div>
        </div>

        <!-- 페이지네이션 -->
        <div class="bg-white border-t py-1 flex-shrink-0" v-if="totalListCount > 0">
          <v-pagination v-model="page" :length="totalPages" :total-visible="5" density="compact" active-color="primary"
            variant="text" size="small"></v-pagination>
        </div>
      </v-col>
    </v-row>

    <!-- 스낵바 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" location="bottom right" timeout="2000">
      {{ snackbar.text }}
    </v-snackbar>

    <!-- 지도 팝업 (상세 정보) -->
    <v-dialog v-model="mapDialog" max-width="800">
      <v-card v-if="selectedPlace" class="rounded-lg overflow-hidden">
        <v-toolbar color="white" density="compact" class="border-b px-2">
          <v-toolbar-title class="text-h6 font-weight-bold text-grey-darken-3">
            {{ selectedPlace.title }}
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon="mdi-close" variant="text" @click="mapDialog = false"></v-btn>
        </v-toolbar>

        <v-card-text class="pa-0">
          <div class="d-flex pa-4 bg-grey-lighten-5">
            <v-avatar size="110" rounded="lg" class="mr-4 border elevation-1">
              <!-- getValidImageUrl 사용 -->
              <v-img :src="getValidImageUrl(selectedPlace)" cover class="bg-white">
                <template v-slot:error>
                  <v-icon size="large" color="grey-lighten-1">mdi-image-off</v-icon>
                </template>
              </v-img>
            </v-avatar>

            <div class="flex-grow-1 d-flex flex-column justify-center">
              <div class="d-flex align-center mb-2">
                <v-rating :model-value="Number(selectedPlace.rating || 0)" color="amber" density="compact" size="small"
                  readonly half-increments></v-rating>
                <span class="ml-2 text-body-2 font-weight-bold">{{ Number(selectedPlace.rating || 0).toFixed(1)
                  }}</span>
                <v-divider vertical class="mx-3 my-1"></v-divider>
                <span class="text-caption text-primary font-weight-bold border px-2 py-1 rounded bg-white">
                  {{ getCategoryName(selectedPlace.contentTypeId) }}
                </span>
              </div>

              <div class="d-flex align-start text-body-2 text-grey-darken-3 mb-1">
                <v-icon size="small" color="primary" class="mr-2 mt-1">mdi-map-marker</v-icon>
                {{ selectedPlace.addr1 }}
              </div>

              <div class="d-flex align-center text-caption text-grey" v-if="selectedPlace.tel">
                <v-icon size="small" class="mr-2">mdi-phone</v-icon>
                {{ selectedPlace.tel }}
              </div>

              <div class="mt-3">
                <v-btn size="small" variant="tonal" color="grey-darken-3" rounded="pill" prepend-icon="mdi-open-in-new"
                  class="font-weight-bold px-3" @click.stop="goToPlaceDetail(selectedPlace.placeNo)">
                  more detail
                </v-btn>
              </div>
            </div>
          </div>

          <v-divider></v-divider>

          <div class="position-relative" style="height: 450px; width: 100%;">
            <div id="popup-map" style="width: 100%; height: 100%;"></div>

            <!-- 좌표가 없을 경우 안내 문구 -->
            <div v-if="!selectedPlace.mapx || !selectedPlace.mapy"
              class="d-flex flex-column align-center justify-center position-absolute w-100 h-100 bg-grey-lighten-4"
              style="top:0; left:0; z-index: 5;">
              <v-icon size="48" color="grey">mdi-map-marker-off</v-icon>
              <div class="mt-2 text-grey-darken-1 font-weight-bold">Location info not available</div>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { http } from '@/api/http';
import { fetchMyBookmarks } from '@/api/mypage.api.js';

// 기본 이미지 import
import defaultImage from '@/assets/SeoulMate_logo_1.png';

const route = useRoute();
const router = useRouter();
const itinNo = route.params.itinNo; // URL 파라미터

const mapDialog = ref(false);
const selectedPlace = ref(null);

// 리스트 아이템 클릭 시 실행될 함수 (검색 결과 & 북마크 공용)
const openMap = async (place) => {
  // 1. 선택된 장소 임시 저장
  let targetPlace = { ...place };

  // 2. 좌표 정보 확인 (북마크 리스트에는 좌표가 없을 수 있음)
  if (!targetPlace.mapy || !targetPlace.mapx) {
    try {
      // 좌표가 없으면 상세 정보를 API로 한 번 더 조회해서 채움
      const res = await http.get(`/places/${targetPlace.placeNo}`);
      if (res.data && res.data.data) {
        // 상세 정보로 교체 (좌표, 전화번호 등 포함됨)
        targetPlace = res.data.data;
      }
    } catch (e) {
      console.error("Failed to fetch place detail:", e);
    }
  }

  selectedPlace.value = targetPlace;
  mapDialog.value = true;

  // 3. 지도 초기화 (좌표가 있는 경우에만)
  if (targetPlace.mapy && targetPlace.mapx) {
    await nextTick();
    setTimeout(() => {
      initMap(Number(targetPlace.mapy), Number(targetPlace.mapx));
    }, 200);
  }
};

// 네이버 지도 SDK를 이용해 지도를 그리는 함수
const initMap = (lat, lng) => {
  if (window.naver && window.naver.maps) {
    try {
      const mapOptions = {
        center: new window.naver.maps.LatLng(lat, lng),
        zoom: 16, // 줌 레벨 설정
        scrollWheel: true,
        draggable: true
      };

      // 'popup-map' ID를 가진 div에 지도 생성
      const map = new window.naver.maps.Map('popup-map', mapOptions);

      // 마커 추가
      new window.naver.maps.Marker({
        position: new window.naver.maps.LatLng(lat, lng),
        map: map
      });
    } catch (e) {
      console.error("Naver Map Init Error:", e);
    }
  } else {
    console.warn("Naver Maps SDK not loaded");
  }
};

// 장소 상세 페이지(새 탭)
const goToPlaceDetail = (placeNo) => {
  const url = `/places/${placeNo}`;
  window.open(url, '_blank');
};

// --- 상태 변수 ---
const isProcessing = ref(null);
const selectedDayIdx = ref(0);
const itineraryDays = ref([]);

// Drag & Drop
const draggedItem = ref(null);
const draggedFromDayIdx = ref(null);
const draggedFromIndex = ref(null);

// 오른쪽 패널 (검색 / 북마크)
const rightPanelTab = ref('search'); // 'search' or 'bookmarks'
const loading = ref(false);
const page = ref(1);
const itemsPerPage = 13;
const snackbar = reactive({ show: false, text: '', color: 'success' });

// 1) 검색 관련 데이터
const allPlaces = ref([]);
const filteredPlaces = ref([]);
const searchKeyword = ref('');
const searchCategory = ref(0);

// 2) 북마크 관련 데이터
const bookmarkedPlaces = ref([]);
const isBookmarksLoaded = ref(false);

// 상수
const categories = [
  { text: 'All Types', value: 0 },
  { text: 'Tourist Spot', value: 76 },
  { text: 'Cultural Facility', value: 78 },
  { text: 'Festival/Event', value: 85 },
  { text: 'Leisure/Sports', value: 75 },
  { text: 'Accomodations', value: 80 },
  { text: 'Shopping', value: 79 },
  { text: 'Restaurants', value: 82 },
  { text: 'Transportations', value: 77 },
];

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

// --- 초기 로딩 ---
const loadItinerary = async () => {
  try {
    // 1. 전체 장소 리스트 로드 (검색용)
    await loadAllPlaces();

    // 2. 일정 상세 정보 로드
    const res = await http.get(`/itineraries/${itinNo}`);

    const responseData = res.data.data || {};
    const basicInfo = responseData.basic || {};
    const placesList = responseData.places || [];

    // 일수(Day Count) 계산
    let daysCount = 1;
    if (basicInfo.dayCnt) {
      daysCount = basicInfo.dayCnt;
    } else if (responseData.dayCnt) {
      daysCount = responseData.dayCnt;
    } else if (basicInfo.startDate && basicInfo.endDate) {
      const start = new Date(basicInfo.startDate);
      const end = new Date(basicInfo.endDate);
      const diffTime = Math.abs(end - start);
      daysCount = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    }

    // 일수만큼 배열 초기화
    itineraryDays.value = Array.from({ length: Math.max(daysCount, 1) }, () => []);

    // 3. 장소 목록 동기화
    if (placesList.length > 0) {
      syncItineraryData(placesList);
    }
  } catch (err) {
    console.error("Failed to load itinerary:", err);
    showSnackbar(`Failed to load itinerary: ${err.message}`, "error");
  }
};

const loadAllPlaces = async () => {
  loading.value = true;
  try {
    const res = await http.get('/places');
    const rawData = res.data.data || [];
    allPlaces.value = rawData.sort((a, b) => Number(a.placeNo) - Number(b.placeNo));
    onSearch(); // 초기 검색 필터링
  } catch (err) {
    console.error("Failed to load places:", err);
  } finally {
    loading.value = false;
  }
};

// 북마크 불러오기
const loadBookmarks = async () => {
  loading.value = true;
  try {
    const response = await fetchMyBookmarks();
    const data = response.data || response;
    
    if (Array.isArray(data)) {
      bookmarkedPlaces.value = data;
    } else {
      bookmarkedPlaces.value = [];
    }
    isBookmarksLoaded.value = true;
  } catch (err) {
    console.error("Failed to load bookmarks:", err);
    showSnackbar("Failed to load bookmarks.", "error");
  } finally {
    loading.value = false;
  }
};

// 탭 변경 시 핸들러
const onTabChange = (val) => {
  page.value = 1; // 페이지 초기화
  if (val === 'bookmarks' && !isBookmarksLoaded.value) {
    loadBookmarks();
  }
};

// --- 검색 로직 ---
const onSearch = () => {
  page.value = 1;
  let result = allPlaces.value;

  if (searchCategory.value !== 0) {
    result = result.filter(p => p.contentTypeId === searchCategory.value);
  }

  if (searchKeyword.value && searchKeyword.value.trim() !== '') {
    const k = searchKeyword.value.toLowerCase().trim();
    result = result.filter(p =>
      p.title.toLowerCase().includes(k) ||
      (p.addr1 && p.addr1.toLowerCase().includes(k))
    );
  }
  filteredPlaces.value = result;
};

// 페이지네이션 데이터 계산 (탭에 따라 소스 변경)
const currentListSource = computed(() => {
  return rightPanelTab.value === 'search' ? filteredPlaces.value : bookmarkedPlaces.value;
});

const totalListCount = computed(() => currentListSource.value.length);
const totalPages = computed(() => Math.ceil(totalListCount.value / itemsPerPage) || 1);

const paginatedPlaces = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return currentListSource.value.slice(start, end);
});

// --- 일정 관리 액션 ---

const addToItinerary = async (place) => {
  if (!itineraryDays.value[selectedDayIdx.value]) {
    itineraryDays.value[selectedDayIdx.value] = [];
  }
  const currentDayList = itineraryDays.value[selectedDayIdx.value];

  // 중복 체크
  const exists = currentDayList.find(p => p.placeNo === place.placeNo);
  if (exists) {
    showSnackbar(`was already added to Day ${selectedDayIdx.value + 1}`, "warning");
    return;
  }

  isProcessing.value = place.placeNo;

  const payload = {
    itinNo: Number(itinNo),
    placeNo: place.placeNo,
    dayOrder: selectedDayIdx.value + 1,
    orderInDay: currentDayList.length + 1,
    moveType: 0,
    moveMinutes: 0
  };

  try {
    const response = await http.post('/itinerary-places', payload);
    if (response.data && response.data.data) {
      syncItineraryData(response.data.data);
      showSnackbar(`"${place.title}" successfully added!`);
    }
  } catch (error) {
    console.error("Add failed:", error);
    showSnackbar("Place Add Failed.", "error");
  } finally {
    isProcessing.value = null;
  }
};

const removeFromItinerary = async (item) => {
  if (!item.itinPlaceNo) {
    const list = itineraryDays.value[selectedDayIdx.value];
    const idx = list.indexOf(item);
    if (idx > -1) list.splice(idx, 1);
    return;
  }

  isProcessing.value = item.itinPlaceNo;

  try {
    const targetItinNo = Number(itinNo);
    const targetItinPlaceNo = Number(item.itinPlaceNo);
    const response = await http.delete(`/itinerary-places/${targetItinNo}/${targetItinPlaceNo}`);

    if (response.data && response.data.data) {
      syncItineraryData(response.data.data);
      showSnackbar("deleted.");
    }
  } catch (error) {
    console.error("Delete failed:", error);
    showSnackbar("failed to delete.", "error");
  } finally {
    isProcessing.value = null;
  }
};

const syncItineraryData = (flatList) => {
  const currentDayCount = itineraryDays.value.length;
  const newDays = Array.from({ length: currentDayCount }, () => []);

  flatList.forEach(item => {
    const dayIndex = item.dayOrder - 1;
    if (dayIndex >= 0 && dayIndex < currentDayCount) {
      newDays[dayIndex].push(item);
    }
  });

  newDays.forEach(dayList => {
    dayList.sort((a, b) => a.orderInDay - b.orderInDay);
  });

  itineraryDays.value = newDays;
};

// --- Drag & Drop ---
const onDragStart = (event, item, dayIdx, index) => {
  draggedItem.value = item;
  draggedFromDayIdx.value = dayIdx;
  draggedFromIndex.value = index;

  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('text/plain', JSON.stringify(item));
  }
};

const onItemDrop = async (event, toDayIdx, toIndex) => {
  const item = draggedItem.value;
  if (!item) return;

  const fromDayIdx = draggedFromDayIdx.value;
  const fromIndex = draggedFromIndex.value;

  if (fromDayIdx === toDayIdx) {
    if (fromIndex === toIndex) return;
    const dayList = itineraryDays.value[fromDayIdx];
    dayList.splice(fromIndex, 1);
    dayList.splice(toIndex, 0, item);
    await updateOrderBackend([fromDayIdx]);
  } else {
    const fromList = itineraryDays.value[fromDayIdx];
    fromList.splice(fromIndex, 1);
    const toList = itineraryDays.value[toDayIdx];
    toList.splice(toIndex, 0, item);
    await updateOrderBackend([fromDayIdx, toDayIdx]);
  }
  resetDragState();
};

const onContainerDrop = async (event) => {
  const item = draggedItem.value;
  if (!item) return;

  const toDayIdx = selectedDayIdx.value;
  const fromDayIdx = draggedFromDayIdx.value;
  const fromIndex = draggedFromIndex.value;

  if (fromDayIdx === toDayIdx) return;

  const fromList = itineraryDays.value[fromDayIdx];
  fromList.splice(fromIndex, 1);
  const toList = itineraryDays.value[toDayIdx];
  toList.push(item);
  await updateOrderBackend([fromDayIdx, toDayIdx]);
  resetDragState();
};

const resetDragState = () => {
  draggedItem.value = null;
  draggedFromDayIdx.value = null;
  draggedFromIndex.value = null;
};

const updateOrderBackend = async (dayIndices) => {
  const updateList = [];
  dayIndices.forEach(dayIdx => {
    const list = itineraryDays.value[dayIdx];
    list.forEach((place, index) => {
      if (place.itinPlaceNo) {
        updateList.push({
          itinPlaceNo: place.itinPlaceNo,
          dayOrder: dayIdx + 1,
          orderInDay: index + 1
        });
      }
    });
  });

  if (updateList.length === 0) return;
  isProcessing.value = 'reorder';
  try {
    const response = await http.patch(`/itinerary-places/${itinNo}/order`, updateList);
    if (response.data && response.data.data) {
      syncItineraryData(response.data.data);
      showSnackbar("Order saved.");
    }
  } catch (error) {
    console.error("Order update failed:", error);
    showSnackbar("Order save failed.", "error");
  } finally {
    isProcessing.value = null;
  }
};

// --- 유틸리티 ---
// 이미지가 유효하지 않을 때(null 또는 빈 문자열) 기본 로고 이미지 반환
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
  
  // 기본 이미지 반환
  return defaultImage;
};

const getCategoryName = (id) => {
  const cat = categories.find(c => c.value === id);
  return cat ? cat.text : '장소';
};

const totalAddedPlaces = computed(() => {
  return itineraryDays.value.reduce((sum, day) => sum + day.length, 0);
});

const showSnackbar = (text, color = 'success') => {
  snackbar.text = text;
  snackbar.color = color;
  snackbar.show = true;
};

onMounted(() => {
  loadItinerary();
});
</script>

<style scoped>
/* 이전과 동일한 스타일 */
.cursor-move { cursor: grab; }
.cursor-move:active { cursor: grabbing; }
.cursor-grab { cursor: grab; }
.opacity-50 { opacity: 0.5; border: 2px dashed #1976D2 !important; }
.draggable-item { transition: all 0.2s ease; }
.hover-card { transition: transform 0.2s, box-shadow 0.2s; cursor: pointer; border: 1px solid #eee; }
.hover-card:hover { transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08) !important; border-color: var(--v-theme-primary); }
::-webkit-scrollbar { width: 5px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #e0e0e0; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #bdbdbd; }
</style>