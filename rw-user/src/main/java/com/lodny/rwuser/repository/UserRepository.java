package com.lodny.rwuser.repository;


import com.lodny.rwuser.entity.RealWorldUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<RealWorldUser, Long> {
//    Optional<RealWorldUser> findById(Long userId);
    Optional<RealWorldUser> findByEmail(final String email);
    Optional<RealWorldUser> findByUsername(final String username);
//    RealWorldUser save(final RealWorldUser user);
}
