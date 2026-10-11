<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card>
          <v-card-title class="text-h6">Log in</v-card-title>

          <v-card-text>
            <!-- 이메일 -->
            <v-text-field
              v-model="email"
              label="email"
              type="email"
              required
              @keyup.enter="handleLogin"
            />

            <!-- 비밀번호 -->
            <v-text-field
              v-model="pw"
              label="password"
              type="password"
              required
              @keyup.enter="handleLogin"
            />
          </v-card-text>

          <v-card-actions>
            <v-spacer />

            <v-btn
              color="primary"
              :loading="auth.loading"
              @click="handleLogin"
            >
              login
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 입력값
const email = ref('')
const pw = ref('')

// 라우터 & authStore
const router = useRouter()
const auth = useAuthStore()

// 로그인 처리
const handleLogin = async () => {
  try {
    await auth.login({
      email: email.value,
      pw: pw.value,
    })

    // 로그인 성공 → 홈으로 이동
    router.push('/')
  } catch (e) {
    alert('failed to login.')
  }
}
</script>