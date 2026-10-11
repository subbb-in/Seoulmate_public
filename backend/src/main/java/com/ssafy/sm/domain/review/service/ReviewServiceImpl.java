package com.ssafy.sm.domain.review.service;

import com.ssafy.sm.common.util.FileStore;
import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.place.repository.PlaceRepository;
import com.ssafy.sm.domain.review.dto.ReviewRequest;
import com.ssafy.sm.domain.review.dto.ReviewResponse;
import com.ssafy.sm.domain.review.entity.Review;
import com.ssafy.sm.domain.review.entity.ReviewImage;
import com.ssafy.sm.domain.review.repository.ReviewRepository;
import com.ssafy.sm.domain.user.entity.User;
import com.ssafy.sm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final FileStore fileStore;

    @Override
    @Transactional
    public void createReview(ReviewRequest.Create createDto, List<MultipartFile> files) throws IOException {
        User user = userRepository.findById(createDto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Place place = placeRepository.findById(createDto.getPlaceNo())
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        Review review = Review.builder()
                .user(user)
                .place(place)
                .rating(createDto.getRating())
                .content(createDto.getContent())
                .hidden(createDto.isHidden())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        // 파일 저장
        if (files != null && !files.isEmpty()) {
            List<FileStore.UploadFile> uploadFiles = fileStore.storeFiles(files);
            for (FileStore.UploadFile uploadFile : uploadFiles) {
                ReviewImage image = ReviewImage.builder()
                        .originName(uploadFile.getOriginName())
                        .reviewImgName(uploadFile.getStoredName())
                        .createdAt(LocalDateTime.now())
                        .build();
                review.addImage(image);
            }
        }

        reviewRepository.save(review);

        // 장소 평점 갱신
        updatePlaceRating(place.getPlaceNo());
    }

    @Override
    public List<ReviewResponse> getReviewsByPlace(Long placeNo) {
        return reviewRepository.findAllByPlace_PlaceNoAndIsDeletedFalseOrderByCreatedAtDesc(placeNo).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReview(Long reviewNo) {
        return reviewRepository.findByReviewNoAndIsDeletedFalse(reviewNo)
                .map(this::convertToResponse)
                .orElse(null);
    }

    @Override
    public List<ReviewResponse> getMyReviews(Long userNo) {
        return reviewRepository.findAllByUser_UserNoAndIsDeletedFalseOrderByCreatedAtDesc(userNo).stream()
                .map(this::convertToResponseWithPlaceTitle)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateReview(ReviewRequest.Update updateDto) {
        Review review = reviewRepository.findByReviewNoAndIsDeletedFalse(updateDto.getReviewNo())
                .orElseThrow(() -> new RuntimeException("리뷰 수정 실패"));

        review.update(updateDto.getRating(), updateDto.getContent(), updateDto.isHidden());

        // 장소 평점 갱신
        updatePlaceRating(review.getPlace().getPlaceNo());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewNo) {
        Review review = reviewRepository.findByReviewNoAndIsDeletedFalse(reviewNo)
                .orElseThrow(() -> new RuntimeException("리뷰 삭제 실패"));

        Long placeNo = review.getPlace().getPlaceNo();

        // 파일 물리 삭제
        review.getImages().forEach(img -> fileStore.deleteFile(img.getReviewImgName()));

        // Soft Delete
        review.delete();

        // 장소 평점 갱신
        updatePlaceRating(placeNo);
    }

    // 장소 평점 재계산
    private void updatePlaceRating(Long placeNo) {
        Float avgRating = reviewRepository.calculateAverageRatingByPlaceNo(placeNo);
        Place place = placeRepository.findById(placeNo).orElse(null);
        if (place != null) {
            place.updateRating(avgRating);
        }
    }

    // Entity -> DTO 변환
    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setReviewNo(review.getReviewNo());
        res.setPlaceNo(review.getPlace().getPlaceNo());
        res.setUserNo(review.getUser().getUserNo());
        res.setUserNickname(review.getUser().getNickname());
        res.setRating(review.getRating());
        res.setContent(review.getContent());
        res.setHidden(review.isHidden());
        res.setCreatedAt(review.getCreatedAt());

        List<com.ssafy.sm.domain.review.dto.ReviewImage> imageDtos = review.getImages().stream()
                .map(img -> {
                    com.ssafy.sm.domain.review.dto.ReviewImage dto = new com.ssafy.sm.domain.review.dto.ReviewImage();
                    dto.setReviewImgNo(img.getReviewImgNo());
                    dto.setReviewNo(review.getReviewNo());
                    dto.setReviewImgName(img.getReviewImgName());
                    dto.setOriginName(img.getOriginName());
                    dto.setCreateAt(img.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        res.setImages(imageDtos);

        return res;
    }

    // Entity -> DTO 변환 (placeTitle 포함, Mypage용)
    private ReviewResponse convertToResponseWithPlaceTitle(Review review) {
        ReviewResponse res = convertToResponse(review);
        res.setPlaceTitle(review.getPlace().getTitle());
        return res;
    }
}
