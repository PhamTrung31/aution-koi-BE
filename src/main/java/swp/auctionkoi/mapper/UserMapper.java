package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import swp.auctionkoi.dto.request.user.StaffCreateUserRequest;
import swp.auctionkoi.dto.request.user.StaffUpdateUserRequest;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.models.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser (User user);
    User toUser(UserCreateRequest request);
    User toUser(StaffCreateUserRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    void updateUser(@MappingTarget User user, StaffUpdateUserRequest request);
    void updateStaff(@MappingTarget User user, UserUpdateRequest request);
}
