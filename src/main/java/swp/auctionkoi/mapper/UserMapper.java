package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
import swp.auctionkoi.models.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
