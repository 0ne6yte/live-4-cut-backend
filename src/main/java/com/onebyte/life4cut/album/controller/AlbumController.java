package com.onebyte.life4cut.album.controller;

import com.onebyte.life4cut.album.controller.dto.CreateAlbumRequest;
import com.onebyte.life4cut.album.controller.dto.CreateAlbumResponse;
import com.onebyte.life4cut.album.controller.dto.CreatePictureRequest;
import com.onebyte.life4cut.album.controller.dto.CreatePictureResponse;
import com.onebyte.life4cut.album.controller.dto.GetMyRoleInAlbumResponse;
import com.onebyte.life4cut.album.controller.dto.GetPicturesInSlotResponse;
import com.onebyte.life4cut.album.controller.dto.SearchTagsRequest;
import com.onebyte.life4cut.album.controller.dto.SearchTagsResponse;
import com.onebyte.life4cut.album.controller.dto.UpdateAlbumRequest;
import com.onebyte.life4cut.album.controller.dto.UpdatePictureRequest;
import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.service.AlbumService;
import com.onebyte.life4cut.auth.dto.CustomUserDetails;
import com.onebyte.life4cut.common.web.ApiResponse;
import com.onebyte.life4cut.common.web.EmptyResponse;
import com.onebyte.life4cut.picture.service.PictureService;
import com.onebyte.life4cut.picture.service.dto.PictureDetailInSlot;
import com.onebyte.life4cut.pictureTag.domain.PictureTag;
import com.onebyte.life4cut.pictureTag.service.PictureTagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

  private final PictureService pictureService;
  private final PictureTagService pictureTagService;
  private final AlbumService albumService;

  @PostMapping("")
  public ApiResponse<CreateAlbumResponse> createAlbum(
      @Valid CreateAlbumRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long albumId =
        albumService.createAlbum(
            request.name(),
            userDetails.getUserId(),
            request.memberUserIds(),
            request.guestUserIds());
    return ApiResponse.OK(new CreateAlbumResponse(albumId));
  }

  @DeleteMapping("/{albumId}")
  public ApiResponse deleteAlbum(
      @Min(1) @PathVariable("albumId") Long albumId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    albumService.deleteAlbum(albumId, userDetails.getUserId());
    return ApiResponse.OK();
  }

  @PatchMapping("/{albumId}")
  public ApiResponse<EmptyResponse> updateAlbum(
      @Min(1) @PathVariable("albumId") Long albumId,
      @Valid UpdateAlbumRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    albumService.updateAlbum(
        albumId,
        request.name(),
        userDetails.getUserId(),
        request.memberUserIds(),
        request.guestUserIds());
    return ApiResponse.OK();
  }

  @PostMapping("/{albumId}/pictures")
  public ApiResponse<CreatePictureResponse> uploadPicture(
      @Min(1) @PathVariable("albumId") Long albumId,
      @Valid @RequestPart("data") CreatePictureRequest request,
      @RequestPart("image") MultipartFile image,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long pictureId =
        pictureService.createInSlot(
            userDetails.getUserId(),
            albumId,
            request.slotId(),
            request.content(),
            request.picturedAt().atTime(0, 0),
            request.tags(),
            image);

    return ApiResponse.OK(new CreatePictureResponse(pictureId));
  }

  @GetMapping("/{albumId}/tags")
  public ApiResponse<SearchTagsResponse> searchTags(
      @Min(1) @PathVariable("albumId") Long albumId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid SearchTagsRequest request) {
    List<PictureTag> pictureTags =
        pictureTagService.searchTags(albumId, userDetails.getUserId(), request.keyword());

    return ApiResponse.OK(SearchTagsResponse.of(pictureTags));
  }

  @PatchMapping("/{albumId}/pictures/{pictureId}")
  public ApiResponse<EmptyResponse> updatePicture(
      @Min(1) @PathVariable("albumId") Long albumId,
      @Min(1) @PathVariable("pictureId") Long pictureId,
      @Valid @RequestPart(value = "data", required = false) UpdatePictureRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    pictureService.updatePicture(
        userDetails.getUserId(),
        albumId,
        pictureId,
        request.getContent(),
        request.tags(),
        request.getPicturedAt(),
        image);

    return ApiResponse.OK();
  }

  @GetMapping("/{albumId}/pictures")
  public ApiResponse<GetPicturesInSlotResponse> getPicturesInSlot(
      @Min(1) @PathVariable("albumId") Long albumId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<PictureDetailInSlot> pictureDetailInSlots =
        pictureService.getPicturesInSlotByAlbum(userDetails.getUserId(), albumId);

    return ApiResponse.OK(GetPicturesInSlotResponse.of(pictureDetailInSlots));
  }

  @GetMapping("/{albumId}/roles/me")
  public ApiResponse<GetMyRoleInAlbumResponse> getMyRoleInAlbum(
      @Min(1) @PathVariable("albumId") Long albumId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UserAlbumRole userAlbumRole = albumService.getRoleInAlbum(albumId, userDetails.getUserId());

    return ApiResponse.OK(new GetMyRoleInAlbumResponse(userAlbumRole));
  }
}
