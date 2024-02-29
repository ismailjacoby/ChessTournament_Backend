package be.technobel.chesstournament.pl.models.dtos;

import be.technobel.chesstournament.dal.models.entities.UserEntity;

public record UserDto (String username, String email) {
    public static UserDto toDto(UserEntity user){
        return new UserDto(user.getUsername(), user.getEmail());
    }
}
