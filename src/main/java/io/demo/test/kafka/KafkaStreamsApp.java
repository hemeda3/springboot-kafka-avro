package io.demo.test.kafka;

import com.ibm.gbs.schema.Customer;
import com.ibm.gbs.schema.CustomerBalance;
import com.ibm.gbs.schema.Transaction;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.apachecommons.CommonsLog;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;



@SpringBootApplication
public class KafkaStreamsApp {



    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsApp.class, args);
    }

}


@Component
@CommonsLog(topic = "Streams Logger")
class UserProcessor {


    /**
     *  Transactions is a fact:  because  it's large, with frequent updates and there is new data arrivals
     *  Customer is a dimension : because smaller with a potentially less frequent update rate
     *
     *
     * @return
     */

    @Bean
    public BiConsumer<KStream<String, Transaction>, KTable<String, Customer>> processCustomerTransactions() {


          final Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        final SpecificAvroSerde<Customer> customerSerder = new SpecificAvroSerde<>();
        final SpecificAvroSerde<Transaction> transactionSerder = new SpecificAvroSerde<>();
        final SpecificAvroSerde<CustomerBalance> customerBalanceSerder = new SpecificAvroSerde<>();

        customerSerder.configure(serdeConfig, false);
        transactionSerder.configure(serdeConfig, false);
        customerBalanceSerder.configure(serdeConfig, false);

        return (transactions, customers) ->
        {

            // join to the customers's channel
            KStream<String, CustomerBalance> join = transactions.join(customers,
                    (v1, v2) -> {

                        CustomerBalance customerBalance = CustomerBalance.newBuilder()
                                .setPhoneNumber(v2.getPhoneNumber()).setCustomerId(v2.getCustomerId())
                                .setAccountId(v1.getAccountId()).setBalance(v1.getBalance()).build();
                        System.out.println(" Print customerBalance : "+customerBalance);

                        return customerBalance;
                    });

            //  instead of using BiFunction used "to"
            join.to("CustomerBalance");




        };

    }

}
