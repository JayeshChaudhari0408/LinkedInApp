package com.linkedin.user_service.service.impl;

import com.linkedin.user_service.dto.LoginRequestDto;
import com.linkedin.user_service.dto.SignupRequestDto;
import com.linkedin.user_service.dto.UserDto;
import com.linkedin.user_service.entity.User;
import com.linkedin.user_service.exception.BadRequestException;
import com.linkedin.user_service.exception.ResourceNotFoundException;
import com.linkedin.user_service.repository.UserRepository;
import com.linkedin.user_service.service.AuthService;
import com.linkedin.user_service.service.JWTService;
import com.linkedin.user_service.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;

    @Override
    public UserDto signup(SignupRequestDto signupRequestDto) {
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists) {
            throw new BadRequestException("User Already Exists by Same Email");
        }

        User user = modelMapper.map(signupRequestDto,User.class);
        user.setPassword(PasswordUtils.hashPassword(signupRequestDto.getPassword()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with email: "+loginRequestDto.getEmail()));
        boolean isPasswordMatch = PasswordUtils.checkPassword(loginRequestDto.getPassword(),user.getPassword());

        if(!isPasswordMatch) {
            throw new BadRequestException("Incorrect Password");
        }

        return jwtService.generateAccessToken(user);
    }
}
