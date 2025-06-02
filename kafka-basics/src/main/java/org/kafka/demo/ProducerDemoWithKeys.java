package org.kafka.demo;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithKeys.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        log.info("I am Kafka producer");

        //create producer properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","192.168.1.3:9092");

        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        properties.setProperty("batch.size", "400");
        properties.setProperty("partitioner.class", RoundRobinPartitioner.class.getName());

        //create the producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        for(int j=0; j<2; j++) {

            for (int i = 0; i < 10; i++) {

                String topic = "demo_java";
                String key = "id_" + i;
                String value = "Hello World " + i;
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

                //send data
                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        //executed every time record successfully sent or an exception is thrown
                        if (e == null) {
                            log.info("Key: " + key + "\n" + "Partition: " + recordMetadata.partition() + "\n");
                        } else {
                            log.error("Error while producing", e);
                        }
                    }
                });

            }
            Thread.sleep(500);
        }


        //tell the producer to send all the data & block until done --synchronous
        producer.flush();

        //close the producer
        producer.close();
    }
}
