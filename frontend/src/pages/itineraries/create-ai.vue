<template>
  <v-container class="py-10" style="max-width: 900px;">
    
    <v-scale-transition>
      <v-card v-if="!generatedResult && !loading" rounded="xl" class="pa-8 elevation-3" key="input-form">
        <div class="text-center mb-8">
          <v-icon icon="mdi-robot-excited-outline" size="64" color="primary" class="mb-4" />
          <h2 class="text-h4 font-weight-bold mb-2">AI Travel Planner</h2>
          <p class="text-grey-darken-1">Tell us your preferences, and AI will plan your Seoul trip!</p>
        </div>

        <v-form @submit.prevent="submitAiRequest">
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="form.destination"
                label="Destination"
                prepend-inner-icon="mdi-map-marker"
                variant="outlined"
                readonly
                hint="Currently, we only support Seoul."
                persistent-hint
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="form.duration"
                :items="durationOptions"
                item-title="title"
                item-value="value"
                label="Duration"
                prepend-inner-icon="mdi-calendar-clock"
                variant="outlined"
                placeholder="How long will you stay?"
                :rules="[v => !!v || 'Please select a duration.']"
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="form.companions"
                :items="companionOptions"
                item-title="title"
                item-value="value"
                label="Companions"
                prepend-inner-icon="mdi-account-group"
                variant="outlined"
                placeholder="Who are you traveling with?"
                :rules="[v => !!v || 'Please select who you are with.']"
              />
            </v-col>

            <v-col cols="12">
              <p class="text-subtitle-1 font-weight-bold mb-2">Trip Theme</p>
              <v-chip-group
                v-model="form.theme"
                selected-class="text-primary border-primary"
                column
              >
                <v-chip
                  v-for="item in themeOptions"
                  :key="item.value"
                  :value="item.value"
                  filter
                  variant="outlined"
                  size="large"
                  class="ma-1"
                >
                  {{ item.title }}
                </v-chip>
              </v-chip-group>
              <div v-if="!form.theme" class="text-caption text-error mt-1 ml-1">
                * Please select at least one theme.
              </div>
            </v-col>
          </v-row>

          <v-btn
            type="submit"
            block
            size="x-large"
            color="primary"
            rounded="xl"
            class="mt-8 font-weight-bold"
            :disabled="!isValid"
          >
            <v-icon start icon="mdi-creation" />
            Generate AI Itinerary
          </v-btn>
        </v-form>
      </v-card>
    </v-scale-transition>

    <v-fade-transition>
      <v-sheet
        v-if="loading"
        class="d-flex flex-column align-center justify-center py-16 text-center"
        color="transparent"
      >
        <v-progress-circular
          indeterminate
          color="primary"
          size="80"
          width="8"
          class="mb-6"
        />
        <h3 class="text-h5 font-weight-bold text-primary mb-2">
          AI is planning your trip...
        </h3>
        <p class="text-grey-darken-1">
          This may take a moment. (approx. 10-20 sec)
        </p>
      </v-sheet>
    </v-fade-transition>

    <v-scale-transition>
      <div v-if="generatedResult && !loading">
        <div class="d-flex align-center justify-space-between mb-6">
          <div>
            <div class="text-overline text-primary mb-1">AI Recommendation</div>
            <h2 class="text-h4 font-weight-bold">{{ generatedResult.title }}</h2>
          </div>
          <v-btn variant="text" color="grey" prepend-icon="mdi-refresh" @click="resetForm">
            Reset
          </v-btn>
        </div>

        <v-tabs v-model="activeTab" color="primary" align-tabs="start" class="mb-6 border-b">
          <v-tab
            v-for="dayPlan in generatedResult.itinerary"
            :key="dayPlan.day"
            :value="dayPlan.day"
          >
            Day {{ dayPlan.day }}
          </v-tab>
        </v-tabs>

        <v-window v-model="activeTab">
          <v-window-item
            v-for="dayPlan in generatedResult.itinerary"
            :key="dayPlan.day"
            :value="dayPlan.day"
          >
            <v-timeline side="end" align="start" truncate-line="both">
              <v-timeline-item
                v-for="(place, index) in dayPlan.places"
                :key="index"
                dot-color="primary"
                size="small"
              >
                <template #icon>
                  <span class="text-caption font-weight-bold text-white">{{ index + 1 }}</span>
                </template>
                
                <v-card class="elevation-2 rounded-lg mb-4">
                  <div class="d-flex flex-no-wrap justify-space-between">
                    <div>
                      <v-card-title class="text-h6 font-weight-bold">
                        {{ place.placeDetail?.title || 'Unknown Place' }}
                      </v-card-title>
                      <v-card-subtitle>
                        {{ place.placeDetail?.addr1 || 'No address info' }}
                      </v-card-subtitle>
                      
                      <v-card-text class="pt-2 pb-0" v-if="place.moveMinutes > 0">
                         <v-chip size="small" color="secondary" variant="flat" label class="mr-2">
                           <v-icon start icon="mdi-bus-clock" />
                           + {{ place.moveMinutes }} min ({{ place.moveType }})
                         </v-chip>
                      </v-card-text>
                    </div>

                    <v-avatar class="ma-3" size="100" rounded="lg">
                      <v-img 
                        :src="place.placeDetail?.firstImage || 'https://cdn.vuetifyjs.com/images/cards/cooking.png'" 
                        cover 
                      >
                         <template #placeholder>
                            <div class="d-flex align-center justify-center fill-height bg-grey-lighten-3">
                              <v-icon color="grey">mdi-image</v-icon>
                            </div>
                         </template>
                      </v-img>
                    </v-avatar>
                  </div>
                </v-card>
              </v-timeline-item>
            </v-timeline>
          </v-window-item>
        </v-window>

        <v-btn
          block
          size="x-large"
          color="success"
          rounded="xl"
          class="mt-8 shadow-lg"
          @click="handleSaveItinerary"
        >
          <v-icon start icon="mdi-content-save-check" />
          Save Itinerary
        </v-btn>
      </div>
    </v-scale-transition>
    
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.text }}
      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">Close</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { generateAiItinerary } from "@/api/ai.api.js"; 
import { saveAiItinerary } from "@/api/itinerary.api.js";


const router = useRouter();

// --- State ---
const loading = ref(false);
const activeTab = ref(1);
const generatedResult = ref(null);

// Form Data
const form = ref({
  destination: "Seoul",
  duration: null,
  theme: null,
  companions: null,
});

const snackbar = ref({ show: false, text: "", color: "error" });

// --- Options (English Display, English Value) ---
const durationOptions = [
  { title: "1 Day (Day Trip)", value: "1 Day" },
  { title: "2 Days (1 Night)", value: "2 Days" },
  { title: "3 Days (2 Nights)", value: "3 Days" },
  { title: "4 Days (3 Nights)", value: "4 Days" }
];

const companionOptions = [
  { title: "Solo", value: "Solo Traveler" },
  { title: "With Friends", value: "Friends" },
  { title: "With Partner/Couple", value: "Couple" },
  { title: "With Family", value: "Family with kids" }
];

const themeOptions = [
  { title: "Healing & Relax", value: "Healing" },
  { title: "Foodie Tour", value: "Food Tour" },
  { title: "History & Culture", value: "History" },
  { title: "Shopping", value: "Shopping" },
  { title: "Activity", value: "Activity" },
  { title: "K-POP", value: "K-POP" }
];

// --- Validation ---
const isValid = computed(() => {
  return form.value.duration && form.value.theme && form.value.companions;
});

// --- AI Request ---
const submitAiRequest = async () => {
  if (!isValid.value) return;

  loading.value = true;
  generatedResult.value = null;

  try {
    const payload = {
      destination: form.value.destination,
      days: form.value.duration,
      theme: form.value.theme,
      companions: form.value.companions,
    };

    console.log("Requesting AI:", payload);
    const res = await generateAiItinerary(payload);

    if (res && res.data) {
      generatedResult.value = res.data;
      activeTab.value = res.data.itinerary?.[0]?.day || 1;
    } else {
      throw new Error("Invalid response format");
    }

  } catch (error) {
    console.error(error);
    snackbar.value = {
      show: true,
      text: "Failed to generate itinerary. Please try again.",
      color: "error"
    };
  } finally {
    loading.value = false;
  }
};

// --- Reset ---
const resetForm = () => {
  generatedResult.value = null;
  form.value = {
    destination: "Seoul",
    duration: null,
    theme: null,
    companions: null,
  };
};

// --- Save Itinerary (Real Logic) ---
const isSaving = ref(false); // 저장 로딩 상태 추가

const handleSaveItinerary = async () => {
  if (!generatedResult.value) return;

  // 1. 백엔드 DTO(ItineraryRequest.SaveAiItinerary)에 맞게 데이터 변환
  const payload = {
    title: generatedResult.value.title,
    dayCnt: generatedResult.value.itinerary.length, // 총 일수 계산
    itinerary: generatedResult.value.itinerary.map((dayPlan) => ({
      day: dayPlan.day,
      places: dayPlan.places.map((place) => ({
        placeNo: place.placeDetail.placeNo, // 핵심: 장소 ID 추출
        moveType: place.moveType,
        moveMinutes: place.moveMinutes
      }))
    }))
  };

  isSaving.value = true;
  try {
    console.log("Saving payload:", payload);
    
    // 2. API 호출
    const res = await saveAiItinerary(payload);
    
    // 3. 성공 시 처리
    // 백엔드에서 생성된 일정 번호(itinNo)를 data로 보내줌
    const newItinNo = res.data?.data; 
    
    snackbar.value = {
      show: true,
      text: "Itinerary saved successfully!",
      color: "success"
    };

    // 잠시 후 목록 페이지(또는 상세 페이지)로 이동
    setTimeout(() => {
      // 상세 페이지로 가려면: router.push(`/itineraries/${newItinNo}`);
      router.push("/itineraries");
    }, 1000);

  } catch (error) {
    console.error("Save failed:", error);
    snackbar.value = {
      show: true,
      text: "Failed to save itinerary. Please try again.",
      color: "error"
    };
  } finally {
    isSaving.value = false;
  }
};

// --- Save (Demo) ---
const saveItinerary = () => {
  alert("Itinerary Saved!");
  router.push("/itineraries/index");
};
</script>

<style scoped>
.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>