package org.example.repository;

import org.example.domain.model.MessageDelivery;
import org.example.domain.model.MessageDeliveryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery, MessageDeliveryKey> {

}