package com.example.demo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Properties;

@Controller
public class FileUploadController {

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();

            Properties producerProps = new Properties();
            producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");

            Producer<String, byte[]> producer = new KafkaProducer<>(producerProps);
            producer.send(new ProducerRecord<>(kafkaTopic, file.getOriginalFilename(), fileBytes));
            producer.close();

            return "redirect:/upload?success=File+uploaded+and+distributed+to+Kafka!";
        } catch (IOException e) {
            return "redirect:/upload?error=Error+uploading+the+file.";
        }
    }
}
