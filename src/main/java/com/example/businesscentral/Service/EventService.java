package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.Event;
import com.example.businesscentral.Entity.Role;
import com.example.businesscentral.Repository.EventRepository;
import com.example.businesscentral.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    final EventRepository eventRepository;
    private final RoleRepository roleRepository;

    public Set<Role> findRolesByNames(Set<String> roleNames) {
        return roleRepository.findByNameIn(roleNames).stream()
                .collect(Collectors.toSet());
    }
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    public  List<Role> getAllRoles(){
        return  roleRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        return eventRepository.findById(id).map(event -> {
            event.setName(eventDetails.getName());
            event.setDateEvent(eventDetails.getDateEvent());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event findEventByName(String eventName) {
        return eventRepository.findByName(eventName)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
