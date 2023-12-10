package com.spartanullnull.otil.domain.reportpost.controller;

import com.spartanullnull.otil.domain.reportpost.service.ReportPostService;
import com.spartanullnull.otil.domain.reportpost.dto.ReportPostRequestDto;
import com.spartanullnull.otil.domain.reportpost.dto.ReportPostResponseDto;
import com.spartanullnull.otil.domain.user.entity.UserRoleEnum;
import com.spartanullnull.otil.global.dto.CommonResponseDto;
import com.spartanullnull.otil.security.Impl.UserDetailsImpl;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportPostController {

    private final ReportPostService reportPostService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<ReportPostResponseDto> createReport(
        @RequestBody ReportPostRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            ReportPostResponseDto reportPostResponseDto = reportPostService.createReport(requestDto,
                userDetails.getUser());
            return ResponseEntity.ok().body(reportPostResponseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/gets/user")
    public ResponseEntity<Page<ReportPostResponseDto>> getUserAllReportPost(
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("sortBy") String sortBy,
        @RequestParam("isAsc") boolean isAsc,
        @RequestBody ReportPostRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl user) {
        System.out.println(user.getUser().getRole());
        if (isValidUser(user, requestDto) && user.getUser().getRole() == UserRoleEnum.USER) {
            Page<ReportPostResponseDto> reports = reportPostService.getUserAllReportPost(user.getUser(), page - 1, size, sortBy, isAsc);
            return ResponseEntity.ok().body(reports);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/gets/admin")
    public ResponseEntity<Page<ReportPostResponseDto>> getAdminAllReportPost(
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("sortBy") String sortBy,
        @RequestParam("isAsc") boolean isAsc,
        @RequestBody ReportPostRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl user) {
        if (isValidAdmin(user, requestDto) && user.getUser().getRole() == UserRoleEnum.ADMIN) {
            Page<ReportPostResponseDto> reports = reportPostService.getAdminAllReportPost(page - 1, size, sortBy, isAsc);
            return ResponseEntity.ok().body(reports);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get/{postid}")
    public ResponseEntity<ReportPostResponseDto> getReport(@PathVariable Long postid,
        @RequestBody ReportPostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            String password = requestDto.getPassword();
            ReportPostResponseDto reportPostResponseDto = reportPostService.getReport(postid, password, userDetails.getUser());
            return ResponseEntity.ok().body(reportPostResponseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{postid}")
    public ResponseEntity<ReportPostResponseDto> updateReport(
        @RequestBody ReportPostRequestDto requestDto, @PathVariable Long postid,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            ReportPostResponseDto reportPostResponseDto = reportPostService.updateReport(postid,
                requestDto,userDetails.getUser());
            return ResponseEntity.ok().body(reportPostResponseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("delete/{postid}")
    public ResponseEntity<CommonResponseDto> deleteReport(@PathVariable Long postid,
        @RequestBody ReportPostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            reportPostService.deleteReport(postid, requestDto.getPassword(), userDetails.getUser());
            return ResponseEntity.ok().body(new CommonResponseDto("삭제 성공", HttpStatus.OK.value()));
        } catch (RejectedExecutionException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(new CommonResponseDto("삭제 실패", HttpStatus.BAD_REQUEST.value()));
        }
    }

    private boolean isValidUser(UserDetailsImpl user, ReportPostRequestDto requestDto) {
        String encodePassword = user.getPassword();
        return isValidCredentials(requestDto.getPassword(),encodePassword);
    }

    private boolean isValidAdmin(UserDetailsImpl user, ReportPostRequestDto requestDto) {
        String encodePassword = user.getPassword();
        return isValidCredentials(requestDto.getPassword(),encodePassword);
    }

    private boolean isValidCredentials(String storedPassword, String providedPassword) {
        return passwordEncoder.matches(storedPassword, providedPassword);
    }
}
