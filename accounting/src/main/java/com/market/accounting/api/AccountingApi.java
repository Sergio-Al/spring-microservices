package com.market.accounting.api;

import com.market.accounting.bl.JournalBl;
import com.market.accounting.dto.JournalDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        try {
            boolean deleted = journalService.deleteJournalEntry(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            // Entry is not in a state that allows deletion (e.g., not draft)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get journal entry by journal entry number
     * This is useful for lookup by the unique journal entry number
     */
    @GetMapping("/journals/entry/{journalEntryNumber}")
    public ResponseEntity<JournalDto> getJournalEntryByNumber(@PathVariable String journalEntryNumber) {
        try {
            JournalDto journalEntry = journalService.getJournalEntryByNumber(journalEntryNumber);
            return journalEntry != null 
                ? ResponseEntity.ok(journalEntry)
                : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
