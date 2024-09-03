package com.project.interaction.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.project.interaction.dto.CustomerProfile;
import com.project.interaction.entity.InteractionEntity;
import com.project.interaction.repository.InteractionRepository;

@Service
public class InteractionService {

    @Autowired
    private InteractionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private final String customerProfileServiceUrl = "http://localhost:9595/api/customers";

    public List<InteractionEntity> findAll() {
        return repository.findAll();
    }

    public Optional<InteractionEntity> findById(String interactionId) {
        return repository.findById(interactionId);
    }

    public InteractionEntity save(InteractionEntity interactionEntity) {
        return repository.save(interactionEntity);
    } 

    public void deleteById(String interactionId) {
        repository.deleteById(interactionId);
    }

    public InteractionEntity getInteractionWithCustomerProfile(String interactionId) {
        Optional<InteractionEntity> interactionOptional = repository.findById(interactionId);
        if (interactionOptional.isPresent()) {
            InteractionEntity interaction = interactionOptional.get();
            CustomerProfile customerProfile = getCustomerProfile(interaction.getCustomerId());
            
            if (customerProfile != null) {
                // Optionally, you could combine interaction and customerProfile into a new DTO.
                System.out.println("Customer Profile: " + customerProfile);
                return interaction;
            } else {
                // Customer profile not found
                System.out.println("Customer profile not found for ID: " + interaction.getCustomerId());
                return null; // or handle as per your logic
            }
        } else {
            // Interaction not found
            return null; // or handle as per your logic
        }
    }

    private CustomerProfile getCustomerProfile(Long customerId) {
        String url = customerProfileServiceUrl + "/" + customerId;
        try {
            CustomerProfile customerProfile = restTemplate.getForObject(url, CustomerProfile.class);
            if (customerProfile == null) {
                System.out.println("No customer profile found for ID: " + customerId);
            }
            return customerProfile;
        } catch (RestClientException e) {
            // Handle the exception
            throw new RuntimeException("Failed to fetch customer info for ID: " + customerId, e);
        }
    }
}
