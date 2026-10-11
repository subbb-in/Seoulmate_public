<template>
  <v-container class="signup-wrap">
    <v-card class="signup-card" rounded="xl" elevation="0">
      <v-card-title class="text-h5 font-weight-bold">Sign up</v-card-title>

      <v-card-text>
        <v-form ref="formRef" v-model="isValid" @submit.prevent="submit">
          <v-text-field
            v-model="form.email"
            label="email"
            type="email"
            autocomplete="email"
            :rules="rules.email"
            required
          />

          <v-text-field
            v-model="form.pw"
            label="password"
            type="password"
            autocomplete="new-password"
            :rules="rules.pw"
            required
          />

          <v-text-field
            v-model="form.nickname"
            label="nickname"
            autocomplete="nickname"
            :rules="rules.nickname"
            required
          />

          <!-- birth: DATE면 보통 yyyy-mm-dd로 받는 게 편함 -->
          <v-text-field
            v-model="form.birth"
            label="birth"
            type="date"
            :rules="rules.birth"
            required
          />

          <v-select
            v-model="form.languageCode"
            label="language"
            :items="languageOptions"
            item-title="label"
            item-value="value"
            :rules="rules.languageCode"
            required
          />

          <v-btn
            class="mt-4"
            type="submit"
            color="#f8496c"
            block
            :loading="loading"
            :disabled="!isValid || loading"
          >
            Sign Up
          </v-btn>

          <v-alert
            v-if="errorMsg"
            class="mt-4"
            type="error"
            variant="tonal"
            density="comfortable"
          >
            {{ errorMsg }}
          </v-alert>

          <v-alert
            v-if="successMsg"
            class="mt-4"
            type="success"
            variant="tonal"
            density="comfortable"
          >
            {{ successMsg }}
          </v-alert>
        </v-form>
      </v-card-text>

      <v-card-actions class="justify-end">
        <v-btn variant="text" to="/login">I already have an account</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { signup } from '@/api/user.api.js'

const router = useRouter()

const formRef = ref(null)
const isValid = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const form = reactive({
  email: '',
  pw: '',
  nickname: '',
  birth: '',         // yyyy-mm-dd
  languageCode: '',  // 예: 'KO', 'EN'
})

const languageOptions = [
  { label: '한국어 (KO)', value: 'KO' },
  { label: 'English (EN)', value: 'EN' },
  { label: '日本語 (JA)', value: 'JA' },
  { label: '中文 (ZH)', value: 'ZH' },
]

const rules = {
  email: [
    v => !!v || 'Email is required',
    v => /.+@.+\..+/.test(v) || 'Please enter a valid email address',
  ],
  pw: [
    v => !!v || 'Password is required',
    v => (v?.length ?? 0) >= 8 || 'Password must be at least 8 characters',
  ],
  nickname: [
    v => !!v || 'Nickname is required',
    v => (v?.length ?? 0) >= 2 || 'Nickname must be at least 2 characters',
  ],
  birth: [
    v => !!v || 'Date of birth is required',
  ],
  languageCode: [
    v => !!v || 'Please select a language',
  ],
}

const submit = async () => {
  errorMsg.value = ''
  successMsg.value = ''

  const ok = await formRef.value?.validate()
  if (!ok?.valid) return

  loading.value = true
  try {
    // ✅ 여기서 실제 회원가입 API 호출
    await signup({ ...form })

    // 임시 성공 처리
    successMsg.value = 'Your account has been created successfully. Redirecting to the login'
    setTimeout(() => router.push('/login'), 800)
  } catch (e) {
    errorMsg.value = e?.response?.data?.message ?? 'An error occurred during sign-up'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.signup-wrap {
  max-width: 520px;
  padding-top: 80px;
}
.signup-card {
  padding: 12px;
}
</style>
