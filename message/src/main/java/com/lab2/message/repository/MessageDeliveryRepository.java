package com.lab2.message.repository;

import com.lab2.message.entity.MessageDelivery;
import com.lab2.message.entity.MessageDeliveryKey;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery, MessageDeliveryKey> {

}
