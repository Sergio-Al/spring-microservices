package com.market.accounting.bl;

import com.market.accounting.dto.JournalDto;
import com.market.accounting.entity.Journal;
import com.market.accounting.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JournalBl {

    @Autowired
    private JournalRepository journalRepository;

    public List<JournalDto> getAllJournalEntries(Pageable pageable) {
        Page<Journal> journalPage = journalRepository.findAll(pageable);
        return journalPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public JournalDto getJournalEntryById(Integer id) {
        Optional<Journal> journal = journalRepository.findById(id);
        return journal.map(this::convertToDto).orElse(null);
    }

    public JournalDto getJournalEntryByNumber(String journalEntryNumber) {
        Optional<Journal> journal = journalRepository.findByJournalEntryNumber(journalEntryNumber);
        return journal.map(this::convertToDto).orElse(null);
    }

    public JournalDto createJournalEntry(JournalDto journalDto) {
        // Validate accounting rules before saving
        validateJournalEntry(journalDto);
        
        Journal journal = convertToEntity(journalDto);
        Journal savedJournal = journalRepository.save(journal);
        return convertToDto(savedJournal);
    }

    public JournalDto updateJournalEntry(JournalDto journalDto) {
        if (journalDto.getId() != null && journalRepository.existsById(journalDto.getId())) {
            // Validate accounting rules before updating
            validateJournalEntry(journalDto);
            
            Journal journal = convertToEntity(journalDto);
            Journal savedJournal = journalRepository.save(journal);
            return convertToDto(savedJournal);
        }
        return null;
    }

    public boolean deleteJournalEntry(Integer id) {
        if (journalRepository.existsById(id)) {
            journalRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Validates journal entry according to accounting principles
     */
    private void validateJournalEntry(JournalDto journalDto) {
        BigDecimal debitAmount = journalDto.getDebitAmount() != null ? journalDto.getDebitAmount() : BigDecimal.ZERO;
        BigDecimal creditAmount = journalDto.getCreditAmount() != null ? journalDto.getCreditAmount() : BigDecimal.ZERO;

        // Rule 1: Cannot have both debit and credit amounts greater than zero
        if (debitAmount.compareTo(BigDecimal.ZERO) > 0 && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Journal entry cannot have both debit and credit amounts. Use separate entries for each.");
        }

        // Rule 2: Must have either debit or credit amount (not both zero)
        if (debitAmount.compareTo(BigDecimal.ZERO) == 0 && creditAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Journal entry must have either a debit or credit amount greater than zero.");
        }
    }

    private JournalDto convertToDto(Journal journal) {
        JournalDto dto = new JournalDto();
        dto.setId(journal.getId());
        dto.setJournalEntryNumber(journal.getJournalEntryNumber());
        dto.setTransactionDate(journal.getTransactionDate());
        dto.setPostingDate(journal.getPostingDate());
        dto.setAccountCode(journal.getAccountCode());
        dto.setAccountName(journal.getAccountName());
        dto.setDebitAmount(journal.getDebitAmount());
        dto.setCreditAmount(journal.getCreditAmount());
        dto.setDescription(journal.getDescription());
        dto.setStatus(journal.getStatus() != null ? journal.getStatus().toString() : null);
        dto.setCreatedBy(journal.getCreatedBy());
        dto.setCreatedDate(journal.getCreatedAt());
        return dto;
    }

    private Journal convertToEntity(JournalDto dto) {
        Journal journal = new Journal();
        journal.setId(dto.getId());
        journal.setJournalEntryNumber(dto.getJournalEntryNumber());
        journal.setTransactionDate(dto.getTransactionDate());
        journal.setPostingDate(dto.getPostingDate());
        journal.setAccountCode(dto.getAccountCode());
        journal.setAccountName(dto.getAccountName());
        journal.setDebitAmount(dto.getDebitAmount());
        journal.setCreditAmount(dto.getCreditAmount());
        journal.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            journal.setStatus(Journal.Status.valueOf(dto.getStatus()));
        }
        journal.setCreatedBy(dto.getCreatedBy());
        // Note: createdAt is managed by JPA lifecycle callbacks
        return journal;
    }
}
