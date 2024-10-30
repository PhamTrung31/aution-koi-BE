package swp.auctionkoi.service.auctionrequest.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.request.KoiFishDTO;
import swp.auctionkoi.dto.request.UserDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.AuctionRequestMapper;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.models.enums.KoiStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AuctionRequestServiceImpl implements AuctionRequestService {

    AuctionRequestRepository auctionRequestRepository;

    KoiFishRepository koiFishRepository;

    AuctionRepository auctionRepository;

    UserRepository userRepository;

    AuctionRequestMapper auctionRequestMapper;

    public AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDTO) {
        User user = userRepository.findById(auctionRequestDTO.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KoiFish fish = koiFishRepository.findById(auctionRequestDTO.getFishId()).orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));

        if (fish.getUser() != user) {
            throw new AppException(ErrorCode.NOT_BELONG_TO_BREEDER);
        }

        checkRequest(auctionRequestDTO, user, fish);

        AuctionRequest auctionRequest = saveRequest(auctionRequestDTO, user, fish);

        AuctionRequestResponse auctionRequestResponse = AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been sent successfully.")
                .data(auctionRequest)
                .build();

        return auctionRequestResponse;
    }

    public AuctionRequestResponse updateAuctionRequestForBreeder(Integer auctionRequestId, AuctionRequestDTO auctionRequestDTO) {

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (auctionRequest.getAuction() != null && !auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }

        log.info(auctionRequest.getBuyOut().toString());

        User user = userRepository.findById(auctionRequestDTO.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KoiFish fish;

        if (auctionRequestDTO.getFishId() != null) {
            fish = koiFishRepository.findById(auctionRequestDTO.getFishId()).orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));
            if (fish.getUser() != user) {
                throw new AppException(ErrorCode.NOT_BELONG_TO_BREEDER);
            }

        } else {
            fish = auctionRequest.getFish();
        }

        checkFieldUpdate(auctionRequest, auctionRequestDTO);

        checkRequest(auctionRequestDTO, user, fish);

        AuctionRequest result = updateRequest(auctionRequest, auctionRequestDTO, fish);

        AuctionRequestResponse auctionRequestResponse = AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been update successfully.")
                .data(result)
                .build();
        return auctionRequestResponse;
    }


    @Override
    public HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequest() {
        HashMap<Integer, AuctionRequestResponseData> auctionRequests = new HashMap<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findAll();
        for (AuctionRequest auctionRequest : auctionRequestList) {
            auctionRequests.put(auctionRequest.getId(), auctionRequestMapper.toAuctionRequestResponseData(auctionRequest));
        }
        return auctionRequests;
    }

    public AuctionRequestResponseData viewAuctionRequestDetail(int auctionRequestId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        AuctionRequestResponseData auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponseData(auctionRequest);
        return auctionRequestResponse;
    }

    @Override
    public HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequestsForBreeder(Integer userId) {
        HashMap<Integer, AuctionRequestResponseData> result = new HashMap<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findListAuctionRequestByUserId(userId);

        for (AuctionRequest auctionRequest : auctionRequestList) {
            result.put(auctionRequest.getId(), auctionRequestMapper.toAuctionRequestResponseData(auctionRequest));
        }

        return result;
    }

    public AuctionRequestResponse cancelAuctionRequest(int auctionRequestId, int breederID) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User breeder = userRepository.findById(breederID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) && auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            auctionRequest.setRequestStatus(AuctionRequestStatus.CANCEL);
            auctionRequest.getFish().setStatus(KoiStatus.CANCELED);

            auctionRequestRepository.save(auctionRequest);
        } else {
            throw new AppException(ErrorCode.CANT_CANCEL_REQUEST);
        }

        return AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been cancelled successfully.")
                .build();
    }

    @Override
    public List<AuctionRequest> getAuctionRequestsInManagerReview() {
        return auctionRequestRepository.findByRequestStatus(AuctionRequestStatus.MANAGER_REVIEW);
    }
    @Override
    public List<AuctionRequest> getAuctionRequestsInWait() {
        return auctionRequestRepository.findByRequestStatus(AuctionRequestStatus.WAIT);
    }

    @Override
    public AuctionRequestUpdateResponse approveAuctionRequestForStaff(int auctionRequestId, int staffId, boolean isSendToManager) {

//        System.out.println("Received auctionRequestId: " + auctionRequestId);
//        System.out.println("Received staffId: " + staffId);
//        System.out.println("isSendToManager: " + isSendToManager);

        // Kiểm tra staff tồn tại
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xác thực quyền của staff
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy thông tin yêu cầu đấu giá
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        // Kiểm tra trạng thái yêu cầu và trạng thái cá
        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) &&
                auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {

            // Nếu staff gửi yêu cầu lên cho manager để xem xét và không gán thông tin
            // staff đã gửi yêu cầu
            if (isSendToManager) {
                auctionRequest.setRequestStatus(AuctionRequestStatus.MANAGER_REVIEW);
                auctionRequest.getFish().setStatus(KoiStatus.PENDING_APPROVAL);
            } else {
                // Nếu staff duyệt thẳng yêu cầu
                auctionRequest.setRequestStatus(AuctionRequestStatus.APPROVE);
                auctionRequest.getFish().setStatus(KoiStatus.APPROVED);
                // Gán thông tin staff chịu trách nhiệm duyệt thẳng yêu cầu
                auctionRequest.setAssignedStaff(staff);
            }


            // Lưu thay đổi vào repository
            auctionRequestRepository.save(auctionRequest);
//            UserDTO userDTO = new UserDTO(auctionRequest.getUser());
//            KoiFishDTO koiFishDTO = new KoiFishDTO(auctionRequest.getFish());
            return new AuctionRequestUpdateResponse(
                    auctionRequest.getId(),
                    UserDTO.fromUser(auctionRequest.getUser()),  // Chuyển User sang UserDTO
                    auctionRequest.getAssignedStaff() != null ? auctionRequest.getAssignedStaff().getId() : null,
                    auctionRequest.getRequestStatus().name(),  // Chuyển enum sang String nếu cần
                    KoiFishDTO.fromKoiFish(auctionRequest.getFish()),  // Chuyển KoiFish sang KoiFishDTO
                    auctionRequest.getBuyOut(),
                    auctionRequest.getIncrementStep(),
                    auctionRequest.getStartPrice(),
                    auctionRequest.getMethodType(),
                    auctionRequest.getRequestCreatedDate(),
                    auctionRequest.getRequestUpdatedDate(),
                    auctionRequest.getStartTime(),
                    auctionRequest.getEndTime()
            );

        } else {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_AND_FISH_STATE);
        }
    }


//    public AuctionRequestResponse rejectAuctionRequestForStaff(int auctionRequestId, int staffId) {
//
//        User staff = userRepository.findById(staffId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        if (!staff.getRole().equals(Role.STAFF)) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//
//        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
//                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
//
//        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT)
//                && auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
//
//            auctionRequest.setRequestStatus(AuctionRequestStatus.CANCEL);
//            auctionRequest.getFish().setStatus(KoiStatus.REJECTED);
//
//            auctionRequestRepository.save(auctionRequest);
//        } else {
//            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_AND_FISH_STATE);
//        }
//
//        return AuctionRequestResponse
//                .builder()
//                .success(true)
//                .message("Request has been rejected successfully.")
//                .build();
//
//    }

    @Override
    public AuctionRequestUpdateResponse reviewAuctionRequestByManager(int auctionRequestId, int managerId, Integer staffId, boolean isApproved, boolean assignToStaff) {

        // Kiểm tra manager tồn tại
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xác thực quyền của manager
        if (!manager.getRole().equals(Role.MANAGER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy thông tin yêu cầu đấu giá
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        // Kiểm tra trạng thái yêu cầu phải ở MANAGER_REVIEW
        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.MANAGER_REVIEW)) {

            if (isApproved) {
                // Manager đồng ý duyệt
                auctionRequest.setRequestStatus(AuctionRequestStatus.APPROVE);
                auctionRequest.getFish().setStatus(KoiStatus.APPROVED);

                if (staffId != null) {
                    User staff = userRepository.findById(staffId)
                            .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
                    if (!staff.getRole().equals(Role.STAFF)) {
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                    }
                    auctionRequest.setAssignedStaff(staff);
                }
            } else if (assignToStaff) {

                if (staffId == null) {
                    throw new AppException(ErrorCode.STAFF_ID_REQUIRED);
                }
                User staff = userRepository.findById(staffId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                if (!staff.getRole().equals(Role.STAFF)) {
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }

                // Manager yêu cầu staff đã gửi đi xem cá trước
                auctionRequest.setRequestStatus(AuctionRequestStatus.ASSIGNED_TO_STAFF);
                auctionRequest.getFish().setStatus(KoiStatus.PENDING_APPROVAL); // Hoặc giữ nguyên
//                auctionRequest.setAssignedStaff(staff); // Gán lại staff đi xem cá


            } else {
                // Manager từ chối yêu cầu đấu giá
                auctionRequest.setRequestStatus(AuctionRequestStatus.CANCEL);
                auctionRequest.getFish().setStatus(KoiStatus.REJECTED);
            }

            // Lưu lại thông tin
            auctionRequestRepository.save(auctionRequest);

            // Trả về kết quả

//            UserDTO userDTO = new UserDTO(auctionRequest.getUser());
//            KoiFishDTO koiFishDTO = new KoiFishDTO(auctionRequest.getFish());

            return new AuctionRequestUpdateResponse(
                    auctionRequest.getId(),
                    UserDTO.fromUser(auctionRequest.getUser()),  // Chuyển User sang UserDTO
                    auctionRequest.getAssignedStaff() != null ? auctionRequest.getAssignedStaff().getId() : null,
                    auctionRequest.getRequestStatus().name(),  // Chuyển enum sang String nếu cần
                    KoiFishDTO.fromKoiFish(auctionRequest.getFish()),  // Chuyển KoiFish sang KoiFishDTO
                    auctionRequest.getBuyOut(),
                    auctionRequest.getIncrementStep(),
                    auctionRequest.getStartPrice(),
                    auctionRequest.getMethodType(),
                    auctionRequest.getRequestCreatedDate(),
                    auctionRequest.getRequestUpdatedDate(),
                    auctionRequest.getStartTime(),
                    auctionRequest.getEndTime()
            );

        } else {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }
    }


    @Override
    public AuctionRequestUpdateResponse reviewAuctionRequestByStaff(int auctionRequestId, int staffId, boolean isApproved) {

        // Kiểm tra staff tồn tại
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xác thực quyền của staff
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy thông tin yêu cầu đấu giá
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        // Kiểm tra trạng thái yêu cầu phải là ASSIGNED_TO_STAFF
        if (!auctionRequest.getRequestStatus().equals(AuctionRequestStatus.ASSIGNED_TO_STAFF)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }

//        // Kiểm tra staff có phải là người được assign không
//        if (!auctionRequest.getApprovedStaff().getId().equals(staffId)) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }

        // Nếu staff đồng ý duyệt yêu cầu sau khi xem cá
        if (isApproved) {
            auctionRequest.setRequestStatus(AuctionRequestStatus.APPROVE);
            auctionRequest.getFish().setStatus(KoiStatus.APPROVED);
            auctionRequest.setAssignedStaff(staff);

        } else {
            // Nếu staff từ chối yêu cầu sau khi xem cá
            auctionRequest.setRequestStatus(AuctionRequestStatus.CANCEL);
            auctionRequest.getFish().setStatus(KoiStatus.REJECTED);
        }

        // Lưu lại thông tin
        auctionRequestRepository.save(auctionRequest);

        // Trả về kết quả

//        UserDTO userDTO = new UserDTO(auctionRequest.getUser());
//        KoiFishDTO koiFishDTO = new KoiFishDTO(auctionRequest.getFish());

        return new AuctionRequestUpdateResponse(
                auctionRequest.getId(),
                UserDTO.fromUser(auctionRequest.getUser()),  // Chuyển User sang UserDTO
                auctionRequest.getAssignedStaff() != null ? auctionRequest.getAssignedStaff().getId() : null,
                auctionRequest.getRequestStatus().name(),  // Chuyển enum sang String nếu cần
                KoiFishDTO.fromKoiFish(auctionRequest.getFish()),  // Chuyển KoiFish sang KoiFishDTO
                auctionRequest.getBuyOut(),
                auctionRequest.getIncrementStep(),
                auctionRequest.getStartPrice(),
                auctionRequest.getMethodType(),
                auctionRequest.getRequestCreatedDate(),
                auctionRequest.getRequestUpdatedDate(),
                auctionRequest.getStartTime(),
                auctionRequest.getEndTime()
        );
    }


    private void checkRequest(AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {

        if (!user.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!fish.getStatus().equals(KoiStatus.NEW) && !fish.getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.FISH_NOT_AVAILABLE);
        }

        if (auctionRequestDTO.getStartPrice() <= 0) {
            throw new AppException(ErrorCode.INVALID_START_PRICE);
        }

        Instant start_time = auctionRequestDTO.getStart_time();
        Instant end_time = auctionRequestDTO.getEnd_time();
        Instant now = Instant.now();

        if (start_time.isAfter(end_time)) {
            throw new AppException(ErrorCode.INVALID_START_TIME);
        }

//        Duration duration = Duration.between(start_time, now);
//
//        if(duration.toDays() < 1){
//            throw new AppException(ErrorCode.START_TIME_TOO_CLOSED);
//        }
    }

    private AuctionRequest saveRequest(AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {
        fish.setStatus(KoiStatus.PENDING_APPROVAL);
        AuctionRequest auctionRequest = AuctionRequest.builder()
                .user(user)
                .fish(fish)
                .startPrice(auctionRequestDTO.getStartPrice())
                .buyOut(auctionRequestDTO.getBuyOut())
                .methodType(auctionRequestDTO.getMethodType())
                .startTime(auctionRequestDTO.getStart_time())
                .endTime(auctionRequestDTO.getEnd_time())
                .requestStatus(AuctionRequestStatus.WAIT)
                .build();

        auctionRequestRepository.save(auctionRequest);

        return auctionRequest;
    }

    private AuctionRequest updateRequest(AuctionRequest auctionRequest, AuctionRequestDTO auctionRequestDTO, KoiFish fish) {
        try {
            fish.setStatus(KoiStatus.PENDING_APPROVAL);
            auctionRequest.setFish(fish);
            auctionRequest.setStartPrice(auctionRequestDTO.getStartPrice());
            auctionRequest.setBuyOut(auctionRequestDTO.getBuyOut());
            auctionRequest.setMethodType(auctionRequestDTO.getMethodType());
            auctionRequest.setStartTime(auctionRequestDTO.getStart_time());
            auctionRequest.setEndTime(auctionRequestDTO.getEnd_time());

            auctionRequestRepository.save(auctionRequest);

            return auctionRequest;
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_UPDATE); //temp error to check, delete after fix it
        }
    }

    //get field from original object
    private void checkFieldUpdate(AuctionRequest auctionRequest, AuctionRequestDTO auctionRequestDTO) {
        if (auctionRequestDTO.getBuyOut() == null) {
            auctionRequestDTO.setBuyOut(auctionRequest.getBuyOut());
        }
        if (auctionRequestDTO.getStartPrice() == null) {
            auctionRequestDTO.setStartPrice(auctionRequest.getStartPrice());
        }
        if (auctionRequestDTO.getStart_time() == null) {
            auctionRequestDTO.setStart_time(auctionRequest.getStartTime());
        }
        if (auctionRequestDTO.getEnd_time() == null) {
            auctionRequestDTO.setEnd_time(auctionRequest.getEndTime());
        }
        if (auctionRequestDTO.getMethodType() == null) {
            auctionRequestDTO.setMethodType(auctionRequest.getMethodType());
        }
    }
}
