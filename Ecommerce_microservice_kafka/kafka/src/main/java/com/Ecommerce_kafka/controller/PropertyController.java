package com.Ecommerce_kafka.controller;
import com.Ecommerce_kafka.Entity.Property;
import com.Ecommerce_kafka.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // Get all available properties
    @GetMapping("/available")
    public ResponseEntity<List<Property>> getAvailableProperties() {
        List<Property> availableProperties = propertyService.getAvailableProperties();
        return ResponseEntity.ok(availableProperties);
    }

    // Add a new property
    @PostMapping
    public ResponseEntity<Property> addProperty(@RequestBody Property property) {
        Property createdProperty = propertyService.addProperty(property);
        return ResponseEntity.ok(createdProperty);
    }

    // Update property status
    @PutMapping("/{propertyId}/status")
    public ResponseEntity<Property> updatePropertyStatus(@PathVariable String propertyId, @RequestParam String status) {
        Property updatedProperty = propertyService.updatePropertyStatus(Long.valueOf(propertyId), status);
        return ResponseEntity.ok(updatedProperty);
    }
}
