<template>
  <v-container class="py-10" max-width="1000">
    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>

    <!-- 에러 처리 -->
    <div v-else-if="!post" class="text-center py-16">
      <v-icon size="64" color="grey-lighten-1">mdi-alert-circle-outline</v-icon>
      <h3 class="text-h5 text-grey-darken-2 mt-4">Post not found</h3>
      <v-btn class="mt-4" color="primary" to="/destinations" variant="outlined">Back to List</v-btn>
    </div>

    <div v-else>
      <!-- Header -->
      <div class="text-center mb-8">
        <v-chip color="primary" variant="tonal" class="mb-4" label>Recommended</v-chip>
        <!-- DTO: boardTitle -->
        <h1 class="text-h3 font-weight-bold mb-4">{{ post.boardTitle }}</h1>
        <div class="d-flex justify-center align-center text-grey gap-4">
          <!-- DTO: boardWriter, createdAt, viewCnt -->
          <span><v-icon size="small" class="mr-1">mdi-account</v-icon> {{ post.boardWriter || 'Admin' }}</span>
          <span><v-icon size="small" class="mr-1">mdi-calendar</v-icon> {{ formatDate(post.createdAt || post.regDate) }}</span>
          <span><v-icon size="small" class="mr-1">mdi-eye</v-icon> {{ post.viewCnt || 0 }}</span>
        </div>
      </div>

      <v-divider class="mb-10"></v-divider>

      <!-- Content -->
      <div class="post-content mb-10">
        <!-- 이미지 리스트 (images) 처리 -->
        <v-carousel 
          v-if="post.images && post.images.length > 0"
          height="500"
          hide-delimiter-background
          show-arrows="hover"
          class="rounded-lg mb-8 elevation-2"
        >
          <!-- getImageUrl 함수를 통해 안전한 경로 사용 -->
          <v-carousel-item
            v-for="(img, i) in post.images"
            :key="i"
            :src="getImageUrl(img)"
            cover
          ></v-carousel-item>
        </v-carousel>
        
        <!-- 본문 내용 (DTO: boardContent) -->
        <div v-html="post.boardContent" class="text-body-1" style="line-height: 1.8;"></div>
      </div>

      <v-divider class="mb-6"></v-divider>

      <!-- Actions -->
      <div class="d-flex justify-space-between align-center">
        <v-btn variant="outlined" to="/destinations" prepend-icon="mdi-arrow-left">
          List
        </v-btn>

        <!-- Admin Actions -->
        <!-- 본인 글이거나 관리자일 때 (백엔드 userNo 비교 필요) -->
        <div v-if="authStore.isAdmin" class="d-flex gap-2">
          <!-- <v-btn color="info" variant="tonal" :to="`/destinations/${post.boardNo}/edit`">Edit</v-btn> -->
          <v-btn color="error" variant="tonal" @click="confirmDelete">Delete</v-btn>
        </div>
      </div>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchBoardDetail, deleteBoard } from '@/api/board.api';
import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const loading = ref(true);
const post = ref(null);

const fetchDetail = async () => {
  const id = route.params.id;
  loading.value = true;
  try {
    const res = await fetchBoardDetail(id);
    console.log("Board Detail Response:", res);
    
    // ApiResponse 처리 (res.data 또는 res가 실제 데이터 객체)
    post.value = res.data || res;
    
  } catch (e) {
    console.error("Failed to load board detail:", e);
    post.value = null;
  } finally {
    loading.value = false;
  }
};

// 이미지 경로 변환 함수
const getImageUrl = (imgObj) => {
  if (!imgObj) return '/placeholder_destination.jpg';

  // DTO 필드명 확인 (imgPath, fileUrl 등)
  const path = imgObj.imgPath || imgObj.fileUrl;
  
  if (!path) return '/placeholder_destination.jpg';

  // 1. 외부 이미지 (http://...)
  if (path.startsWith('http')) return path;

  // 2. 백엔드 정적 리소스 경로와 결합
  //    DB에 "/uploads/filename.jpg" 형태로 저장되어 있다면 도메인만 붙임
  const baseUrl = '';
  const safePath = path.startsWith('/') ? path : `/uploads/${path}`;
  
  return `${baseUrl}${safePath}`;
};

const confirmDelete = async () => {
  if (!confirm("Are you sure you want to delete this post?")) return;
  
  try {
    // DTO: boardNo
    await deleteBoard(post.value.boardNo);
    alert("Deleted successfully.");
    router.push('/destinations');
  } catch (e) {
    console.error(e);
    alert("Failed to delete. " + (e.response?.data?.message || ""));
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('en-US');
};

onMounted(() => {
  fetchDetail();
});
</script>

<style scoped>
.gap-4 { gap: 16px; }
.gap-2 { gap: 8px; }
.post-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 16px 0;
}
</style>