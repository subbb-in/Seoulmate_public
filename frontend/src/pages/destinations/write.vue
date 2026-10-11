<template>
  <v-container class="py-10" max-width="900">
    <v-card class="pa-6 rounded-xl elevation-3">
      <h1 class="text-h4 font-weight-bold mb-2">여행지 공유하기</h1>
      <p class="text-body-2 text-grey mb-8">나만의 특별한 여행 장소를 소개해 주세요.</p>

      <v-form @submit.prevent="submitPost">
        
        <v-text-field
          v-model="title"
          label="제목"
          placeholder="예: 가을 단풍이 아름다운 경복궁 산책"
          variant="outlined"
          class="mb-6"
          :rules="[v => !!v || '제목을 입력해주세요']"
        ></v-text-field>

        <div class="mb-6">
          <label class="text-subtitle-1 font-weight-bold mb-2 d-block">대표 이미지</label>
          <v-file-input
            v-model="thumbnailFile"
            accept="image/*"
            label="목록과 상단에 노출될 이미지를 선택하세요"
            prepend-icon="mdi-camera"
            variant="outlined"
            @update:model-value="onThumbnailChange"
            show-size
            hint="첫 번째 이미지로 저장됩니다."
            persistent-hint
          ></v-file-input>

          <v-card 
            v-if="thumbnailPreview" 
            class="mt-3 rounded-lg overflow-hidden" 
            elevation="2"
            height="300"
          >
            <v-img :src="thumbnailPreview" cover height="100%"></v-img>
          </v-card>
        </div>

        <v-divider class="my-6"></v-divider>

        <div class="mb-6">
          <label class="text-subtitle-1 font-weight-bold mb-2 d-block">소개글</label>
          <v-textarea
            v-model="content"
            label="여행지에 대한 설명을 자유롭게 작성해주세요"
            placeholder="어떤 점이 좋았나요? 가는 길 팁이 있나요?"
            variant="outlined"
            rows="10"
            no-resize
            :rules="[v => !!v || '내용을 입력해주세요']"
          ></v-textarea>
        </div>

        <div class="mb-8">
          <label class="text-subtitle-1 font-weight-bold mb-2 d-block">추가 사진 (선택)</label>
          <p class="text-caption text-grey mb-2">상세 사진들을 선택해 주세요. (대표 이미지 뒤에 순서대로 저장됩니다)</p>
          
          <v-file-input
            v-model="galleryFiles"
            accept="image/*"
            label="사진 추가하기 (여러 장 선택 가능)"
            prepend-icon="mdi-image-multiple"
            variant="outlined"
            multiple
            chips
            @update:model-value="onGalleryChange"
            show-size
          ></v-file-input>

          <v-row v-if="galleryPreviews.length > 0" class="mt-2">
            <v-col 
              v-for="(src, index) in galleryPreviews" 
              :key="index" 
              cols="6" md="4" lg="3"
            >
              <v-card class="rounded-lg" elevation="1" height="150">
                <v-img :src="src" cover height="100%"></v-img>
              </v-card>
            </v-col>
          </v-row>
        </div>

        <div class="d-flex justify-end gap-3 mt-10">
          <v-btn 
            size="large" 
            variant="outlined" 
            color="grey-darken-1" 
            rounded="lg"
            to="/destinations"
          >
            취소
          </v-btn>
          <v-btn 
            size="large" 
            color="primary" 
            rounded="lg" 
            type="submit" 
            :loading="isSubmitting"
            elevation="2"
          >
            등록하기
          </v-btn>
        </div>

      </v-form>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createBoard } from '@/api/board.api';

const router = useRouter();

// 상태 변수
const isSubmitting = ref(false);
const title = ref('');
const content = ref('');

// 파일 관련 상태 
const thumbnailFile = ref(null);      
const thumbnailPreview = ref('');     

const galleryFiles = ref([]);         
const galleryPreviews = ref([]);      


// --- 파일 핸들러 (미리보기) ---

const onThumbnailChange = (file) => {
  // Vuetify 3.x v-file-input 모델값은 배열일 수 있음
  const selectedFile = Array.isArray(file) ? file[0] : file;
  if (selectedFile) {
    thumbnailPreview.value = URL.createObjectURL(selectedFile);
  } else {
    thumbnailPreview.value = '';
  }
};

const onGalleryChange = (files) => {
  galleryPreviews.value = [];
  if (files && files.length > 0) {
    files.forEach(file => {
      galleryPreviews.value.push(URL.createObjectURL(file));
    });
  }
};


// --- 제출 로직 (Backend 스펙 준수) ---
const submitPost = async () => {
  if (!title.value || !content.value) {
    alert("제목과 내용은 필수입니다.");
    return;
  }

  isSubmitting.value = true;
  try {
    const formData = new FormData();

    // 1. Board DTO (JSON) - @RequestPart("board")
    // Backend의 BoardRequest.Create 클래스 필드명과 일치해야 함
    const boardDto = {
      boardTitle: title.value,   // backend: boardTitle
      boardContent: content.value // backend: boardContent
      // userNo, boardWriter는 백엔드 session에서 처리하므로 안 보냄
    };
    
    // JSON 객체를 Blob으로 변환하여 추가
    formData.append('board', new Blob([JSON.stringify(boardDto)], { type: 'application/json' }));


    // 2. 파일 리스트 통합 - @RequestPart("files")
    // Controller가 List<MultipartFile> files 하나만 받으므로
    // 썸네일과 갤러리 이미지를 모두 'files'라는 키로 append 해야 함.
    
    // (1) 썸네일 먼저 추가 (순서 보장을 위해)
    const mainFile = Array.isArray(thumbnailFile.value) ? thumbnailFile.value[0] : thumbnailFile.value;
    if (mainFile) {
      formData.append('files', mainFile);
    }

    // (2) 갤러리 이미지들 추가
    if (galleryFiles.value && galleryFiles.value.length > 0) {
      galleryFiles.value.forEach((file) => {
        formData.append('files', file); 
      });
    }

    // API 호출 (Header에 multipart/form-data 설정은 보통 axios가 자동 처리하거나 api 함수 내부에서 처리)
    await createBoard(formData);

    alert("성공적으로 등록되었습니다!");
    router.push('/destinations'); // 목록 페이지로 이동

  } catch (error) {
    console.error("Upload failed:", error);
    // Backend에서 보낸 에러 메시지가 있다면 표시
    const errorMsg = error.response?.data?.message || "게시글 등록에 실패했습니다.";
    alert(errorMsg);
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.gap-3 { gap: 12px; }
</style>