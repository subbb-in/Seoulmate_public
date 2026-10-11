<template>
  <v-container class="py-10">
    <div class="mb-8">
      <h2 class="text-h4 font-weight-bold text-primary mb-2">My Itineraries</h2>
      <p class="text-body-1 text-grey-darken-1">Make a itinerary for your trip</p>
      <v-divider class="mt-4" color="primary" thickness="3" length="60" />
    </div>

    <v-row v-if="loading">
      <v-col v-for="n in 4" :key="n" cols="12" sm="6" md="4" lg="3">
        <v-skeleton-loader type="card" class="rounded-lg" elevation="2" />
      </v-col>
    </v-row>

    <v-sheet
      v-else-if="!itineraries.length"
      class="d-flex flex-column align-center justify-center py-16"
      color="transparent"
    >
      <v-icon icon="mdi-map-marker-off" size="64" color="grey-lighten-1" class="mb-4" />
      <h3 class="text-h6 text-grey-darken-1 mb-6">There are no registered itineraries🥲</h3>
      <v-btn
        color="primary"
        rounded="pill"
        size="large"
        to="/itineraries/createItinerary" 
        prepend-icon="mdi-plus"
      >
        Create New Itinerary
      </v-btn>
    </v-sheet>

    <v-row v-else>
      <v-col
        v-for="item in itineraries"
        :key="item.itinNo"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card
          class="h-100 rounded-lg elevation-2 hover-card cursor-pointer"
          @click="goToDetail(item.itinNo)"
        >
          <!-- 함수 호출 -->
          <v-img
            :src="getItineraryImage(item)"
            cover
            height="200"
            class="align-end"
            gradient="to bottom, rgba(0,0,0,0) 60%, rgba(0,0,0,0.7) 100%"
          >
            <template #error>
              <v-sheet height="200" color="grey-lighten-3" class="d-flex align-center justify-center">
                <v-icon icon="mdi-image-off" color="grey" size="40" />
              </v-sheet>
            </template>
            
            <v-card-title class="text-white font-weight-bold">
              {{ item.itinTitle }}
            </v-card-title>
          </v-img>

          <v-card-item>
            <div class="d-flex align-center justify-space-between">
              <v-chip color="primary" size="small" variant="flat" label>
                {{ item.dayCnt }} Days
              </v-chip>
              <v-btn
                icon="mdi-trash-can-outline"
                variant="text"
                color="error"
                size="small"
                @click.stop="confirmDelete(item)"
              />
            </div>
            
            <div class="text-caption text-grey mt-2">
              Travel period: {{ item.dayCnt }} days, {{ item.dayCnt - 1 }} night
            </div>
          </v-card-item>
        </v-card>
      </v-col>
    </v-row>

    <div class="text-center mt-12" v-if="totalPages > 1">
      <v-pagination
        v-model="currentPage"
        :length="totalPages"
        :total-visible="7"
        color="primary"
        rounded="circle"
        @update:model-value="fetchItineraries"
      />
    </div>

    <v-dialog v-model="deleteDialog.show" max-width="400">
      <v-card>
        <v-card-title class="text-h6">Delete Itinerary</v-card-title>
        <v-card-text>
          Are you sure you want to delete<br/>
          <strong>{{ deleteDialog.item?.itinTitle }}</strong>?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="grey-darken-1" variant="text" @click="deleteDialog.show = false">Cancel</v-btn>
          <v-btn color="error" variant="text" @click="handleDelete">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getItineraries, deleteItinerary } from '@/api/itinerary.api.js';
import defaultImage from '@/assets/climate.jpg'; // 기본 이미지 (최후의 수단)

const router = useRouter();

// 데이터 상태
const loading = ref(false);
const itineraries = ref([]);
const currentPage = ref(1);
const totalPages = ref(0);
const pageSize = 12;

const deleteDialog = ref({ show: false, item: null });

// [필수] 플레이스홀더 이미지 경로 배열 (public 폴더 내 존재해야 함)
const koreanPlaceholders = [
  '/images/placeholders/t1.jpg',
  '/images/placeholders/t2.jpg',
  '/images/placeholders/t3.jpg',
  '/images/placeholders/t4.jpg',
  '/images/placeholders/t5.jpg',
  '/images/placeholders/t6.jpg',
  '/images/placeholders/t7.jpg',
  '/images/placeholders/t8.jpg',
];

// API 호출 함수
const fetchItineraries = async () => {
  loading.value = true;
  try {
    const res = await getItineraries({
      page: currentPage.value,
      size: pageSize
    });
    
    // API 응답 구조 유연하게 처리
    const pageData = res.data?.data || res.data || res; 
    
    if (pageData) {
      const list = pageData.content || (Array.isArray(pageData) ? pageData : []);
      itineraries.value = list;
      
      // [디버깅] 첫 번째 아이템의 데이터 확인 (개발자 도구 콘솔)
      // if (list.length > 0) {
      //   console.log("First Itinerary Data:", list[0]);
      //   console.log("- thumbnail:", list[0].thumbnail);
      //   console.log("- thumbnailPath:", list[0].thumbnailPath);
      // }

      if (pageData.totalCount) {
        totalPages.value = Math.ceil(pageData.totalCount / pageSize);
      } else if (pageData.totalPages) {
        totalPages.value = pageData.totalPages;
      }
    }
  } catch (error) {
    console.error("Failed to load the list:", error);
  } finally {
    loading.value = false;
  }
};

// [핵심 수정] 이미지 결정 로직 (경로 처리 강화)
const getItineraryImage = (item) => {
  // (1) 백엔드 데이터 확인 (thumbnail 혹은 thumbnailPath 필드)
  // DB에 값이 있어도 DTO에 없으면 undefined가 됩니다.
  const val = item.thumbnail || item.thumbnailPath;

  if (val && val.length > 5) {
    // 1-1. http로 시작하면 외부 이미지
    if (val.startsWith('http')) return val;

    // 1-2. "images/"가 포함된 경우 (우리가 저장한 플레이스홀더)
    // "/images/placeholders/t5.jpg" 처럼 앞에 슬래시가 있든 없든 처리
    if (val.includes('images/')) {
        return val.startsWith('/') ? val : `/${val}`;
    }
    
    // 1-3. 그 외 (백엔드 업로드 파일 등) -> 그대로 반환하거나 백엔드 URL 붙이기
    return val;
  }

  // (2) 값이 없으면(null/undefined), 'itinNo'를 기준으로 프론트엔드에서 자동 배정
  //     (API에서 썸네일을 안 줄 경우를 대비한 안전장치)
  if (item.itinNo && koreanPlaceholders.length > 0) {
    const index = Number(item.itinNo) % koreanPlaceholders.length;
    return koreanPlaceholders[index];
  }

  // (3) 그래도 없다면 기본 이미지
  return defaultImage;
};

const goToDetail = (itinNo) => {
  router.push(`/itineraries/${itinNo}`);
};

const confirmDelete = (item) => {
  deleteDialog.value = { show: true, item };
};

const handleDelete = async () => {
  if (!deleteDialog.value.item) return;
  try {
    await deleteItinerary(deleteDialog.value.item.itinNo);
    deleteDialog.value.show = false;
    await fetchItineraries(); 
  } catch (error) {
    console.error("Failed to delete:", error);
  }
};

onMounted(() => {
  fetchItineraries();
});
</script>

<style scoped>
.hover-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15) !important;
}
.cursor-pointer {
  cursor: pointer;
}
</style>