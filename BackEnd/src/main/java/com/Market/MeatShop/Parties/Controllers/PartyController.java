package com.Market.MeatShop.Parties.Controllers;


import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.PartyFilterReq;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Repositories.PartyRepo;
import com.Market.MeatShop.Parties.Services.PartyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parties")
@Slf4j
public class PartyController {
    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }
    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheck()
    {
        log.info("GET /parties/health-check requested");
        return ResponseEntity.ok().build();
    }
    @PostMapping("")
    public ResponseEntity<?> createParty(@RequestBody CreatePartyRequest req){
        log.info("POST /parties {} requested", req);
        return ResponseEntity.status(HttpStatus.CREATED).body(partyService.createParty(req));
    }
    @GetMapping("/filter")
    public ResponseEntity<?> getPartyByFilter(PartyFilterReq req , Pageable pageable){
        log.info("GET /parties/filter query{} requested", req);
        return ResponseEntity.status(HttpStatus.OK).body(partyService.findByFilter(req , pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getPartyById(@PathVariable Long id){
        log.info("GET /parties/{} requested", id);
        return ResponseEntity.status(HttpStatus.OK).body(partyService.findPartyById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateParty(@PathVariable Long id, @RequestBody UpdatePartyReq req){
        log.info("PUT /parties/{} {} requested", id, req);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(partyService.updateParty(req , id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParty(@PathVariable Long id){
        log.info("DELETE /parties/{} requested", id);
        partyService.deleteParty(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    
}
