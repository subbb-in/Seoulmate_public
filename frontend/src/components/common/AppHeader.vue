<template>
  <v-app-bar class="px-4 border-b" color="white" flat height="80">


    <!-- 1. Logo Area -->
    <template #prepend>
      <div class="nav-left">
        <router-link class="d-flex align-center text-decoration-none" to="/">
          <v-img class="mr-2" height="120" src="@/assets/SeoulMate_logo_2_1.png" width="120" />
          <!--
          <v-icon class="mr-2" color="primary" icon="mdi-map-marker-star" size="32" />
          <span class="text-h5 font-weight-bold text-primary">SeoulMate</span>
          -->
        </router-link>
      </div>
    </template>

    <v-spacer />

    <!-- 2. Navigation Menu (Desktop) -->
    <!-- 메뉴 바 -->
    <div class="nav-center">
      <!-- Build Itinerary -->
      <v-menu open-on-hover location="bottom center" origin="top center" content-class="mega-menu mega-build">
        <template #activator="{ props }">
          <v-btn v-bind="props" class="nav-btn" variant="flat">
            Build Your Itinerary
          </v-btn>
        </template>

        <!-- 가로 펼침 영역 -->
        <v-sheet class="mega-inner d-flex justify-center align-center" rounded="xl" elevation="0">
          <v-btn variant="text" to="/itineraries/createItinerary">Create Itinerary</v-btn>
          <v-btn variant="text" to="/itineraries/create-ai">Create AI Itinerary</v-btn>
          <v-btn variant="text" to="/itineraries">My Itinerary</v-btn>
        </v-sheet>
      </v-menu>

      <!-- Where To Go -->
      <v-menu open-on-hover location="bottom center" origin="top center" content-class="mega-menu mega-where">
        <template #activator="{ props }">
          <v-btn v-bind="props" class="nav-btn" variant="flat">
            Where To Go
          </v-btn>
        </template>

        <v-sheet class="mega-inner d-flex justify-center align-center" rounded="xl" elevation="0" color="white">
          <v-btn variant="text" @click="destination">Destination</v-btn>
          <v-btn variant="text" @click="places">Places</v-btn>
        </v-sheet>
      </v-menu>

      <!-- Trip Information -->
      <v-btn class="nav-btn" variant="flat" to="/trip-info/basics">
        Trip Information
      </v-btn>

      <!-- Contact Us -->
      <v-btn class="nav-btn" variant="text" to="/contact">
        Contact Us
      </v-btn>
    </div>

    <v-spacer />

    <!-- 3. Auth Buttons -->
    <div class="nav-right">
      <template v-if="auth.checked">
        <!-- 로그인 O -->
        <v-menu v-if="auth.isAuthenticated" open-on-hover location="bottom">
          <template #activator="{ props }">
            <v-btn v-bind="props" color="#f8496c" variant="flat">
              {{ nickname }}
            </v-btn>
          </template>

          <v-list>
            <v-list-item to="/mypage">
              <v-list-item-title>My Page</v-list-item-title>
            </v-list-item>

            <v-list-item @click="handleLogout">
              <v-list-item-title>Log Out</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <!-- 로그인 X -->
        <template v-else>
          <v-btn variant="text" to="/login">Log In</v-btn>
          <v-btn variant="text" to="/signup">sign up</v-btn>
        </template>
      </template>
    </div>

    <!-- Mobile Menu Button -->
    <v-app-bar-nav-icon class="d-md-none" @click="drawer = !drawer" />
  </v-app-bar>

  <!-- Mobile Drawer -->
  <v-navigation-drawer v-model="drawer" location="right" temporary>
    <v-list>
      <v-list-item title="Home" to="/" />
      <v-list-item title="Places" to="/places" />
      <v-list-item title="Trip Information" to="/trip-info/basics"></v-list-item>
      <v-list-item title="Itinerary" to="/itinerary" />
      <v-list-item title="Community" to="/community" />
      <v-divider class="my-2" />
      <v-list-item v-if="!auth.isAuthenticated" title="Log In" to="/login" />
      <v-list-item v-if="!auth.isAuthenticated" class="text-primary" title="Sign Up" to="/signup" />
    </v-list>
  </v-navigation-drawer>
</template>

<script setup>
import { ref, computed } from 'vue'
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const drawer = ref(false)

const { user } = storeToRefs(auth);
const nickname = computed(() => {
  return user.value?.nickname || user.value?.email || 'Guest';
});

const handleLogout = async () => {
  await auth.logout()
  router.push('/login')
}

// 라우터 연결
const destination = () => router.push('/destinations')
const places = () => router.push('/places')
// const tripInfo = () => router.push('/trip-info') // 드롭다운으로 대체되어 더 이상 사용하지 않음
</script>

<style scoped>
/* 헤더 요소 중앙 정렬 */
.nav-left {
  width: 200px;
  display: flex;
  align-items: center;
}

.nav-center {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 18px;
  align-items: center;
  transform: translateY(6px);
}

.nav-right {
  width: 200px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* 메뉴 버튼 회색 배경 제거 */
:deep(.nav-btn.v-btn--active),
:deep(.nav-btn.v-btn--active:hover),
:deep(.nav-btn.v-btn--active:focus) {
  background: transparent !important;
  box-shadow: none !important;
}

/* 메뉴 기본 그림자 제거 */
:deep(.v-overlay__content.mega-menu) {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}

/* mega-inner가 theme surface 배경을 먹는 문제 방지 */
:deep(.mega-inner) {
  background-color: white !important;
  box-shadow: none !important;
}

/* mega-menu */
:deep(.mega-menu) {
  width: max-content;
  max-width: 860px;
  margin: 0 auto;
  border-radius: 30px;
  overflow: visible;
}

/* 드롭다운 내용 내부 */
.mega-inner {
  padding: 10px 18px;
  gap: 12px 12px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

/* 메인 메뉴 버튼 */
.nav-btn {
  font-size: 13px;
  letter-spacing: 0.08em;
  font-weight: 600;
}

/* 드롭다운(세부 메뉴) 버튼 */
:deep(.mega-inner .v-btn) {
  font-size: 12px;
  letter-spacing: 0.06em;
  font-weight: 500;
}

/* app bar 공백 */
.appbar {
  padding: 0 30px;
}
</style>
