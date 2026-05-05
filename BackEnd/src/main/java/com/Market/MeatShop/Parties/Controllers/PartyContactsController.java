package com.Market.MeatShop.Parties.Controllers;

import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyContactReq;
import com.Market.MeatShop.Parties.Entities.PartyContact;
import com.Market.MeatShop.Parties.Services.PartyContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parties/contacts")
public class PartyContactsController {
    private PartyContactService partyContactService ;

    public PartyContactsController(PartyContactService partyContactService) {
        this.partyContactService = partyContactService;
    }
    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheck()
    {
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createPartyContact(@Valid @RequestBody CreatePartyContactReq req){
        return ResponseEntity.status(HttpStatus.CREATED).body(partyContactService.createPartyContact(req));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePartyContact(@Valid @RequestBody UpdatePartyContactReq req ,@PathVariable Long id){
     return ResponseEntity.status(HttpStatus.ACCEPTED).body(partyContactService.updatePartyContact(req,id));
    }

   @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePartyContact(@PathVariable Long id){
        partyContactService.deletePartyContact(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }
}
