<template>
  <v-container class="py-10" style="max-width: 720px;">
    <v-card rounded="xl" class="pa-6 elevation-3">
      <v-card-title class="text-h5 font-weight-bold mb-4">Create New Itinerary</v-card-title>

      <v-form @submit.prevent="submit">
        <v-text-field
          v-model="tripName"
          label="Trip Name"
          placeholder="2025 Seoul Trip for Museum Visits"
          variant="outlined"
          density="comfortable"
          :rules="[v => !!v || 'Please write a travel title']"
        />

        <v-menu v-model="dateMenu" :close-on-content-click="false" location="bottom center">
          <template #activator="{ props }">
            <v-text-field
              v-bind="props"
              label="Trip Dates"
              variant="outlined"
              density="comfortable"
              readonly
              prepend-inner-icon="mdi-calendar"
              :model-value="dateRangeText"
              :rules="[
                () => !!startDate && !!endDate || 'Please choose the travel dates',
                () => isDurationValid || `Trip cannot exceed ${MAX_DAYS} days.`
              ]"
              class="mt-2"
            />
          </template>

          <v-card rounded="xl" class="pa-0">
            <v-date-picker
              v-model="range"
              multiple="range"
              color="primary"
              show-adjacent-months
              hide-header
            />
            <v-card-actions class="justify-end pt-0 pb-2 pr-4">
              <v-btn variant="text" color="grey" @click="resetDates">reset</v-btn>
              <v-btn variant="flat" color="primary" @click="dateMenu = false">done</v-btn>
            </v-card-actions>
          </v-card>
        </v-menu>

        <v-text-field
          class="mt-2"
          label="Total travel days"
          variant="filled"
          density="comfortable"
          readonly
          :model-value="tripDaysText"
          :error="!isDurationValid && calculatedDays > 0"
          :error-messages="!isDurationValid && calculatedDays > 0 ? [`Maximum duration is ${MAX_DAYS} days.`] : []"
          hint="This will be calculated automatically once the period is selected."
          persistent-hint
        />

        <v-btn
          class="mt-8"
          type="submit"
          block
          size="x-large"
          rounded="xl"
          color="primary"
          :disabled="!canSubmit || isSubmitting"
          :loading="isSubmitting"
        >
          Create Itinerary
        </v-btn>
      </v-form>
    </v-card>
  </v-container>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { createItinerary } from "@/api/itinerary.api.js";

const router = useRouter();

// [설정] 최대 여행 가능 일수
const MAX_DAYS = 7;

// 상태 값
const tripName = ref("");
const isSubmitting = ref(false);

// 날짜 관련 상태
const dateMenu = ref(false);
const range = ref([]); 
const startDate = ref(null);
const endDate = ref(null);

// 랜덤 썸네일용 이미지
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

// 날짜 범위 선택 감지 및 정렬 로직
watch(range, (val) => {
  if (!val || val.length === 0) {
    startDate.value = null;
    endDate.value = null;
    return;
  }
  const sortedDates = [...val].sort((a, b) => new Date(a) - new Date(b));
  startDate.value = sortedDates[0];
  if (sortedDates.length > 1) {
    endDate.value = sortedDates[sortedDates.length - 1];
  } else {
    endDate.value = null; 
  }
}, { deep: true });

// 현재 선택된 일수 계산 (숫자)
const calculatedDays = computed(() => {
  if (!startDate.value || !endDate.value) return 0;
  return daysBetweenInclusive(startDate.value, endDate.value);
});

// 기간 유효성 검사 (7일 이하인지)
const isDurationValid = computed(() => {
  if (calculatedDays.value === 0) return true; // 아직 선택 안함
  return calculatedDays.value <= MAX_DAYS;
});

// 화면 표시용 텍스트
const dateRangeText = computed(() => {
  if (!startDate.value) return "";
  if (!endDate.value) return formatDate(startDate.value); 
  return `${formatDate(startDate.value)} ~ ${formatDate(endDate.value)}`;
});

const tripDaysText = computed(() => {
  if (!startDate.value || !endDate.value) return "";
  return `${calculatedDays.value} days`;
});

// 제출 가능 여부 체크 (기간 제한 조건 추가)
const canSubmit = computed(() => {
  return (
    !!tripName.value && 
    !!startDate.value && 
    !!endDate.value && 
    isDurationValid.value // 7일 이하일 때만 true
  );
});

// 유틸리티 함수
function formatDate(date) {
  if (!date) return "";
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function daysBetweenInclusive(start, end) {
  const s = new Date(start);
  const e = new Date(end);
  s.setHours(0,0,0,0);
  e.setHours(0,0,0,0);
  const diffMs = e - s;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  return diffDays + 1;
}

function resetDates() {
  range.value = [];
  startDate.value = null;
  endDate.value = null;
}

// 제출 함수
async function submit() {
  if (!canSubmit.value || isSubmitting.value) return;

  // 한번 더 방어 코드 (7일 초과 시 중단)
  if (calculatedDays.value > MAX_DAYS) {
    alert(`Travel period cannot exceed ${MAX_DAYS} days.`);
    return;
  }

  // 랜덤 썸네일 선택
  const randomIndex = Math.floor(Math.random() * koreanPlaceholders.length);
  const randomThumbnail = koreanPlaceholders[randomIndex];

  const payload = {
    itinTitle: tripName.value,
    dayCnt: calculatedDays.value,
    startDate: formatDate(startDate.value), 
    endDate: formatDate(endDate.value),
    thumbnail: randomThumbnail,
  };

  isSubmitting.value = true;
  try {
    const res = await createItinerary(payload);
    // 목록 페이지로 이동
    router.push("/itineraries"); 
  } catch (error) {
    console.error("Failed to create:", error);
    alert("Failed to create itinerary.");
  } finally {
    isSubmitting.value = false;
  }
}
</script>