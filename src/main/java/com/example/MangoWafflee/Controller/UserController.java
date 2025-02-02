package com.example.MangoWafflee.Controller;

import com.example.MangoWafflee.DTO.JWTDTO;
import com.example.MangoWafflee.DTO.OAuth2CodeDTO;
import com.example.MangoWafflee.DTO.UserDTO;
import com.example.MangoWafflee.Entity.UserEntity;
import com.example.MangoWafflee.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원 가입
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<JWTDTO> login(@RequestBody UserDTO userDTO) {
        JWTDTO login = userService.login(userDTO.getUid(), userDTO.getPassword());
        return ResponseEntity.ok(login);
    }

    //id로 유저 조회
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.getUserById(id, userDetails);
        return ResponseEntity.ok(user);
    }

    //uid로 유저 조회
    @GetMapping("/uid/{uid}")
    public ResponseEntity<UserDTO> getUserByUid(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.getUserByUid(uid, userDetails);
        return ResponseEntity.ok(user);
    }

    //닉네임으로 유저 조회
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserDTO> getUserByNickname(@PathVariable String nickname, @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.getUserByNickname(nickname, userDetails);
        return ResponseEntity.ok(user);
    }

    //아이디 중복 확인
    @PostMapping("/check-uid")
    public ResponseEntity<Map<String, String>> checkUidDuplicate(@RequestBody Map<String, String> request) {
        String uid = request.get("uid");
        Map<String, String> message = userService.isUidDuplicate(uid);
        return ResponseEntity.ok(message);
    }

    //닉네임 중복 확인
    @PostMapping("/check-nickname")
    public ResponseEntity<Map<String, String>> checkNicknameDuplicate(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        Map<String, String> message = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(message);
    }

    //회원 정보 수정
    @SneakyThrows
    @PutMapping(value = "/{uid}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDTO> updateUser(@PathVariable String uid, @RequestPart(value = "userData", required = false) String userData, @RequestPart(value = "image", required = false) MultipartFile image, @AuthenticationPrincipal UserDetails userDetails) {
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = userData != null ? mapper.readValue(userData, UserDTO.class) : new UserDTO();
        userDTO.setUid(uid);
        UserDTO updatedUser = userService.updateUser(userDTO, image, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    //회원 탈퇴
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(uid, userDetails);
        return ResponseEntity.noContent().build();
    }

    //토큰 유효 시간 확인
    @GetMapping("/token-remaining-time")
    public ResponseEntity<Long> getTokenRemainingTime(@AuthenticationPrincipal UserDetails userDetails) {
        Long remainingTime = userService.getTokenRemainingTime(userDetails);
        return ResponseEntity.ok(remainingTime);
    }

    //토큰 연장(오류나는 중)
    @PostMapping("/extend-token")
    public ResponseEntity<Long> refreshToken(@AuthenticationPrincipal UserDetails userDetails) {
        Long remainingTime = userService.refreshToken(userDetails);
        return ResponseEntity.ok(remainingTime);
    }

    //유저 토큰 정보 조회
    @GetMapping("/token/{uid}")
    public ResponseEntity<JWTDTO> getUserWithTokenInfo(@PathVariable String uid, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        JWTDTO userWithTokenInfo = userService.getUserWithTokenInfo(uid, token);
        return ResponseEntity.ok(userWithTokenInfo);
    }

    //카카오 로그인 성공 시 호출되는 엔드포인트 (GET)
    @CrossOrigin(origins = "http://127.0.0.1:3000")
    @GetMapping("/oauth2/code/kakao")
    public ResponseEntity<JWTDTO> kakaoCallback(@RequestParam String code) {
        JWTDTO jwtDto = userService.loginWithOAuth2(code);
        return ResponseEntity.ok(jwtDto);
    }

    //카카오 로그인 성공 시 호출되는 엔드포인트 (POST)
    @CrossOrigin(origins = "http://127.0.0.1:3000")
    @PostMapping("/oauth2/code/kakao")
    public ResponseEntity<JWTDTO> kakaoLoginPost(@RequestBody OAuth2CodeDTO codeDTO) {
        JWTDTO jwtDto = userService.loginWithOAuth2(codeDTO.getCode());
        return ResponseEntity.ok(jwtDto);
    }

    //카카오 로그인 유저 정보 조회
    @GetMapping("/kakao/{uid}")
    public ResponseEntity<UserDTO> getKakaoUserInfo(@PathVariable String uid) {
        UserDTO user = userService.getKakaoUserInfo(uid);
        return ResponseEntity.ok(user);
    }

    //카카오 유저 프로필 이미지 설정
    @SneakyThrows
    @PostMapping(value = "/image", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDTO> addImageToUser(@RequestPart("uid") String uid, @RequestPart("image") MultipartFile image, @AuthenticationPrincipal UserDetails userDetails) {
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = mapper.readValue(uid, UserDTO.class);
        UserDTO updatedUser = userService.addImageToUser(userDTO.getUid(), image, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }

    //카카오 유저 닉네임 설정
    @PostMapping("/nickname/{uid}")
    public ResponseEntity<UserDTO> updateNickname(@PathVariable String uid, @RequestBody Map<String, String> request, @AuthenticationPrincipal UserDetails userDetails) {
        String nickname = request.get("nickname");
        UserDTO updatedUser = userService.updateNickname(uid, nickname, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @GetMapping("/entry")
    public ResponseEntity<UserDTO> getUserEntry(@RequestParam Long userId) {
        UserDTO userDTO = userService.getUserById(userId);

        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userDTO);
    }
}
