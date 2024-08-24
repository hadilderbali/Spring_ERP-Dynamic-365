package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Category;
import com.example.businesscentral.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByEnabled(boolean b);

    List<Notification> findByCategory(Category category);

}
