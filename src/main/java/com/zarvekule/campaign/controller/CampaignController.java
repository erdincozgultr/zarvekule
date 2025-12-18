package com.zarvekule.campaign.controller;

import com.zarvekule.campaign.dto.ApplicationRequest;
import com.zarvekule.campaign.dto.ApplicationResponse;
import com.zarvekule.campaign.dto.CampaignRequest;
import com.zarvekule.campaign.dto.CampaignResponse;
import com.zarvekule.campaign.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CampaignResponse> create(Principal principal,
                                                   @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campaignService.create(principal.getName(), request));
    }

    @PostMapping("/{id}/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> apply(Principal principal,
                                      @PathVariable Long id,
                                      @Valid @RequestBody ApplicationRequest request) {
        campaignService.applyToCampaign(principal.getName(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApplicationResponse>> getCampaignApplications(Principal principal,
                                                                             @PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getApplicationsForCampaign(principal.getName(), id));
    }

    @PatchMapping("/applications/{appId}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> acceptApplication(Principal principal, @PathVariable Long appId) {
        campaignService.manageApplication(principal.getName(), appId, true);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/applications/{appId}/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> rejectApplication(Principal principal, @PathVariable Long appId) {
        campaignService.manageApplication(principal.getName(), appId, false);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(Principal principal) {
        return ResponseEntity.ok(campaignService.getMyApplications(principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping("/count/open")
    public ResponseEntity<Long> countOpenCampaigns() {
        return ResponseEntity.ok(campaignService.countOpenCampaigns());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCampaign(Principal principal, @PathVariable Long id) {
        campaignService.deleteCampaign(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}