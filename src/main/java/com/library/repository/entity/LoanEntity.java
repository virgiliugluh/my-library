package com.library.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity(name = "loans")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BookEntity book;

    @ManyToOne
    private UserEntity user;

    private ZonedDateTime loanDate;
    private ZonedDateTime dueDate;
    private ZonedDateTime returnDate;
}
