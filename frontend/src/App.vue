<template>
  <v-app>
    <!-- Header -->
    <AppHeader />

    <!-- Main Content (Router View) -->
    <v-main>
      <router-view />
    </v-main>

    <!-- Footer -->
    <v-footer class="bg-gray-light text-center d-flex flex-column py-6">
      <div class="d-flex w-100 align-center justify-center px-4">
        <strong class="text-primary mr-2">SeoulMate</strong> - Connect with Seoul
      </div>
      <div class="text-caption text-text-sub mt-2">
        {{ new Date().getFullYear() }} — <strong>SSAFY SeoulMate Team</strong>
      </div>
    </v-footer>
  </v-app>
</template>

<script setup>
import AppHeader from '@/components/common/AppHeader.vue'
import { onMounted } from 'vue'
// Pinia store 중에서 인증 상태를 관리하는 store -> 로그인 유저 정보, 로그인 여부 등을 들고 있음
import { useAuthStore } from '@/stores/auth'

// auth store의 실제 인스턴스 -> auth.user, auth.isAuthenticated, auth.fetchMe() 등 사용가능
const auth = useAuthStore()

onMounted(() => {
  auth.fetchMe()
  // 백엔드에 로그인이 되어있는지 확인함
  // 로그인 상태면 auth.user에 유저 정보 저장 아니라면 null -> 이를 통해 프론트와 백엔드 로그인 상태를 동기화
})
</script>
