<template>
  <v-container class="py-10" max-width="1200">
    <!-- 로딩 화면 -->
    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      <div class="mt-4 text-grey">Loading details...</div>
    </div>

    <!-- 에러 화면 -->
    <div v-else-if="!place" class="text-center py-16">
      <v-icon size="64" color="grey-lighten-1">mdi-alert-circle-outline</v-icon>
      <h3 class="text-h5 text-grey-darken-2 mt-4">Place not found</h3>
      <v-btn class="mt-4" color="primary" to="/places" variant="outlined">Back to List</v-btn>
    </div>

    <!-- 상세 컨텐츠 -->
    <div v-else>
      <!-- [1] Header Section -->
      <header class="mb-6">
        <v-breadcrumbs :items="['Home', 'Places', place.title]" class="px-0 text-caption text-grey"></v-breadcrumbs>
        
        <h1 class="text-h3 font-weight-black text-grey-darken-4 mb-2">{{ place.title }}</h1>
        
        <div class="d-flex align-center flex-wrap gap-4">
          <!-- 평점 & 리뷰 수 -->
          <div class="d-flex align-center">
            <v-rating
              :model-value="place.rating || 0"
              color="amber"
              active-color="amber"
              half-increments
              readonly
              size="small"
              density="compact"
            ></v-rating>
            <span class="text-body-2 font-weight-bold ml-2">{{ formatRating(place.rating) }}</span>
            <span class="text-caption text-grey ml-1">({{ reviews.length }} reviews)</span>
          </div>

          <v-divider vertical class="mx-2"></v-divider>

          <!-- 카테고리 -->
          <div class="text-body-2 text-grey-darken-1">
            {{ getCategoryName(place.contentTypeId) }}
          </div>

          <v-spacer></v-spacer>

          <!-- 액션 버튼 -->
          <div class="d-flex gap-2">
            <!-- 찜하기 버튼 -->
            <v-btn 
              :prepend-icon="isBookmarked ? 'mdi-heart' : 'mdi-heart-outline'" 
              :variant="isBookmarked ? 'flat' : 'outlined'" 
              rounded="pill" 
              :color="isBookmarked ? 'primary' : 'primary'"
              @click="toggleBookmark"
            >
              {{ isBookmarked ? 'Saved' : 'Save' }}
            </v-btn>
            
            <!-- 공유하기 버튼 -->
            <v-btn 
              prepend-icon="mdi-share-variant" 
              variant="text" 
              rounded="pill" 
              color="grey-darken-2"
              @click="sharePlace"
            >
              Share
            </v-btn>
          </div>
        </div>
      </header>

      <!-- [2] Official Photo Grid (Modified: Main Image Logic) -->
      <section class="mb-10">
        <v-row>
          <v-col cols="12">
            <!-- Case 1: 유효한 이미지가 있는 경우 (꽉 채워서 보여줌) -->
            <v-img
              v-if="getValidImageUrl(place.firstImage) !== defaultImage"
              :src="getValidImageUrl(place.firstImage)"
              height="450"
              cover
              class="rounded-lg elevation-2 cursor-pointer"
              @click="openGallery(0)"
            >
              <template v-slot:error>
                <v-sheet color="grey-lighten-3" class="d-flex align-center justify-center fill-height">
                  <span class="text-grey">Image Load Error</span>
                </v-sheet>
              </template>
            </v-img>

            <!-- Case 2: 이미지가 없는 경우 (로고를 중앙에 적당한 사이즈로 배치) -->
            <v-sheet
              v-else
              height="450"
              color="grey-lighten-4"
              class="rounded-lg elevation-2 d-flex align-center justify-center cursor-pointer border"
              @click="openGallery(0)"
            >
              <!-- 로고 이미지: 너비 200px 정도로 설정하여 중앙 배치 -->
              <img 
                :src="defaultImage" 
                alt="SeoulMate Logo" 
                style="width: 200px; height: auto; opacity: 0.8;"
              />
            </v-sheet>
          </v-col>
        </v-row>
      </section>

      <!-- Traveler Photos Section -->
      <section v-if="allReviewImages.length > 0" class="mb-12">
        <div class="d-flex align-center justify-space-between mb-4">
          <h2 class="text-h5 font-weight-bold">Traveler Photos ({{ allReviewImages.length }})</h2>
        </div>
        <v-row dense>
          <v-col
            v-for="(img, i) in allReviewImages.slice(0, 5)"
            :key="i"
            cols="6"
            md="2"
            :class="{'d-none d-md-block': i > 3}"
          >
            <v-img
              :src="img"
              height="150"
              cover
              class="rounded cursor-pointer hover-zoom"
              @click="openImage(img)"
            >
               <template v-slot:error>
                  <v-sheet class="fill-height d-flex align-center justify-center bg-grey-lighten-3">
                     <v-icon>mdi-image-broken</v-icon>
                  </v-sheet>
               </template>
            </v-img>
          </v-col>
          <v-col v-if="allReviewImages.length > 5" cols="6" md="2">
            <v-sheet
              height="150"
              color="grey-lighten-3"
              class="rounded d-flex align-center justify-center cursor-pointer hover-bg"
              @click="openImage(allReviewImages[5])"
            >
              <span class="text-body-2 font-weight-bold text-grey-darken-1">
                +{{ allReviewImages.length - 5 }} more
              </span>
            </v-sheet>
          </v-col>
        </v-row>
        <v-divider class="mt-8"></v-divider>
      </section>

      <!-- [3] Main Content -->
      <v-row>
        <v-col cols="12" md="8">
          <!-- About -->
          <section class="mb-10">
            <h2 class="text-h5 font-weight-bold mb-4">About</h2>
            <p class="text-body-1 text-grey-darken-3" style="line-height: 1.8; white-space: pre-line;">
              {{ place.description || place.overview || generateFallbackDescription(place) }}
            </p>
          </section>

          <v-divider class="mb-10"></v-divider>

          <!-- Location -->
          <section id="location-section" class="mb-10">
            <h2 class="text-h5 font-weight-bold mb-4">Location</h2>
            <div class="d-flex align-center mb-4 text-body-2">
              <v-icon color="primary" class="mr-2">mdi-map-marker</v-icon>
              {{ place.addr1 }} {{ place.addr2 }}
            </div>
            <v-sheet
              height="400"
              color="grey-lighten-4"
              class="rounded-lg overflow-hidden position-relative border"
            >
              <div id="naver-map" style="width: 100%; height: 100%;"></div>
              <div v-if="!isMapLoaded" class="d-flex flex-column align-center justify-center fill-height bg-grey-lighten-4 position-absolute w-100 h-100" style="top:0; left:0; z-index: 1;">
                <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-map</v-icon>
                <div class="text-body-1 text-grey-darken-1 font-weight-bold">Map Loading...</div>
              </div>
            </v-sheet>
          </section>

          <v-divider class="mb-10"></v-divider>

          <!-- Reviews Section -->
          <section>
            <div class="d-flex align-center justify-space-between mb-6">
              <h2 class="text-h5 font-weight-bold">All Reviews ({{ reviews.length }})</h2>
              <v-btn color="primary" variant="flat" prepend-icon="mdi-pencil" @click="openReviewDialog(false)">
                Write a Review
              </v-btn>
            </div>

            <div v-if="paginatedReviews.length > 0">
              <v-card
                v-for="review in paginatedReviews"
                :key="review.reviewNo" 
                variant="flat"
                class="mb-6 border-b rounded-0 pb-6"
              >
                <!-- User Info Header -->
                <div class="d-flex align-center justify-space-between mb-3">
                  <div class="d-flex align-center">
                    <v-avatar color="grey-lighten-2" size="40" class="mr-3 elevation-1">
                      <span class="text-primary font-weight-bold">{{ review.userNickname?.charAt(0) || 'U' }}</span>
                    </v-avatar>
                    <div>
                      <div class="text-subtitle-2 font-weight-bold">{{ review.userNickname || 'Traveler' }}</div>
                      <div class="text-caption text-grey">{{ formatReviewDate(review.createdAt) }}</div>
                    </div>
                  </div>
                  
                  <v-menu v-if="isMyReview(review)">
                    <template v-slot:activator="{ props }">
                      <v-btn icon="mdi-dots-vertical" variant="text" size="small" v-bind="props"></v-btn>
                    </template>
                    <v-list density="compact">
                      <v-list-item @click="editReview(review)" prepend-icon="mdi-pencil" title="Edit"></v-list-item>
                      <v-list-item @click="deleteReviewHandler(review.reviewNo)" prepend-icon="mdi-delete" title="Delete" class="text-error"></v-list-item>
                    </v-list>
                  </v-menu>
                </div>

                <!-- Rating -->
                <v-rating
                  :model-value="review.rating"
                  color="amber"
                  density="compact"
                  readonly
                  size="small"
                  class="mb-2"
                ></v-rating>

                <!-- Content -->
                <p class="text-body-1 text-grey-darken-3 mb-3" style="white-space: pre-wrap;">{{ review.content }}</p>
                
                <!-- Review Images -->
                <div v-if="getReviewImages(review).length > 0" class="d-flex gap-2 mt-3 overflow-x-auto pb-2">
                  <v-img
                    v-for="(imgUrl, idx) in getReviewImages(review)"
                    :key="idx"
                    :src="imgUrl" 
                    width="120"
                    height="120"
                    cover
                    class="rounded cursor-pointer"
                    @click="openImage(imgUrl)"
                  >
                     <template v-slot:error>
                        <v-sheet class="fill-height d-flex align-center justify-center bg-grey-lighten-3">
                           <v-icon>mdi-image-broken</v-icon>
                        </v-sheet>
                     </template>
                  </v-img>
                </div>
              </v-card>

              <!-- Pagination -->
              <div class="text-center mt-6" v-if="reviews.length > itemsPerPage">
                <v-pagination
                  v-model="page"
                  :length="totalPages"
                  rounded="circle"
                  active-color="primary"
                ></v-pagination>
              </div>
            </div>
            
            <div v-else class="text-center py-10 bg-grey-lighten-5 rounded-lg">
              <p class="text-body-1 text-grey">There are no reviews for this attraction yet.</p>
              <p class="text-caption text-grey">Share your experience with the world!</p>
            </div>
          </section>
        </v-col>

        <!-- Right Column -->
        <v-col cols="12" md="4">
          <div class="sticky-sidebar">
            <v-card elevation="3" class="rounded-lg pa-5 border-t-lg border-primary" style="border-top-width: 6px !important;">
              <h3 class="text-h6 font-weight-bold mb-4">Essential Information</h3>
              <v-list density="compact" class="pa-0">
                <v-list-item class="px-0" v-if="place.tel">
                  <template v-slot:prepend><v-icon color="primary" class="mr-3">mdi-phone</v-icon></template>
                  <v-list-item-title class="text-body-2 font-weight-medium">{{ place.tel }}</v-list-item-title>
                </v-list-item>
                <v-list-item class="px-0" @click="copyAddress" style="cursor: pointer;">
                  <template v-slot:prepend><v-icon color="primary" class="mr-3">mdi-map-marker-outline</v-icon></template>
                  <v-list-item-title class="text-body-2 text-truncate">{{ place.addr1 }}</v-list-item-title>
                  <template v-slot:append><v-icon size="small" color="grey">mdi-content-copy</v-icon></template>
                </v-list-item>
              </v-list>
              <v-divider class="my-4"></v-divider>
              <v-btn block color="primary" size="large" prepend-icon="mdi-calendar-plus" class="font-weight-bold mb-2" @click="addToItinerary">
                Add to Itinerary
              </v-btn>
              
              <!-- Naver Map Directions Button -->
              <v-btn 
                block 
                variant="outlined" 
                color="success" 
                prepend-icon="mdi-navigation" 
                :href="`https://map.naver.com/?lng=${place.mapx}&lat=${place.mapy}&title=${encodeURIComponent(place.title)}`" 
                target="_blank"
                class="mb-2"
              >
                Directions (Naver Map)
              </v-btn>

              <!-- Google Maps Directions Button -->
              <v-btn 
                block 
                variant="outlined" 
                color="secondary" 
                prepend-icon="mdi-google-maps" 
                :href="`https://www.google.com/maps/dir/?api=1&destination=${place.mapy},${place.mapx}`" 
                target="_blank"
              >
                Directions (Google Map)
              </v-btn>

            </v-card>
          </div>
        </v-col>
      </v-row>
    </div>

    <!-- Review Write/Edit Dialog -->
    <v-dialog v-model="reviewDialog" max-width="600" persistent>
      <v-card class="rounded-lg">
        <v-card-title class="text-h6 font-weight-bold pa-4 border-b">
          {{ isEditing ? 'Edit Review' : 'Write a Review' }}
          <v-spacer></v-spacer>
          <v-btn icon="mdi-close" variant="text" size="small" @click="closeReviewDialog" class="position-absolute" style="right: 12px; top: 12px;"></v-btn>
        </v-card-title>
        
        <v-card-text class="pa-6">
          <div class="text-center mb-4">
            <div class="text-subtitle-1 font-weight-medium mb-1">How was your experience?</div>
            <v-rating v-model="reviewForm.rating" color="amber" hover size="large"></v-rating>
          </div>

          <v-textarea
            v-model="reviewForm.content"
            label="Tell us about your visit"
            placeholder="Share your experience..."
            variant="outlined"
            rows="4"
            counter
            no-resize
          ></v-textarea>

          <v-file-input
            v-model="reviewForm.images"
            label="Add Photos"
            prepend-icon="mdi-camera"
            variant="outlined"
            multiple
            accept="image/*"
            chips
            show-size
            @update:model-value="onImageChange"
            :disabled="isEditing" 
            hint="Photos cannot be changed during edit"
            persistent-hint
          ></v-file-input>

          <!-- Image Previews -->
          <div v-if="imagePreviews.length > 0" class="d-flex gap-2 mt-2 overflow-x-auto">
            <div v-for="(src, idx) in imagePreviews" :key="idx" class="position-relative">
              <v-img :src="src" width="80" height="80" cover class="rounded border"></v-img>
              <v-btn v-if="!isEditing" icon="mdi-close" size="x-small" color="error" class="position-absolute" style="top: -5px; right: -5px;" @click="removeImage(idx)"></v-btn>
            </div>
          </div>
        </v-card-text>

        <v-card-actions class="pa-4 pt-0">
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey-darken-1" @click="closeReviewDialog">Cancel</v-btn>
          <v-btn color="primary" variant="flat" @click="submitReview" :loading="submittingReview">
            {{ isEditing ? 'Update Review' : 'Submit Review' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Image Viewer Dialog (Lightbox) -->
    <v-dialog v-model="imageDialog" max-width="900" max-height="90%">
      <v-card class="bg-black d-flex align-center justify-center position-relative" height="100%">
        <v-btn 
          icon="mdi-close" 
          variant="text" 
          color="white" 
          class="position-absolute" 
          style="top: 10px; right: 10px; z-index: 10;" 
          @click="imageDialog = false"
        ></v-btn>
        
        <v-img
          :src="selectedImageUrl"
          max-height="80vh"
          contain
        ></v-img>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { http, httpFile } from '@/api/http';
import { getPlaceDetail } from '@/api/place.api';
import { fetchReviewsByPlace, createReview, updateReview, deleteReview } from '@/api/review.api';
import { useAuthStore } from '@/stores/auth';
import defaultImage from '@/assets/seoulmate_logo.svg';

const route = useRoute();
const authStore = useAuthStore();

const loading = ref(true);
const place = ref(null);
const reviews = ref([]);
const isMapLoaded = ref(false);

const isBookmarked = ref(false);
const reviewDialog = ref(false);
const submittingReview = ref(false);
const isEditing = ref(false);
const editingReviewId = ref(null);

const imageDialog = ref(false);
const selectedImageUrl = ref('');

const reviewForm = reactive({
  rating: 5,
  content: '',
  images: []
});
const imagePreviews = ref([]);

// Pagination logic
const page = ref(1);
const itemsPerPage = 10; 

const totalPages = computed(() => Math.ceil(reviews.value.length / itemsPerPage) || 1);

const paginatedReviews = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return reviews.value.slice(start, end);
});

// [Helper] 리뷰 객체에서 이미지 URL 목록 추출
const getReviewImages = (review) => {
  if (!review || !review.images || review.images.length === 0) return [];
  
  // 백엔드에서 이미지 다운로드 URL 경로
  const baseUrl = '/api/files/download/';
  
  return review.images.map(img => {
    // ReviewImage 객체의 필드명: reviewImgName
    if (img.reviewImgName) {
      if (img.reviewImgName.startsWith('http')) return img.reviewImgName;
      return `${baseUrl}${img.reviewImgName}`;
    }
    return img.imageUrl || img.fileUrl || null;
  }).filter(url => url);
};

// [Computed] Traveler Photos 섹션에 모든 이미지를 수집
const allReviewImages = computed(() => {
  const images = [];
  reviews.value.forEach(review => {
    const reviewImgs = getReviewImages(review);
    if (reviewImgs.length > 0) {
      images.push(...reviewImgs);
    }
  });
  return images;
});

const fetchDetail = async () => {
  const contentId = route.params.id;
  if (!contentId) return;

  loading.value = true;
  try {
    const res = await getPlaceDetail(contentId);
    place.value = res.data || res;
    checkBookmarkStatus();

    const targetId = place.value.placeNo || place.value.id || place.value.contentId;
    if (targetId) {
        await loadReviews(targetId);
    }

    loading.value = false;
    if (place.value && place.value.mapx && place.value.mapy) {
      await nextTick();
      initMap(Number(place.value.mapy), Number(place.value.mapx)); 
    }
  } catch (err) {
    console.error("Failed to load detail:", err);
    loading.value = false;
  }
};

const loadReviews = async (placeNo) => {
    try {
        const reviewRes = await fetchReviewsByPlace(placeNo);
        const payload = reviewRes.data || reviewRes;
        
        if (Array.isArray(payload)) {
            reviews.value = payload;
        } else if (payload.data && Array.isArray(payload.data)) {
            reviews.value = payload.data;
        } else {
            reviews.value = [];
        }
        // 최신순 정렬 (리뷰 번호 내림차순 가정)
        reviews.value.sort((a, b) => b.reviewNo - a.reviewNo);
        
        // console.log("Loaded Reviews:", reviews.value); // Debugging
    } catch (e) {
        console.warn("Reviews load failed", e);
        reviews.value = [];
    }
};

const isMyReview = (review) => {
  return authStore.user && authStore.user.userNo === review.userNo;
};

const openReviewDialog = (isEdit = false, review = null) => {
  if (!authStore.isAuthenticated) {
    alert('Please log in to write a review.');
    return;
  }
  
  isEditing.value = isEdit;
  if (isEdit && review) {
    editingReviewId.value = review.reviewNo;
    reviewForm.rating = review.rating;
    reviewForm.content = review.content;
    reviewForm.images = []; 
    imagePreviews.value = []; 
  } else {
    editingReviewId.value = null;
    reviewForm.rating = 5;
    reviewForm.content = '';
    reviewForm.images = [];
    imagePreviews.value = [];
  }
  
  reviewDialog.value = true;
};

const editReview = (review) => {
  openReviewDialog(true, review);
};

const deleteReviewHandler = async (reviewNo) => {
  if (!confirm("Are you sure you want to delete this review?")) return;
  
  try {
    await deleteReview(reviewNo);
    alert("Review deleted.");
    await loadReviews(place.value.placeNo);
  } catch (e) {
    alert("Failed to delete review.");
  }
};

const closeReviewDialog = () => {
  reviewDialog.value = false;
  reviewForm.rating = 5;
  reviewForm.content = '';
  reviewForm.images = [];
  imagePreviews.value = [];
};

const onImageChange = () => {
  imagePreviews.value = [];
  if (reviewForm.images && reviewForm.images.length > 0) {
    reviewForm.images.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        imagePreviews.value.push(e.target.result);
      };
      reader.readAsDataURL(file);
    });
  }
};

const removeImage = (index) => {
  reviewForm.images.splice(index, 1);
  imagePreviews.value.splice(index, 1);
};

const submitReview = async () => {
  if (!reviewForm.content.trim()) {
    alert("Please write some content.");
    return;
  }

  submittingReview.value = true;
  try {
    if (isEditing.value) {
      const updateDto = {
        rating: reviewForm.rating,
        content: reviewForm.content
      };
      await updateReview(editingReviewId.value, updateDto);
      alert("Review updated!");
    } else {
      const formData = new FormData();
      const reviewData = {
        placeNo: place.value.placeNo,
        rating: reviewForm.rating,
        content: reviewForm.content
      };
      formData.append('review', new Blob([JSON.stringify(reviewData)], { type: 'application/json' }));
      
      if (reviewForm.images && reviewForm.images.length > 0) {
        reviewForm.images.forEach(file => {
          formData.append('files', file); 
        });
      }
      await createReview(formData);
      alert("Review submitted successfully!");
    }
    
    await loadReviews(place.value.placeNo);
    closeReviewDialog();
  } catch (e) {
    console.error("Review action failed", e);
    alert(`Failed to ${isEditing.value ? 'update' : 'submit'} review.`);
  } finally {
    submittingReview.value = false;
  }
};

const checkBookmarkStatus = async () => {
  if (!authStore.isAuthenticated) return;
  try {
    const res = await http.get('/bookmarks');
    const bookmarks = res.data.data || [];
    // bookmarks의 placeNo와 현재 place.placeNo 비교
    isBookmarked.value = bookmarks.some(b => b.placeNo === place.value.placeNo);
  } catch (e) {
    console.error("Bookmark check failed", e);
  }
};

// 찜하기/취소하기 (Toggle)
const toggleBookmark = async () => {
  if (!authStore.isAuthenticated) {
    alert('Please log in to save places.');
    return;
  }
  try {
    // 백엔드가 토글 방식이므로 항상 POST 호출
    const res = await http.post(`/bookmarks/${place.value.placeNo}`);
    
    // 응답 데이터에 새로운 상태가 포함되어 있다면 사용
    if (res.data && typeof res.data.data === 'boolean') {
        isBookmarked.value = res.data.data;
    } else {
        // 응답이 명확하지 않으면 수동으로 반전
        isBookmarked.value = !isBookmarked.value;
    }
    
    // 성공 메시지 (선택 사항)
    // alert(res.data.message || "Updated bookmark");
  } catch (e) {
    console.error("Toggle bookmark failed", e);
    alert("Failed to update bookmark.");
  }
};

// 공유하기 기능
// 추후에 Social 공유 기능 추가
const sharePlace = async () => {
    try {
        const url = window.location.href;
        await navigator.clipboard.writeText(url);
        alert("Link copied to clipboard!");
    } catch (err) {
        console.error('Failed to copy: ', err);
        alert("Failed to copy link.");
    }
};

const initMap = (lat, lng) => {
  if (window.naver && window.naver.maps) {
    try {
      const mapOptions = {
        center: new window.naver.maps.LatLng(lat, lng),
        zoom: 15,
        scrollWheel: false, 
        draggable: true
      };
      const map = new window.naver.maps.Map('naver-map', mapOptions);
      new window.naver.maps.Marker({
        position: new window.naver.maps.LatLng(lat, lng),
        map: map
      });
      isMapLoaded.value = true;
    } catch (e) {
      console.error("Naver Map Init Error:", e);
    }
  } else {
    isMapLoaded.value = false;
  }
};

const generateFallbackDescription = (place) => {
  if (!place) return '';
  const type = getCategoryName(place.contentTypeId);
  const location = place.addr1 ? `located at ${place.addr1}` : 'in Seoul';
  return `This is a popular ${type.toLowerCase()} ${location}. \n\nDetailed description is currently being updated. Please refer to the essential information on the right or check the reviews below for more insights from other travelers.`;
};

const getValidImageUrl = (url) => {
  if (url && url.length > 5) return url;
  return defaultImage; 
};

const getCategoryName = (typeId) => {
  const types = {
    76: 'Tourist Attraction',
    78: 'Cultural Facility',
    85: 'Festival / Event',
    75: 'Leisure / Sports',
    80: 'Accommodation',
    79: 'Shopping',
    82: 'Restaurant',
    77: 'Transportation'
  };
  return types[typeId] || 'Attraction';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString();
};

const formatReviewDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const months = ["Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."];
  const month = months[date.getMonth()];
  const day = date.getDate();
  const year = date.getFullYear();
  return `${month} ${day}, ${year}`;
};

const formatRating = (rating) => {
    return Number(rating || 0).toFixed(1);
}

const scrollToMap = () => {
  const element = document.getElementById('location-section');
  if (element) element.scrollIntoView({ behavior: 'smooth' });
};

const copyAddress = () => {
  if (place.value?.addr1) {
    const tempInput = document.createElement("input");
    tempInput.value = place.value.addr1;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand("copy");
    document.body.removeChild(tempInput);
    alert("Address copied!");
  }
};

const addToItinerary = () => {
  console.log("Add to itinerary clicked", place.value.placeNo);
};

const openGallery = (index) => {
  console.log("Open gallery image", index);
};

const openImage = (url) => {
  if (!url) return;
  selectedImageUrl.value = url;
  imageDialog.value = true;
};

onMounted(() => {
  fetchDetail();
});
</script>

<style scoped>
.gap-2 { gap: 8px; }
.gap-4 { gap: 16px; }
.sticky-sidebar { position: sticky; top: 100px; }
.text-grey-darken-4 { color: #212121 !important; }
.hover-zoom { transition: transform 0.2s; }
.hover-zoom:hover { transform: scale(1.05); }
.hover-bg:hover { background-color: #eee !important; }
</style>