package com.appsdeveloperblog.ws.userservice.mapper;


import com.appsdeveloperblog.ws.userservice.dto.UserDto;
import com.appsdeveloperblog.ws.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User toEntity(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .email(userDto.getEmail())
                .address(userDto.getAddress())
                .alerting(userDto.isAlerting())
                .energyAlertingThreshold(userDto.getEnergyAlertingThreshold())
                .build();
    }

    public static UserDto toDTO(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .address(user.getAddress())
                .alerting(user.isAlerting())
                .energyAlertingThreshold(user.getEnergyAlertingThreshold())
                .build();
    }

    public static void updated(UserDto userDto, User user) {
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setAddress(userDto.getAddress());
        user.setEmail(userDto.getEmail());
        user.setAlerting(userDto.isAlerting());
        user.setEnergyAlertingThreshold(userDto.getEnergyAlertingThreshold());
    }

}
