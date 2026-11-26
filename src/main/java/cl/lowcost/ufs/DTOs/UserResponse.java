package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.User;

import java.util.UUID;

public record UserResponse(UUID id, String nickname, boolean admin) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getNickname(), u.isAdmin());
    }
}
