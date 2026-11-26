package cl.lowcost.ufs.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NicknameRequest(@NotBlank @Size(max = 30) String nickname) {
}
