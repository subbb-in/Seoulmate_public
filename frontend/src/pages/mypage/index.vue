<template>
  <v-container class="py-10" max-width="1000">
    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>

    <div v-else>
      <!-- 1. Profile Section -->
      <v-card class="mb-8 pa-6 rounded-lg elevation-2">
        <div class="d-flex flex-column flex-md-row align-center">
          <div class="position-relative mr-md-8 mb-4 mb-md-0">
            <v-avatar size="120" color="grey-lighten-2" class="elevation-3">
              <v-img v-if="profile.profileImg" :src="getProfileImageUrl(profile.profileImg)" alt="Profile"
                cover></v-img>
              <v-icon v-else size="60" color="grey-darken-1">mdi-account</v-icon>
            </v-avatar>
            <v-btn icon="mdi-camera" size="small" color="primary" class="position-absolute" style="bottom: 0; right: 0;"
              @click="triggerFileInput"></v-btn>
            <input type="file" ref="fileInput" style="display: none" accept="image/*" @change="onFileChange">
          </div>

          <div class="flex-grow-1 text-center text-md-left">
            <h2 class="text-h4 font-weight-bold mb-1">{{ profile.nickname }}</h2>
            <p class="text-body-1 text-grey-darken-1 mb-4">{{ profile.email }}</p>

            <div class="d-flex gap-4 justify-center justify-md-start">
              <v-chip prepend-icon="mdi-map-marker-path" color="primary" variant="tonal">
                {{ itineraries.length }} Trips
              </v-chip>
              <v-chip prepend-icon="mdi-pencil" color="secondary" variant="tonal">
                {{ reviews.length }} Reviews
              </v-chip>
              <v-chip prepend-icon="mdi-bookmark" color="amber" variant="tonal">
                {{ bookmarks.length }} Bookmarks
              </v-chip>
            </div>
          </div>

          <div class="mt-4 mt-md-0">
            <v-btn variant="outlined" color="primary" prepend-icon="mdi-cog" @click="editProfileDialog = true">
              Edit Profile
            </v-btn>
          </div>
        </div>
      </v-card>

      <!-- 2. Tabs Section -->
      <v-card class="rounded-lg elevation-1">
        <v-tabs v-model="activeTab" color="primary" align-tabs="start">
          <v-tab value="itineraries">My Itineraries</v-tab>
          <v-tab value="reviews">My Reviews</v-tab>
          <v-tab value="bookmarks">Bookmarks</v-tab>
        </v-tabs>

        <v-card-text class="bg-grey-lighten-5 pa-6">
          <v-window v-model="activeTab">

            <!-- Tab 1: Itineraries -->
            <v-window-item value="itineraries">
              <!-- paginatedItineraries 사용 -->
              <v-row v-if="paginatedItineraries.length > 0">
                <v-col v-for="item in paginatedItineraries" :key="item.itinNo" cols="12" sm="6" md="4">
                  <v-card class="h-100 rounded-lg hover-card" @click="goToItinerary(item.itinNo)">
                    <v-img :src="item.thumbnail || '/placeholder_itinerary.jpg'" height="180" cover class="align-end"
                      gradient="to bottom, rgba(0,0,0,0) 60%, rgba(0,0,0,0.6) 100%">
                      <v-card-title class="text-white font-weight-bold text-subtitle-1">
                        {{ item.itinTitle }}
                      </v-card-title>
                    </v-img>
                    <v-card-text class="pt-3">
                      <div class="text-caption text-grey mb-1">
                        Total {{ item.dayCnt }} Days Trip
                      </div>
                      <div class="d-flex align-center justify-space-between">
                        <span class="text-caption font-weight-bold text-primary">
                          {{ item.dayCnt }} Days
                        </span>
                      </div>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>
              <div v-else class="text-center py-10">
                <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-map-plus</v-icon>
                <p class="text-grey mb-4">You haven't created any itineraries yet.</p>
                <v-btn color="primary" to="/itineraries/createItinerary">Create New Trip</v-btn>
              </div>

              <!-- Itinerary Pagination -->
              <div class="text-center mt-6" v-if="totalItineraryPages > 1">
                <v-pagination v-model="itineraryPage" :length="totalItineraryPages" rounded="circle"
                  active-color="primary" density="compact"></v-pagination>
              </div>
            </v-window-item>

            <!-- Tab 2: Reviews -->
            <v-window-item value="reviews">
              <!-- paginatedReviews 사용 -->
              <div v-if="paginatedReviews.length > 0">
                <v-card v-for="review in paginatedReviews" :key="review.reviewNo" class="mb-4 pa-4 rounded-lg"
                  variant="flat" border>
                  <div class="d-flex justify-space-between align-start mb-2">
                    <div class="font-weight-bold text-subtitle-1 cursor-pointer text-primary"
                      @click="goToPlace(review.placeNo)">
                      {{ review.placeTitle || 'Unknown Place' }}
                      <v-icon size="small" icon="mdi-chevron-right"></v-icon>
                    </div>

                    <div class="d-flex align-center">
                      <div class="text-caption text-grey mr-2">{{ formatDate(review.createdAt) }}</div>
                      <v-menu>
                        <template v-slot:activator="{ props }">
                          <v-btn icon="mdi-dots-vertical" variant="text" size="small" v-bind="props"></v-btn>
                        </template>
                        <v-list density="compact">
                          <v-list-item @click="openEditReview(review)" prepend-icon="mdi-pencil"
                            title="Edit"></v-list-item>
                          <v-list-item @click="deleteReviewHandler(review.reviewNo)" prepend-icon="mdi-delete"
                            title="Delete" class="text-error"></v-list-item>
                        </v-list>
                      </v-menu>
                    </div>
                  </div>
                  <v-rating :model-value="review.rating" color="amber" density="compact" readonly size="small"
                    class="mb-2"></v-rating>
                  <p class="text-body-2 text-grey-darken-3 mb-0">{{ review.content }}</p>
                </v-card>
              </div>
              <div v-else class="text-center py-10">
                <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-message-draw</v-icon>
                <p class="text-grey">No reviews written yet.</p>
              </div>

              <!-- Review Pagination -->
              <div class="text-center mt-6" v-if="totalReviewPages > 1">
                <v-pagination v-model="reviewPage" :length="totalReviewPages" rounded="circle" active-color="primary"
                  density="compact"></v-pagination>
              </div>
            </v-window-item>

            <!-- Tab 3: Bookmarks -->
            <v-window-item value="bookmarks">
              <!-- paginatedBookmarks 사용 -->
              <v-list v-if="paginatedBookmarks.length > 0" lines="two" bg-color="transparent">
                <v-list-item v-for="place in paginatedBookmarks" :key="place.placeNo"
                  :prepend-avatar="place.firstImage || '/src/assets/Seoul_1 1.png'" :title="place.title"
                  :subtitle="place.addr1" class="mb-2 bg-white rounded-lg elevation-1 cursor-pointer"
                  @click="goToPlace(place.placeNo)">
                  <template v-slot:append>
                    <v-btn icon="mdi-heart" color="error" variant="text" size="small"
                      @click.stop="removeBookmark(place.placeNo)"></v-btn>
                  </template>
                </v-list-item>
              </v-list>
              <div v-else class="text-center py-10">
                <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-heart-off</v-icon>
                <p class="text-grey">No bookmarked places.</p>
                <v-btn color="primary" to="/places" variant="text">Explore Places</v-btn>
              </div>

              <!-- Bookmark Pagination -->
              <div class="text-center mt-6" v-if="totalBookmarkPages > 1">
                <v-pagination v-model="bookmarkPage" :length="totalBookmarkPages" rounded="circle"
                  active-color="primary" density="compact"></v-pagination>
              </div>
            </v-window-item>

          </v-window>
        </v-card-text>
      </v-card>
    </div>

    <!-- Edit Profile Dialog -->
    <v-dialog v-model="editProfileDialog" max-width="500">
      <v-card>
        <v-card-title class="font-weight-bold">Edit Profile</v-card-title>
        <v-card-text>
          <v-text-field v-model="editForm.nickname" label="Nickname" variant="outlined" class="mb-2"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="editProfileDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="saveProfile">Save Changes</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Edit Review Dialog -->
    <v-dialog v-model="reviewDialog" max-width="500">
      <v-card>
        <v-card-title class="font-weight-bold">Edit Review</v-card-title>
        <v-card-text>
          <div class="d-flex justify-center mb-4">
            <v-rating v-model="reviewEditForm.rating" color="amber" hover size="large"></v-rating>
          </div>
          <v-textarea v-model="reviewEditForm.content" label="Content" variant="outlined" rows="4"></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="reviewDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="submitEditReview">Update</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
// API functions
import { useAuthStore } from '@/stores/auth';
import { fetchMyProfile, fetchMyItineraries, fetchMyReviews, fetchMyBookmarks, updateMyProfile } from '@/api/mypage.api.js';
import { deleteReview, updateReview } from '@/api/review.api';
import { updateUserInfo, updateUserProfileImage } from '@/api/user.api.js';
import { http } from '@/api/http';

const router = useRouter();
const loading = ref(true);
const activeTab = ref('itineraries');
const editProfileDialog = ref(false);
const fileInput = ref(null);
const authStore = useAuthStore();

const API_BASE_URL = "http://localhost:8080";
const getProfileImageUrl = (filename) => {
  if (!filename) return null;

  // 1. 이미 http로 시작하는(외부 이미지) 경우 그대로 반환
  if (filename.startsWith('http') || filename.startsWith('blob:')) return filename;

  // 2. 파일 이름만 있는 경우 앞에 '/uploads/' 붙이기
  //    (캐시 문제 방지를 위해 ?t=시간 추가)
  return `${API_BASE_URL}/uploads/${filename}`;
};

const onFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const previewUrl = URL.createObjectURL(file);
  profile.value.profileImg = previewUrl;

  try {
    // userNo 가져오기
    const userNo = authStore.user?.userNo;

    if (!userNo) {
      alert("로그인 정보가 없습니다.");
      return;
    }

    // user.api.js의 updateUserProfileImage 사용
    // (함수 내부에서 FormData를 생성하므로 여기선 file만 넘김)
    await updateUserProfileImage(userNo, file);

    // 업로드 후 스토어 다시 호출
    await authStore.fetchMe();
    profile.value.profileImg = authStore.user.profileImg;

    if (authStore.user?.profileImg) {
      profile.value.profileImg = authStore.user.profileImg;
    }
    alert("프로필 이미지가 변경되었습니다.");

  } catch (e) {
    console.error("Image upload failed", e);
    alert("이미지 업로드에 실패했습니다.");
  }
};

// 프로필 정보(닉네임) 수정 로직 수정
const saveProfile = async () => {
  try {
    const userNo = authStore.user?.userNo;
    if (!userNo) return;

    // user.api.js의 updateUserInfo 사용 (JSON 전송)
    // 기존처럼 FormData를 쓰지 않고 객체를 전달
    const updateData = {
      nickname: editForm.nickname
    };

    // 1. 서버 DB 업데이트
    await updateUserInfo(userNo, updateData);

    if(authStore.user) {
      authStore.user.nickname = editForm.nickname;
    }
    // 2. 전역 스토어 정보 새로고침
    // await authStore.fetchMe();

    // 성공 시 UI 반영 및 모달 닫기
    profile.value.nickname = editForm.nickname;
    editProfileDialog.value = false;
    alert("회원 정보가 수정되었습니다.");

    // Store 정보도 갱신해주면 좋음
    if (authStore.fetchMe) await authStore.fetchMe();

  } catch (e) {
    console.error("Profile update failed", e);
    alert("정보 수정에 실패했습니다.");
  }
};

// Review Edit
const reviewDialog = ref(false);
const reviewEditForm = reactive({
  reviewNo: null,
  rating: 5,
  content: ''
});

// Data
const profile = ref({
  nickname: 'User',
  email: 'user@example.com',
  profileImg: null
});
const itineraries = ref([]);
const reviews = ref([]);
const bookmarks = ref([]);

const editForm = reactive({
  nickname: ''
});

// Pagination Logic
const itineraryPage = ref(1);
const reviewPage = ref(1);
const bookmarkPage = ref(1);

const itineraryItemsPerPage = 10;
const reviewItemsPerPage = 10;
const bookmarkItemsPerPage = 20;

// [추가] Computed Properties for Pagination
const paginatedItineraries = computed(() => {
  return itineraries.value;
  // if (!Array.isArray(itineraries.value)) return [];
  // const start = (itineraryPage.value - 1) * itineraryItemsPerPage;
  // const end = start + itineraryItemsPerPage;
  // return itineraries.value.slice(start, end);
});

const totalItineraryPages = ref(1);
// const totalItineraryPages = computed(() => {
//   if (!Array.isArray(itineraries.value)) return 1;
//   return Math.ceil(itineraries.value.length / itineraryItemsPerPage) || 1;
// });

const paginatedReviews = computed(() => {
  if (!Array.isArray(reviews.value)) return [];
  const start = (reviewPage.value - 1) * reviewItemsPerPage;
  const end = start + reviewItemsPerPage;
  return reviews.value.slice(start, end);
});

const totalReviewPages = computed(() => {
  if (!Array.isArray(reviews.value)) return 1;
  return Math.ceil(reviews.value.length / reviewItemsPerPage) || 1;
});

const paginatedBookmarks = computed(() => {
  if (!Array.isArray(bookmarks.value)) return [];
  const start = (bookmarkPage.value - 1) * bookmarkItemsPerPage;
  const end = start + bookmarkItemsPerPage;
  return bookmarks.value.slice(start, end);
});

const totalBookmarkPages = computed(() => {
  if (!Array.isArray(bookmarks.value)) return 1;
  return Math.ceil(bookmarks.value.length / bookmarkItemsPerPage) || 1;
});


// Fetch All Data
const loadData = async () => {
  loading.value = true;
  try {
    const [profileRes, itineraryRes, reviewRes, bookmarkRes] = await Promise.allSettled([
      fetchMyProfile(),
      fetchMyItineraries(),
      fetchMyReviews(),
      fetchMyBookmarks()
    ]);

    // Profile
    if (profileRes.status === 'fulfilled' && profileRes.value) {
      const data = profileRes.value.data || profileRes.value;

      console.log("서버에서 받은 데이터:", data);
      profile.value = data;
      editForm.nickname = data.nickname;
      editForm.profileImg = data.profileImg;
    } else {
      // Mock Data Fallback
      profile.value = { nickname: 'Seoul Traveler', email: 'traveler@seoulmate.com', profileImg: null };
    }

    // Itineraries
    if (itineraryRes.status === 'fulfilled' && itineraryRes.value) {
      const data = itineraryRes.value.data || itineraryRes.value;

      // 1. DTO 구조에 맞춰 'content' 필드 확인
      if (data && Array.isArray(data.content)) {
        itineraries.value = data.content;

        // (선택) 전체 페이지 수도 필요하다면 여기서 업데이트
        totalItineraryPages.value = Math.ceil(data.totalCount / itineraryItemsPerPage) || 1;
      }
      // 2. 방어 코드: 혹시 바로 배열로 들어오는 경우
      else if (Array.isArray(data)) {
        itineraries.value = data;
      }
      else {
        console.warn("여행 일정 데이터 형식을 확인하세요:", data);
        itineraries.value = [];
      }
    } else {
      itineraries.value = [];
    }

    // Reviews
    if (reviewRes.status === 'fulfilled' && reviewRes.value) {
      const data = reviewRes.value.data || reviewRes.value;
      reviews.value = Array.isArray(data) ? data : [];
    } else {
      reviews.value = [];
    }

    // Bookmarks
    if (bookmarkRes.status === 'fulfilled' && bookmarkRes.value) {
      const data = bookmarkRes.value.data || bookmarkRes.value;
      bookmarks.value = Array.isArray(data) ? data : [];
    } else {
      bookmarks.value = [];
    }

  } catch (error) {
    console.error("Error loading mypage data", error);
  } finally {
    loading.value = false;
  }
};

const triggerFileInput = () => {
  fileInput.value.click();
};

const removeBookmark = async (placeNo) => {
  try {
    await http.delete(`/bookmarks/${placeNo}`);
    bookmarks.value = bookmarks.value.filter(b => b.placeNo !== placeNo);
  } catch (e) {
    console.error("Remove bookmark failed", e);
  }
};

// 리뷰 삭제
const deleteReviewHandler = async (reviewNo) => {
  if (!confirm("Are you sure you want to delete this review?")) return;
  try {
    await deleteReview(reviewNo);
    reviews.value = reviews.value.filter(r => r.reviewNo !== reviewNo);
  } catch (e) {
    console.error("Failed to delete review", e);
    alert("Failed to delete review");
  }
}

// 리뷰 수정 모달 열기
const openEditReview = (review) => {
  reviewEditForm.reviewNo = review.reviewNo;
  reviewEditForm.rating = review.rating;
  reviewEditForm.content = review.content;
  reviewDialog.value = true;
}

// 리뷰 수정 제출
const submitEditReview = async () => {
  try {
    await updateReview(reviewEditForm.reviewNo, {
      rating: reviewEditForm.rating,
      content: reviewEditForm.content
    });

    // 목록 갱신 (로컬 반영)
    const target = reviews.value.find(r => r.reviewNo === reviewEditForm.reviewNo);
    if (target) {
      target.rating = reviewEditForm.rating;
      target.content = reviewEditForm.content;
    }

    reviewDialog.value = false;
  } catch (e) {
    console.error("Failed to update review", e);
    alert("Failed to update review");
  }
}

// Navigation
const goToItinerary = (id) => router.push(`/itineraries/${id}`);
const goToPlace = (id) => router.push(`/places/${id}`);

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString();
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.gap-4 {
  gap: 16px;
}

.hover-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cursor-pointer {
  cursor: pointer;
}
</style>