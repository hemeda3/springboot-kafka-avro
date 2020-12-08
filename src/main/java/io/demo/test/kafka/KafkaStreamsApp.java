package io.demo.test.kafka;

import com.ibm.gbs.schema.Customer;
import com.ibm.gbs.schema.CustomerBalance;
import com.ibm.gbs.schema.Transaction;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.apachecommons.CommonsLog;

import java.util.function.BiFunction;


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
    public BiFunction<KStream<String, Transaction>, GlobalKTable<String, Customer>, KStream<String, CustomerBalance>>
    processCustomerTransactions() {

        return (transactions, customers) ->
        {


            // join to the customers's channel
            KStream<String, CustomerBalance> join = transactions.join(customers,
                    (k, v) -> v.getAccountId(),
                    (v1, v2) -> {
                        CustomerBalance customerBalance = CustomerBalance.newBuilder()
                                .setPhoneNumber(v2.getPhoneNumber()).setCustomerId(v2.getCustomerId())
                                .setAccountId(v1.getAccountId()).setBalance(v1.getBalance()).build();
                        return customerBalance;
                    }
            );

            return join;


        };

    }








    public BiFunction<KStream<String,Transaction>, KTable<String,Customer>,KStream<String,CustomerBalance>>
    processCustomerTransactions1(){

        return(transactions,customers)->
        {
            KStream<String,CustomerBalance>join=transactions.map((key,purchase)->
                    KeyValue.pair(purchase.getAccountId(),purchase))
                    .join(customers,
                            (v1,v2)->{
                                CustomerBalance customerBalance=CustomerBalance.newBuilder()
                                        .setPhoneNumber(v2.getPhoneNumber()).setCustomerId(v2.getCustomerId())
                                        .setAccountId(v1.getAccountId()).setBalance(v1.getBalance()).build();
                                return customerBalance;
                            }
                    );


            return join;


        };

    }


  @Bean
  public java.util.function.Consumer<KStream<String, Customer>> processCustomer() {
    return input ->
            input.foreach((key, value) -> {

              System.out.println(" Customer Key: " + key + " Value: " + value);
            });

  }





  @Bean
  public java.util.function.Consumer<KStream<String, Transaction>> processTransaction() {
    return input ->
    {

        input.foreach((key, value) -> {


            System.out.println("Transaction Key: " + key + " Value: " + value);
        });
    };
  }



//
//  public java.util.function.BiConsumer<KStream<String, Transaction>,KStream<String, Customer>> processCustomerTransactions() {
//    return (transactionKStream,customerKStream) ->
//    {
//
//      customerKStream.foreach((key, value) -> {
//
//                System.out.println(" Customer Key - : " + key + " Value: " + value);
//              }
//      );
//      transactionKStream.foreach((key, value) -> {
//
//                System.out.println(" Transaction Key - : " + key + " Value: " + value);
//              }
//      );
//
//    };
//  }




//    @Bean
//	public BiFunction<KStream<String, Transaction>,KStream<String, Customer>,
//            KStream<String, CustomerBalance>> processCustomerTransactions2() {
//        return (transactionKStream,customerKStream) ->
//        {
//
//            customerKStream.foreach((key, value) -> {
//
//                        System.out.println(" Customer Key - : " + key + " Value: " + value);
//                    }
//            );
//            transactionKStream.foreach((key, value) -> {
//
//                        System.out.println(" Transaction Key - : " + key + " Value: " + value);
//                    }
//            );
//
//            return null;
//        };
//
//	}


}
