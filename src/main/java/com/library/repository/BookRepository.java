package com.library.repository;

import com.library.repository.entity.BookEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    @Query("select b from books b where b.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    BookEntity findByIdForUpdate(Long id);
}
