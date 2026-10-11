<template>
  <v-container class="py-10" max-width="1200">
    <!-- Header Section -->
    <div class="text-center mb-12">
      <h1 class="text-h3 font-weight-bold text-primary mb-3">Recommended Destinations</h1>
      <p class="text-h6 text-grey-darken-1">
        Curated travel destinations picked by SeoulMate editors
      </p>
      <v-divider class="mx-auto mt-6" length="60" thickness="3" color="primary"></v-divider>
    </div>

    <!-- Search & Filter Bar -->
    <div class="d-flex flex-column flex-md-row justify-space-between align-center mb-8 gap-4">
      <!-- Tag Filter (현재는 UI만 구현해 둠. 로직 구현 필요.) -->
      <div class="d-flex gap-2 w-100 w-md-auto overflow-x-auto flex-shrink-0">
        <v-chip-group v-model="selectedTag" selected-class="text-primary" column>
          <v-chip v-for="tag in tags" :key="tag" :value="tag" filter variant="outlined">
            {{ tag }}
          </v-chip>
        </v-chip-group>
      </div>

      <!-- Search Bar & Write Button -->
      <div class="d-flex align-center gap-2 w-100 w-md-auto flex-grow-1 justify-end" style="min-width: 300px;">
        <v-text-field
          v-model="searchKeyword"
          placeholder="Search keywords..."
          variant="outlined"
          density="compact"
          hide-details
          prepend-inner-icon="mdi-magnify"
          @keyup.enter="onSearch"
          class="flex-grow-1"
        ></v-text-field>
        
        <v-btn color="primary" height="40" @click="onSearch">Search</v-btn>
        
        <!-- Admin Write Button -->
        <v-btn 
          v-if="authStore.isAdmin" 
          color="secondary" 
          prepend-icon="mdi-pencil" 
          height="40"
          @click="goToWrite"
        >
          Write
        </v-btn>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>

    <!-- Board List (Grid) -->
    <v-row v-else-if="paginatedPosts.length > 0">
      <v-col
        v-for="post in paginatedPosts"
        :key="post.boardNo"
        cols="12"
        sm="6"
        md="4"
      >
        <v-card 
          class="h-100 rounded-lg hover-card cursor-pointer d-flex flex-column"
          elevation="2"
          @click="goToDetail(post.boardNo)"
        >
          <!-- getThumbnailUrl 함수를 통해 이미지 경로 바인딩 -->
          <v-img
            :src="getThumbnailUrl(post)"
            height="240"
            cover
            class="bg-grey-lighten-2"
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-progress-circular indeterminate color="grey-lighten-4"></v-progress-circular>
              </div>
            </template>
            <template v-slot:error>
              <div class="d-flex align-center justify-center fill-height bg-grey-lighten-3 text-grey">
                <v-icon size="40">mdi-image-off</v-icon>
              </div>
            </template>
            
            <!-- NEW Badge (7일 이내) -->
            <v-chip
              v-if="isNew(post.createdAt || post.regDate)"
              color="error"
              size="small"
              class="ma-2 font-weight-bold"
              variant="flat"
            >
              NEW
            </v-chip>
          </v-img>

          <v-card-text class="flex-grow-1 pt-4 pb-0">
            <h3 class="text-h6 font-weight-bold mb-2 text-truncate-2">{{ post.boardTitle }}</h3>
            <!-- 내용 미리보기가 없다면 고정 텍스트 사용 -->
            <!-- <p class="text-body-2 text-grey-darken-1 text-truncate-3 mb-4">
              {{ stripHtml(post.boardContent) || 'Check out this amazing place recommended by SeoulMate.' }}
            </p> -->
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions class="px-4 py-3 text-caption text-grey">
            <div class="d-flex align-center">
              <v-icon size="small" class="mr-1">mdi-calendar</v-icon>
              {{ formatDate(post.createdAt || post.regDate) }}
            </div>
            <v-spacer></v-spacer>
            <div class="d-flex align-center mr-3">
              <v-icon size="small" class="mr-1">mdi-eye</v-icon>
              {{ post.viewCnt || 0 }}
            </div>
            <div class="d-flex align-center">
              <v-icon size="small" class="mr-1">mdi-account</v-icon>
              {{ post.boardWriter }}
            </div>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- No Data -->
    <div v-else class="text-center py-16 bg-grey-lighten-5 rounded-lg">
      <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-post-outline</v-icon>
      <h3 class="text-h6 text-grey-darken-2">No destinations found.</h3>
    </div>

    <!-- Pagination -->
    <div class="text-center mt-10" v-if="totalPages > 1">
      <v-pagination
        v-model="page"
        :length="totalPages"
        rounded="circle"
        active-color="primary"
        @update:model-value="onPageChange"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchBoardList } from '@/api/board.api';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const allPosts = ref([]);
const filteredPosts = ref([]);

const page = ref(1);
const itemsPerPage = 9;
const searchKeyword = ref('');
const selectedTag = ref(null);

const tags = ['Culture', 'Nature', 'Food', 'Activity', 'Shopping'];

// [API] Load Posts
const loadPosts = async () => {
  loading.value = true;
  try {
    const res = await fetchBoardList(); 
    // console.log("Board List API Response:", res);

    // API 응답 구조 유연하게 처리 (res.data.data 또는 res.data)
    const responseData = res.data || res;
    let list = [];

    if (Array.isArray(responseData)) {
        list = responseData;
    } else if (responseData && Array.isArray(responseData.data)) {
        list = responseData.data;
    } 

    // boardNo 내림차순 정렬 (최신순)
    if (Array.isArray(list)) {
        allPosts.value = list.sort((a, b) => b.boardNo - a.boardNo);
    } else {
        allPosts.value = [];
    }
    
    applyFilter();

  } catch (e) {
    console.error("Failed to load board list:", e);
  } finally {
    loading.value = false;
  }
};

// 썸네일 URL 변환
const getThumbnailUrl = (post) => {
  // 1. DTO에 썸네일 필드가 있는지 확인 (thumbnail, thumbnailName, fileId 등 백엔드 DTO에 맞게)
  // 여기서는 post.thumbnail 에 '파일명(UUID)'이 들어있다고 가정합니다.
  const thumbnailName = post.thumbnail || post.thumbnailPath || post.thumbnailName;

  if (thumbnailName && thumbnailName.length > 0) {
    // http로 시작하면 그대로 반환 (외부 이미지)
    if (thumbnailName.startsWith('http')) return thumbnailName;
    
    // 파일명이면 백엔드 다운로드 API 경로와 결합
    // (vite.config.js에서 /api 프록시가 설정되어 있다고 가정)
    // 백엔드 정적 리소스 경로와 결합
    // 만약 DB에 "/uploads/..."로 저장되어 있다면 도메인만 붙여줍니다.
    const baseUrl = ''; // 백엔드 주소 확인!
    
    // path가 '/'로 시작하지 않으면 붙여줌
    const safePath = thumbnailName.startsWith('/') ? thumbnailName : `/uploads/${thumbnailName}`;
    
    return `${baseUrl}${safePath}`; 
  }

  // 없으면 기본 이미지
  return '/placeholder_destination.jpg'; 
};

// HTML 태그 제거 (본문 미리보기용)
const stripHtml = (html) => {
   if (!html) return '';
   return html.replace(/<[^>]*>?/gm, '');
};

const applyFilter = () => {
  let result = allPosts.value;

  if (searchKeyword.value.trim()) {
    const k = searchKeyword.value.toLowerCase();
    result = result.filter(post => 
      (post.boardTitle && post.boardTitle.toLowerCase().includes(k)) || 
      (post.boardWriter && post.boardWriter.toLowerCase().includes(k))
    );
  }
  
  // (Optional) 태그 필터링 로직이 필요하면 여기에 추가
  
  filteredPosts.value = result;
  page.value = 1;
};

const onSearch = () => {
  applyFilter();
};

const totalPages = computed(() => {
  return Math.ceil(filteredPosts.value.length / itemsPerPage) || 1;
});

const paginatedPosts = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredPosts.value.slice(start, end);
});

const onPageChange = (newPage) => {
  page.value = newPage;
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const goToDetail = (id) => {
  router.push(`/destinations/${id}`);
};

const goToWrite = () => {
  router.push('/destination-write');
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
};

const isNew = (dateStr) => {
  if (!dateStr) return false;
  const date = new Date(dateStr);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
  return diffDays <= 7;
};

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.gap-2 { gap: 8px; }
.gap-4 { gap: 16px; }

.hover-card {
  transition: transform 0.2s, box-shadow 0.2s;
}
.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.15) !important;
}

.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>