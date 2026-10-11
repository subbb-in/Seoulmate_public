<template>
  <v-container class="py-10" style="max-width: 1200px;">
    <div class="text-center mb-10">
      <h2 class="text-h3 font-weight-bold text-primary mb-3">Contact Us</h2>
      <p class="text-h6 text-grey-darken-1 font-weight-regular">
        Have questions about your Seoul trip? We're here to help!
      </p>
      <v-divider class="mx-auto mt-4" color="primary" thickness="3" length="60" />
    </div>

    <v-row>
      <v-col cols="12" md="5" lg="4">
        <v-card color="primary" rounded="xl" class="pa-8 h-100 elevation-4 text-white">
          <h3 class="text-h5 font-weight-bold mb-6">Get in touch</h3>
          <p class="mb-8 text-body-1 opacity-90">
            Feel free to reach out to us for any inquiries, partnerships, or assistance with your itinerary.
          </p>

          <div class="d-flex align-start mb-6">
            <v-icon icon="mdi-map-marker" class="mr-4 mt-1" size="large" />
            <div>
              <div class="font-weight-bold text-h6">Office</div>
              <div class="opacity-80">
                SSAFY Seoul Campus,<br>
                Gangnam-gu, Seoul, South Korea
              </div>
            </div>
          </div>

          <div class="d-flex align-center mb-6">
            <v-icon icon="mdi-email" class="mr-4" size="large" />
            <div>
              <div class="font-weight-bold text-h6">Email</div>
              <div class="opacity-80">support@seoulmate.com</div>
            </div>
          </div>

          <div class="d-flex align-center mb-8">
            <v-icon icon="mdi-phone" class="mr-4" size="large" />
            <div>
              <div class="font-weight-bold text-h6">Phone</div>
              <div class="opacity-80">+82 2-1234-5678</div>
            </div>
          </div>

          <div class="mt-auto pt-4">
            <p class="font-weight-bold mb-3">Follow Us</p>
            <div class="d-flex gap-4">
              <v-btn icon="mdi-instagram" variant="text" color="white" density="comfortable" />
              <v-btn icon="mdi-facebook" variant="text" color="white" density="comfortable" />
              <v-btn icon="mdi-twitter" variant="text" color="white" density="comfortable" />
              <v-btn icon="mdi-youtube" variant="text" color="white" density="comfortable" />
            </div>
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" md="7" lg="8">
        <v-card rounded="xl" class="pa-8 h-100 elevation-2">
          <h3 class="text-h5 font-weight-bold mb-6 text-grey-darken-3">Send us a message</h3>
          
          <v-form ref="formRef" @submit.prevent="submitForm">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.name"
                  label="Your Name"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-account"
                  :rules="[v => !!v || 'Name is required']"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.email"
                  label="Email Address"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-email-outline"
                  :rules="emailRules"
                />
              </v-col>
              
              <v-col cols="12">
                <v-text-field
                  v-model="form.subject"
                  label="Subject"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-format-title"
                  placeholder="What is this regarding?"
                  :rules="[v => !!v || 'Subject is required']"
                />
              </v-col>

              <v-col cols="12">
                <v-textarea
                  v-model="form.message"
                  label="Message"
                  variant="outlined"
                  rows="5"
                  auto-grow
                  prepend-inner-icon="mdi-comment-text-outline"
                  placeholder="Tell us more about your inquiry..."
                  :rules="[v => !!v || 'Message is required']"
                />
              </v-col>
            </v-row>

            <div class="d-flex justify-end mt-4">
              <v-btn
                type="submit"
                color="primary"
                size="x-large"
                rounded="pill"
                min-width="150"
                class="font-weight-bold px-8"
                :loading="loading"
              >
                Send Message
                <v-icon end icon="mdi-send" />
              </v-btn>
            </div>
          </v-form>
        </v-card>
      </v-col>
    </v-row>

    <!-- 약관 -->
     <v-row class="mt-8">
      <v-col cols="12">
        <h2 class="text-h4 font-weight-bold text-primary mb-3 text-center">Legal Information</h2>
        <v-divider class="mx-auto mt-4" color="primary" thickness="3" length="60" />
        <v-expansion-panels variant="accordion" class="elevation-1 rounded-lg mt-6">
          <v-expansion-panel title="이용약관 (Terms of Service)">
            <v-expansion-panel-text class="text-body-2 text-grey-darken-1">
              <p class="mb-2"><strong>제1조 (목적)</strong></p>
              <p class="mb-4">
                본 약관은 서울메이트(이하 "회사")가 제공하는 여행 정보 및 커뮤니티 서비스(이하 "서비스")의 이용과 관련하여 회사와 회원의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.
              </p>
              <p class="mb-2"><strong>제2조 (약관의 명시와 개정)</strong></p>
              <p class="mb-4">
                회사는 이 약관의 내용을 회원이 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다. 회사는 관련 법령을 위배하지 않는 범위에서 이 약관을 개정할 수 있습니다.
              </p>
              <p class="text-caption text-grey">
                ※ 본 내용은 예시이며, 실제 운영 시 구체적인 이용약관 내용으로 대체되어야 합니다.
              </p>
            </v-expansion-panel-text>
          </v-expansion-panel>

          <v-expansion-panel title="개인정보 처리방침 (Privacy Policy)">
            <v-expansion-panel-text class="text-body-2 text-grey-darken-1">
              <p class="mb-2"><strong>1. 개인정보의 수집 및 이용 목적</strong></p>
              <p class="mb-4">
                회사는 고객 문의 응대, 서비스 제공, 회원 관리 등을 위하여 최소한의 개인정보를 수집하고 있습니다.
              </p>
              <p class="mb-2"><strong>2. 수집하는 개인정보의 항목</strong></p>
              <p class="mb-4">
                - 필수항목: 이름, 이메일 주소<br>
                - 선택항목: 전화번호, 기타 문의 내용에 포함된 개인정보
              </p>
              <p class="mb-2"><strong>3. 개인정보의 보유 및 이용 기간</strong></p>
              <p class="mb-4">
                회사는 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 단, 관계법령의 규정에 의하여 보존할 필요가 있는 경우 일정 기간 동안 보관합니다.
              </p>
              <p class="text-caption text-grey">
                ※ 본 내용은 예시이며, 실제 운영 시 구체적인 개인정보 처리방침 내용으로 대체되어야 합니다.
              </p>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar" color="success" timeout="3000" location="top">
      <div class="d-flex align-center">
        <v-icon icon="mdi-check-circle" class="mr-2" />
        <span class="font-weight-bold">Message Sent Successfully!</span>
      </div>
      <p class="text-caption mb-0">We'll get back to you soon.</p>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';

const loading = ref(false);
const snackbar = ref(false);
const formRef = ref(null);

const form = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
});

const emailRules = [
  v => !!v || 'Email is required',
  v => /.+@.+\..+/.test(v) || 'E-mail must be valid'
];

const submitForm = async () => {
  // 폼 유효성 검사
  const { valid } = await formRef.value.validate();
  if (!valid) return;

  loading.value = true;

  try {
    // TODO: 실제 백엔드 API 연결 (예: postContactUs(form.value))
    // 현재는 전송 시뮬레이션 (1.5초 대기)
    await new Promise(resolve => setTimeout(resolve, 1500));

    console.log("Form Submitted:", form.value);
    
    // 성공 처리
    snackbar.value = true;
    formRef.value.reset(); // 폼 초기화

  } catch (error) {
    console.error("Error sending message:", error);
    alert("Failed to send message. Please try again later.");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.gap-4 {
  gap: 16px;
}
</style>