package com.market.accounting.api;

import com.market.accounting.bl.JournalBl;
import com.market.accounting.dto.JournalDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting")
public class AccountingApi {

    @Autowired
    private JournalBl journalService;

    @GetMapping("/health")
    public String health() {
        return "Accounting service is running";
    }

    @GetMapping("/journals")
    public ResponseEntity<List<JournalDto>> getAllJournals(Pageable pageable) {
        List<JournalDto> journals = journalService.getAllJournalEntries(pageable);
        return ResponseEntity.ok(journals);
    }

    @GetMapping("/journals/{id}")
    public ResponseEntity<JournalDto> getJournalById(@PathVariable Integer id) {
        JournalDto journal = journalService.getJournalEntryById(id);
        if (journal != null) {
            return ResponseEntity.ok(journal);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/journals")
    public ResponseEntity<JournalDto> createJournal(@RequestBody JournalDto journalDto) {
        JournalDto createdJournal = journalService.createJournalEntry(journalDto);
        return ResponseEntity.ok(createdJournal);
    }

    @PutMapping("/journals")
    public ResponseEntity<JournalDto> updateJournal(@RequestBody JournalDto journalDto) {
        JournalDto updatedJournal = journalService.updateJournalEntry(journalDto);
        if (updatedJournal != null) {
            return ResponseEntity.ok(updatedJournal);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/journals/{id}")
    public ResponseEntity<Void> deleteJournal(@PathVariable Integer id) {
        boolean deleted = journalService.deleteJournalEntry(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
