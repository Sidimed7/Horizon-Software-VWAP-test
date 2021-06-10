package hsoft;

import com.hsoft.codingtest.DataProvider;
import com.hsoft.codingtest.DataProviderFactory;
import com.hsoft.codingtest.MarketDataListener;
import com.hsoft.codingtest.PricingDataListener;
import javafx.util.Pair;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VwapTrigger {
  final static Map<String, Double> values = new HashMap<>();
  final static Map<String, Integer> numberTransactions = new HashMap<>();
  final static HashMap<String, List<Pair<Long, Double>>> valuesTransactions = new HashMap<>();
  final static Map<String, Double> transactionOccured = new HashMap<>();

  public static void main(String[] args) {
    DataProvider provider = DataProviderFactory.getDataProvider();
    provider.addMarketDataListener(new MarketDataListener() {
      public void transactionOccured(String productId, long quantity, double price) {
        // TODO Start to code here when a transaction occurred
        if (numberTransactions.containsKey(productId)) {
          if(numberTransactions.get(productId) < 5) {
            numberTransactions.put(productId , numberTransactions.get(productId) +1);
            List<Pair<Long, Double>> liste = valuesTransactions.get(productId);
            Pair<Long, Double> pair = new Pair<>(quantity,price);
            liste.add(pair);
            valuesTransactions.put(productId,liste );
          }
        }
        else {
          numberTransactions.put(productId , 1);
          List<Pair<Long, Double>> liste = valuesTransactions.get(productId);
          Pair<Long, Double> pair = new Pair<>(quantity,price);
          liste.add(pair);
          valuesTransactions.put(productId,liste );
        }
        valuesTransactions.forEach((k, v) -> {
          // de chaque prodcut
          double num = 0.0;
          int deno = 0;
          for(Pair<Long, Double> quantPrice : v) {
            num += quantPrice.getKey() * quantPrice.getValue();
            deno += quantPrice.getValue();
          }
          Double VWAP = num/deno;

          transactionOccured.put(productId,VWAP);
        });

      }
    });
    provider.addPricingDataListener(new PricingDataListener() {
      public void fairValueChanged(String productId, double fairValue) {
        // TODO Start to code here when a fair value changed
        if(values.containsKey(productId) ) {
          values.replace(productId, fairValue);
        }
        else {
          values.put(productId,fairValue);
        }
      }
    });

    provider.listen();
    // When this method returns, the test is finished and you can check your results
  }
}