package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Category;
import com.example.businesscentral.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.users WHERE n.enabled = true")
    List<Notification> getSentNotifications();

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.users WHERE n.enabled = false")
    List<Notification> getDraftNotifications();
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.users WHERE n.category = :category")
    List<Notification> findByCategory(Category category);

}
